# 実装計画: アップロード済み画像から顔ランドマークを取得できる

## 基本情報

- **Feature**: 静止画の顔検出（1 人）
- **Scenario**: アップロード済み画像から顔ランドマークを取得できる
- **Milestone**: m0
- **Spec**: `docs/spec/features/02_face_detection_static.md`
- **Architecture**: `docs/spec/architecture/m0.md`
- **E2E Feature**: `e2e/features/face_detection_static.feature`

## 入力（候補一覧）

### ドメインモデル（候補）

- `docs/spec/models/face_detection.md`
- `docs/spec/models/photo_upload.md`

### OpenAPI 仕様（候補）

- `docs/spec/api/photos.yaml`

## このシナリオで採用する仕様（選択結果）

### ドメインモデル

- **Primary**: `docs/spec/models/face_detection.md`
- **Related（任意）**: `docs/spec/models/photo_upload.md`

### OpenAPI

- **Primary**: `docs/spec/api/photos.yaml`

## OpenAPI 仕様

参照（このシナリオで採用する OpenAPI）:

- Primary: `docs/spec/api/photos.yaml`

### エンドポイント一覧

| Spec(OpenAPI)                         | エンドポイント                           | メソッド | 概要                 | 使用ステップ       |
| ------------------------------------- | ---------------------------------------- | -------- | -------------------- | ------------------ |
| `docs/spec/api/photos.yaml` (Primary) | /api/v1/photos                           | POST     | 写真をアップロード   | 前提のセットアップ |
| `docs/spec/api/photos.yaml` (Primary) | /api/v1/photos/{photoId}/face-detections | POST     | 顔検出（1 人）を実行 | もし/ならば        |

参照（JSON Pointer）:

- `POST /photos`: `docs/spec/api/photos.yaml#/paths/~1photos/post`
- `POST /photos/{photoId}/face-detections`: `docs/spec/api/photos.yaml#/paths/~1photos~1{photoId}~1face-detections/post`

### 主要スキーマ

- **PhotoUploadResponse**: `photoId`, `mimeType`, `fileSizeBytes`, `dimensions`, `expiresAt`
  - JSONPath: `$.components.schemas.PhotoUploadResponse`
- **FaceDetectionResponse**: `result`
  - JSONPath: `$.components.schemas.FaceDetectionResponse`
- **FaceDetectionResult**: `landmarks`, `boundingBox`, `confidence?`
  - JSONPath: `$.components.schemas.FaceDetectionResult`
- **ProblemDetails**: RFC 9457 準拠のエラーレスポンス
  - JSONPath: `$.components.schemas.ProblemDetails`

## ステップ別実装分類

| ステップ                                                      | 分類         | API                                    | 状態依存 | 備考                                                      |
| ------------------------------------------------------------- | ------------ | -------------------------------------- | -------- | --------------------------------------------------------- |
| 前提: ユーザーがブラウザを開いている                          | フロントのみ | -                                      | なし     | Playwright 起動                                           |
| もし: トップページにアクセスする                              | フロントのみ | -                                      | なし     | `page.goto('/')`                                          |
| かつ: ユーザーが「写真を選択」ボタンをクリックする            | フロントのみ | -                                      | UI 状態  | file input を開く                                         |
| かつ: ユーザーがファイルサイズ 5MB の JPEG ファイルを選択する | フロントのみ | -                                      | UI 状態  | ローカル検証 + プレビュー準備                             |
| ならば: 写真アップロードが成功する                            | **API 依存** | POST /photos                           | UI 状態  | OpenAPI: `docs/spec/api/photos.yaml#/paths/~1photos/post` |
| かつ: プレビューエリアに選択した画像が表示される              | フロントのみ | -                                      | UI 状態  | `previewUrl` など                                         |
| かつ: 「顔検出を実行」ボタンが有効になる                      | フロントのみ | -                                      | UI 状態  | `photoId` 取得後に有効                                    |
| もし: ユーザーが「顔検出を実行」ボタンをクリックする          | **API 依存** | POST /photos/{photoId}/face-detections | UI 状態  | 検出開始                                                  |
| ならば: 顔検出が成功する                                      | **API 依存** | POST /photos/{photoId}/face-detections | UI 状態  | 200 受領                                                  |
| かつ: プレビュー上にランドマークが重ねて表示される            | フロントのみ | -                                      | UI 状態  | canvas オーバーレイ                                       |

## 推奨実装グルーピング

### グループ 1: 顔検出 UI の土台（フロントのみ）

- かつ: 「顔検出を実行」ボタンが有効になる
- もし: ユーザーが「顔検出を実行」ボタンをクリックする（UI イベントだけ先に）
- かつ: プレビュー上にランドマークが重ねて表示される（表示枠だけ先に）

**実装内容**:

- フロント: 顔検出の状態（未実行/検出中/成功/失敗）表示、canvas オーバーレイ枠

**API**: なし

### グループ 2: 顔検出 API 統合（API: POST /photos/{photoId}/face-detections）

- もし: ユーザーが「顔検出を実行」ボタンをクリックする
- ならば: 顔検出が成功する

**実装内容**:

- フロント: API 呼び出し、ローディング/成功/失敗表示
- バックエンド: 画像参照（短命）から検出結果を生成して返す

**API**:

- `POST /api/v1/photos/{photoId}/face-detections`

## Gherkin ステップごとの実装要件

### もし ユーザーが「顔検出を実行」ボタンをクリックする

**Frontend**

- **Components**: 顔検出ボタン
- **State**: `faceDetectionStatus` を `loading` に遷移
- **UI/UX**: ローディング表示（スピナー等）

**Backend API**

- **Endpoint**: `POST /api/v1/photos/{photoId}/face-detections`
- **Request**: path param `photoId`（uuid）

---

### ならば 顔検出が成功する

**Frontend**

- **State**: `faceDetectionResult` を保持
- **UI/UX**: 成功表示

**Backend API**

- **Response**: `200 application/json` で `FaceDetectionResponse`

---

### かつ プレビュー上にランドマークが重ねて表示される

**Frontend**

- **Components**: プレビュー上に canvas を重ねる
- **State**: `faceDetectionResult.landmarks` を描画に反映
- **UI/UX**: デバッグ用途の最小表示（点）

**Backend API**

- なし

---

## データモデル (共通)

**参照**: `docs/spec/models/face_detection.md`

### 追加の DTO (フロントエンド用)

```typescript
export interface FaceLandmarkDto {
  x: number; // 0..1
  y: number; // 0..1
}

export interface FaceBoundingBoxDto {
  xMin: number;
  yMin: number;
  width: number;
  height: number;
}

export interface FaceDetectionResultDto {
  landmarks: FaceLandmarkDto[];
  boundingBox: FaceBoundingBoxDto;
  confidence?: number;
}

export interface FaceDetectionResponseDto {
  result: FaceDetectionResultDto;
}
```

## 技術的課題・リスク

- [ ] `photoId` が短命であるため、期限切れ（404）を UI で適切に扱う必要がある
- [ ] 本シナリオは Protected Photo Data を扱うため、派生データをログに出さない（console/log/例外通知含む）

## 備考

- 既存の写真アップロード（`POST /api/v1/photos`）を前提として使用する。
