# 仕様テンプレート

このディレクトリには、プロジェクトで使用する仕様書のテンプレートが格納されています。

## テンプレート一覧

### feature-spec.template.md

機能仕様書のテンプレートです。新しい機能を追加する際に使用します。

**用途:**

- ユーザー向け機能の仕様定義
- UI/UX 要件の明確化
- Specification by Example による受け入れ基準の定義

**使用例:**

```bash
cp docs/spec/templates/feature-spec.template.md docs/spec/features/02_new_feature.md
```

**参考サンプル:**

- `docs/spec/features/01_photo_upload.md`

**主なセクション:**

- 概要とスコープ
- ユースケース（基本フロー・代替フロー）
- 機能要件
- UI/UX 要件
- 受け入れ基準（Gherkin 形式のシナリオ）
- 将来の拡張候補

### domain-model.template.md

ドメインモデルのテンプレートです。機能のドメイン層の設計に使用します。

**用途:**

- エンティティの定義
- バリューオブジェクトの定義
- リポジトリインターフェースの設計
- ドメインサービスの設計

**使用例:**

```bash
cp docs/spec/templates/domain-model.template.md docs/spec/models/02_new_feature_model.md
```

**主なセクション:**

- エンティティとプロパティ
- エンティティ関連図
- バリューオブジェクト
- リポジトリインターフェース
- ドメインサービス

### openapi.template.yaml

OpenAPI 仕様書のテンプレートです。REST API の設計に使用します。

**用途:**

- API エンドポイントの定義
- リクエスト/レスポンススキーマの定義
- エラーレスポンスの定義

**使用例:**

```bash
cp docs/spec/templates/openapi.template.yaml docs/spec/api/02_new_feature_api.yaml
```

## テンプレート使用のワークフロー

1. **機能仕様の作成**

   - `feature-spec.template.md` をコピー
   - 機能要件とユースケースを定義
   - Gherkin 形式で受け入れ基準を記述

2. **ドメインモデルの設計**

   - `domain-model.template.md` をコピー
   - エンティティとリポジトリを定義
   - 機能仕様に基づいてモデリング

3. **API 仕様の設計**
   - `openapi.template.yaml` をコピー
   - エンドポイントとスキーマを定義
   - ドメインモデルと整合性を保つ

## テンプレートのカスタマイズ

テンプレートは必要に応じてカスタマイズしてください:

- 不要なセクションは削除可能
- プロジェクト固有のセクションを追加可能
- セクションの順序は変更可能

## 注意事項

- テンプレートの `[...]` 部分は実際の内容に置き換えてください
- テンプレート自体を直接編集しないでください（コピーして使用）
- 仕様書は `docs/spec/` 配下の適切なディレクトリに配置してください
  - 機能仕様: `docs/spec/features/`
  - ドメインモデル: `docs/spec/models/`
  - API 仕様: `docs/spec/api/`
