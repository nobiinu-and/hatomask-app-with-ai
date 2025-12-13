#!/bin/bash

#############################################################################
# プロンプト自動展開スクリプト
#
# 用途: modes/配下のマスタープロンプトを system/とtasks/に展開
# 使用方法:
#   ./scripts/deploy-prompts.sh --mode=predictive [--dry-run]
#   ./scripts/deploy-prompts.sh --mode=discovery [--dry-run]
#############################################################################

set -euo pipefail

# カラー定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# デフォルト値
MODE=""
DRY_RUN=false
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
PROMPTS_DIR="$PROJECT_ROOT/docs/ai/prompts"
EXPERIMENTS_DIR="$PROJECT_ROOT/docs/experiments"
DEPLOYMENT_LOG="$EXPERIMENTS_DIR/.deployment-log"

#############################################################################
# ヘルプメッセージ
#############################################################################
show_help() {
    cat << EOF
プロンプト自動展開スクリプト

使用方法:
    $0 --mode=<predictive|discovery> [--dry-run]

オプション:
    --mode=MODE       展開するモード（predictive または discovery）
    --dry-run         実際のコピーは行わず、差分のみ表示
    -h, --help        このヘルプを表示

例:
    # 差分を確認（コピーしない）
    $0 --mode=discovery --dry-run
    
    # Discoveryモードを展開
    $0 --mode=discovery
    
    # Predictiveモードを展開
    $0 --mode=predictive

注意事項:
    - 展開前に未コミットの変更がある場合は中止されます
    - 差分が表示されるので、内容を確認してから承認してください
    - 展開記録は $DEPLOYMENT_LOG に記録されます

EOF
}

#############################################################################
# 引数解析
#############################################################################
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --mode=*)
                MODE="${1#*=}"
                shift
                ;;
            --dry-run)
                DRY_RUN=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                echo -e "${RED}エラー: 不明なオプション: $1${NC}"
                show_help
                exit 1
                ;;
        esac
    done

    # モードの検証
    if [[ -z "$MODE" ]]; then
        echo -e "${RED}エラー: --mode を指定してください${NC}"
        show_help
        exit 1
    fi

    if [[ "$MODE" != "predictive" && "$MODE" != "discovery" ]]; then
        echo -e "${RED}エラー: --mode は predictive または discovery のいずれかを指定してください${NC}"
        exit 1
    fi
}

#############################################################################
# 未コミット変更のチェック
#############################################################################
check_uncommitted_changes() {
    echo -e "${BLUE}[1/5] 未コミット変更のチェック...${NC}"
    
    cd "$PROJECT_ROOT"
    
    # system/とtasks/に未コミット変更があるかチェック
    if git status --porcelain -- "$PROMPTS_DIR/system" "$PROMPTS_DIR/tasks" 2>/dev/null | grep -q '^'; then
        echo -e "${RED}エラー: system/ または tasks/ に未コミットの変更があります${NC}"
        echo ""
        echo "以下のファイルに変更があります:"
        git status --short -- "$PROMPTS_DIR/system" "$PROMPTS_DIR/tasks"
        echo ""
        echo "展開を続行すると、これらの変更が上書きされる可能性があります。"
        echo "以下のいずれかを実行してください:"
        echo "  1. 変更をコミット: git add . && git commit -m '...'"
        echo "  2. 変更をスタッシュ: git stash"
        echo "  3. 変更を破棄: git checkout -- docs/ai/prompts/system docs/ai/prompts/tasks"
        exit 1
    fi
    
    echo -e "${GREEN}✓ 未コミット変更なし${NC}"
    echo ""
}

