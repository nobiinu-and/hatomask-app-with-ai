---
description: ドメインモデルを参照しながらOpenAPI仕様を作成し、モデルにフィードバックするプロンプト
---

# API Contract設計 + ドメインモデル見直し

## 目的

**Phase 2で作成したドメインモデルを基に、API仕様（OpenAPI）を設計し、両者の整合性を確保する。**

API Contract Firstの原則に従い、フロントエンドとバックエンドの中立的な契約を確立します。

## 依頼内容

以下のドメインモデルとSpec を参照し、OpenAPI 3.0仕様書を作成してください。
作成過程で気づいたドメインモデルの問題点があれば、フィードバックしてください。

## 入力情報

- **Specファイル**: `docs/spec/features/{feature_name}.md`
- **ドメインモデル**: `docs/spec/models/{feature_name}.md`（Phase 2で作成）
- **テンプレート**: `docs/spec/templates/openapi.template.yaml`
- **ガイドライン**: `docs/dev/OPENAPI_GUIDELINES.md`

## 作業手順

### 1. ドメインモデルの理解

`docs/spec/models/{feature_name}.md` を読み、以下を把握：

- Entity構造（プロパティ、型、バリデーション）
- ValueObject定義
- Repository Interface（メソッドシグネチャ）
- DomainService（ビジネスルール）

### 2. Specからエンドポイント抽出

`docs/spec/features/{feature_name}.md` から必要なAPIエンドポイントを特定：

#### エンドポイント抽出例

Specの記載:
```markdown
### 1. 写真アップロード
ユーザーがファイル選択ボタンから写真を選択し、バックエンドにアップロード

### 2. プレビュー表示
アップロードした写真を画面上にプレビュー表示

### 3. ダウンロード機能
「ダウンロード」ボタンをクリックで写真をダウンロード
```

抽出されるエンドポイント:
- `POST /api/v1/photos` - 写真アップロード
- `GET /api/v1/photos/{id}` - 写真取得（プレビュー、ダウンロード共通）

### 3. OpenAPI仕様の作成

#### 3.1 ファイル作成

保存先: `docs/spec/api/{feature_name}.yaml`

例: `docs/spec/api/photo_upload_download.yaml`

#### 3.2 基本情報記述

```yaml
openapi: 3.0.3
info:
  title: HatoMask API - Photo Upload/Download
  description: 写真のアップロードとダウンロード機能
  version: 1.0.0

servers:
  - url: http://localhost:8080/api/v1
    description: バックエンド開発環境

tags:
  - name: photos
    description: 写真管理
```

#### 3.3 エンドポイント定義

各エンドポイントについて:

1. **HTTPメソッドとパス**
2. **リクエストパラメータ/ボディ**
3. **レスポンススキーマ**
4. **エラーレスポンス（RFC 9457準拠）**

##### エンドポイント例: POST /photos

```yaml
paths:
  /photos:
    post:
      tags:
        - photos
      summary: 写真をアップロード
      description: |
        写真ファイルをアップロードし、サーバーに保存します。
        
        ### ビジネスルール
        - ファイルサイズは10MB以下
        - 対応形式: JPEG, PNG
        
        ### 処理フロー
        1. ファイル形式をバリデーション
        2. ファイルサイズをチェック
        3. ローカルストレージに保存
        4. DBにメタデータを記録
      operationId: uploadPhoto
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
                  description: アップロードする写真ファイル
              required:
                - file
      responses:
        '201':
          description: アップロード成功
          headers:
            Location:
              description: 作成された写真リソースのURI
              schema:
                type: string
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PhotoResponse'
        '400':
          description: バリデーションエラー
          content:
            application/problem+json:
              schema:
                $ref: '#/components/schemas/ProblemDetails'
              examples:
                fileSizeExceeded:
                  summary: ファイルサイズ超過
                  value:
                    type: "about:blank"
                    title: "Bad Request"
                    status: 400
                    detail: "File size exceeds maximum allowed size of 10MB"
                invalidFormat:
                  summary: 非対応形式
                  value:
                    type: "about:blank"
                    title: "Bad Request"
                    status: 400
                    detail: "Unsupported file format. Only JPEG and PNG are allowed"
```

#### 3.4 スキーマ定義

ドメインモデルのEntityをDTOスキーマにマッピング：

**ドメインモデル**:
```markdown
### Photo
- id: UUID
- fileName: String (1-255文字)
- fileSize: Long (1-10485760バイト)
- mimeType: String (image/jpeg, image/png)
- filePath: String (内部実装のみ)
- createdAt: LocalDateTime
```

