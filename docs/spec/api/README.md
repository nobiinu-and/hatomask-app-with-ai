# API 仕様（OpenAPI）

このディレクトリには、API 契約（OpenAPI 仕様: YAML）を配置します。

原則として **リソース/ドメイン境界** を表す単位で 1 ファイルにまとめ、feature 名に引きずられて API 境界が分断されないようにします。

- **保存先**: `docs/spec/api/{api_name}.yaml`（迷う場合のフォールバック: `{feature_name}.yaml`）
- **テンプレート**: `docs/spec/templates/openapi.template.yaml`

## 目的

- フロントエンドとバックエンドの **契約（Contract）** を先に確定する
- 実装（スタブ生成/本実装）・テストの基準点を作る

## 関連

- OpenAPI の書き方（規約）: `docs/dev/standards/openapi.md`
- 開発タスクの正本: `docs/ai/prompts/tasks/`（01〜06）
