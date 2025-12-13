# Phase 5: Backend Stub 動作確認手順

## 作成ファイル

**バックエンド**:
- ✅ `src/backend/src/main/java/com/hatomask/presentation/controller/PhotoController.java` (Stub実装、TODO付き)
- ✅ `src/backend/src/main/java/com/hatomask/presentation/dto/PhotoResponse.java` (DTO)
- ✅ `src/backend/src/main/java/com/hatomask/config/WebConfig.java` (CORS設定 - 既存)

## Stub実装の特徴

### PhotoController

- **POST /api/v1/photos**: multipart/form-dataでファイルを受け取り、ランダムなUUIDと現在時刻を含むPhotoResponseを返却
- **GET /api/v1/photos/{id}**: 空のバイト配列を返却（Stub実装）
  - `download=true` パラメータでContent-Dispositionヘッダーを設定

### TODOコメント

各メソッドに以下のTODOコメントを付与:
```java
// TODO: Replace stub implementation
// Phase 6でドメイン層・UseCase実装後に置き換える
```

## 動作確認手順

### 1. バックエンドの起動

```bash
cd /workspaces/hatomask-app-with-ai/src/backend
mvn spring-boot:run
```

**確認ポイント**:
- ポート8080で起動することを確認
- コンソールに "Started HatomaskApplication" が表示される

### 2. テスト画像の作成

```bash
# 簡単なテストファイルを作成（実際のJPEG画像の代わり）
echo "test image data" > /tmp/test.jpg
```

または、既存の画像ファイルを使用:
```bash
# プロジェクト内の画像があれば使用
ls /workspaces/hatomask-app-with-ai/uploads_data/
```

### 3. API動作確認

#### POST /api/v1/photos (アップロード)

```bash
curl -X POST http://localhost:8080/api/v1/photos \
  -F "file=@/tmp/test.jpg" \
  -v
```

**期待されるレスポンス**:
- ステータスコード: `201 Created`
- Header: `Location: /api/v1/photos/{生成されたUUID}`
- Body:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "originalFileName": "test.jpg",
  "contentType": "image/jpeg",
  "fileSize": 17,
  "createdAt": "2025-12-13T12:30:00",
  "updatedAt": "2025-12-13T12:30:00"
}
```

#### GET /api/v1/photos/{id} (プレビュー)

```bash
curl http://localhost:8080/api/v1/photos/550e8400-e29b-41d4-a716-446655440000 \
  -v
```

**期待されるレスポンス**:
- ステータスコード: `200 OK`
- Header: `Content-Type: image/jpeg`
- Body: 空のバイト配列（Stub実装のため）

#### GET /api/v1/photos/{id}?download=true (ダウンロード)

```bash
curl "http://localhost:8080/api/v1/photos/550e8400-e29b-41d4-a716-446655440000?download=true" \
  -v
```

**期待されるレスポンス**:
- ステータスコード: `200 OK`
- Header: `Content-Type: image/jpeg`
- Header: `Content-Disposition: attachment; filename="photo_550e8400-e29b-41d4-a716-446655440000.jpg"`
- Body: 空のバイト配列（Stub実装のため）

### 4. CORS確認

フロントエンドから接続する際、CORS設定が正しく機能することを確認:

```bash
# プリフライトリクエストのシミュレーション
curl -X OPTIONS http://localhost:8080/api/v1/photos \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -v
```

**期待されるレスポンス**:
- ステータスコード: `200 OK`
- Header: `Access-Control-Allow-Origin: http://localhost:5173`
- Header: `Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS`

## CheckStyle対応

以下のCheckStyleエラーを修正済み:
- ✅ 未使用のimport削除 (`HttpStatus`)
- ✅ `.*` 形式のimportを個別importに変更

## 次のステップ

### Phase 6: 縦切り実装サイクル

実装計画（[docs/plans/01_photo_upload_jpeg_download.md](../plans/01_photo_upload_jpeg_download.md)）に基づき、以下の順序で実装:

1. **グループ1**: 初期表示とファイル選択UI（フロントのみ）
2. **グループ2**: 写真アップロードとプレビュー表示（API実装）⭐ 最重要
3. **グループ3**: ダウンロード機能

各グループで、PhotoControllerのTODOコメント部分を実装で置き換えていきます。

## トラブルシューティング

### Backend起動エラー

**症状**: CheckStyleエラーで起動失敗

**対策**: PhotoController.javaのimport文を確認し、個別importに修正

### CORS エラー

**症状**: フロントエンドから `CORS policy: No 'Access-Control-Allow-Origin'`

**確認事項**:
- `src/backend/src/main/resources/application.yml` の `cors.allowed-origins` に `http://localhost:5173` が含まれているか確認済み

### ポート競合

**症状**: `Port 8080 is already in use`

**対策**: 既存のプロセスを停止
```bash
# プロセス確認
lsof -i :8080
# 停止
kill -9 <PID>
```
