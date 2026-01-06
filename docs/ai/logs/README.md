# AI チャットログ（ふりかえり用）

このフォルダには、AI 協働開発のふりかえりに使うログを置きます。

## 方針（おすすめ）

- `chat.json`（生ログ）は巨大になりやすいので、**そのまま共有/貼り付け**せず、用途別に整形して扱います。
- ふりかえりで主に見るのは、次の 2 つです。
  - **読み物用**: 会話本文中心（思考やツール詳細を落とす）
  - **分析用**: ツール呼び出し回数などのメタ情報（本文は最小）

## 生成物

- `chat.compact.json`: 会話本文中心に整形した JSON
- `chat.compact.md`: `chat.compact.json` を読み物向け Markdown に変換したもの
- `chat.stats.json`: ツール呼び出し回数など、ふりかえり用の統計

## 生成方法

生ログを `docs/ai/logs/chat.json` に置いた上で、以下を実行します。

- `bash scripts/ai-chatlog-export.sh docs/ai/logs/chat.json`

### 日付ごとに保存する

- `bash scripts/ai-chatlog-export.sh docs/ai/logs/chat.json --date`
  - 出力先: `docs/ai/logs/YYYY-MM-DD/`

### Spec ごとに保存する

- `bash scripts/ai-chatlog-export.sh docs/ai/logs/chat.json --spec 01_photo_upload`
  - 出力先: `docs/ai/logs/01_photo_upload/`

### 日付 + Spec で保存する（おすすめ）

- `bash scripts/ai-chatlog-export.sh docs/ai/logs/chat.json --date --spec 01_photo_upload`
  - 出力先: `docs/ai/logs/YYYY-MM-DD/01_photo_upload/`

## 注意（データ取り扱い）

- 実在人物を含む写真、個人情報、秘匿情報がログに混入しないよう注意してください。
- 詳細は [dev/policies/data-handling.md](../../dev/policies/data-handling.md) を参照してください。