#############################################################################
# 差分の計算と表示
#############################################################################
calculate_diff() {
    local source_dir=$1
    local target_dir=$2
    local label=$3
    
    echo -e "${BLUE}$label の差分を計算中...${NC}"
    
    local added=0
    local modified=0
    local deleted=0
    local unchanged=0
    
    # ソースディレクトリのファイルをチェック
    if [[ -d "$source_dir" ]]; then
        while IFS= read -r -d '' file; do
            local relative_path="${file#$source_dir/}"
            local target_file="$target_dir/$relative_path"
            
            if [[ ! -f "$target_file" ]]; then
                ((added++))
                echo -e "  ${GREEN}[追加]${NC} $relative_path"
            elif ! diff -q "$file" "$target_file" > /dev/null 2>&1; then
                ((modified++))
                echo -e "  ${YELLOW}[変更]${NC} $relative_path"
            else
                ((unchanged++))
            fi
        done < <(find "$source_dir" -type f -print0)
    fi
    
    # ターゲットディレクトリで削除されるファイルをチェック
    if [[ -d "$target_dir" ]]; then
        while IFS= read -r -d '' file; do
            local relative_path="${file#$target_dir/}"
            local source_file="$source_dir/$relative_path"
            
            if [[ ! -f "$source_file" ]]; then
                ((deleted++))
                echo -e "  ${RED}[削除]${NC} $relative_path"
            fi
        done < <(find "$target_dir" -type f -print0)
    fi
    
    echo ""
    echo -e "${BLUE}サマリー:${NC}"
    echo "  追加: $added"
    echo "  変更: $modified"
    echo "  削除: $deleted"
    echo "  変更なし: $unchanged"
    echo ""
    
    # 変更がない場合
    if [[ $added -eq 0 && $modified -eq 0 && $deleted -eq 0 ]]; then
        return 1  # 変更なし
    fi
    
    return 0  # 変更あり
}

#############################################################################
# 詳細差分の表示
#############################################################################
show_detailed_diff() {
    local source_dir=$1
    local target_dir=$2
    local label=$3
    
    echo -e "${BLUE}=== $label の詳細差分 ===${NC}"
    echo ""
    
    if [[ -d "$source_dir" && -d "$target_dir" ]]; then
        while IFS= read -r -d '' file; do
            local relative_path="${file#$source_dir/}"
            local target_file="$target_dir/$relative_path"
            
            if [[ -f "$target_file" ]] && ! diff -q "$file" "$target_file" > /dev/null 2>&1; then
                echo -e "${YELLOW}--- $relative_path の差分 ---${NC}"
                diff -u "$target_file" "$file" | head -n 50 || true
                echo ""
            fi
        done < <(find "$source_dir" -type f -print0)
    fi
}

