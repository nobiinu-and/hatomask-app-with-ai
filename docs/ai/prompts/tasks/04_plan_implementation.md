---
description: 実装前にシナリオを分析し、技術的な実装計画を策定するためのプロンプト（OpenAPI参照、縦切り実装対応）
---

# Gherkinシナリオ作成 + 実装計画策定

## 目的

**Phase 3で作成したOpenAPI仕様を参照しながら、Gherkinシナリオと実装計画を作成する。**

Phase 6の縦切り実装に備えて、ステップの依存関係と推奨グルーピングを明確にします。

## 依頼内容

以下の情報を基に、Gherkinシナリオと実装計画を作成してください。

## 入力情報

- **Specファイル**: `docs/spec/features/{feature_name}.md`
- **ドメインモデル**: `docs/spec/models/{feature_name}.md`
- **OpenAPI仕様**: `docs/spec/api/{feature_name}.yaml` ← **Phase 3で作成**
- **テンプレート**: `docs/plans/templates/implementation_plan.template.md`

## 作業手順

### 1. Gherkinシナリオ作成

#### 1.1 シナリオ選択

Specファイルの「受け入れ基準」から、実装する**1つのシナリオ**を選択：

```markdown
## 受け入れ基準

### Scenario: JPEGファイルのアップロードとダウンロード
### Scenario: PNGファイルのアップロードとダウンロード
### Scenario: ファイルサイズ超過エラー
```

#### 1.2 Featureファイル作成

**保存先**: `e2e/features/{feature_name}.feature`

例: `e2e/features/photo_upload_download.feature`

**Gherkin記述**:

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
- Specファイル名をプレフィックス
- シナリオ内容を表す短い識別子（英数字、アンダースコア）
- 例: `01_photo_upload_jpeg_download.md`, `01_photo_upload_size_error.md`

#### 2.2 実装計画作成

`docs/plans/templates/implementation_plan.template.md` のテンプレートに従い、以下のセクションを記述してください。

**記述のポイント**:
- **基本情報**: Feature名、シナリオ名、参照ドキュメントのパス
- **OpenAPI仕様参照**: エンドポイント一覧と主要スキーマ
- **ステップ別実装分類**: フロントのみ/API依存/状態依存を明確に分類
- **推奨実装グルーピング**: Phase 6の縦切り実装のためのグループ提案
- **Gherkinステップごとの実装要件**: 各ステップの詳細要件（Frontend/Backend/Database/Validation）
- **データモデル**: ドメインモデル参照、追加のDB Schema/DTO
- **技術的課題・リスク**: 実装時の注意点

### 3. チェックリスト

作成完了前に確認:
- [ ] Gherkinシナリオが作成されている（`e2e/features/`）
- [ ] テンプレートの全セクションが記述されている
- [ ] OpenAPI仕様参照セクションがある
- [ ] ステップ別実装分類が明記されている（フロントのみ/API依存/状態依存）
- [ ] 推奨実装グルーピングが提案されている
- [ ] 各ステップの詳細要件が記載されている
- [ ] API依存ステップでOpenAPI参照リンク（JSONPath形式）がある
- [ ] 技術的課題・リスクが洗い出されている

## 出力ファイル

1. **Gherkinシナリオ**: `e2e/features/{feature_name}.feature`
2. **実装計画書**: `docs/plans/[Specファイル名]_[シナリオ識別子].md`
   - テンプレート: `docs/plans/templates/implementation_plan.template.md`

## 次のステップ

Phase 4完了後:
- **Phase 5: Backend Stub生成** - OpenAPI仕様に基づきスタブ実装を作成
- **Phase 6: 縦切り実装サイクル** - この実装計画に基づき、推奨グルーピングを参考に実装

## 参考資料

- **OpenAPI仕様**: `docs/spec/api/{feature_name}.yaml`
- **縦切り戦略**: `docs/dev/VERTICAL_SLICE_STRATEGY.md`
- **テンプレート**: `docs/plans/templates/implementation_plan.template.md`

## 注意事項

### OpenAPI参照の徹底

- API依存ステップでは必ずOpenAPI仕様の該当箇所を参照リンクで明示
- リクエスト/レスポンス構造はOpenAPIと完全一致

### 縦切り実装への配慮

- ステップ分類（フロントのみ/API依存/状態依存）を明確に
- 状態依存（特にDB状態）があるステップはグループ化を推奨
- Phase 6でAIが粒度提案する際のベース情報となる
