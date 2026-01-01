# システムプロンプト

このディレクトリには、AI アシスタントがタスクを実行する際の基本的な振る舞いや制約を定義するドキュメントが格納されています。

## ファイル構成

### 00_role.md - 役割定義

AI アシスタントの基本的な役割とペルソナを定義します。

**内容:**

- ペルソナ(経験豊富なフルスタック開発者)
- 行動指針(段階的アプローチ、Green の維持、ドキュメントファースト)
- コミュニケーションスタイル
- 参照すべきドキュメント(`docs/dev/`配下の規約)
- AI 特有の実行ルール(ファイル操作、テスト生成、エラーハンドリング)
- 制約と優先順位
- 破壊的変更の扱い

**使用タイミング:**

- AI アシスタントの初期化時
- 全般的な振る舞いを確認したいとき

### 01_implementation_workflow.md - 実装ワークフロー

具体的な実装手順を定義します。

**内容:**

- 新機能実装のワークフロー
  - `docs/ai/prompts/tasks/`（01〜06）のタスクに沿った進め方
- バグ修正のワークフロー
- リファクタリングのワークフロー
- 実行コマンド例

**使用タイミング:**

- 新機能を実装するとき
- バグを修正するとき
- リファクタリングを行うとき
- 実装手順を確認したいとき

### 02_quality_standards.md - 品質基準

コード品質の基準とベストプラクティスを定義します。

**内容:**

- コード品質の基本原則(可読性、単一責任、DRY)
- テスト品質基準(FIRST 原則、AAA パターン、カバレッジ目標)
- エラーハンドリング基準(フロントエンド/バックエンド)
- セキュリティ基準(入力バリデーション、SQL インジェクション対策)
- パフォーマンス基準(遅延読み込み、N+1 問題の回避)
- アクセシビリティ基準(セマンティック HTML、ARIA 属性)
- コードレビュー チェックリスト

**使用タイミング:**

- コードを書くとき
- コードレビューを行うとき
- 品質基準を確認したいとき

## 人間向けドキュメントとの関係

### docs/dev/ - 人間と AI で共有

プロジェクトの規約や標準を定義します。**人間の開発者も AI アシスタントも参照します。**

**内容の性質:** 「何を守るべきか」(規約、標準)

### docs/ai/prompts/system/ - AI 専用

代わりに、`docs/dev/`配下のドキュメントを参照してください:

- コーディング規約: `docs/dev/standards/coding.md`
- 開発プロセス（補足）: `docs/dev/howto/development.md`
- 環境構築: `docs/dev/howto/docker.md`
- Linter 設定: `docs/dev/standards/linting.md`

## 使い方

### AI アシスタント向け

1. **初期化時**

   - `00_role.md`を読んで基本的な役割を理解する
   - 参照すべきドキュメント(`docs/dev/`)を確認する

2. **実装時**

   - `docs/ai/prompts/tasks/README.md`でタスク（Task01〜Task06）の正本を確認
   - `01_implementation_workflow.md`で具体的な手順を確認
   - `docs/dev/standards/coding.md`でコーディング規約を確認
   - `02_quality_standards.md`で品質基準を確認

3. **レビュー時**
   - `02_quality_standards.md`のチェックリストを使用
   - `docs/dev/standards/coding.md`の規約に従っているか確認

### 人間の開発者向け

人間の開発者は、`docs/ai/prompts/system/`を読む必要はありません。
代わりに、`docs/dev/`配下のドキュメントを参照してください:

- コーディング規約: `docs/dev/standards/coding.md`
- 開発プロセス（補足）: `docs/dev/howto/development.md`
- 環境構築: `docs/dev/howto/docker.md`
- Linter 設定: `docs/dev/standards/linting.md`

ただし、AI アシスタントの振る舞いを理解したい場合や、AI への指示を改善したい場合は、
このディレクトリのドキュメントを参照することができます。

## ドキュメントのメンテナンス

### 更新が必要な場合

以下のような場合は、ドキュメントの更新を検討してください:

1. **新しいベストプラクティスが確立された**

   - `02_quality_standards.md`に追加

2. **開発プロセスが変更された**

   - `docs/ai/prompts/tasks/`（Task01〜Task06）と`01_implementation_workflow.md`を更新

3. **AI の振る舞いを改善したい**

   - `00_role.md`や`01_implementation_workflow.md`を調整

4. **コーディング規約が変更された**
   - `docs/dev/standards/coding.md`を更新(AI 側の更新は不要)

### 更新の原則

1. **人間と AI で共有すべき情報は`docs/dev/`に**

   - コーディング規約、開発プロセス、技術標準

2. **AI 特有の実行指針は`docs/ai/prompts/system/`に**

   - 思考プロセス、出力フォーマット、エラーリトライ戦略

3. **一貫性を保つ**
   - 複数のドキュメント間で矛盾がないようにする
   - `docs/dev/`と`docs/ai/prompts/system/`の内容を整合させる

## 参考: タスクプロンプト

タスク固有の指示は、`docs/ai/prompts/tasks/`ディレクトリに配置します。

### タスクファイル一覧

1. **01_create_feature_spec.md** - 機能仕様(Spec)作成
2. **02_simple_modeling.md** - ドメインモデリング(初稿)
3. **03_design_api_contract.md** - API Contract 設計 + モデル見直し
4. **04_plan_implementation.md** - Gherkin シナリオ + 実装計画策定
5. **05_generate_stubs.md** - Backend Stub 生成
6. **06_vertical_slice_implementation.md** - 縦切り実装サイクル(Vertical Slice)

### ワークフローとの対応

```
Task01: 機能仕様(Spec)作成
   → 01_create_feature_spec.md
   ↓
Task02: ドメインモデリング(初稿)
   → 02_simple_modeling.md
   ↓
Task03: API Contract設計 + モデル見直し
   → 03_design_api_contract.md
   ↓
Task04: Gherkinシナリオ + 実装計画策定
   → 04_plan_implementation.md
   ↓
Task05: Backend Stub生成
   → 05_generate_stubs.md
   ↓
Task06: 縦切り実装サイクル
   → 06_vertical_slice_implementation.md

統合確認（シナリオ完結確認）は、E2E 実行で担保します。
（実行手順は `e2e/README.md` と `docs/dev/howto/ci.md` を参照）
```

タスクプロンプトは、システムプロンプトを前提として、特定のタスクに必要な追加情報を提供します。

詳細は`docs/ai/prompts/tasks/README.md`を参照してください。
