# ドメイン層テストリスト: ならば 写真から顔の特徴点を検出できる

**Feature**: `docs/spec/features/02_face_detection_static.md`  
**Domain Model**: `docs/spec/models/face_detection.md`  
**Plan**: `docs/plans/02_face_detection_static_detect_landmarks.md`

---

## テストリストの目的

縦切り実装サイクル（Task06）の中で、`POST /api/v1/photos/{photoId}/face-detections`（顔ランドマーク検出）を成立させるために必要なドメイン層（ValueObject 相当）のテストケースを列挙します。

---

## 1. Entity/ValueObject

- FaceLandmark

  - 有効なプロパティを受け取ると FaceLandmark が生成される
    - [ ] name, x, y が妥当な場合に生成できる
      - [ ] name="left_eye", x=0.0, y=0.0 で生成できる
      - [ ] name="right_eye", x=1.0, y=1.0 で生成できる
  - バリデーションエラーで生成が失敗する
    - [ ] name が空の場合、IllegalArgumentException が発生する
      - [ ] name="" を受け取ると例外が発生する
      - [ ] name=" " を受け取ると例外が発生する
      - [ ] name=null を受け取ると例外が発生する
    - [ ] x が範囲外の場合、IllegalArgumentException が発生する
      - [ ] x=-0.0001 を受け取ると例外が発生する
      - [ ] x=1.0001 を受け取ると例外が発生する
    - [ ] y が範囲外の場合、IllegalArgumentException が発生する
      - [ ] y=-0.0001 を受け取ると例外が発生する
      - [ ] y=1.0001 を受け取ると例外が発生する

- FaceBoundingBox

  - 有効なプロパティを受け取ると FaceBoundingBox が生成される
    - [ ] xMin, yMin, width, height が妥当な場合に生成できる
      - [ ] xMin=0.0, yMin=0.0, width=1.0, height=1.0 で生成できる
      - [ ] xMin=0.2, yMin=0.3, width=0.6, height=0.5 で生成できる
  - バリデーションエラーで生成が失敗する
    - [ ] width/height が 0 以下の場合、IllegalArgumentException が発生する
      - [ ] width=0.0 を受け取ると例外が発生する
      - [ ] height=0.0 を受け取ると例外が発生する
    - [ ] xMin/yMin が範囲外の場合、IllegalArgumentException が発生する
      - [ ] xMin=-0.0001 を受け取ると例外が発生する
      - [ ] yMin=1.0001 を受け取ると例外が発生する
    - [ ] width/height が範囲外の場合、IllegalArgumentException が発生する
      - [ ] width=1.0001 を受け取ると例外が発生する
      - [ ] height=1.0001 を受け取ると例外が発生する
    - [ ] xMin + width が 1.0 を超える場合、IllegalArgumentException が発生する
      - [ ] xMin=0.8, width=0.21 を受け取ると例外が発生する
    - [ ] yMin + height が 1.0 を超える場合、IllegalArgumentException が発生する
      - [ ] yMin=0.8, height=0.21 を受け取ると例外が発生する

- FaceDetectionResult
  - 有効なプロパティを受け取ると FaceDetectionResult が生成される
    - [ ] landmarks=5 点, boundingBox, confidence=0.5 で生成できる
    - [ ] landmarks=68 点, boundingBox, confidence=0.5 で生成できる
  - バリデーションエラーで生成が失敗する
    - [ ] landmarks が null/空の場合、IllegalArgumentException が発生する
      - [ ] landmarks=null を受け取ると例外が発生する
      - [ ] landmarks=[] を受け取ると例外が発生する
    - [ ] landmarks の点数が 5 または 68 以外の場合、IllegalArgumentException が発生する
      - [ ] landmarks=4 点 を受け取ると例外が発生する
      - [ ] landmarks=69 点 を受け取ると例外が発生する
    - [ ] boundingBox が null の場合、IllegalArgumentException が発生する
      - [ ] boundingBox=null を受け取ると例外が発生する
    - [ ] confidence が範囲外の場合、IllegalArgumentException が発生する
      - [ ] confidence=-0.0001 を受け取ると例外が発生する
      - [ ] confidence=1.0001 を受け取ると例外が発生する