**OpenAPI スキーマ**:
```yaml
components:
  schemas:
    PhotoResponse:
      type: object
      description: 写真レスポンス
      properties:
        id:
          type: string
          format: uuid
          description: 写真ID
        fileName:
          type: string
          minLength: 1
          maxLength: 255
          description: ファイル名
        fileSize:
          type: integer
          format: int64
          minimum: 1
          maximum: 10485760
          description: ファイルサイズ（バイト）
        mimeType:
          type: string
          enum:
            - image/jpeg
            - image/png
          description: MIMEタイプ
        createdAt:
          type: string
          format: date-time
          description: 作成日時（ISO 8601形式）
      required:
        - id
        - fileName
        - fileSize
        - mimeType
        - createdAt
      example:
        id: "550e8400-e29b-41d4-a716-446655440000"
        fileName: "sample.jpg"
        fileSize: 5242880
        mimeType: "image/jpeg"
        createdAt: "2024-01-15T10:30:00Z"

    ProblemDetails:
      type: object
      description: RFC 9457準拠のエラーレスポンス
      properties:
        type:
          type: string
          example: "about:blank"
        title:
          type: string
          example: "Bad Request"
        status:
          type: integer
          example: 400
        detail:
          type: string
          example: "Validation failed"
      required:
        - type
        - title
        - status
```

**重要な変換ルール**:
- `filePath`は内部実装なので**レスポンスに含めない**
- Java型をOpenAPI型にマッピング（UUID→string/uuid, LocalDateTime→string/date-time）
- バリデーションルールをスキーマに反映（minLength, maximum等）

### 4. ドメインモデルへのフィードバック

API設計中に気づいた点を報告：

#### フィードバック例

```markdown
## ドメインモデルへのフィードバック

### 修正提案

1. **Photo Entity - mimeType**
   - 現状: String型で自由入力
   - 提案: MimeType ValueObjectを追加し、列挙型で制約
   - 理由: APIでは `enum` で制限しているため、ドメイン層でも型安全性を確保

2. **PhotoRepository - findById**
   - 現状: Optional<Photo> findById(UUID id)
   - 確認: エラーハンドリング方針（404返却）と一致しているか？

3. **新規ValueObject提案: FileSize**
   - 提案: ファイルサイズのバリデーション（1-10MB）をValueObjectで表現
   - 理由: APIとドメイン層で制約を二重管理せず、ドメイン層が真実の源泉となる

### 確認事項

1. ダウンロード機能でContent-Typeヘッダーを動的に設定する必要があるが、
   PhotoにmimeType情報があれば対応可能？ → 対応可能と判断
```

### 5. 成果物確認

作成したOpenAPI仕様書について：

- [ ] 全エンドポイントにoperationIdが定義されている
- [ ] リクエスト/レスポンススキーマが完全に定義されている
- [ ] エラーレスポンスがRFC 9457準拠
- [ ] バリデーションルールがドメインモデルと一致
- [ ] 内部実装の詳細（filePathなど）が漏れていない
- [ ] 例（example）が記載されている
- [ ] ドメインモデルへのフィードバックがある

## 出力形式

### 1. OpenAPI仕様書

**ファイルパス**: `docs/spec/api/{feature_name}.yaml`

### 2. ドメインモデルフィードバック

```markdown
## ドメインモデルへのフィードバック

### 修正提案
1. [提案内容]

### 確認事項
1. [確認したい点]

### 新規追加提案
1. [追加すべきValueObject/メソッド等]
```

### 3. Phase 3完了報告

```markdown
## Phase 3完了

### 作成ファイル
- `docs/spec/api/{feature_name}.yaml`

### エンドポイント一覧
- POST /api/v1/photos - 写真アップロード
- GET /api/v1/photos/{id} - 写真取得

### 次のアクション
- ドメインモデル修正（必要な場合）
- Phase 4: Gherkinシナリオ作成 + 実装計画策定へ進む
```

## 参考資料

- **ガイドライン**: `docs/dev/OPENAPI_GUIDELINES.md`
- **テンプレート**: `docs/spec/templates/openapi.template.yaml`
- **OpenAPI Specification**: https://spec.openapis.org/oas/v3.0.3
- **RFC 9457**: https://www.rfc-editor.org/rfc/rfc9457.html

## 注意事項

### API Contract Firstの原則

- **ドメインモデルに忠実**: エンティティ構造を尊重
- **中立な契約**: フロント/バックどちらにも偏らない設計
- **実装詳細の隠蔽**: 内部実装（filePath等）は公開しない

### ユビキタス言語の一貫性

- エンドポイント名、プロパティ名はドメインモデルの用語を使用
- 例: `Photo`, `fileName`, `mimeType` など

### フロントエンドとの互換性確保

- Phase 5でBackend Stubを生成するため、明確なスキーマ定義が必要
- 曖昧な定義（`type: object` のみ）は避ける
