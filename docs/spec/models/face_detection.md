# 顔検出（静止画・1 人）ドメインモデル

## 概要

ユーザーがアップロードした静止画（短命な `UploadedPhotoReference.photoId` で参照される入力）から、主要被写体（最も大きい顔）を 1 人分だけ検出し、後続工程へ渡すための **顔ランドマーク** と **顔領域（バウンディングボックス）** を生成する。
本モデルの範囲は、機能仕様 [docs/spec/features/02_face_detection_static.md](../features/02_face_detection_static.md) に明示された「顔検出の実行、成功/失敗の状態、デバッグ表示用の特徴点・矩形を返す」までとする。

補足（マイルストーン設計との整合）:

- M0 は [docs/spec/architecture/m0.md](../architecture/m0.md) の方針（サーバ中心）に基づき、バックエンドが検出を実行し結果を返す。
- 入力画像および派生データ（ランドマーク/バウンディングボックス等）は Protected Photo Data に該当しうるため、[docs/dev/policies/data-handling.md](../../dev/policies/data-handling.md) に従い **非永続/短命/非ログ** を前提とする。

## エンティティ

（この機能仕様の範囲では、永続的なライフサイクルを持つエンティティは定義しない）

## バリューオブジェクト

### FaceLandmark

**説明**: 顔の特徴点（ランドマーク）1 点。座標は入力画像に対する正規化座標（0.0〜1.0）で表す。

**プロパティ**:

- name: String（必須、例: `left_eye`, `right_eye`, `nose`, `left_mouth`, `right_mouth`）
- x: double（必須、`0.0 <= x <= 1.0`）
- y: double（必須、`0.0 <= y <= 1.0`）

**バリデーションルール**:

- `name` は空でない
- `0.0 <= x <= 1.0`
- `0.0 <= y <= 1.0`

### FaceBoundingBox

**説明**: 顔領域の矩形。入力画像に対する正規化座標（0.0〜1.0）で表す。

**プロパティ**:

- xMin: double（必須、`0.0 <= xMin <= 1.0`）
- yMin: double（必須、`0.0 <= yMin <= 1.0`）
- width: double（必須、`0.0 < width <= 1.0`）
- height: double（必須、`0.0 < height <= 1.0`）

**バリデーションルール**:

- `0.0 <= xMin <= 1.0`
- `0.0 <= yMin <= 1.0`
- `0.0 < width <= 1.0`
- `0.0 < height <= 1.0`
- `xMin + width <= 1.0`
- `yMin + height <= 1.0`

### FaceDetectionResult

**説明**: 1 人分の顔検出結果。

**プロパティ**:

- landmarks: List<FaceLandmark>（必須、5 点または 68 点）
- boundingBox: FaceBoundingBox（必須）
- confidence: double（必須、M0 は固定値 0.5）

**バリデーションルール**:

- `landmarks` は空でない
- `landmarks.size()` は 5 または 68
- `boundingBox` は必須
- `0.0 <= confidence <= 1.0`

### FaceDetectionStatus（任意: UI 表示用）

**説明**: UI 表示の状態（検出中 / 成功 / 失敗）を表す。

**値**:

- DETECTING
- SUCCEEDED
- FAILED

※本値は UI での状態表現用途であり、API 契約に必須とはしない（Task03 で必要性を判断）。

## リポジトリインターフェース

### UploadedPhotoDataRepository

**説明**: `photoId` で参照される「アップロード済み写真の短命データ」を取得する。
M0 の方針上、永続化は行わず、短命 TTL の範囲でのみ保持されることを前提とする。

**メソッド**:

- findBytesByPhotoId(photoId: UUID): Optional<byte[]>
  - `photoId` に対応する画像バイナリを取得する（存在しない/期限切れの場合は empty）

## ドメインサービス

（この機能仕様の範囲では、ドメインサービスは必須ではない。顔検出アルゴリズムや外部ライブラリ依存は、アプリケーション層/インフラ層に寄せる）

## 補足・注意事項

- 「最も大きい顔のみを対象」とする要件は、検出で複数候補が得られた場合の選択規則として実装される。
- 画像の向き（EXIF 回転）と、プレビューおよび返却座標系の整合は重要要件だが、どの層が正規化を担うかは実装計画（Task04）で確定する。
