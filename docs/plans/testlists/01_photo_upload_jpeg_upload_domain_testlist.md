# ドメイン層テストリスト: ならば 写真アップロードが成功する

**Feature**: `e2e/features/photo_upload_jpeg_upload.feature`  
**Domain Model**: `docs/spec/models/photo_upload.md`  
**Plan**: `docs/plans/01_photo_upload_jpeg_upload.md`

---

## テストリストの目的

縦切り実装サイクル（Task06）の中で、`POST /api/v1/photos`（写真アップロード）を成立させるために必要なドメイン層（ValueObject / Entity 相当）のテストケースを列挙します。

Milestone 0 の方針（永続化禁止）により、DB/ストレージ向けの Repository 実装テストはこの段階では作成しません。

---

## 1. Entity/ValueObject

- MimeType

  - 許可された MIME type は生成できる
    - [ ] `image/jpeg` を受け取ると生成できる
    - [ ] `image/png` を受け取ると生成できる
  - 許可されていない MIME type は拒否される
    - [ ] `image/gif` を受け取ると例外が発生する
    - [ ] 空文字列を受け取ると例外が発生する
    - [ ] null を受け取ると例外が発生する

- FileSizeBytes

  - 有効なサイズは生成できる
    - [ ] 1 バイトを受け取ると生成できる
    - [ ] 10MB（`10 * 1024 * 1024`）を受け取ると生成できる
  - 不正なサイズは拒否される
    - [ ] 0 バイトを受け取ると例外が発生する
    - [ ] 負の値を受け取ると例外が発生する
    - [ ] 10MB+1 バイトを受け取ると例外が発生する

- ImageDimensions

  - 有効な幅/高さは生成できる
    - [ ] `width=1, height=1` を受け取ると生成できる
  - 不正な幅/高さは拒否される
    - [ ] `width=0` を受け取ると例外が発生する
    - [ ] `height=0` を受け取ると例外が発生する
    - [ ] 負の値を受け取ると例外が発生する

- UploadedPhotoReference
  - 有効な入力で生成できる
    - [ ] `photoId` / `expiresAt` / `mimeType` / `dimensions` / `fileSizeBytes` を受け取ると生成できる
  - バリデーションエラーは拒否される
    - [ ] `photoId=null` を受け取ると例外が発生する
    - [ ] `expiresAt=null` を受け取ると例外が発生する
    - [ ] `expiresAt` が過去の場合、例外が発生する

---

## 2. Repository Interface & 実装

- なし（Milestone 0 では永続化禁止のため）

---

## 3. DomainService (必要な場合)

- 追加する場合は、アップロード入力（MIME/サイズ）検証ロジックをドメインサービスとして切り出し、正常系/異常系のテストを追加する
