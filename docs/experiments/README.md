# 実験管理ディレクトリ

このディレクトリは、予測駆動開発（Predictive）と発見駆動開発（Discovery-Driven）の比較実験を記録・管理するためのものです。

## 📁 ディレクトリ構成

```
experiments/
├── README.md                    # このファイル
├── .deployment-log              # プロンプト展開の記録（自動生成）
├── experiment-log.template.md   # 実験ログのテンプレート
├── comparison-report.template.md # 比較レポートのテンプレート
├── predictive-experiments/      # 予測駆動実験の記録
│   └── jpeg-upload-log.md
└── discovery-experiments/       # 発見駆動実験の記録
    └── multi-upload-log.md
```

## 🎯 実験の目的

2つの開発アプローチを実際に比較し、それぞれの強み・弱み・適用場面を実証的に理解します。

### Predictiveモード（予測駆動）
- 思想: 「正しさを作る」
- 事前に完全な設計
- 実装時の手戻りを最小化

### Discoveryモード（発見駆動）
- 思想: 「発見しながら育てる」  
- 最小限から開始
- 実装中の発見で設計を進化

## 🚀 実験の進め方

### ステップ1: 実験ブランチ作成

```bash
# Predictive実験用
git checkout main
git pull
git checkout -b experiment/predictive-[scenario-name]

# Discovery実験用
git checkout main
git pull
git checkout -b experiment/discovery-[scenario-name]
```

### ステップ2: プロンプトの展開

```bash
# Predictiveモード
./scripts/deploy-prompts.sh --mode=predictive --dry-run  # 差分確認
./scripts/deploy-prompts.sh --mode=predictive

# Discoveryモード
./scripts/deploy-prompts.sh --mode=discovery --dry-run   # 差分確認
./scripts/deploy-prompts.sh --mode=discovery
```

### ステップ3: 実験ログの作成

```bash
# テンプレートをコピー
cp docs/experiments/experiment-log.template.md \
   docs/experiments/predictive-experiments/jpeg-upload-log.md

# または
cp docs/experiments/experiment-log.template.md \
   docs/experiments/discovery-experiments/multi-upload-log.md
```

### ステップ4: 実装と記録

- Phase 1-7を実施
- タイムスタンプを記録
- 発見・気づきを逐一記録
- 設計変更があれば理由と影響を記録

### ステップ5: 振り返り

両実験完了後:
1. メトリクスを集計
2. 定性評価を整理  
3. 比較レポート作成（`comparison-report.template.md`使用）
4. 今後のプロセスを決定

## 📊 記録すべきメトリクス

### 定量メトリクス

| 指標 | 測定方法 |
|------|---------|
| Phase 2完了時間 | タイムスタンプ記録 |
| 最初のE2Eグリーン時間 | 初回テスト成功時刻 |
| 総実装時間 | Phase 1開始〜Phase 7完了 |
| ドメインモデル修正回数 | Git commit/diff |
| API仕様変更回数 | OpenAPIファイル変更回数 |
| コミット数 | `git log --oneline \| wc -l` |
| ドキュメント行数（Phase 1-4） | `find docs -name "*.md" \| xargs wc -l` |

### 定性評価

| 項目 | 評価方法 |
|------|---------|
| ストレスレベル | 5段階評価 |
| 学習効果 | 発見・気づきの数と質 |
| 柔軟性 | 追加要件への対応しやすさ |
| AIとの協働 | プロンプトの複雑さ、ターン数 |
| 設計の進化 | モデル変更の理由と効果 |

## 📝 実験ログの書き方

### タイムラインの記録

```markdown
| 時刻 | Phase | イベント | 備考 |
|------|-------|---------|------|
| 10:00 | Phase 1 | 開始 | - |
| 10:30 | Phase 2 | ドメインモデル作成 | Photoエンティティ定義 |
| 11:00 | Phase 2 | モデル修正 | MimeType ValueObject追加 |
```

### 発見の記録（Discovery版のみ）

```markdown
## 主要な発見・気づき

### 設計の進化
- [2025-12-15 11:00] 実装中にPhotoのファイルパスとURLを分離する必要に気づいた
  - 理由: ダウンロード時にpublicなURLが必要
  - 対応: filePathとpublicUrlプロパティを追加

### 実装のブロッカー
- [2025-12-15 14:00] Phase 3でMimeType制約を過剰定義し、Phase 6で修正が必要だった
  - 教訓: 最初は緩い制約から開始すべき
```

## 🔄 実験中の改善フィードバック

実験中に重要な改善を発見した場合:

```bash
# 単一ファイルの改善をmodesに反映
./scripts/sync-to-modes.sh --mode=discovery --file=tasks/02_simple_modeling.md

# コミット
git add docs/ai/prompts/modes/discovery/tasks/02_simple_modeling.md
git commit -m "[modes] 実験からのフィードバック: Phase 2の曖昧さ表現を改善"
```

## 📈 比較レポートの作成

両実験完了後、`comparison-report.template.md`を使用してレポートを作成:

```bash
cp docs/experiments/comparison-report.template.md \
   docs/experiments/comparison-report-2025-12.md
```

レポートには以下を含める:
- エグゼクティブサマリー（結論）
- 定量結果の比較表
- 定性結果の分析
- 発見事項の整理
- 推奨事項（いつどちらを使うべきか）

## 🎉 実験完了後

### 統合の決定

比較レポートを基に、以下を決定:

1. **Predictive採用**: `modes/predictive/` → `system/`, `tasks/`
2. **Discovery採用**: `modes/discovery/` → `system/`, `tasks/`
3. **ハイブリッド**: 両方の良い点を組み合わせた新プロンプトを作成

### アーカイブ

```bash
# 未採用モードをアーカイブ
mkdir -p docs/ai/prompts/modes/archive/2025-12
mv docs/ai/prompts/modes/[未採用モード] docs/ai/prompts/modes/archive/2025-12/

# 実験記録もアーカイブ
mkdir -p docs/experiments/archive/2025-12
mv docs/experiments/predictive-experiments docs/experiments/archive/2025-12/
mv docs/experiments/discovery-experiments docs/experiments/archive/2025-12/
```

## 🔍 トラブルシューティング

### プロンプト展開で意図しない上書きが発生

```bash
# Git stashで一時退避
git stash

# プロンプトを再展開
./scripts/deploy-prompts.sh --mode=discovery

# 必要な変更のみ戻す
git stash pop
```

### 実験中にモードを間違えた

```bash
# 正しいモードで新ブランチを作成
git checkout -b experiment/discovery-[scenario-name]-retry

# プロンプトを正しく展開
./scripts/deploy-prompts.sh --mode=discovery

# 実装をやり直す
```

## 📚 関連ドキュメント

- [プロンプトモードREADME](../ai/prompts/modes/README.md)
- [Discovery版変更点](../ai/prompts/modes/discovery/CHANGES.md)
- [機能仕様](../spec/features/)
- [実装計画](../plans/)

## 💡 ベストプラクティス

1. **タイムスタンプは正確に**: 時間測定が比較の鍵
2. **発見は即座に記録**: 後で思い出すのは困難
3. **主観評価も重要**: 数値化できない価値も記録
4. **写真・スクリーンショット**: 可能ならUI変化を記録
5. **振り返りは早めに**: 実験終了直後が最適

## 🤝 コミュニケーション

実験中の疑問・発見はプロジェクトチームで共有しましょう。
この実験は、チーム全体の開発プロセス改善に貢献します。
