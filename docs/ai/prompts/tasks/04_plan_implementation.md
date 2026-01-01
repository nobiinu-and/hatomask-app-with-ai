---
description: 実装前にシナリオを分析し、技術的な実装計画を策定するためのプロンプト（OpenAPI参照、縦切り実装対応）
---

# Gherkin シナリオ作成 + 実装計画策定

## 目的

**Task03 で作成した OpenAPI 仕様を参照しながら、Gherkin シナリオと実装計画を作成する。**

Task06 の縦切り実装に備えて、ステップの依存関係と推奨グルーピングを明確にします。

## 依頼内容

以下の情報を基に、Gherkin シナリオと実装計画を作成してください。

## 入力情報

- **Spec ファイル**: `docs/spec/features/{feature_name}.md`
- **ドメインモデル**: `docs/spec/models/{feature_name}.md`
- **OpenAPI 仕様**: `docs/spec/api/{feature_name}.yaml` ← **Task03 で作成**
- **テンプレート**: `docs/plans/templates/implementation_plan.template.md`

## 作業手順

### 1. Gherkin シナリオ作成

#### 1.1 シナリオ選択

Spec ファイルの「受け入れ基準」から、実装する**1 つのシナリオ**を選択：

```markdown
## 受け入れ基準

### Scenario: JPEG ファイルのアップロードとダウンロード

### Scenario: PNG ファイルのアップロードとダウンロード

### Scenario: ファイルサイズ超過エラー
```

#### 1.2 Feature ファイル作成

**保存先**: `e2e/features/{feature_name}.feature`

例: `e2e/features/photo_upload_download.feature`

**Gherkin 記述**:

```gherkin
Feature: 写真のアップロードとダウンロード
  ユーザーが写真をアップロードし、プレビュー表示およびダウンロードできる

  Scenario: JPEGファイルのアップロードとダウンロード
    Given ユーザーがHatoMaskアプリケーションにアクセスしている
    When ユーザーが「写真を選択」ボタンをクリックする
    And ユーザーがファイルサイズ5MBのJPEGファイルを選択する
    Then アップロードが成功する
    And プレビューエリアに選択した画像が表示される
    When ユーザーが「ダウンロード」ボタンをクリックする
    Then 元の画像がダウンロードされる
```

### 2. 実装計画作成

#### 2.1 ファイル作成

**保存先**: `docs/plans/[Specファイル名]_[シナリオ識別子].md`

例: `docs/plans/01_photo_upload_jpeg_download.md`

**ファイル名規則**:

- Spec ファイル名をプレフィックス
- シナリオ内容を表す短い識別子（英数字、アンダースコア）
- 例: `01_photo_upload_jpeg_download.md`, `01_photo_upload_size_error.md`

#### 2.2 実装計画作成

`docs/plans/templates/implementation_plan.template.md` のテンプレートに従い、以下のセクションを記述してください。

**記述のポイント**:

- **基本情報**: Feature 名、シナリオ名、参照ドキュメントのパス
- **OpenAPI 仕様参照**: エンドポイント一覧と主要スキーマ
- **ステップ別実装分類**: フロントのみ/API 依存/状態依存を明確に分類
- **推奨実装グルーピング**: Task06 の縦切り実装のためのグループ提案
- **Gherkin ステップごとの実装要件**: 各ステップの詳細要件（Frontend/Backend/Database/Validation）
- **データモデル**: ドメインモデル参照、追加の DB Schema/DTO
- **技術的課題・リスク**: 実装時の注意点

### 3. チェックリスト

作成完了前に確認:

- [ ] Gherkin シナリオが作成されている（`e2e/features/`）
- [ ] テンプレートの全セクションが記述されている
- [ ] OpenAPI 仕様参照セクションがある
- [ ] ステップ別実装分類が明記されている（フロントのみ/API 依存/状態依存）
- [ ] 推奨実装グルーピングが提案されている
- [ ] 各ステップの詳細要件が記載されている
- [ ] API 依存ステップで OpenAPI 参照リンク（JSONPath 形式）がある
- [ ] 技術的課題・リスクが洗い出されている

## 出力ファイル

1. **Gherkin シナリオ**: `e2e/features/{feature_name}.feature`
2. **実装計画書**: `docs/plans/[Specファイル名]_[シナリオ識別子].md`
   - テンプレート: `docs/plans/templates/implementation_plan.template.md`

## 次のステップ

Task04 完了後:

- **Task05: Backend Stub 生成** - OpenAPI 仕様に基づきスタブ実装を作成
- **Task06: 縦切り実装サイクル** - この実装計画に基づき、推奨グルーピングを参考に実装

## 参考資料

- **OpenAPI 仕様**: `docs/spec/api/{feature_name}.yaml`
- **縦切り戦略**: `docs/dev/guidelines/vertical-slice.md`
- **テンプレート**: `docs/plans/templates/implementation_plan.template.md`

## 注意事項

### OpenAPI 参照の徹底

- API 依存ステップでは必ず OpenAPI 仕様の該当箇所を参照リンクで明示
- リクエスト/レスポンス構造は OpenAPI と完全一致

### 縦切り実装への配慮

- ステップ分類（フロントのみ/API 依存/状態依存）を明確に
- 状態依存（特に DB 状態）があるステップはグループ化を推奨
- Task06 で AI が粒度提案する際のベース情報となる
