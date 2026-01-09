# API 層テストリスト: ならば 写真アップロードが成功する

**Feature**: `e2e/features/photo_upload_jpeg_upload.feature`  
**Domain Model**: `docs/spec/models/photo_upload.md`  
**Plan**: `docs/plans/01_photo_upload_jpeg_upload.md`

---

## テストリストの目的

縦切り実装サイクル（Task06）の中で、`POST /api/v1/photos` を Stub から本実装に置き換えるために必要な API 層（UseCase, Controller, DTO マッピング）のテストケースを列挙します。

このリポジトリには現時点で MSW ハンドラが存在しないため、MSW 互換性ではなく **OpenAPI（`docs/spec/api/photos.yaml`）との一致**を確認対象とします。

---

## 1. UseCase 層

- UploadPhotoUseCase
  - 有効なファイルを受け取ると `UploadedPhotoReference` を返す
    - [ ] JPEG を受け取ると `photoId`（UUID）/ `expiresAt` / `mimeType` / `fileSizeBytes` / `dimensions` を返す
    - [ ] PNG を受け取ると同様に返す
    - [ ] `expiresAt` は「現在時刻以上」かつ「最大5分以内」の TTL である
  - バリデーションエラーで失敗する
    - [ ] `file` が null の場合、例外が発生する
    - [ ] `file` が empty の場合、例外が発生する
    - [ ] サイズが 10MB を超える場合、例外が発生する（413 相当）
    - [ ] `contentType` が JPEG/PNG 以外の場合、例外が発生する（415 相当）
    - [ ] 画像としてデコードできない場合、例外が発生する（400 相当）

---

## 2. Controller 層

- PhotoController
  - `POST /api/v1/photos` は有効なリクエストで 201 Created を返す
    - [ ] `multipart/form-data` の `file` を受け取ると 201 を返す
    - [ ] `Location` ヘッダーが `/api/v1/photos/{uuid}` 形式で返る
    - [ ] レスポンス JSON が `photoId`, `mimeType`, `fileSizeBytes`, `dimensions{width,height}`, `expiresAt` を含む
    - [ ] `Content-Type` が `application/json` である

  - `POST /api/v1/photos` は無効なリクエストを RFC9457 形式で拒否する
    - [ ] `file` 未指定/空の場合、400 + `application/problem+json` を返す
    - [ ] サイズ超過の場合、413 + `application/problem+json` を返す
    - [ ] 未対応形式の場合、415 + `application/problem+json` を返す

---

## 3. 統合確認（任意）

- `mvn test` が通り、E2E の "Then 写真アップロードが成功する" が（Stubなしで）201になること
