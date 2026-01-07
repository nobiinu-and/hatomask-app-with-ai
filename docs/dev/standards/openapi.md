# OpenAPI 仕様書作成ガイドライン

このドキュメントは、HatoMask プロジェクトで OpenAPI 仕様書を手動作成する際のベストプラクティスをまとめたものです。

## ファイル配置

- **保存先**: `docs/spec/api/{api_name}.yaml`
- **テンプレート**: `docs/spec/templates/openapi.template.yaml`

テンプレートは「写経されやすい実装詳細」を避けるため、**最小構成（GET/POST の例 + 必要最小限のスキーマ）**に寄せています。
必要になった要素（例: `tags`, `securitySchemes`, 追加レスポンス、詳細スキーマ）は、仕様とマイルストーン方針に従って後から足してください。

## 最小構成（テンプレの意図）

OpenAPI 3.0.3 として成立する必須要素は以下です。

- **必須**: `openapi`, `info`（`title`, `version`）, `paths`
- **推奨（本プロジェクト）**: `servers`（`/api/v1` を含む）

また、`paths` に operation（`get`, `post` など）を書く場合、operation には **`responses` が必須**です。

例の値やエラーメッセージは固定しないでください（`<human readable message>` などのプレースホルダ推奨）。

## 基本原則

### 1. API Contract First

- **先に API を設計**: 実装前に OpenAPI 仕様を確定させる
- **ドメインモデルとの同期**: `docs/ai/prompts/tasks/02_simple_modeling.md` の成果物を参照し、`03_design_api_contract.md` で整合性を確保
- **中立な契約**: フロントエンドとバックエンドの中立的な仕様として機能

### 2. OpenAPI 3.0.3 を使用

```yaml
openapi: 3.0.3
```

### 3. バージョニング

- **API バージョン**: URL パスに `/api/v1` を含める
- **仕様書バージョン**: `info.version` でドキュメントのバージョン管理

```yaml
servers:
  - url: http://localhost:8080/api/v1
    description: バックエンド開発環境
```

## エンドポイント設計

### RESTful 命名規則

```yaml
# リソース指向の命名
/photos              # コレクション
/photos/{id}         # 単一リソース
/photos/{id}/download # サブリソース/アクション

# ✅ 良い例
GET    /photos           # 一覧取得
POST   /photos           # 新規作成
GET    /photos/{id}      # 詳細取得
PUT    /photos/{id}      # 更新
DELETE /photos/{id}      # 削除

# ❌ 悪い例
/getPhotos
/createPhoto
/photoDetail?id=123
```

### HTTP メソッドの使い分け

| メソッド | 用途                 | 冪等性 | レスポンス |
| -------- | -------------------- | ------ | ---------- |
| GET      | リソース取得         | ✅     | 200, 404   |
| POST     | リソース作成         | ❌     | 201, 400   |
| PUT      | リソース更新（全体） | ✅     | 200, 404   |
| PATCH    | リソース更新（部分） | ❌     | 200, 404   |
| DELETE   | リソース削除         | ✅     | 204, 404   |

### operationId 命名規則

```yaml
operationId: uploadPhoto      # 動詞 + 名詞（camelCase）
operationId: getPhotoById
operationId: listPhotos
operationId: deletePhoto
```

## スキーマ設計

### DTO 命名規則

```yaml
components:
  schemas:
    # リクエスト: 動詞 + リソース + Request
    UploadPhotoRequest:
      type: object

    # レスポンス: リソース + Response
    PhotoResponse:
      type: object

    # 一覧レスポンス
    PhotoListResponse:
      type: object
      properties:
        photos:
          type: array
          items:
            $ref: "#/components/schemas/PhotoResponse"
```

### プロパティ命名規則

- **camelCase**: `fileName`, `createdAt`, `mimeType`
- **日付時刻**: ISO 8601 形式 (`format: date-time`)
- **ID**: UUID 形式 (`format: uuid`)

```yaml
properties:
  id:
    type: string
    format: uuid
    description: リソースID
  fileName:
    type: string
    description: ファイル名
  createdAt:
    type: string
    format: date-time
    description: 作成日時（ISO 8601形式）
```

### バリデーションルール

ドメインモデルのバリデーションをスキーマに反映：

```yaml
properties:
  fileName:
    type: string
    minLength: 1
    maxLength: 255
  fileSize:
    type: integer
    format: int64
    minimum: 1
    maximum: 10485760 # 10MB
  mimeType:
    type: string
    enum:
      - image/jpeg
      - image/png
```

## エラーレスポンス (RFC 9457)

### 標準エラー形式

全てのエラーは `application/problem+json` で返却：

```yaml
responses:
  "400":
    description: バリデーションエラー
    content:
      application/problem+json:
        schema:
          $ref: "#/components/schemas/ProblemDetails"
        example:
          type: "about:blank"
          title: "Bad Request"
          status: 400
          detail: "<human readable message>"
```

