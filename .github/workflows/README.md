# GitHub Actions Workflows

このディレクトリには、CI/CDパイプラインのワークフロー定義が含まれています。

## ワークフロー一覧

### 1. [pr-checks.yml](pr-checks.yml)
**PR Checks** - 最速のフィードバック

- Backend Checkstyle
- Backend Unit Tests  
- Frontend Lint & TypeCheck
- Frontend Unit Tests

実行時間: 約3分

### 2. [build.yml](build.yml)
**Build & Coverage** - ビルド検証とカバレッジ測定

- Backend Coverage (JaCoCo)
- Frontend Coverage (Vitest + PRコメント)
- Backend Build
- Frontend Build

実行時間: 約3〜5分

### 3. [integration-e2e.yml](integration-e2e.yml)
**Integration & E2E Tests** - システム全体の動作確認

- Backend Integration Tests (Testcontainers)
- E2E Tests (Cucumber + Playwright)

実行時間: 約5〜10分

## 依存関係

```
PR Checks → Build & Coverage → Integration & E2E Tests
```

各ワークフローは、前段が成功した場合のみ次段を実行します（`workflow_run`イベント使用）。

## 詳細ドキュメント

詳しい使い方やトラブルシューティングは [docs/dev/howto/ci.md](../../docs/dev/howto/ci.md) を参照してください。
