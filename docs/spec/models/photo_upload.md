# 写真アップロード（静止画） ドメインモデル

## 概要

ユーザーがローカル端末から静止画（JPEG/PNG）を選択し、アプリ上でプレビュー可能な「選択済み画像」として保持するためのモデルです。
本モデルの範囲は、ファイル選択、バリデーション、画像デコードと正規化、プレビュー表示、後続機能が参照できる状態保持までです（サーバ永続化は含めません）。

## エンティティ

### SelectedImage

**説明**: アプリ内で「現在選択されている画像」を表すエンティティ。ユーザーが新しい画像を選択すると差し替わり、後続処理の結果は破棄/再計算の対象となる。

**プロパティ**:

- content: ImageContent (必須、プレビューと後続処理が参照できる“画像本体”)
- metadata: ImageMetadata (必須、幅/高さ/MIME type を含む)

**バリデーションルール**:

- content.source.mimeType は `image/jpeg` または `image/png` のいずれか
- content.source.sizeBytes は 10MB 以下
- content.decoded は「プレビュー可能」な状態である（デコードに失敗した場合は SelectedImage として成立しない）

## バリューオブジェクト

### ImageSource

**説明**: ユーザーが選択した入力ファイル（ローカル画像）の情報。

**プロパティ**:

- mimeType: MimeType
- sizeBytes: FileSizeBytes

**バリデーションルール**:

- mimeType は `image/jpeg` または `image/png`
- sizeBytes は 10MB 以下

### ImageContent

**説明**: アプリがプレビュー/後続処理に利用できる形の画像データ。

**プロパティ**:

- source: ImageSource
- decoded: DecodedImageRef (デコード済み、または参照可能な形)
- normalized: NormalizedImageRef (任意、採用する場合のみ。向きを考慮した“正規化画像”への参照)

**バリデーションルール**:

- decoded はプレビューに使用できる参照である
- normalized を採用する場合、アプリ内で“見えている向き”と“検出に使う向き”が一致する

### ImageMetadata

**説明**: 後続機能が参照できる画像のメタ情報。

**プロパティ**:

- widthPx: PixelSize (必須)
- heightPx: PixelSize (必須)
- mimeType: MimeType (必須)

**バリデーションルール**:

- widthPx > 0
- heightPx > 0
- mimeType は `image/jpeg` または `image/png`

### MimeType

**説明**: 画像の MIME type。

**プロパティ**:

- value: String

**バリデーションルール**:

- value は `image/jpeg` または `image/png`

### FileSizeBytes

**説明**: ファイルサイズ（バイト）。

**プロパティ**:

- value: Integer

**バリデーションルール**:

- value >= 0
- value <= 10MB（10 _ 1024 _ 1024 bytes）

### PixelSize

**説明**: 画像の幅/高さ（ピクセル）。

**プロパティ**:

- value: Integer

**バリデーションルール**:

- value > 0

### DecodedImageRef

**説明**: デコード済み画像、またはそれを参照可能なハンドル（実装に依存）。

**プロパティ**:

- value: Any

**バリデーションルール**:

- 参照先がプレビュー表示に使用できる

### NormalizedImageRef（任意）

**説明**: 正規化画像（向きを考慮し、アプリ内で一貫して扱える向きになった画像）への参照。

**プロパティ**:

- value: Any

**バリデーションルール**:

- 正規化後の画像の見え方が、後続処理で利用する向きと一致する

## エンティティ関連図

```
[SelectedImage] --1:1--> [ImageContent]
[SelectedImage] --1:1--> [ImageMetadata]
[ImageContent] --1:1--> [ImageSource]
```

## リポジトリインターフェース

この機能（Milestone 0 の写真アップロード）は、Spec 上「サーバ永続化」をスコープ外としているため、永続化のためのリポジトリは定義しません。

## ドメインサービス

この機能の範囲では、複数エンティティにまたがるドメインサービスは必須ではありません。
（画像デコードや正規化の処理自体はアプリケーションサービス/ユーティリティとして実装され得るが、ここではドメインサービスとしては定義しない）

## 補足・注意事項

- キャンセル時は状態を変更しない。
- バリデーション失敗（形式不一致/サイズ超過）およびデコード失敗時は、SelectedImage は更新されない。
- 新しい画像を選択した場合、後続処理の状態（顔検出結果・マスク状態など）は破棄/再計算の対象となる（後続 feature 側で扱う前提のルールとして明記）。
- 画像の取り扱い方針（永続化禁止/短命 TTL/非ログ）はデータハンドリング方針に従う。これは実装（特にサーバが受信する場合）で満たすべき制約であり、本モデルの永続化要素は持たない。
