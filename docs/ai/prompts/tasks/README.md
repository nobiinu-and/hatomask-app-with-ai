# AI プロンプトタスク

Task 別の AI プロンプトテンプレートです。

## Task 対応表（新フロー）

| Task   | 内容                                  | ファイル                               |
| ------ | ------------------------------------- | -------------------------------------- |
| Task00 | マイルストーン指定 アーキテクチャ設計 | `00_architecture_design.md` |
| Task01 | 機能仕様作成                          | `01_create_feature_spec.md`            |
| Task02 | ドメインモデリング（初稿）            | `02_simple_modeling.md`                |
| Task03 | API Contract 設計 + モデル見直し      | `03_design_api_contract.md`            |
| Task04 | Gherkin シナリオ + 実装計画策定       | `04_plan_implementation.md`            |
| Task05 | Backend Stub 生成                     | `05_generate_stubs.md`                 |
| Task06 | 縦切り実装サイクル                    | `06_vertical_slice_implementation.md`  |

## 使用方法

各 Task で対応するプロンプトファイル（01〜06）を参照し、AI に指示を出してください。

### 例: Task01 で機能仕様を作成

```bash
# プロンプトファイルを確認
cat docs/ai/prompts/tasks/01_create_feature_spec.md

# AIに依頼
「docs/ai/prompts/tasks/01_create_feature_spec.mdの手順に従って、
顔検出機能の仕様書を作成してください。
対話的にヒアリングしながら進めてください。」
```

### 例: Task03 で API Contract 設計

```bash
# プロンプトファイルを確認
cat docs/ai/prompts/tasks/03_design_api_contract.md

# AIに依頼
「docs/ai/prompts/tasks/03_design_api_contract.mdの手順に従って、
photo_upload_download機能のOpenAPI仕様を作成してください」
```

## 参考ドキュメント

- **開発プロセス全体（補足）**: `docs/dev/howto/development.md`
- **OpenAPI ガイド**: `docs/dev/standards/openapi.md`
- **縦切り戦略**: `docs/dev/guidelines/vertical-slice.md`
