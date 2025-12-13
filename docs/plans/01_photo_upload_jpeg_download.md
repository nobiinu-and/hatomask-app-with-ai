# 実装計画: JPEGファイルのアップロードとダウンロード

## 基本情報
- **Feature**: 写真のアップロードとダウンロード
- **Scenario**: JPEGファイルのアップロードとダウンロード
- **Spec**: [docs/spec/features/01_photo_upload_download.md](../spec/features/01_photo_upload_download.md)
- **Domain Model**: [docs/spec/models/photo_upload_download.md](../spec/models/photo_upload_download.md)
- **OpenAPI**: [docs/spec/api/photo_upload_download.yaml](../spec/api/photo_upload_download.yaml)
- **Gherkin**: [e2e/features/photo_upload_download.feature](../../e2e/features/photo_upload_download.feature)

## OpenAPI仕様

参照: `docs/spec/api/photo_upload_download.yaml`

### エンドポイント一覧

| エンドポイント | メソッド | 概要 | 使用ステップ |
|---------------|---------|------|-------------|
| /api/v1/photos | POST | 写真をアップロード | ユーザーがJPEGファイルを選択する |
| /api/v1/photos/{id} | GET | 写真を取得（プレビュー・ダウンロード） | プレビュー表示、ダウンロード |

### 主要スキーマ

- **PhotoResponse**: 写真アップロード成功時のレスポンス（id, originalFileName, contentType, fileSize, createdAt, updatedAt）
- **ProblemDetails**: エラーレスポンス（RFC 9457準拠）

## ステップ別実装分類

Phase 6の縦切り実装のため、各ステップを分類します。

| ステップ | 分類 | API | 状態依存 | 備考 |
|---------|------|-----|---------|------|
| Given: ユーザーがHatoMaskアプリケーションにアクセスしている | フロントのみ | - | なし | ページ表示のみ |
| When: ユーザーが「写真を選択」ボタンをクリックする | フロントのみ | - | なし | ファイル選択ダイアログ表示 |
| And: ユーザーがファイルサイズ5MBのJPEGファイルを選択する | **API依存** | POST /api/v1/photos | UI状態 | ファイル選択→自動アップロード |
| Then: アップロードが成功する | **API依存** | POST /api/v1/photos | UI状態 | レスポンス検証 |
| And: プレビューエリアに選択した画像が表示される | **API依存** | GET /api/v1/photos/{id} | **DB状態** | 前ステップで保存したPhotoをDBから取得 |
| When: ユーザーが「ダウンロード」ボタンをクリックする | フロントのみ | - | UI状態 | ダウンロードボタンクリック |
| Then: 元の画像がダウンロードされる | **API依存** | GET /api/v1/photos/{id}?download=true | **DB状態** | 同じPhotoをダウンロード用に取得 |

### 凡例
- **フロントのみ**: API呼び出しなし、フロントエンド実装のみ
- **API依存**: バックエンドAPIを呼び出す（フロント+バックエンド両方実装必要）
- **状態依存**:
  - UI状態: フロントエンドのReact State等に依存
  - DB状態: 前ステップでDBに保存したデータに依存

## 推奨実装グルーピング

Phase 6で縦切り実装する際の推奨グループ分けです。
AIとの相談時に調整可能です。

### グループ1: 初期表示とファイル選択UI（フロントのみ）
- Given: ユーザーがHatoMaskアプリケーションにアクセスしている
- When: ユーザーが「写真を選択」ボタンをクリックする

**実装内容**:
- トップページ表示
- ファイル選択ボタンとダイアログの実装
- プレビューエリアの初期表示

**API**: なし

**グルーピング理由**:
- フロントエンドのみで完結し、API実装不要
- 基本的なUI構造を先に確立できる

### グループ2: 写真アップロードとプレビュー表示（API: POST /photos, GET /photos/{id}）
- And: ユーザーがファイルサイズ5MBのJPEGファイルを選択する
- Then: アップロードが成功する
- And: プレビューエリアに選択した画像が表示される

