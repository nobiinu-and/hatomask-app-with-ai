# 顔検出（静止画・1 人）ドメインモデル

## 概要

ユーザーがアップロードした静止画から、1 人分の顔ランドマーク（点群）を検出し、後続工程（3D 描画/マスク配置）の入力として返す。

本モデルは `docs/spec/features/02_face_detection_static.md` に明示されている「静止画の顔検出（1 人）」の要件のみを対象とし、複数人同時検出やリアルタイム追従は扱わない。

## エンティティ

本機能は「派生データ（顔ランドマーク等）を永続化しない」前提のため、永続ライフサイクルを持つエンティティは定義しない。

## バリューオブジェクト

### FaceDetectionResult

**説明**: 静止画から推定した 1 人分の顔検出結果。後続処理に渡せる形で、ランドマークと簡易情報（バウンディングボックス等）を持つ。

**プロパティ**:

- landmarks: FaceLandmarks（必須）
- boundingBox: FaceBoundingBox（任意: 検出できた場合）
- confidence: ConfidenceScore（任意: 取得可能な場合）

**バリデーションルール**:

- landmarks: 1 点以上を含む
- landmarks: 各点は画像座標系（正規化またはピクセル）において妥当な範囲である

### FaceLandmarks

**説明**: 顔ランドマーク点の集合。

**プロパティ**:

- points: FaceLandmarkPoint[]（必須、1 以上）

### FaceLandmarkPoint

**説明**: 顔ランドマークの 1 点（2D）。

**プロパティ**:

- x: number（必須）
- y: number（必須）

**補足**:

- 座標系は「正規化座標（0..1）」または「ピクセル座標」のいずれかを採用する。
- マイルストーン 0 では、後続処理で扱いやすい一貫した座標系（例: 正規化）を優先する。

### FaceBoundingBox

**説明**: 顔領域の矩形。

**プロパティ**:

- xMin: number（必須）
- yMin: number（必須）
- width: number（必須）
- height: number（必須）

### ConfidenceScore

**説明**: 検出結果の信頼度（取得可能な場合）。

**プロパティ**:

- value: number（必須、0..1）

**バリデーションルール**:

- 0 <= value <= 1

## リポジトリインターフェース

本機能仕様は検出結果の永続化を要求しないため、`FaceDetectionResult` を永続化するリポジトリは定義しない。

## ドメインサービス

### FaceDetector

**説明**: 入力画像から顔検出結果を生成する。

**メソッド**:

- detect(image): FaceDetectionResult | null
  - 画像から 1 人分の結果を返す（検出できない場合は null）

## 補足・注意事項

- デバッグ用途であっても、ランドマーク等の派生データを永続ログに出力しない（仕様: `docs/spec/features/02_face_detection_static.md`）。
- 複数の顔が検出された場合は、最も確度が高い 1 件を採用する（本モデルでは採用結果のみを扱う）。
