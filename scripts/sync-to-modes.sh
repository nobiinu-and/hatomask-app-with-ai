#!/bin/bash

#############################################################################
# modesへのフィードバックスクリプト
#
# 用途: 実験ブランチでの改善を modes/ ディレクトリにフィードバック
# 使用方法:
#   ./scripts/sync-to-modes.sh --mode=discovery --file=tasks/02_simple_modeling.md
#   ./scripts/sync-to-modes.sh --mode=predictive --dir=system
#############################################################################

set -euo pipefail

# カラー定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# デフォルト値
MODE=""
FILE=""
DIR=""
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
PROMPTS_DIR="$PROJECT_ROOT/docs/ai/prompts"

#############################################################################
# ヘルプ
#############################################################################
show_help() {
    cat << EOF
modesへのフィードバックスクリプト

使用方法:
    $0 --mode=<predictive|discovery> --file=<relative-path>
    $0 --mode=<predictive|discovery> --dir=<system|tasks>

オプション:
    --mode=MODE       対象モード（predictive または discovery）
    --file=FILE       同期するファイルの相対パス（system/ または tasks/ からの相対パス）
    --dir=DIR         同期するディレクトリ（system または tasks）
    -h, --help        このヘルプを表示

例:
    # 単一ファイルの同期
    $0 --mode=discovery --file=tasks/02_simple_modeling.md
    $0 --mode=predictive --file=system/00_role.md
    
    # ディレクトリ全体の同期
    $0 --mode=discovery --dir=system
    $0 --mode=predictive --dir=tasks

注意事項:
    - 差分が表示されるので、内容を確認してから承認してください
    - 同期後は必ずコミットしてください（コミットメッセージに[modes]を含める）
    - 例: git commit -m "[modes] 実験からのフィードバック: Phase 2の改善"

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
            --file=*)
                FILE="${1#*=}"
                shift
                ;;
            --dir=*)
                DIR="${1#*=}"
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

    # ファイルまたはディレクトリの検証
    if [[ -z "$FILE" && -z "$DIR" ]]; then
        echo -e "${RED}エラー: --file または --dir を指定してください${NC}"
        show_help
        exit 1
    fi

    if [[ -n "$FILE" && -n "$DIR" ]]; then
        echo -e "${RED}エラー: --file と --dir は同時に指定できません${NC}"
        exit 1
    fi

    if [[ -n "$DIR" && "$DIR" != "system" && "$DIR" != "tasks" ]]; then
        echo -e "${RED}エラー: --dir は system または tasks のいずれかを指定してください${NC}"
        exit 1
    fi
}

#############################################################################
# ファイルパスの検証と準備
#############################################################################
prepare_paths() {
    if [[ -n "$FILE" ]]; then
        # ファイル指定の場合
        SOURCE_FILE="$PROMPTS_DIR/$FILE"
        TARGET_FILE="$PROMPTS_DIR/modes/$MODE/$FILE"
        
        if [[ ! -f "$SOURCE_FILE" ]]; then
            echo -e "${RED}エラー: ソースファイルが見つかりません: $SOURCE_FILE${NC}"
            exit 1
        fi
        
        # ターゲットディレクトリ作成
        mkdir -p "$(dirname "$TARGET_FILE")"
        
    elif [[ -n "$DIR" ]]; then
        # ディレクトリ指定の場合
        SOURCE_DIR="$PROMPTS_DIR/$DIR"
        TARGET_DIR="$PROMPTS_DIR/modes/$MODE/$DIR"
        
        if [[ ! -d "$SOURCE_DIR" ]]; then
            echo -e "${RED}エラー: ソースディレクトリが見つかりません: $SOURCE_DIR${NC}"
            exit 1
        fi
        
        mkdir -p "$TARGET_DIR"
    fi
}

#############################################################################
# 差分の表示
#############################################################################
show_diff() {
    local source=$1
    local target=$2
    local label=$3
    
    echo -e "${BLUE}=== $label の差分 ===${NC}"
    echo ""
    
    if [[ ! -f "$target" ]]; then
        echo -e "${GREEN}[新規ファイル]${NC}"
        echo "modes/ に新しいファイルが追加されます"
    else
        if diff -q "$source" "$target" > /dev/null 2>&1; then
            echo -e "${GREEN}変更なし${NC}"
            return 1
        else
            echo -e "${YELLOW}以下の変更があります:${NC}"
            echo ""
            diff -u "$target" "$source" || true
        fi
    fi
    echo ""
    return 0
}

#############################################################################
# ファイルの同期
#############################################################################
sync_file() {
    local source=$1
    local target=$2
    local label=$3
    
    echo -e "${BLUE}$label を同期中...${NC}"
    cp "$source" "$target"
    echo -e "${GREEN}✓ 同期完了: $target${NC}"
    echo ""
}

#############################################################################
# ディレクトリの同期
#############################################################################
sync_directory() {
    local source_dir=$1
    local target_dir=$2
    local label=$3
    
    echo -e "${BLUE}$label の全ファイルを同期中...${NC}"
    
    local count=0
    while IFS= read -r -d '' file; do
        local relative_path="${file#$source_dir/}"
        local target_file="$target_dir/$relative_path"
        
        mkdir -p "$(dirname "$target_file")"
        cp "$file" "$target_file"
        echo "  ✓ $relative_path"
        ((count++))
    done < <(find "$source_dir" -type f -print0)
    
    echo ""
    echo -e "${GREEN}✓ $count ファイルを同期しました${NC}"
    echo ""
}

#############################################################################
# メイン処理
#############################################################################
main() {
    parse_args "$@"
    
    echo ""
    echo -e "${GREEN}================================${NC}"
    echo -e "${GREEN}modesフィードバックスクリプト${NC}"
    echo -e "${GREEN}================================${NC}"
    echo ""
    echo "モード: $MODE"
    echo "対象: ${FILE:-$DIR (directory)}"
    echo ""
    
    prepare_paths
    
    # ファイル同期の場合
    if [[ -n "$FILE" ]]; then
        if show_diff "$SOURCE_FILE" "$TARGET_FILE" "$FILE"; then
            echo ""
            read -p "この変更をmodesに反映しますか？ (y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                sync_file "$SOURCE_FILE" "$TARGET_FILE" "$FILE"
                
                echo -e "${YELLOW}次のステップ:${NC}"
                echo "  1. 変更を確認: git diff $TARGET_FILE"
                echo "  2. コミット: git add $TARGET_FILE"
                echo "  3. コミットメッセージ例:"
                echo "     git commit -m \"[modes] 実験からのフィードバック: $(basename "$FILE" .md)の改善\""
                echo ""
            else
                echo "同期を中止しました"
            fi
        else
            echo -e "${GREEN}変更がないため、同期は不要です${NC}"
        fi
        
    # ディレクトリ同期の場合
    elif [[ -n "$DIR" ]]; then
        echo -e "${YELLOW}警告: $DIR/ 配下の全ファイルが上書きされます${NC}"
        echo ""
        read -p "続行しますか？ (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            sync_directory "$SOURCE_DIR" "$TARGET_DIR" "$DIR/"
            
            echo -e "${YELLOW}次のステップ:${NC}"
            echo "  1. 変更を確認: git diff $TARGET_DIR"
            echo "  2. コミット: git add $TARGET_DIR"
            echo "  3. コミットメッセージ例:"
            echo "     git commit -m \"[modes] 実験からのフィードバック: $DIR/ 全体の改善\""
            echo ""
        else
            echo "同期を中止しました"
        fi
    fi
}

# スクリプト実行
main "$@"