#############################################################################
# ファイルのコピー
#############################################################################
copy_files() {
    local source_dir=$1
    local target_dir=$2
    local label=$3
    
    echo -e "${BLUE}$label をコピー中...${NC}"
    
    # ターゲットディレクトリをクリーン
    if [[ -d "$target_dir" ]]; then
        rm -rf "$target_dir"
    fi
    
    # ディレクトリ作成
    mkdir -p "$target_dir"
    
    # ファイルコピー
    cp -r "$source_dir"/* "$target_dir"/
    
    echo -e "${GREEN}✓ コピー完了${NC}"
    echo ""
}

#############################################################################
# 展開記録
#############################################################################
log_deployment() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    local branch=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "unknown")
    local commit=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
    
    mkdir -p "$EXPERIMENTS_DIR"
    
    cat >> "$DEPLOYMENT_LOG" << EOF
---
timestamp: $timestamp
mode: $MODE
branch: $branch
commit: $commit
system_files: $(find "$PROMPTS_DIR/modes/$MODE/system" -type f | wc -l)
tasks_files: $(find "$PROMPTS_DIR/modes/$MODE/tasks" -type f | wc -l)
EOF
    
    echo -e "${GREEN}✓ 展開記録を $DEPLOYMENT_LOG に保存しました${NC}"
}

#############################################################################
# メイン処理
#############################################################################
main() {
    parse_args "$@"
    
    echo ""
    echo -e "${GREEN}================================${NC}"
    echo -e "${GREEN}プロンプト自動展開スクリプト${NC}"
    echo -e "${GREEN}================================${NC}"
    echo ""
    echo "モード: $MODE"
    echo "Dry-run: $DRY_RUN"
    echo ""
    
    # ソースとターゲットのパス
    local source_system="$PROMPTS_DIR/modes/$MODE/system"
    local source_tasks="$PROMPTS_DIR/modes/$MODE/tasks"
    local target_system="$PROMPTS_DIR/system"
    local target_tasks="$PROMPTS_DIR/tasks"
    
    # ソースディレクトリの存在確認
    if [[ ! -d "$source_system" || ! -d "$source_tasks" ]]; then
        echo -e "${RED}エラー: $MODE モードのソースディレクトリが見つかりません${NC}"
        echo "  期待するパス:"
        echo "    $source_system"
        echo "    $source_tasks"
        exit 1
    fi
    
    # ステップ1: 未コミット変更のチェック
    if [[ "$DRY_RUN" = false ]]; then
        check_uncommitted_changes
    fi
    
    # ステップ2: system/の差分確認
    echo -e "${BLUE}[2/5] system/ の差分確認${NC}"
    echo ""
    if calculate_diff "$source_system" "$target_system" "system/"; then
        echo ""
        read -p "詳細な差分を表示しますか？ (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            show_detailed_diff "$source_system" "$target_system" "system/"
        fi
    else
        echo -e "${GREEN}system/ に変更はありません${NC}"
    fi
    echo ""
    
    # ステップ3: tasks/の差分確認
    echo -e "${BLUE}[3/5] tasks/ の差分確認${NC}"
    echo ""
    if calculate_diff "$source_tasks" "$target_tasks" "tasks/"; then
        echo ""
        read -p "詳細な差分を表示しますか？ (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            show_detailed_diff "$source_tasks" "$target_tasks" "tasks/"
        fi
    else
        echo -e "${GREEN}tasks/ に変更はありません${NC}"
    fi
    echo ""
    
    # Dry-runの場合はここで終了
    if [[ "$DRY_RUN" = true ]]; then
        echo -e "${YELLOW}Dry-runモード: 実際のコピーは行いませんでした${NC}"
        echo ""
        echo "実際に展開する場合は、--dry-run オプションを外して実行してください:"
        echo "  $0 --mode=$MODE"
        echo ""
        exit 0
    fi
    
    # ステップ4: ユーザー承認
    echo -e "${BLUE}[4/5] 展開の確認${NC}"
    echo ""
    echo -e "${YELLOW}警告: system/ と tasks/ の内容が上書きされます${NC}"
    echo ""
    read -p "展開を続行しますか？ (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "展開を中止しました"
        exit 0
    fi
    echo ""
    
    # ステップ5: ファイルコピー
    echo -e "${BLUE}[5/5] ファイルのコピー${NC}"
    echo ""
    copy_files "$source_system" "$target_system" "system/"
    copy_files "$source_tasks" "$target_tasks" "tasks/"
    
    # 展開記録
    log_deployment
    
    echo ""
    echo -e "${GREEN}================================${NC}"
    echo -e "${GREEN}展開が完了しました！${NC}"
    echo -e "${GREEN}================================${NC}"
    echo ""
    echo "次のステップ:"
    echo "  1. 展開されたファイルを確認: git diff docs/ai/prompts/system docs/ai/prompts/tasks"
    echo "  2. AIへのセッション開始指示例:"
    echo ""
    echo -e "${BLUE}     今回は ${MODE^} モードで実装を進めます。"
    echo "     以下のシステムプロンプトを参照してください："
    echo ""
    echo "     - docs/ai/prompts/system/00_role.md"
    echo "     - docs/ai/prompts/system/01_implementation_workflow.md"
    echo "     - docs/ai/prompts/system/02_quality_standards.md"
    echo ""
    echo "     Phase別のタスクは docs/ai/prompts/tasks/ 配下を参照してください。${NC}"
    echo ""
}

# スクリプト実行
main "$@"
