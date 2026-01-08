# OpenCV LBF モデル（LBF_68 用）

このディレクトリには、LBF_68（精密モード）で必要な `lbfmodel.yaml` を配置します。

- モデルファイルはサイズが大きいため、リポジトリにはコミットしません
- 代わりにスクリプトでダウンロードして配置します

```bash
bash ./scripts/download-opencv-lbf-model.sh
```

ダウンロード後は `docker compose up --build` で Backend が自動的に `/models/lbfmodel.yaml` を参照します。
