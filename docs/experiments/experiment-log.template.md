# 実験記録: [予測駆動 / 発見駆動] - [シナリオ名]

## メタデータ

- **開始日時**: YYYY-MM-DD HH:MM
- **完了日時**: YYYY-MM-DD HH:MM（未完了の場合は空欄）
- **アプローチ**: [予測駆動 / 発見駆動]
- **シナリオ**: [シナリオ名（例: JPEGファイルのアップロードとダウンロード）]
- **ブランチ**: `experiment/[mode]-[scenario]`
- **実施者**: [名前]

---

## 定量メトリクス

| 指標 | 値 | 備考 |
|------|-----|------|
| Phase 1完了時間 | XX分 | Spec作成 |
| Phase 2完了時間 | XX分 | ドメインモデリング |
| Phase 3完了時間 | XX分 | API Contract設計 |
| Phase 4完了時間 | XX分 | 実装計画策定 |
| Phase 5完了時間 | XX分 | Backend Stub生成 |
| Phase 6完了時間 | XX分 | 縦切り実装 |
| Phase 7完了時間 | XX分 | 統合テスト |
| **総実装時間** | **XX分** | Phase 1開始〜Phase 7完了 |
| 最初のE2Eグリーン時間 | XX分 | Phase 1開始から初回テスト成功まで |
| ドメインモデル修正回数 | X回 | `git log -- docs/spec/models/` |
| API仕様変更回数 | X回 | `git log -- docs/spec/api/` |
| コミット数 | X | `git log --oneline \| wc -l` |
| Phase 1-4ドキュメント行数 | X行 | `wc -l docs/spec/features/* docs/spec/models/* docs/spec/api/* docs/plans/*/` |
| テストファイル数 | X | E2E + Backend + Frontend |
| テスト実行回数 | X回 | 記録した回数 |

### コマンド記録

実際に実行したコマンドを記録しておくと、振り返りが容易です:

```bash
# メトリクス取得コマンド例
git log --oneline | wc -l
git log --oneline -- docs/spec/models/
wc -l docs/spec/features/* docs/spec/models/* docs/spec/api/*
```

---

## タイムライン

| 時刻 | Phase | イベント | 備考 |
|------|-------|---------|------|
| 10:00 | Phase 1 | 開始 | Spec作成開始 |
| 10:30 | Phase 1 | 完了 | 受け入れ基準3シナリオ定義 |
| 10:35 | Phase 2 | 開始 | ドメインモデリング開始 |
| 11:00 | Phase 2 | モデル作成 | Photoエンティティ、PhotoId定義 |
| 11:15 | Phase 2 | Discovery | ファイル保存先の不明確さを発見 |
| 11:20 | Phase 2 | 完了 | 仮説を置いて進める |
| 11:25 | Phase 3 | 開始 | API Contract設計開始 |
| ... | ... | ... | ... |

---

## 定性評価（5段階: 1=悪い 〜 5=良い）

| 項目 | 評価 | コメント |
|------|:----:|---------|
| **ストレスレベル** | X | 不確実性への耐性、手戻り感 |
| **学習効果** | X | 実装中の気づき・発見の質と量 |
| **柔軟性** | X | 追加要件への対応しやすさ |
| **AIとの協働** | X | プロンプトの明確さ、やり取りの円滑さ |
| **早期フィードバック** | X | 動作確認までの速さ |
| **設計の質** | X | 最終的な設計の妥当性 |
| **コードの質** | X | 可読性、保守性 |
| **ドキュメントの質** | X | わかりやすさ、メンテナンス性 |

### 総合コメント

[全体的な感想を自由記述]

---

## 主要な発見・気づき

### 設計の進化

#### [日時] - [発見内容]
- **発見**: [何に気づいたか]
- **理由**: [なぜその発見があったか]
- **対応**: [どう対応したか]
- **影響**: [設計やコードへの影響]
- **学び**: [この発見から学んだこと]

#### 例: 2025-12-15 11:00 - PhotoのファイルパスとURLの分離
- **発見**: ダウンロード時にpublicなURLが必要と判明
- **理由**: フロントエンドが`/uploads/{uuid}.jpg`を直接参照できない
- **対応**: PhotoにfilePathとpublicUrlプロパティを追加
- **影響**: ドメインモデルとAPI仕様を更新、Phase 3に10分追加
- **学び**: ファイル管理の設計は早期に検証すべき

---

### 実装のブロッカー