### ステータスコード使い分け

| コード | 用途                         | 例                 |
| ------ | ---------------------------- | ------------------ |
| 200    | 成功                         | GET, PUT 成功      |
| 201    | 作成成功                     | POST 成功          |
| 204    | 成功（レスポンスボディなし） | DELETE 成功        |
| 400    | クライアントエラー           | バリデーション失敗 |
| 404    | リソース不在                 | ID 該当なし        |
| 409    | 競合                         | 重複エラー         |
| 500    | サーバーエラー               | 予期しないエラー   |

### バリデーションエラーの詳細

```yaml
example:
  type: "about:blank"
  title: "Bad Request"
  status: 400
  detail: "<human readable message>"
  errors:
    - field: "fileName"
      message: "<message>"
    - field: "fileSize"
      message: "<message>"
```

## ファイルアップロード

### multipart/form-data

```yaml
requestBody:
  required: true
  content:
    multipart/form-data:
      schema:
        type: object
        properties:
          file:
            type: string
            format: binary
            description: アップロードするファイル
        required:
          - file
# `metadata` 等の追加フィールドは、仕様で必要になった場合のみ追加する。
```

### レスポンスヘッダー

```yaml
responses:
  "201":
    description: アップロード成功
    headers:
      Location:
        description: 作成されたリソースのURI
        schema:
          type: string
          example: "/api/v1/photos/<id>"
```

## ドキュメント記述

### description 活用

`description` には「この API が何を保証し、何を要求するか」を、実装詳細に踏み込まずに記述します。

最低限、以下を含めてください。

- **目的**: 何をする endpoint か（UI 操作ではなくドメイン用語で）
- **入力**: 期待する入力（例: 必須/任意、形式、サイズ制限）
- **出力**: 主な成功時の結果（作成されるリソース、返るデータの種類）
- **ビジネスルール**: 重要な制約・前提（ドメインモデル/機能仕様と一致させる）
- **方針（必須）**: 保存/非保存、ログ、TTL 等のデータ取り扱いに関する方針
  - 参照: `docs/spec/architecture/{milestone_id}.md`
  - 参照: `docs/dev/policies/data-handling.md`

書かない（契約に含めない）もの:

- 保存方式・DB 有無・ストレージ種別などの実装詳細
- エラーメッセージの固定文言（`<human readable message>` などを用い、形式のみを固定する）

## ドメインモデルとのマッピング

### 基本原則

- **Entity → Response DTO**: ドメインモデルの Entity を API レスポンスにマッピング
- **内部実装の隠蔽**: `filePath`などの内部実装プロパティはレスポンスに含めない
- **ValueObject → Schema**: ドメインモデルの ValueObject を再利用
- **型変換**: Java 型を OpenAPI 型にマッピング（UUID→string/uuid, LocalDateTime→string/date-time）
- **バリデーションの同期**: ドメインモデルのバリデーションルールをスキーマに反映

### 型マッピング表

| Java 型       | OpenAPI 型 | format    | 例                          |
| ------------- | ---------- | --------- | --------------------------- |
| UUID          | string     | uuid      | "<uuid>"                    |
| LocalDateTime | string     | date-time | "<date-time>"               |
| Long          | integer    | int64     | 1234                        |
| String        | string     | -         | "<string>"                  |
| Enum          | string     | enum      | ["image/jpeg", "image/png"] |

## ツール・バリデーション

### オンラインエディタ

- [Swagger Editor](https://editor.swagger.io/) - YAML 検証、プレビュー
- [Stoplight Studio](https://stoplight.io/studio) - ビジュアルエディタ

### VS Code 拡張機能

- **Swagger Viewer**: YAML 内でプレビュー表示
- **OpenAPI (Swagger) Editor**: シンタックスハイライト、補完

### バリデーション

```bash
# CLIツールでバリデーション（オプション）
npx @apidevtools/swagger-cli validate docs/spec/api/{api_name}.yaml
```

## 品質チェックリスト

OpenAPI 仕様書作成時の確認項目：

- [ ] OpenAPI 3.0.3 形式で記述されている
- [ ] 全エンドポイントに `operationId` が定義されている
- [ ] リクエスト/レスポンススキーマが完全に定義されている
- [ ] RFC 9457 準拠のエラーレスポンスがある
- [ ] バリデーションルール（minLength, maximum 等）が定義されている
- [ ] `description` で処理内容・制約が説明されている
- [ ] 内部実装の詳細が漏れていない
- [ ] `example` は任意（固定文言を避け、プレースホルダ中心で記載する）
- [ ] RESTful 命名規則に従っている

## 参考資料

- [OpenAPI Specification 3.0.3](https://spec.openapis.org/oas/v3.0.3)
- [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457.html)
- [RESTful API Design Best Practices](https://stackoverflow.blog/2020/03/02/best-practices-for-rest-api-design/)
