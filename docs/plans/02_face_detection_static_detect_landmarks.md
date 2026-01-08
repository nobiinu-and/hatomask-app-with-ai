# 実装計画: 写真から顔の特徴点を検出できる

## 基本情報

- **Feature**: 顔検出（静止画・1 人）
- **Scenario**: 写真から顔の特徴点を検出できる
- **Spec**: `docs/spec/features/02_face_detection_static.md`

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

| Spec(OpenAPI)                         | エンドポイント                           | メソッド | 概要                 | 使用ステップ               |
| ------------------------------------- | ---------------------------------------- | -------- | -------------------- | -------------------------- |
| `docs/spec/api/photos.yaml` (Primary) | /api/v1/photos                           | POST     | 写真アップロード     | 写真アップロードが成功する |
| `docs/spec/api/photos.yaml` (Primary) | /api/v1/photos/{photoId}/face-detections | POST     | 顔ランドマークを検出 | 顔検出が成功する           |

参照箇所（JSON Pointer）:

- `docs/spec/api/photos.yaml#/paths/~1photos/post`
- `docs/spec/api/photos.yaml#/paths/~1photos~1%7BphotoId%7D~1face-detections/post`

### 主要スキーマ

- **PhotoUploadResponse**: `photoId`, `mimeType`, `fileSizeBytes`, `dimensions{width,height}`, `expiresAt`
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/PhotoUploadResponse`
- **FaceDetectionResponse**: `result{landmarks[], boundingBox, confidence}`
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/FaceDetectionResponse`
- **ProblemDetails**: RFC 9457 準拠のエラーレスポンス
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/ProblemDetails`

## ステップ別実装分類

| ステップ                                                     | 分類         | API                                    | 状態依存 | 備考                                                                                      |
| ------------------------------------------------------------ | ------------ | -------------------------------------- | -------- | ----------------------------------------------------------------------------------------- |
| Given: ユーザーがブラウザを開いている                        | フロントのみ | -                                      | なし     | E2E セットアップ                                                                          |
| When: トップページにアクセスする                             | フロントのみ | -                                      | なし     | 画面遷移                                                                                  |
| And: ユーザーが「写真を選択」ボタンをクリックする            | フロントのみ | -                                      | UI 状態  | file input を開く                                                                         |
| And: ユーザーがファイルサイズ 5MB の JPEG ファイルを選択する | フロントのみ | -                                      | UI 状態  | ローカル検証、プレビュー URL を生成                                                       |
| Then: 写真アップロードが成功する                             | **API 依存** | POST /photos                           | UI 状態  | OpenAPI: `docs/spec/api/photos.yaml#/paths/~1photos/post`                                 |
| And: プレビューエリアに選択した画像が表示される              | フロントのみ | -                                      | UI 状態  | `previewUrl` がある                                                                       |
| And: 「顔検出を実行」ボタンが有効になる                      | フロントのみ | -                                      | UI 状態  | `photoId` を保持していること                                                              |
| When: ユーザーが「顔検出を実行」ボタンをクリックする         | フロントのみ | -                                      | UI 状態  | 顔検出 API 呼び出し開始                                                                   |
| Then: 顔検出が成功する                                       | **API 依存** | POST /photos/{photoId}/face-detections | UI 状態  | OpenAPI: `docs/spec/api/photos.yaml#/paths/~1photos~1%7BphotoId%7D~1face-detections/post` |
| And: 5 つの特徴点がプレビュー上に表示される                  | フロントのみ | -                                      | UI 状態  | 返却されたランドマークで描画                                                              |
| And: 顔領域（矩形）がプレビュー上に表示される                | フロントのみ | -                                      | UI 状態  | 返却された boundingBox で描画                                                             |

## 推奨実装グルーピング

### グループ 1: UI 状態の整備（フロントのみ）

- ユーザーが「写真を選択」ボタンをクリックする
- ユーザーがファイルサイズ 5MB の JPEG ファイルを選択する
- プレビューエリアに選択した画像が表示される