**実装内容**:
- フロント: ファイル選択→multipart/form-dataでアップロード→PhotoResponse受信→プレビュー表示
- バックエンド: POST /api/v1/photos（ファイル保存、DB記録）、GET /api/v1/photos/{id}（画像取得）
- DB: photos テーブル作成、PhotoRepository実装

**API**: 
- POST /api/v1/photos
- GET /api/v1/photos/{id}

**グルーピング理由**:
- アップロード→保存→取得の一連のフローを完成させる
- DB状態に依存するステップ（プレビュー表示）を含むため、一緒に実装する必要がある
- この時点でエンドツーエンドの基本フローが完成する

### グループ3: ダウンロード機能（API: GET /photos/{id}?download=true）
- When: ユーザーが「ダウンロード」ボタンをクリックする
- Then: 元の画像がダウンロードされる

**実装内容**:
- フロント: ダウンロードボタン→GET /api/v1/photos/{id}?download=true→ブラウザのダウンロード機能
- バックエンド: downloadパラメータに応じてContent-Dispositionヘッダーを設定

**API**: 
- GET /api/v1/photos/{id}?download=true

**グルーピング理由**:
- グループ2で実装したGET /photos/{id}の拡張（クエリパラメータ追加）
- DB状態（既存Photo）に依存するが、グループ2で基盤が完成しているため独立実装可能

## Gherkinステップごとの実装要件

### Given: ユーザーがHatoMaskアプリケーションにアクセスしている

**Frontend**
- **Components**: App.tsx（トップページ）
- **State**: なし（初期状態）
- **UI/UX**: 
  - タイトル「HatoMask App」表示
  - 「写真を選択」ボタン表示（中央揃え）
  - プレビューエリア（初期状態は空白）
  - 「ダウンロード」ボタン（初期状態は無効化）

**Backend API**
- なし（静的ページ表示のみ）

**Database**
- なし

**Validation & Logic**
- なし

---

### When: ユーザーが「写真を選択」ボタンをクリックする

**Frontend**
- **Components**: 
  - FileUploadButton コンポーネント
  - input[type="file"] をhiddenで配置
- **State**: なし
- **UI/UX**: 
  - ボタンクリック→ファイル選択ダイアログ表示
  - accept属性で"image/jpeg,image/png"に制限

**Backend API**
- なし

**Database**
- なし

**Validation & Logic**
- フロント側でファイル形式のプリバリデーション（accept属性）

---

### And: ユーザーがファイルサイズ5MBのJPEGファイルを選択する

**Frontend**
- **Components**: FileUploadButton
- **State**: 
  - selectedFile: File | null（選択されたファイル）
  - uploadedPhotoId: string | null（アップロード後のPhotoID）
- **UI/UX**: 
  - ファイル選択直後、自動的にアップロード開始
  - ローディング表示（オプション）

**Backend API**
- **Endpoint**: `POST /api/v1/photos`
- **OpenAPI参照**: `paths./photos.post`
- **Request**: 
  - Content-Type: multipart/form-data
  - Body: file (binary)
- **Response**: 
  - 201 Created
  - Body: PhotoResponse { id, originalFileName, contentType, fileSize, createdAt, updatedAt }
  - Header: Location: /api/v1/photos/{id}

**Backend実装**:
- **Controller**: PhotoController.uploadPhoto()
  - multipart/form-data受信
  - ファイルサイズ検証（最大10MB）
  - Content-Type検証（image/jpeg, image/png）
  - PhotoService呼び出し
- **Service**: PhotoService.uploadPhoto(file)
  - ContentType, FileSize ValueObject生成（バリデーション含む）
  - Photo Entity生成
  - ファイル保存（ローカルストレージ）
  - PhotoRepository.save()
