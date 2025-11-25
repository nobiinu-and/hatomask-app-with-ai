# 開発プロセスガイド (BDD & TDD)

Specを基に、BDD(振る舞い駆動開発)とTDD(テスト駆動開発)を組み合わせ、**1ステップずつ確実に**実装を進めるためのガイドラインです。

## 🤖 AI協働プロトコル (厳守)

AIと協働する際は、以下のルールを絶対に守ってください。

1.  **1プロンプト1ステップ**: AIへの指示は、シナリオ全体ではなく**「現在実装すべき1つのステップ(Given/When/Thenのいずれか1行)」のみ**を渡すこと。
2.  **先読み実装の禁止**: AIが気を利かせて後続のステップを実装しようとした場合は拒否し、修正させること。
3.  **完了確認の義務化**: 1ステップがGreenになるたびに作業を停止し、人間の確認を得ること。
4.  **Red状態の提示**: 実装コード(Green)を書く前に、必ずテストが失敗すること(Red)を確認・提示させること。

## 🔄 開発フロー

### Phase 1: Spec & Scenario (要件定義)
1.  `spec/features/` に仕様と受け入れ基準(Given-When-Then)を記述する。
2.  実装するシナリオを**1つだけ**選び、`e2e/features/` に `.feature` ファイルとして作成する。

### Phase 2: Frontend & E2E (BDD - 1ステップずつ)
**シナリオの各ステップ(Given/When/Then)に対して、以下のサイクルを回す:**

1.  **Step Definition (Red)**: `e2e/step-definitions/` にステップ定義を追加し、E2Eテストが失敗することを確認。
2.  **Mock API**: 必要に応じて `src/frontend/src/test/mocks/` にMSWハンドラを追加。
3.  **Unit Test (Red)**: フロントエンドのユニットテストを作成し、失敗することを確認。
4.  **Implementation (Green)**: 最小限の実装を行い、ユニットテストとE2Eテスト(該当ステップまで)を通す。
5.  **Refactor**: コードを整理する。
6.  **Next Step**: 次のステップへ進む。

> **⚠️ 重要**: 複数のステップをまとめて実装することは禁止。必ず1つずつ完了させること。

### Phase 3: Backend (TDD)
フロントエンド完成後、モックAPIを実実装に置き換える。

1.  **Test List**: 実装すべきテスト項目をリストアップする(`testlists/`)。
2.  **TDD Cycle**: テスト作成(Red) → 実装(Green) → リファクタリング を繰り返す。
3.  **Integration**: モックを無効化し、リアルAPIでE2Eテストが通ることを確認する。

## 🛠 主要コマンド

### フロントエンド (Mock有効)
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

### E2Eテスト
```bash
cd e2e
# 特定のシナリオのみ実行
npm test -- --name "シナリオ名"
```