**実装内容**:

- 既存のプレビュー機能を維持しつつ、後続 API 用に「選択ファイル」「アップロード結果」を状態として保持できる形にする

**API**: なし

### グループ 2: アップロード API 統合（API: POST /photos）

- 写真アップロードが成功する
- 「顔検出を実行」ボタンが有効になる

**実装内容**:

- フロント: `POST /api/v1/photos` のレスポンス JSON を保持し、`photoId` を次のステップへ渡す
- バックエンド: 既存（変更が必要なら Task06 で対応）

**API**:

- `POST /api/v1/photos`

### グループ 3: 顔検出 API 統合と描画（API: POST /photos/{photoId}/face-detections）

- ユーザーが「顔検出を実行」ボタンをクリックする
- 顔検出が成功する
- 5 つの特徴点がプレビュー上に表示される
- 顔領域（矩形）がプレビュー上に表示される

**実装内容**:

- フロント: ローディング表示、エラー表示（顔検出できない場合）、成功時にランドマークと矩形をオーバーレイ表示
- バックエンド: OpenAPI に準拠した顔検出エンドポイントを実装

**API**:

- `POST /api/v1/photos/{photoId}/face-detections`

## Gherkin ステップごとの実装要件

### And 「顔検出を実行」ボタンが有効になる

**Frontend**

- **Components**: 顔検出ボタン
- **State**: アップロード成功後に `photoId` を保持
- **UI/UX**: `photoId` がある場合に enabled（アップロード失敗/未実行なら disabled）

**Backend API**

- なし

---

### When ユーザーが「顔検出を実行」ボタンをクリックする

**Frontend**

- **State**: 検出中フラグ（ローディング）を保持
- **UI/UX**: 検出中はスピナー等を表示（仕様に従う）

**Backend API**

- **Endpoint**: `POST /api/v1/photos/{photoId}/face-detections`
- **Request**: path `photoId`
- **Response**: `200` + `FaceDetectionResponse`

---

### Then 顔検出が成功する

**Frontend**

- **State**: `FaceDetectionResult`（landmarks, boundingBox, confidence）を保持
- **UI/UX**: 成功状態を表示する

**Backend API**

- **Endpoint**: `POST /api/v1/photos/{photoId}/face-detections`
- **Response**: `200` + `FaceDetectionResponse`
- **Error Response**:
  - `404`（photoId 不明/期限切れ等）
  - `422`（顔が検出できない）
  - `500`（予期しないエラー）

---

### And 5 つの特徴点がプレビュー上に表示される

**Frontend**

- **UI/UX**: 画像プレビュー上にランドマークをマーカーで表示できる

---

### And 顔領域（矩形）がプレビュー上に表示される

**Frontend**

- **UI/UX**: 画像プレビュー上に顔領域（矩形）を表示できる

---

## データモデル (共通)

- Primary: `docs/spec/models/face_detection.md`
- Related: `docs/spec/models/photo_upload.md`

### 追加の DTO (フロントエンド用)

```typescript
export interface FaceLandmarkDto {
  name: string;
  x: number;
  y: number;
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
  confidence: number;
}

export interface FaceDetectionResponseDto {
  result: FaceDetectionResultDto;
}
```

## 技術的課題・リスク

- [ ] 画像プレビュー（object-fit: contain）と、正規化座標（0〜1）の描画座標の変換がずれる可能性
- [ ] EXIF 回転の正規化がフロント/バックのどちらで行われるかが曖昧（実装前に決める必要）
- [ ] 顔検出エンジン（OpenCV/JavaCV）依存の導入と CI 実行時間

## 備考

- 仕様には「ブラウザで顔検出」想定の記述があるが、M0 アーキテクチャ（サーバ中心）および技術仕様に従い、API を通して検出結果を返す前提で実装する（齟齬があれば相談する）。
