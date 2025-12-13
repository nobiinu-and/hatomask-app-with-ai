# 実験用プロンプトモード

このディレクトリには、予測駆動開発と発見駆動開発を比較実験するための2つのプロンプトセットが格納されています。

## 📁 ディレクトリ構造

```
modes/
├── README.md              # このファイル
├── predictive/            # 予測駆動モード（現行プロセス）
│   ├── system/            # システムプロンプト
│   └── tasks/             # Phase別タスクプロンプト
└── discovery/             # 発見駆動モード（実験的プロセス）
    ├── system/            # システムプロンプト
    ├── tasks/             # Phase別タスクプロンプト
    └── CHANGES.md         # Predictiveからの変更点

```

## 🎯 モードの思想

### Predictiveモード（予測駆動）

**思想**: 「正しさを作る」 - 事前に完全な設計を行い、実装時の手戻りを最小化

**特徴**:
- Phase 2で完全なドメインモデルを作成
- Phase 3で全エンドポイントを詳細定義
- Phase 4で詳細な実装計画を策定
- 推測・拡張を厳格に禁止
- 完全性・正確性を重視

**向いている場面**:
- 要件が明確で変更が少ない
- リスク管理が重要
- チーム規模が大きい

### Discoveryモード（発見駆動）

**思想**: 「発見しながら育てる」 - 最小限から開始し、実装中の発見で設計を進化させる

**特徴**:
- Phase 2で今必要な最小限のモデルを作成
- Phase 3で必要なエンドポイントから順次定義
- Phase 4で柔軟な計画（詳細は実装時に決定）
- 実装中の設計変更を歓迎
- 早期フィードバックと学習を重視

**向いている場面**:
- 要件が不確実で探索が必要
- 早期の価値提供が重要
- 小規模チームやソロ開発

## 🚀 使用方法

### 1. 実験ブランチを作成

```bash
# 予測駆動実験用
git checkout -b experiment/predictive-[scenario-name]

# 発見駆動実験用
git checkout -b experiment/discovery-[scenario-name]
```

### 2. 対応モードを展開

```bash
# Predictiveモードを展開
./scripts/deploy-prompts.sh --mode=predictive

# Discoveryモードを展開（dry-runで確認後）
./scripts/deploy-prompts.sh --mode=discovery --dry-run
./scripts/deploy-prompts.sh --mode=discovery
```

### 3. AIへの指示

展開後、AIには通常通り`docs/ai/prompts/system/`と`tasks/`を参照させます。

```
【セッション開始時の指示例】

今回は Discovery モードで実装を進めます。
以下のシステムプロンプトを参照してください：

- docs/ai/prompts/system/00_role.md
- docs/ai/prompts/system/01_implementation_workflow.md  
- docs/ai/prompts/system/02_quality_standards.md

Phase別のタスクは docs/ai/prompts/tasks/ 配下を参照してください。
```

### 4. 実験記録

実装中は `docs/experiments/[branch-name]-log.md` に記録します。

### 5. 改善のフィードバック

実験中に重要な改善を発見した場合：

```bash
# modesディレクトリへフィードバック
./scripts/sync-to-modes.sh --mode=discovery --file=tasks/02_simple_modeling.md
```

## 📊 実験完了後

1. 両実験の比較分析（`docs/experiments/comparison-report.md`）
2. 採用モードの決定（予測 / 発見 / ハイブリッド）
3. mainブランチの `system/` と `tasks/` に反映
4. 未採用モードを `modes/archive/YYYY-MM/` に移動

## ⚠️ 注意事項

- **mainブランチでは `system/` と `tasks/` を直接編集しない**
  - 変更は `modes/predictive/` または `modes/discovery/` で行う
  
- **実験ブランチでは `modes/` を編集しない**
  - 変更は展開後の `system/` と `tasks/` で行い、重要な改善のみ `sync-to-modes.sh` でフィードバック

- **展開前に必ず差分確認**
  - `--dry-run` オプションで意図しない上書きがないか確認

## 📚 関連ドキュメント

- [実験ガイド](../../experiments/README.md)
- [Discovery-Driven Development概要](../../experiments/discovery-driven-overview.md)（作成予定）
- [比較実験レポート](../../experiments/comparison-report.md)（実験完了後作成）