- **Repository**: PhotoRepository.save(photo)
  - DBにメタデータ保存
  - idとタイムスタンプ自動生成

**Database**
- **Tables**: photos
  ```sql
  CREATE TABLE photos (
    id UUID PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    image_data BYTEA NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
  );
  ```
- **Changes**: INSERT INTO photos

**Validation & Logic**
- フロント: ファイルサイズチェック（10MB以下）→バックエンドに委譲可
- バックエンド:
  - ContentType検証（"image/jpeg", "image/png"のみ）
  - FileSize検証（1 - 10,485,760 bytes）
  - バリデーション失敗時は400 Bad Requestで詳細メッセージ返却

---

### Then: アップロードが成功する

**Frontend**
- **Components**: FileUploadButton
- **State**: uploadedPhotoId を設定
- **UI/UX**: 
  - レスポンスステータス201を確認
  - エラーがないことを確認

**Backend API**
- （前ステップと同じ POST /api/v1/photos のレスポンス検証）

**Database**
- （前ステップでINSERT済み）

**Validation & Logic**
- HTTPステータス201の確認
- PhotoResponse.idの存在確認

---

### And: プレビューエリアに選択した画像が表示される

**Frontend**
- **Components**: ImagePreview コンポーネント
- **State**: 
  - uploadedPhotoId（前ステップで設定済み）
  - previewImageUrl: string（取得した画像のURL）
- **UI/UX**: 
  - GET /api/v1/photos/{id} を呼び出し
  - 取得した画像をimg要素で表示
  - 画像は表示領域に収まるようにリサイズ（CSS: max-width, max-height、aspect-ratio維持）

**Backend API**
- **Endpoint**: `GET /api/v1/photos/{id}`
- **OpenAPI参照**: `paths./photos/{id}.get`
- **Request**: 
  - Path Parameter: id (UUID)
  - Query Parameter: download=false（デフォルト）
- **Response**: 
  - 200 OK
  - Content-Type: image/jpeg または image/png
  - Body: 画像バイナリデータ

**Backend実装**:
- **Controller**: PhotoController.getPhotoById(id, download)
  - PhotoRepository.findById(id)
  - 404処理（存在しない場合）
  - Content-Typeヘッダー設定（photo.contentType）
  - downloadパラメータに応じてContent-Dispositionヘッダー設定
  - 画像データ（byte[]）を返却

**Database**
- **Tables**: photos
- **Changes**: SELECT * FROM photos WHERE id = ?

**Validation & Logic**
- バックエンド: Photo存在確認（なければ404 Not Found）
- フロント: 画像表示成功確認

---

### When: ユーザーが「ダウンロード」ボタンをクリックする

**Frontend**
- **Components**: DownloadButton コンポーネント
- **State**: なし（uploadedPhotoIdを参照）
- **UI/UX**: 
  - ボタンクリック→ダウンロードAPI呼び出し
  - ボタンは画像アップロード後に有効化

**Backend API**
- なし（次ステップで実行）

**Database**
- なし

**Validation & Logic**
- uploadedPhotoId が存在することを確認（未アップロードならボタン無効化）

---

### Then: 元の画像がダウンロードされる

**Frontend**
- **Components**: DownloadButton
- **State**: なし
- **UI/UX**: 
  - GET /api/v1/photos/{id}?download=true を呼び出し
  - ブラウザのダウンロード機能で画像保存

**実装方法**:
- aタグのhref属性に `/api/v1/photos/{id}?download=true` を設定
- download属性でファイル名指定（オプション）
- または fetch() → Blob → URL.createObjectURL() → aタグでクリック

**Backend API**
- **Endpoint**: `GET /api/v1/photos/{id}?download=true`
- **OpenAPI参照**: `paths./photos/{id}.get` (downloadパラメータ)
- **Request**: 
  - Path Parameter: id (UUID)
  - Query Parameter: download=true
