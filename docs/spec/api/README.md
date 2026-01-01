# API 仕様（OpenAPI）

このディレクトリには、機能ごとの OpenAPI 仕様（YAML）を配置します。

- **保存先**: `docs/spec/api/{feature_name}.yaml`
- **テンプレート**: `docs/spec/templates/openapi.template.yaml`

## 目的

- フロントエンドとバックエンドの **契約（Contract）** を先に確定する
- 実装（スタブ生成/本実装）・テストの基準点を作る

## 関連

- OpenAPI の書き方（規約）: `docs/dev/standards/openapi.md`
- 開発タスクの正本: `docs/ai/prompts/tasks/`（01〜06）
