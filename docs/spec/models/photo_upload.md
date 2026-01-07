# 写真アップロード（静止画）ドメインモデル

## 概要

ユーザーがローカル端末から JPEG/PNG の静止画を選択し、バリデーション・デコード・向き正規化を行ったうえで「プレビュー可能な選択済み画像」状態を保持する。
本モデルの範囲は、機能仕様 [docs/spec/features/01_photo_upload.md](../features/01_photo_upload.md) に明示された「ファイル選択、バリデーション、画像デコード、正規化、プレビュー可能な状態の保持（画像本体/サイズ/MIME type）」までとする。

## 命名規約（任意項目を採用する場合）

このモデルでは、Milestone 0 で永続化を必須にしないため、識別子や監査フィールドは必須にしない。
ただし将来の拡張で識別子が必要になった場合は、テンプレート推奨に従い `id` を用いる。

## エンティティ

### SelectedPhoto

**説明**: アプリが「プレビュー可能」と判定し、後続機能が参照できる形で保持する「選択済み画像」。新しい画像を選択すると置き換わる（同時に複数を保持しない）。

**プロパティ**:

- image: NormalizedImage (必須、デコード済み/参照可能であること)
- mimeType: MimeType (必須、`image/jpeg` または `image/png`)
- dimensions: ImageDimensions (必須、幅/高さ)

**バリデーションルール**:

- image: デコード済みでプレビュー可能であること
- mimeType: JPEG/PNG のいずれか
- dimensions: `width > 0` かつ `height > 0`

**関連**:

- `SelectedPhoto` は `NormalizedImage`（バリューオブジェクト）を 1:1 で保持する

## エンティティ関連図

```
[SelectedPhoto] --1:1--> (NormalizedImage)
[SelectedPhoto] --1:1--> (ImageDimensions)
[SelectedPhoto] --1:1--> (MimeType)
```

## バリューオブジェクト

### MimeType

**説明**: 選択画像の MIME type。仕様上、JPEG/PNG のみ許可する。

**プロパティ**:

- value: String

**バリデーションルール**:

- `value` は `image/jpeg` または `image/png`

### ImageDimensions

**説明**: 画像サイズ（幅/高さ）。

**プロパティ**:

- width: Int
- height: Int

**バリデーションルール**:

- `width > 0`
- `height > 0`

### FileSizeBytes

**説明**: ファイルサイズ（バリデーション用途）。

**プロパティ**:

- value: Long

**バリデーションルール**:

- `0 < value <= 10 * 1024 * 1024`（最大 10MB）

### NormalizedImage

**説明**: EXIF 回転等を考慮し、アプリ内で「見えている向き」と「後続処理（顔検出）に使う向き」が一致するように正規化された画像。

**プロパティ**:

- decoded: ImageDataRef

**バリデーションルール**:

- `decoded` はデコードに成功しており、参照可能であること
- 向きが正規化されており、プレビューと後続処理で同一の向きとして扱えること

### PhotoSelectionError

**説明**: 選択操作が失敗した場合にユーザーへ提示するエラー情報。

**プロパティ**:

- message: String

**バリデーションルール**:

- `message` は空でない

## リポジトリインターフェース

### SelectedPhotoRepository

**説明**: `SelectedPhoto`（選択済み画像状態）を保持・取得する。Milestone 0 では永続化は必須にせず、アプリ内メモリ等の短命ストアを想定する。

**メソッド**:

- setCurrent(photo: SelectedPhoto): void
  - 選択済み画像を更新する（新しい画像で置換）
- getCurrent(): Optional<SelectedPhoto>
  - 現在の選択済み画像を取得する
- clear(): void
  - 選択済み画像を破棄する

## ドメインサービス

### PhotoSelectionService

**説明**: ローカルで選択された画像ファイルを、仕様に従って検証し、デコード・向き正規化して `SelectedPhoto` を生成する。

**メソッド**:

- validate(mimeType: MimeType, fileSize: FileSizeBytes): Optional<PhotoSelectionError>
  - JPEG/PNG と 10MB 以下の制約を検証し、失敗時はエラーを返す
- decodeAndNormalize(file: LocalImageFile): NormalizedImage | PhotoSelectionError
  - 画像デコードと向き正規化を行い、失敗時はエラーを返す
- createSelectedPhoto(image: NormalizedImage, mimeType: MimeType, dimensions: ImageDimensions): SelectedPhoto
  - 後続機能へ受け渡せる `SelectedPhoto` を生成する

## 補足・注意事項

- 本モデルは、機能仕様に明示された「ファイル選択・検証・デコード/正規化・プレビュー可能状態の保持・後続機能への受け渡し（画像本体/サイズ/MIME type）」のみを対象とする。
- 選択キャンセル時に状態を変更しない、バリデーション失敗時に「選択済み画像」を更新しない、という振る舞いは `SelectedPhotoRepository` を更新しない（あるいは更新を行う前に失敗を返す）ことで表現する。
- 「新しい画像を選択した場合、後続処理の結果（顔検出結果・マスク状態など）は破棄/再計算の対象」とあるが、後続処理の具体的なエンティティは本 feature に含まれないため、このモデルでは定義しない（統合点としては `SelectedPhoto` の置換がトリガになる）。
- データ取り扱いの制約（非ログ等）は仕様に明記されているが、ログ方針は実装・運用設計に属するため、本モデルでは「ファイル名」等のプロパティを追加しない。
