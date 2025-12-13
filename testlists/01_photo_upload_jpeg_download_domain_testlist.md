# ドメイン層テストリスト: 写真アップロードとプレビュー表示

**Feature**: 写真のアップロードとダウンロード（ステップ3-5）  
**Domain Model**: `docs/spec/models/photo_upload_download.md`  
**Plan**: `docs/plans/01_photo_upload_jpeg_download.md`

---

## 1. Entity/ValueObject

### ContentType ValueObject

* ContentType ValueObject
  * 有効なMIMEタイプを受け取るとContentTypeが生成される
    * [ ] "image/jpeg"を受け取ると正常に生成される
    * [ ] "image/png"を受け取ると正常に生成される
  * バリデーションエラーでContentType生成が失敗する
    * [ ] "image/gif"を受け取るとIllegalArgumentExceptionが発生する
    * [ ] "text/plain"を受け取るとIllegalArgumentExceptionが発生する
    * [ ] 空文字列を受け取るとIllegalArgumentExceptionが発生する
    * [ ] nullを受け取るとIllegalArgumentExceptionが発生する
  * 値の取得と比較
    * [ ] getValue()は設定したMIMEタイプを返す
    * [ ] 同じ値のContentTypeは等価である
    * [ ] 異なる値のContentTypeは等価でない

### FileSize ValueObject

* FileSize ValueObject
  * 有効なファイルサイズを受け取るとFileSizeが生成される
    * [ ] 1バイトを受け取ると正常に生成される
    * [ ] 5242880バイト（5MB）を受け取ると正常に生成される
    * [ ] 10485760バイト（10MB）を受け取ると正常に生成される
  * バリデーションエラーでFileSize生成が失敗する
    * [ ] 0バイトを受け取るとIllegalArgumentExceptionが発生する
    * [ ] 負の値を受け取るとIllegalArgumentExceptionが発生する
    * [ ] 10485761バイト（10MB+1）を受け取るとIllegalArgumentExceptionが発生する
    * [ ] nullを受け取るとIllegalArgumentExceptionが発生する
  * 値の取得と比較
    * [ ] getBytes()は設定したバイト数を返す
    * [ ] 同じ値のFileSizeは等価である
    * [ ] 異なる値のFileSizeは等価でない

### Photo Entity

* Photo Entity
  * 有効なプロパティを受け取るとPhotoエンティティが生成される
    * [ ] id、originalFileName、contentType、fileSize、imageDataを受け取ると正常に生成される
      * [ ] "sample.jpg", ContentType(image/jpeg), FileSize(5242880), byte配列を受け取ると生成される
      * [ ] "photo.png", ContentType(image/png), FileSize(1024), byte配列を受け取ると生成される
    * [ ] 生成されたエンティティは正しいプロパティ値を持つ
      * [ ] getId()はUUIDを返す（nullでない）
      * [ ] getOriginalFileName()は設定したファイル名を返す
      * [ ] getContentType()は設定したContentTypeを返す
      * [ ] getFileSize()は設定したFileSizeを返す
      * [ ] getImageData()は設定したバイト配列を返す
      * [ ] getCreatedAt()は現在時刻を返す（nullでない）
      * [ ] getUpdatedAt()は現在時刻を返す（nullでない）
  * バリデーションエラーでエンティティ生成が失敗する
    * [ ] originalFileNameが空の場合、IllegalArgumentExceptionが発生する
      * [ ] 空文字列を受け取ると例外が発生する
      * [ ] nullを受け取ると例外が発生する
    * [ ] originalFileNameが255文字を超える場合、IllegalArgumentExceptionが発生する
    * [ ] contentTypeがnullの場合、IllegalArgumentExceptionが発生する
    * [ ] fileSizeがnullの場合、IllegalArgumentExceptionが発生する
    * [ ] imageDataが空の場合、IllegalArgumentExceptionが発生する
      * [ ] 空配列を受け取ると例外が発生する
      * [ ] nullを受け取ると例外が発生する

---

## 2. Repository Interface & 実装

### PhotoRepository

* PhotoRepository
  * 基本的なCRUD操作
    * [ ] save()でPhotoを保存できる
      * [ ] 新規Photoを保存するとidとタイムスタンプが設定される
      * [ ] 保存されたPhotoのidはnullでない
      * [ ] 保存されたPhotoのcreatedAtは現在時刻に近い
      * [ ] 保存されたPhotoのupdatedAtは現在時刻に近い
    * [ ] findById()で保存したPhotoを取得できる
      * [ ] 保存直後のidで検索すると、同じ内容のPhotoが取得できる
      * [ ] originalFileName、contentType、fileSize、imageDataが一致する
    * [ ] findById()で存在しないidを検索するとOptional.empty()が返る
      * [ ] ランダムなUUIDで検索するとOptional.empty()が返る
    * [ ] deleteById()でPhotoを削除できる
      * [ ] 削除後、findById()でOptional.empty()が返る
  * imageDataの保存と取得
    * [ ] 5MBのバイナリデータを保存・取得できる
      * [ ] 保存前と取得後のバイト配列が完全に一致する
    * [ ] 画像データの内容が破損していない
      * [ ] バイト配列の先頭と末尾が一致する

---

## 実装順序の推奨

1. **ContentType ValueObject** (依存なし)
2. **FileSize ValueObject** (依存なし)
3. **Photo Entity** (ContentType, FileSizeに依存)
4. **PhotoRepository Interface** (Photo Entityに依存)
5. **PhotoRepositoryImpl** (全てに依存)

---

## テスト実行チェックリスト

各テストケースを実装・実行したらチェックを入れてください。

### ContentType ValueObject
- [ ] 全テスト実装完了
- [ ] 全テスト通過（Green）

### FileSize ValueObject
- [ ] 全テスト実装完了
- [ ] 全テスト通過（Green）

### Photo Entity
- [ ] 全テスト実装完了
- [ ] 全テスト通過（Green）

### PhotoRepository
- [ ] 全テスト実装完了
- [ ] 全テスト通過（Green）
