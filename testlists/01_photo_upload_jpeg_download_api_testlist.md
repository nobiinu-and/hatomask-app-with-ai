# API層テストリスト: 写真アップロードとプレビュー表示

**Feature**: 写真のアップロードとダウンロード（ステップ3-5）  
**Domain Model**: `docs/spec/models/photo_upload_download.md`  
**Plan**: `docs/plans/01_photo_upload_jpeg_download.md`  
**OpenAPI**: `docs/spec/api/photo_upload_download.yaml`

---

## 1. UseCase層

### UploadPhotoUseCase

* UploadPhotoUseCase
  * 有効なファイルを受け取るとPhotoを保存してメタデータを返す
    * [ ] JPEG画像を受け取るとPhotoを保存してメタデータを返す
      * [ ] "sample.jpg", 5242880, "image/jpeg", byte配列を受け取るとPhotoが保存される
      * [ ] 保存後、PhotoResponseが返される
      * [ ] PhotoResponseにはid、originalFileName、contentType、fileSize、createdAt、updatedAtが含まれる
    * [ ] PNG画像を受け取るとPhotoを保存してメタデータを返す
      * [ ] "photo.png", 1024000, "image/png", byte配列を受け取るとPhotoが保存される
    * [ ] 保存時にRepositoryが正しく呼び出される
      * [ ] PhotoRepository.save()が1回呼び出される
      * [ ] save()に渡されるPhotoエンティティは入力と同じプロパティを持つ
  * バリデーションエラーでPhotoの保存が失敗する
    * [ ] ファイルサイズが上限を超える場合例外が発生する
      * [ ] 10485761バイト（10MB+1）を受け取ると例外が発生する
      * [ ] Repository.save()は呼び出されない
    * [ ] 許可されていないMIMEタイプの場合例外が発生する
      * [ ] "image/gif"を受け取ると例外が発生する
      * [ ] Repository.save()は呼び出されない

### GetPhotoUseCase

* GetPhotoUseCase
  * 有効なIDを受け取ると保存されているPhotoを返す
    * [ ] 存在するIDで検索するとPhotoが返される
      * [ ] Repository.findById()が1回呼び出される
      * [ ] 取得したPhotoのプロパティが正しい
    * [ ] 取得したPhotoに画像データが含まれる
      * [ ] imageDataがnullでない
      * [ ] imageDataのサイズが0より大きい
  * 存在しないIDを受け取ると例外が発生する
    * [ ] 存在しないUUIDで検索するとPhotoNotFoundExceptionが発生する
      * [ ] Repository.findById()がOptional.empty()を返す
      * [ ] 適切なエラーメッセージが含まれる

---

## 2. Controller層

### PhotoController - POST /api/v1/photos

* PhotoController POST /api/v1/photos
  * 正常系: ファイルアップロード成功
    * [ ] 有効なJPEGファイルをアップロードすると201が返る
      * [ ] multipart/form-dataでファイルを送信
      * [ ] レスポンスステータス: 201 Created
      * [ ] Locationヘッダーに /api/v1/photos/{id} が含まれる
      * [ ] レスポンスボディにPhotoResponseが含まれる
    * [ ] 有効なPNGファイルをアップロードすると201が返る
    * [ ] UploadPhotoUseCaseが1回呼び出される
  * 異常系: バリデーションエラー
    * [ ] ファイルサイズ超過で400が返る
      * [ ] 10MB+1のファイルで400 Bad Request
      * [ ] レスポンスボディはProblemDetails形式
      * [ ] detail: "ファイルサイズが最大許容サイズ10MBを超えています"
    * [ ] 非対応形式で400が返る
      * [ ] GIFファイルで400 Bad Request
      * [ ] detail: "サポートされていないファイル形式です。JPEG または PNG のみ許可されています"
    * [ ] ファイル未指定で400が返る
      * [ ] fileパラメータなしで400 Bad Request
      * [ ] detail: "ファイルは必須です"
  * 異常系: サーバーエラー
    * [ ] Repository保存失敗で500が返る
      * [ ] UseCaseが例外をスローすると500 Internal Server Error
      * [ ] レスポンスボディはProblemDetails形式

### PhotoController - GET /api/v1/photos/{id}

* PhotoController GET /api/v1/photos/{id}
  * 正常系: 写真取得成功
    * [ ] 存在するIDで検索すると200が返る
      * [ ] レスポンスステータス: 200 OK
      * [ ] Content-Type: image/jpeg または image/png
      * [ ] レスポンスボディに画像バイナリが含まれる
    * [ ] download=falseでContent-Dispositionヘッダーがない
      * [ ] プレビュー用として画像データのみ返却
    * [ ] download=trueでContent-Dispositionヘッダーが設定される
      * [ ] Content-Disposition: attachment; filename="photo_{id}.jpg"
    * [ ] GetPhotoUseCaseが1回呼び出される
  * 異常系: 写真が見つからない
    * [ ] 存在しないIDで検索すると404が返る
      * [ ] レスポンスステータス: 404 Not Found
      * [ ] レスポンスボディはProblemDetails形式
      * [ ] detail: "ID '{id}' の写真が見つかりません"
  * 異常系: サーバーエラー
    * [ ] Repository取得失敗で500が返る
      * [ ] UseCaseが例外をスローすると500 Internal Server Error

---

## 実装順序の推奨

1. **UploadPhotoUseCase** (PhotoRepositoryに依存)
2. **GetPhotoUseCase** (PhotoRepositoryに依存)
3. **PhotoController - POST** (UploadPhotoUseCaseに依存)
4. **PhotoController - GET** (GetPhotoUseCaseに依存)

---

## テスト実行チェックリスト

### UploadPhotoUseCase
- [ ] 全テスト実装完了
- [ ] 全テスト通過（Green）

### GetPhotoUseCase
- [ ] 全テスト実装完了
- [ ] 全テスト通過（Green）

### PhotoController POST
- [ ] 全テスト実装完了
- [ ] 全テスト通過（Green）

### PhotoController GET
- [ ] 全テスト実装完了
- [ ] 全テスト通過（Green）