- **Response**: 
  - 200 OK
  - Content-Type: image/jpeg または image/png
  - Content-Disposition: `attachment; filename="photo_{id}.jpg"`
  - Body: 画像バイナリデータ

**Backend実装**:
- PhotoController.getPhotoById(id, download=true)
  - downloadパラメータがtrueの場合、Content-Dispositionヘッダーを設定
  - ファイル名は `photo_{id}.{拡張子}` 形式

**Database**
- **Tables**: photos
- **Changes**: SELECT * FROM photos WHERE id = ?（プレビューと同じ）

**Validation & Logic**
- バックエンド: Photo存在確認（なければ404）
- フロント: ダウンロード成功確認（ブラウザのダウンロードUI表示）

---

## データモデル (共通)

**参照**: `docs/spec/models/photo_upload_download.md`

ドメインモデルで定義されたエンティティ、バリューオブジェクト、リポジトリインターフェースを使用します。

### Entity
- **Photo**: id, originalFileName, contentType, fileSize, imageData, createdAt, updatedAt

### Value Objects
- **ContentType**: value ("image/jpeg" | "image/png")
- **FileSize**: bytes (1 - 10,485,760)

### Repository Interface
- **PhotoRepository**: save(photo), findById(id), deleteById(id)

### DB Schema
```sql
CREATE TABLE photos (
  id UUID PRIMARY KEY,
  original_file_name VARCHAR(255) NOT NULL,
  content_type VARCHAR(50) NOT NULL CHECK (content_type IN ('image/jpeg', 'image/png')),
  file_size BIGINT NOT NULL CHECK (file_size > 0 AND file_size <= 10485760),
  image_data BYTEA NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_photos_created_at ON photos(created_at);
```

### フロントエンドDTO
```typescript
interface PhotoResponse {
  id: string; // UUID
  originalFileName: string;
  contentType: 'image/jpeg' | 'image/png';
  fileSize: number;
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
}

interface ProblemDetails {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance?: string;
}
```

## 技術的課題・リスク

- [ ] **ファイルサイズ制限の適切な設定**
  - フロント: ファイル選択時にプリバリデーション可能だが、バックエンドでも必須
  - バックエンド: Spring Bootの`spring.servlet.multipart.max-file-size`を10MBに設定
  
- [ ] **画像データの保存方法**
  - Phase 1ではDBに直接保存（BYTEA）
  - 将来的にクラウドストレージ（S3等）に移行する可能性を考慮
  - PhotoRepositoryのインターフェースは変更不要なように設計
  
- [ ] **Content-Typeの検証**
  - ブラウザのaccept属性だけでは不十分（ユーザーが回避可能）
  - バックエンドでファイルのマジックバイトを確認する必要があるか？
  - 現時点はContent-Typeヘッダーのみで判定
  
- [ ] **プレビュー表示のパフォーマンス**
  - 大きな画像（10MB）の場合、表示が遅くなる可能性
  - サムネイル生成は将来の拡張として検討
  
- [ ] **同時アップロード時の挙動**
  - 現仕様では複数回アップロードすると新しいPhotoが作成される
  - 古いPhotoの削除タイミングは未定（将来の拡張）
  
- [ ] **エラーハンドリングのUI**
  - バリデーションエラー時のメッセージ表示方法
  - 現時点はシンプルなalert()またはエラーメッセージ表示エリアで対応

## 備考

- このシナリオは写真アップロード・ダウンロード機能の**基本ハッピーパス**を実装します
- JPEGファイル（5MB）を使用して、アップロード→プレビュー→ダウンロードの一連のフローを検証します
- Phase 6の縦切り実装では、グループ1→グループ2→グループ3の順に進めることを推奨
- グループ2が最も重要で、エンドツーエンドの基本フローが完成します
- エラーケース、PNGファイル、複数回アップロードなどは、Phase 4を繰り返して別の実装計画で対応します
