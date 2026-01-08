# 技術仕様: 顔検出実装

## 概要

このドキュメントは、機能仕様 [02_face_detection_static.md](../features/02_face_detection_static.md) を実装するための技術詳細を記載しています。

実装クラス: `com.hatomask.application.usecase.DetectFaceLandmarksUseCase`

## 実装方針

### 顔検出エンジン

- **エンジン**: Haar Cascade Classifier（OpenCV）
- **ライブラリ**: JavaCV（Java バインディング）
- **モデル**: `haarcascade_frontalface_default.xml`
  - クラスパス → システム標準パス の優先順で探索

### ランドマーク検出方式

2 つのモードをサポートし、環境変数で切り替え可能：

| モード  | 名称       | ランドマーク数 | 特徴                         | デフォルト |
| ------- | ---------- | -------------- | ---------------------------- | ---------- |
| DUMMY_5 | 高速モード | 5 点           | 合成（標準顔比率）、高速処理 | ×          |
| LBF_68  | 精密モード | 68 点          | LBF モデル、精密検出         | ○          |

**設定方法**:

```bash
# JVM プロパティ
-Dhatomask.faceDetection.landmarkMethod=DUMMY_5

# または環境変数
export HATOMASK_FACE_DETECTION_LANDMARK_METHOD=LBF_68
```

**デフォルト**: `LBF_68`（精密モード）

## ダミー 5 点ランドマークの座標（高速モード）

顔バウンディングボックスを基準とした正規化座標（0.0 ～ 1.0）：

| ランドマーク          | X 位置 | Y 位置 | 説明                               |
| --------------------- | ------ | ------ | ---------------------------------- |
| 左目（left_eye）      | 0.30   | 0.35   | 顔幅の左 30%、顔高さの 35%         |
| 右目（right_eye）     | 0.70   | 0.35   | 顔幅の右 30%（対称）、顔高さの 35% |
| 鼻（nose）            | 0.50   | 0.55   | 顔中央、顔高さの 55%               |
| 左口角（left_mouth）  | 0.35   | 0.75   | 口の左側、顔高さの 75%             |
| 右口角（right_mouth） | 0.65   | 0.75   | 口の右側、顔高さの 75%             |

**根拠**: Farkas et al. の顔幾何学研究に基づく標準顔比率

## 処理フロー

```
入力: byte[] (画像バイナリ)
  ↓
1. 画像デコード (JPEG/PNG → Mat)
  ↓
2. グレースケール変換 (BGR → Gray)
  ↓
3. ヒストグラム均等化 (対比度改善)
  ↓
4. Haar Cascade で顔検出 (複数候補検出)
  ↓
5. 最大面積の顔を選択
  │ (理由: 主要被写体は通常、写真内で最大の顔)
  ↓
6. ランドマーク検出
  ├─ DUMMY_5 モード: 標準比率で5点を合成
  └─ LBF_68 モード: LBF モデルで68点を検出
  ↓
7. 座標正規化 (0.0～1.0 の範囲に変換)
  ↓
出力: FaceDetectionResult
  - landmarks: List<FaceLandmark> (5点 or 68点)
  - boundingBox: FaceBoundingBox (正規化座標)
  - confidence: double (現在は固定値 0.5)
```

## API インターフェース

### UseCase

```java
@Service
public class DetectFaceLandmarksUseCase {
    public FaceDetectionResult execute(UUID photoId)
        throws PhotoNotFoundException, FaceNotDetectedException;
}
```

### 入力

- `photoId`: UUID - 処理対象の写真 ID（事前にアップロード済み）

### 出力

```java
public class FaceDetectionResult {
    List<FaceLandmark> landmarks;    // 5点 or 68点
    FaceBoundingBox boundingBox;     // 顔領域
    double confidence;               // 信頼度（M0: 固定値 0.5）
}

public class FaceLandmark {
    String name;                     // "left_eye", "nose" など
    double x;                        // 正規化座標 (0.0～1.0)
    double y;                        // 正規化座標 (0.0～1.0)
}

public class FaceBoundingBox {
    double xMin, yMin, width, height; // 正規化座標
}
```

## エラーハンドリング

### `FaceNotDetectedException`

以下の場合にスロー：

- 画像のデコードに失敗した
- Haar Cascade で顔が検出されなかった
- LBF モデルでランドマークを検出できなかった

**ユーザーへの表示**: 「顔を検出できませんでした」メッセージ

### `PhotoNotFoundException`

指定された `photoId` が見つからない場合

**ユーザーへの表示**: 「写真が見つかりません」メッセージ

### `IllegalStateException`

内部エラー（開発時のデバッグに使用）：

- ランドマーク数が予期と異なる
- 設定値が不正
- OpenCV ライブラリが見つからない

## 環境依存な設定

### Cascade ファイル（haarcascade_frontalface_default.xml）

**探索順序**:

1. JVM プロパティ `-Dhatomask.opencv.cascadePath=<path>`
2. 環境変数 `HATOMASK_OPENCV_CASCADE_PATH`
3. クラスパス（`org.bytedeco.opencv:opencv-core` に含まれる）
4. システム標準パス
   - `/usr/share/opencv4/haarcascades/haarcascade_frontalface_default.xml`
   - `/usr/local/share/opencv/haarcascades/haarcascade_frontalface_default.xml`
   - など

### LBF モデル（LBF_68 モード使用時）

LBF モデル（`lbfmodel.yaml`）はサイズが大きいため、リポジトリには同梱しません。
以下のスクリプトでダウンロードして利用します：

```bash
bash ./scripts/download-opencv-lbf-model.sh
```

**設定キー**:

```bash
-Dhatomask.opencv.lbfModelPath=<path>
export HATOMASK_OPENCV_LBF_MODEL_PATH=<path>
```

docker compose 利用時の例（モデルファイルは手元で用意してマウント）：

```yaml
services:
  backend:
    volumes:
      - ./dev/opencv-models:/models:ro
    environment:
      - HATOMASK_FACE_DETECTION_LANDMARK_METHOD=LBF_68
      - HATOMASK_OPENCV_LBF_MODEL_PATH=/models/lbfmodel.yaml
```

## Milestone 0 での制限事項

| 項目                   | 状態                   | 将来の改善                      |
| ---------------------- | ---------------------- | ------------------------------- |
| **信頼度スコア**       | 固定値 0.5             | M1 以降：実際の信頼度を計算     |
| **顔の向き推定**       | 未実装                 | M1 以降：顔の回転角度を推定     |
| **ランドマーク安定化** | 未実装                 | M1 以降：時系列でスムージング   |
| **複数顔対応**         | 未実装（大きい顔のみ） | M2 以降：複数顔の同時検出・処理 |

## 参考資料

- OpenCV Face Detection: https://docs.opencv.org/master/db/d28/tutorial_cascade_classifier.html
- JavaCV: https://github.com/bytedeco/javacv
- Farkas et al. (顔幾何学): 標準顔比率の根拠

## テスト戦略

### ユニットテスト（ドメインロジック）

```java
// 例：複数顔の選定ロジック
@Test
void selectLargestFace() {
    // 3つの顔候補があるとき、面積最大のものが選ばれること
}

// 例：ダミー5点ランドマーク
@Test
void dummyFivePointLandmarks() {
    // 指定座標比率で正確に配置されること
}
```

### テスト画像

- `test-fixtures/face-single.jpg`: 顔 1 つ、正面向き
- `test-fixtures/face-multiple.jpg`: 複数顔
- `test-fixtures/no-face.jpg`: 顔なし
- `test-fixtures/face-profile.jpg`: 横顔（検出困難）
