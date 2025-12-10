# AIプロンプトタスク

Phase別のAIプロンプトテンプレートです。

## Phase対応表（新フロー）

| Phase | タスク | ファイル |
|-------|--------|----------|
| Phase 1 | 機能仕様(Spec)作成 | `01_create_feature_spec.md` |
| Phase 2 | ドメインモデリング（初稿） | `02_simple_modeling.md` |
| Phase 3 | API Contract設計 + モデル見直し | `03_design_api_contract.md` |
| Phase 4 | Gherkinシナリオ + 実装計画策定 | `04_plan_implementation.md` |
| Phase 5 | Backend Stub生成 | `05_generate_stubs.md` |
| Phase 6 | 縦切り実装サイクル | `06_vertical_slice_implementation.md` |
| Phase 7 | 統合テスト | （DEVELOPMENT.mdに記載） |

## 使用方法

各Phaseで対応するプロンプトファイルを参照し、AIに指示を出してください。

### 例: Phase 1で機能仕様を作成

```bash
# プロンプトファイルを確認
cat docs/ai/prompts/tasks/01_create_feature_spec.md

# AIに依頼
「docs/ai/prompts/tasks/01_create_feature_spec.mdの手順に従って、
顔検出機能の仕様書を作成してください。
対話的にヒアリングしながら進めてください。」
```

### 例: Phase 3でAPI Contract設計

```bash
# プロンプトファイルを確認
cat docs/ai/prompts/tasks/03_design_api_contract.md

# AIに依頼
「docs/ai/prompts/tasks/03_design_api_contract.mdの手順に従って、
photo_upload_download機能のOpenAPI仕様を作成してください」
```

## 参考ドキュメント

- **開発プロセス全体**: `docs/dev/DEVELOPMENT.md`
- **OpenAPIガイド**: `docs/dev/OPENAPI_GUIDELINES.md`
- **縦切り戦略**: `docs/dev/VERTICAL_SLICE_STRATEGY.md`
