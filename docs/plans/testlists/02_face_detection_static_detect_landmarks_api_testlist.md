# API 層テストリスト: ならば 写真から顔の特徴点を検出できる

**Feature**: `docs/spec/features/02_face_detection_static.md`  
**Domain Model**: `docs/spec/models/face_detection.md`  
**Plan**: `docs/plans/02_face_detection_static_detect_landmarks.md`  
**MSW Handler**: `src/frontend/src/test/mocks/handlers.ts`（現時点では face-detections のハンドラ未実装）

---

## テストリストの目的

縦切り実装サイクル（Task06）の中で、`POST /api/v1/photos/{photoId}/face-detections` の API 層（UseCase, Controller, DTO マッピング）のテストケースを列挙し、OpenAPI と互換性のあるレスポンス形式を担保します。

参照（OpenAPI）:

- `docs/spec/api/photos.yaml#/paths/~1photos~1%7BphotoId%7D~1face-detections/post`
- `docs/spec/api/photos.yaml#/components/schemas/FaceDetectionResponse`

---

## 1. UseCase 層

- DetectFaceLandmarksUseCase
  - photoId を受け取ると顔検出結果（landmarks, boundingBox, confidence）を返す
    - [ ] 保存済みの画像を取得できる場合、FaceDetectionResult を返す
      - [ ] landmarkMethod=DUMMY_5 の場合、landmarks が 5 点である
      - [ ] 返却される座標は正規化座標（0.0〜1.0）である
    - [ ] 存在しない photoId の場合、PhotoNotFoundException が発生する
      - [ ] Repository が empty を返すと例外が発生する
    - [ ] 顔が検出できない場合、FaceNotDetectedException が発生する
      - [ ] 画像デコードに失敗すると例外が発生する
      - [ ] 顔検出結果が 0 件の場合に例外が発生する

---

## 2. Controller 層

- PhotoController
  - POST /api/v1/photos/{photoId}/face-detections は有効なリクエストを受け取ると 200 と FaceDetectionResponse を返す
    - [ ] 有効な UUID の photoId を受け取ると 200 OK を返す
      - [ ] Content-Type が "application/json" である
      - [ ] レスポンスボディは result.landmarks[], result.boundingBox, result.confidence を持つ
      - [ ] landmarks の name が OpenAPI の例（left_eye 等）と一致する
      - [ ] boundingBox のキーが xMin, yMin, width, height である
  - POST /api/v1/photos/{photoId}/face-detections は不正な入力/処理不能を適切に返す
    - [ ] 不正な UUID の場合、400 + application/problem+json を返す
      - [ ] errors[0].field が "photoId" である
    - [ ] photoId が存在しない場合、404 + application/problem+json を返す
      - [ ] title が "Not Found" である
    - [ ] 顔が検出できない場合、422 + application/problem+json を返す
      - [ ] title が "Unprocessable Entity" である
      - [ ] errors[0].field が "face" である

---

## 3. 統合テスト（既存確認）

- FaceDetection API Integration
  - [ ] 不正な UUID の場合 400 を返す（既存: `FaceDetectionControllerIntegrationTest`）
  - [ ] 存在しない photoId の場合 404 を返す（既存: `FaceDetectionControllerIntegrationTest`）

※成功系の統合テストは OpenCV のカスケード/モデルファイル依存があるため、必要になった時点で追加（CI 環境含めて実行可能性を確認してから）
