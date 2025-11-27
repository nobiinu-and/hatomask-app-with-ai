# 実装計画: JPEGファイルのアップロードとダウンロード

## 基本情報
- **Feature**: 写真のアップロードとダウンロード
- **Scenario**: JPEGファイルのアップロードとダウンロード
- **Spec**: `docs/spec/features/01_photo_upload_download.md`

## Gherkinステップごとの実装要件

### Given ユーザーがHatoMaskアプリケーションにアクセスしている
**Frontend**
- **Components**: `App.tsx` (または `TopPage.tsx`)
  - アプリケーションのメインレイアウトを表示。
  - タイトル "HatoMask App" を表示。
- **State**: 初期状態。
- **UI/UX**: シンプルなレイアウト。

**Backend API**
- なし

---

### When ユーザーが「写真を選択」ボタンをクリックする
**Frontend**
- **Components**: `PhotoUploader.tsx` (新規作成)
  - `<input type="file" accept="image/jpeg, image/png" style={{ display: 'none' }} />` を配置。
  - 「写真を選択」ボタンを配置し、クリック時に上記inputの `click()` をトリガーする。
- **UI/UX**: ボタンのホバー効果など。

**Backend API**
- なし

---

### And ユーザーがファイルサイズ5MBのJPEGファイルを選択する
**Frontend**
- **Components**: `PhotoUploader.tsx`
  - `onChange` イベントハンドラを実装。
  - 選択されたファイルを取得 (`event.target.files[0]`)。
- **Validation**:
  - ファイル形式が `image/jpeg` または `image/png` であること。
  - ファイルサイズが 10MB 以下であること (シナリオは5MBなのでOK)。
  - エラーがある場合はアラートを表示 (仕様通り)。

**Backend API**
- なし

---

### Then アップロードが成功する
**Frontend**
- **Components**: `PhotoUploader.tsx`
  - バリデーション通過後、APIクライアント (`services/api.ts`) を呼び出す。
  - `FormData` を作成し、ファイルを `file` キーで追加。
- **State**:
  - `isUploading` (boolean): アップロード中は true。
  - `uploadedPhoto` (Photo | null): アップロード成功時にレスポンスデータを格納。
- **Mock API (MSW)**:
  - `handlers.ts` に `POST /api/v1/photos` のハンドラを追加。
  - 成功レスポンス (201 Created) を返す。

**Backend API**
- **Endpoint**: `POST /api/v1/photos`
- **Request**: `multipart/form-data`
  - `file`: Binary (image/jpeg)
- **Response**: `201 Created`
  ```json
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "fileName": "sample.jpg",
    "fileSize": 5242880,
    "mimeType": "image/jpeg",
    "createdAt": "2023-11-26T10:00:00Z"
  }
  ```

**Database**
- **Tables**: `photos`
- **Changes**: レコードの挿入。

**Validation & Logic**
- **Backend Validation**:
  - ファイルが空でないこと。
  - ファイルサイズ制限 (10MB)。
  - サポートされるMIMEタイプ (image/jpeg, image/png)。
- **Logic**:
  - ファイルを `uploads/photos/` ディレクトリに保存 (ファイル名はUUIDなどでユニーク化)。
  - DBにメタデータを保存。

---

### And プレビューエリアに選択した画像が表示される
**Frontend**
- **Components**: `PhotoPreview.tsx` (新規作成)
  - `uploadedPhoto` が存在する場合に表示。
  - `<img>` タグの `src` 属性に `/api/v1/photos/{uploadedPhoto.id}` を設定。
  - 画像がない場合はプレースホルダーまたは空白を表示。
- **UI/UX**: 画像が領域に収まるように `max-width: 100%`, `max-height: 100%` 等を設定。

**Backend API**
- **Endpoint**: `GET /api/v1/photos/{id}`
- **Request**: Path parameter `id`
- **Response**: `200 OK`
  - Content-Type: `image/jpeg`
  - Body: 画像バイナリ

---

### When ユーザーが「ダウンロード」ボタンをクリックする
**Frontend**
- **Components**: `PhotoDownloader.tsx` (または `PhotoPreview.tsx` 内)
  - 「ダウンロード」ボタンを配置。
  - `uploadedPhoto` がない場合は `disabled`。
  - クリック時に `window.location.href = '/api/v1/photos/' + uploadedPhoto.id` 等でダウンロードを開始、または `<a>` タグで `download` 属性を使用。
  - ※APIから直接ダウンロードさせるため、特別なJS処理は不要な場合が多いが、認証等が必要な場合はBlobとして取得して保存する処理が必要。今回は最小実装なので直接リンクまたは `window.open` で可。

**Backend API**
- `GET /api/v1/photos/{id}` を再利用。
- レスポンスヘッダーに `Content-Disposition: attachment; filename="sample.jpg"` を付与することで、ブラウザにダウンロードを強制できる。

---

### Then 元の画像がダウンロードされる
**Frontend**
- ブラウザのネイティブなダウンロード動作を確認。

**Backend API**
- 正しいバイナリデータとヘッダーを返すこと。

---

## データモデル設計 (共通)

### Entities / Interfaces

**TypeScript (Frontend)**
```typescript
export interface Photo {
  id: string;
  fileName: string;
  fileSize: number;
  mimeType: string;
  createdAt: string;
}

export interface ApiError {
  type: string;
  title: string;
  status: number;
  detail: string;
}
```

**Java (Backend)**
```java
// Entity
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private String mimeType;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}

// DTO
public record PhotoResponse(
    UUID id,
    String fileName,
    Long fileSize,
    String mimeType,
    LocalDateTime createdAt
) {}
```

### DB Schema
```sql
CREATE TABLE photos (
  id UUID PRIMARY KEY,
  file_name VARCHAR(255) NOT NULL,
  file_size BIGINT NOT NULL,
  mime_type VARCHAR(50) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 技術的課題・リスク
- [ ] **MSWでのバイナリ処理**: 画像アップロードと画像表示(バイナリレスポンス)のモック化は、JSON APIに比べて少し複雑になる可能性がある。`res(ctx.set('Content-Type', 'image/jpeg'), ctx.body(buffer))` のように実装する必要がある。
- [ ] **ファイル保存権限**: バックエンド(Dockerコンテナ内)で `uploads/photos/` への書き込み権限が適切に設定されているか確認が必要。
- [ ] **CORS**: フロントエンド(Vite)とバックエンド(Spring Boot)のポートが異なるため、開発環境でのCORS設定が必要。
- [ ] **エラーハンドリング**: RFC 9457 形式のエラーレスポンスをフロントエンドで適切にパースして表示する共通処理が必要。

## 備考
- まずはHappy Path (正常系) を通すことを最優先とし、エラーハンドリングは基本的なものに留める。
- テスト駆動開発(TDD)を意識し、バックエンドはController/Service/Repositoryの単体テスト、フロントエンドはコンポーネントテストを書きながら進める。