#### [日時] - [ブロッカー内容]
- **問題**: [何が起きたか]
- **原因**: [なぜ起きたか]
- **解決**: [どう解決したか]
- **所要時間**: [ブロック時間]
- **予防策**: [今後どうすれば防げるか]

#### 例: 2025-12-15 14:00 - Phase 3のMimeType制約過剰定義
- **問題**: OpenAPIでMimeTypeをenumで厳密定義したが、実装時に柔軟性が必要と判明
- **原因**: 事前設計で完璧を目指しすぎた
- **解決**: enum削除、stringに変更、バリデーションをアプリ層に移動
- **所要時間**: 15分
- **予防策**: 最初は緩い制約から開始し、必要に応じて厳密化

---

### 良かった点

- [予想以上にうまくいったこと]
- [役に立ったプラクティス]
- [有効だったツール・手法]

#### 例
- Phase 2で最小限のモデルから開始したため、Phase 6での変更が容易だった（Discovery版）
- Phase 3で詳細にAPI仕様を定義したため、Phase 6での実装が迷わず進んだ（Predictive版）

---

### 改善点

- [うまくいかなかったこと]
- [次回改善したいこと]
- [プロセスへの提案]

#### 例
- Phase 4の実装計画が詳細すぎて、作成に時間がかかった（Predictive版）
- Phase 2でモデルが不足しすぎて、Phase 6で何度も戻る必要があった（Discovery版）

---

## AIとのやり取り

### プロンプトの効果

| Phase | プロンプトの明確さ | AIの理解度 | 修正回数 | コメント |
|-------|:----------------:|:----------:|:--------:|---------|
| Phase 1 | ○ | ○ | 1回 | Spec作成は順調 |
| Phase 2 | △ | △ | 3回 | Discovery指示が曖昧だった |
| Phase 3 | ○ | ○ | 0回 | - |
| Phase 4 | ○ | ○ | 1回 | - |
| Phase 5 | ○ | ○ | 0回 | - |
| Phase 6 | ○ | ○ | 2回 | 設計変更の指示が必要だった |

### AIの振る舞いで良かった点

- [AIが期待通りに動作した場面]
- [役に立ったAIの提案]

### AIの振る舞いで改善が必要な点

- [AIが混乱した場面]
- [プロンプト改善が必要と感じた点]

---

## 成果物リンク

### ドキュメント

- Spec: [docs/spec/features/XX.md](../../spec/features/XX.md)
- ドメインモデル: [docs/spec/models/XX.md](../../spec/models/XX.md)
- API仕様: [docs/spec/api/XX.yaml](../../spec/api/XX.yaml)
- 実装計画: [docs/plans/XX/implementation_plan.md](../../plans/XX/implementation_plan.md)
- テストリスト: [testlists/XX/](../../testlists/XX/)

### コード

- E2Eテスト: [e2e/features/XX.feature](../../e2e/features/XX.feature)
- Step Definitions: [e2e/step-definitions/XX_steps.ts](../../e2e/step-definitions/XX_steps.ts)
- フロントエンド: [src/frontend/src/...](../../src/frontend/src/)
- バックエンド: [src/backend/src/...](../../src/backend/src/)

### テスト結果

- E2Eテスト結果: [e2e/test-results/](../../e2e/test-results/)
- Backend単体テスト: [src/backend/target/surefire-reports/](../../src/backend/target/surefire-reports/)
- Frontend単体テスト: [src/frontend/coverage/](../../src/frontend/coverage/)

---

## 次回への申し送り

### このアプローチを続ける場合の推奨事項

- [次の機能実装で活かすべきこと]
- [注意すべきポイント]

### 他のアプローチを試す場合の参考情報

- [このアプローチの弱点]
- [他のアプローチで試すべきこと]

---

## 添付資料

### スクリーンショット

- [UI完成時のスクリーンショット]
- [重要な設計変更時の差分]

### ログファイル

- [エラーログ]
- [パフォーマンス測定結果]

---

## 振り返りメモ

[実験完了後の自由記述。印象に残ったこと、感じたこと、チームで共有したいことなど]

---

## 変更履歴

| 日時 | 更新内容 |
|------|---------|
| YYYY-MM-DD HH:MM | 実験記録作成 |
| YYYY-MM-DD HH:MM | Phase X完了、メトリクス更新 |
| YYYY-MM-DD HH:MM | 実験完了、振り返り追記 |
