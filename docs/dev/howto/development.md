# 開発プロセスガイド (BDD & TDD)

Spec を基に、BDD(振る舞い駆動開発)と TDD(テスト駆動開発)を組み合わせ、**1 ステップずつ確実に**実装を進めるためのガイドラインです。

## 🤖 AI 協働プロトコル (厳守)

AI と協働する際は、以下のルールを絶対に守ってください。

1.  **1 プロンプト 1 ステップ**: AI への指示は、シナリオ全体ではなく**「現在実装すべき 1 つのステップ(Given/When/Then のいずれか 1 行)」のみ**を渡すこと。
2.  **先読み実装の禁止**: AI が気を利かせて後続のステップを実装しようとした場合は拒否し、修正させること。
3.  **完了確認の義務化**: 1 ステップが Green になるたびに作業を停止し、人間の確認を得ること。
4.  **Red 状態の提示**: 実装コード(Green)を書く前に、必ずテストが失敗すること(Red)を確認・提示させること。

## 🔄 開発フロー (API Contract First × 縦切り開発)

開発タスク（Task01〜Task06）の**正本**は `docs/ai/prompts/tasks/`（01〜06）です。
このドキュメントでは、タスクの詳細手順を重複して書かず、補足（プロトコル/実行コマンド）だけを扱います。

- Task 対応表: `docs/ai/prompts/tasks/README.md`
- タスクプロンプト: `docs/ai/prompts/tasks/01_create_feature_spec.md` 〜 `06_vertical_slice_implementation.md`

統合確認（シナリオ完結確認）は、E2E 実行で担保します。
E2E の実行手順は `e2e/README.md` を参照してください。

## 🛠 主要コマンド

### フロントエンド (Mock 有効)

```bash
cd src/frontend
npm run dev      # サーバー起動
npm test         # ユニットテスト
```

### バックエンド

```bash
cd src/backend
mvn spring-boot:run  # サーバー起動
mvn test             # ユニットテスト
```

### E2E テスト

```bash
cd e2e
# 特定のシナリオのみ実行
npm test -- --name "シナリオ名"
```
