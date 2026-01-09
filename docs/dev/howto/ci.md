# CI/CD ガイド

このプロジェクトでは、GitHub Actions を使用した段階的な CI パイプラインを構築しています。

## 概要

CI/CD パイプラインは、早期フィードバックと並列実行により、効率的な開発フローを実現します。

### ワークフロー構成

```
PR作成 / mainブランチへのプッシュ
    ↓
┌─────────────────────────────────────┐
│ 1️⃣ PR Checks (3分以内)              │
│  └─ 4ジョブ並列実行                  │
│     ├─ Backend Checkstyle           │
│     ├─ Backend Unit Tests           │
│     ├─ Frontend Lint & TypeCheck    │
│     └─ Frontend Unit Tests          │
└─────────────────────────────────────┘
    ↓ (成功時のみ)
┌─────────────────────────────────────┐
│ 2️⃣ Build & Coverage (3〜5分)        │
│  └─ 4ジョブ並列実行                  │
│     ├─ Backend Coverage (JaCoCo)    │
│     ├─ Frontend Coverage (Vitest)   │
│     ├─ Backend Build                │
│     └─ Frontend Build               │
└─────────────────────────────────────┘
    ↓ (成功時のみ)
┌─────────────────────────────────────┐
│ 3️⃣ Integration & E2E Tests (5〜10分)│
│  └─ 2ジョブ実行                      │
│     ├─ Backend Integration Tests    │
│     └─ E2E Tests (Cucumber+Playwright)│
└─────────────────────────────────────┘
```

### ワークフロー間の依存関係

- **初回実行**: PR 作成時、3 つのワークフローが並列で開始（早期フィードバック）
- **依存実行**: 前段が成功すると、`workflow_run`イベントで次段が自動実行
- **失敗時**: 前段が失敗した場合、後続のワークフローはスキップ

## ワークフロー詳細

### 1. PR Checks (`.github/workflows/pr-checks.yml`)

**目的**: 最速でコード品質の問題を検出

**実行内容**:

- **Backend Checkstyle**: コーディング規約チェック
  - 命名規則、インデント、行長、循環的複雑度など
  - 設定ファイル: `src/backend/checkstyle.xml`
- **Backend Unit Tests**: ユニットテストの実行
  - JUnit 5 + Mockito
- **Frontend Lint & TypeCheck**:
  - ESLint: コード品質チェック
  - TypeScript: 型チェック（`tsc --noEmit`）
- **Frontend Unit Tests**:
  - Vitest + Testing Library

**トリガー**: `pull_request`, `push` (main), `workflow_dispatch`

**キャッシュ**:

- Maven: `~/.m2/repository`
- npm: `node_modules`

---

### 2. Build & Coverage (`.github/workflows/build.yml`)

**目的**: ビルド可能性の検証とカバレッジ測定

**実行内容**:

- **Backend Coverage**: JaCoCo によるカバレッジ測定
  - レポート出力先: `src/backend/target/site/jacoco/`
  - **現在のカバレッジ**: Instructions 84%, Lines 89%, Methods 87%
  - **目標**: 80% (Instructions, Lines, Methods)
  - HelloController: 100% カバレッジ達成 ✨
- **Frontend Coverage**: Vitest によるカバレッジ測定
  - レポート出力先: `src/frontend/coverage/`
  - **現在のカバレッジ**: App.tsx 98.23% (実装コード)
  - **目標**: 80% (Statements, Branches, Functions, Lines)
  - PR 時、カバレッジをコメント投稿（`romeovs/lcov-reporter-action`）
- **Backend Build**: JAR ファイルの作成（テストスキップ）
- **Frontend Build**: プロダクションビルド

**トリガー**: `pull_request`, `push` (main), `workflow_dispatch`, `workflow_run` (PR Checks 完了時)

**成果物保存**: 3 日間保存

- カバレッジレポート（Backend/Frontend）
- ビルド成果物（JAR ファイル、dist フォルダ）

---

### 3. Integration & E2E Tests (`.github/workflows/integration-e2e.yml`)

**目的**: システム全体の動作確認

**実行内容**:

- **Backend Integration Tests**:
  - Testcontainers を使用した PostgreSQL との統合テスト
  - `mvn verify` で実行
- **E2E Tests**:
  - `docker-compose.ci.yml`で環境構築（本番用 Dockerfile 使用）
  - Cucumber + Playwright でブラウザテスト
  - 失敗時、スクリーンショットと動画を保存

**トリガー**: `pull_request`, `push` (main), `workflow_dispatch`, `workflow_run` (Build 完了時)

**Docker 最適化**:

- buildx キャッシュを使用してビルド時間短縮
- PostgreSQL は tmpfs（メモリ）で動作（高速化）

**成果物保存**: 3 日間保存

- テストレポート
- スクリーンショット（失敗時）
- 動画（失敗時）

---

## ローカルでの CI テスト

### act を使用（推奨）

GitHub Actions をローカルで実行できるツール。

```bash
# actのインストール（初回のみ）
curl -s https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# PR Checksをテスト
./bin/act push -W .github/workflows/pr-checks.yml \
  -P ubuntu-latest=quay.io/jamezp/act-maven \
  --artifact-server-path /workspaces/act-artifacts

# Buildをテスト
./bin/act push -W .github/workflows/build.yml \
  -P ubuntu-latest=quay.io/jamezp/act-maven \
  --artifact-server-path /workspaces/act-artifacts

# Integration & E2Eをテスト
./bin/act push -W .github/workflows/integration-e2e.yml \
  -P ubuntu-latest=quay.io/jamezp/act-maven \
  --artifact-server-path /workspaces/act-artifacts

# 全ワークフローを一度にテスト
./bin/act push \
  -P ubuntu-latest=quay.io/jamezp/act-maven \
  --artifact-server-path /workspaces/act-artifacts
```

**注意**:

- `pull_request`イベントではなく`push`イベントでテストする（act の制限）
- Docker 環境が必要

---

## CI 専用設定ファイル

### docker-compose.ci.yml

CI 環境用の Docker Compose 設定。開発用（`docker-compose.yml`）との違い：

| 項目           | 開発用           | CI 用                    |
| -------------- | ---------------- | ------------------------ |
| Dockerfile     | `Dockerfile.dev` | `Dockerfile` (本番用)    |
| ソースマウント | あり             | なし（イメージに含める） |
| ホットリロード | 有効             | 無効                     |
| PostgreSQL     | 永続化ボリューム | tmpfs（メモリ）          |
| ポート公開     | 3000, 8080       | 3000, 8080               |

### scripts/wait-for-services.sh

docker-compose 起動後、サービスが準備完了するまで待機するスクリプト。

```bash
# 使用例
TIMEOUT=120 ./scripts/wait-for-services.sh

# 環境変数で設定変更可能
FRONTEND_URL=http://localhost:3000
BACKEND_URL=http://localhost:8080/api/v1/hello
TIMEOUT=60
```

---

## カバレッジ設定

### Backend（JaCoCo）

**設定ファイル**: `src/backend/pom.xml`

現在の設定：

- ユニットテスト・統合テストの両方を測定
- レポート形式: HTML, XML
- **閾値チェック**: デフォルト無効（測定のみ）

カバレッジレポートの確認：

```bash
cd src/backend
mvn clean test jacoco:report  # レポート生成（測定のみ）

# 閾値ゲートを有効化したい場合
mvn clean verify -Pcoverage-gate
open target/site/jacoco/index.html
```

### Frontend（Vitest）

**設定ファイル**: `src/frontend/vite.config.ts`

現在の設定：

- カバレッジプロバイダー: v8
- レポート形式: text, json, html, lcov
- 除外: `node_modules/`, テストファイル, 型定義ファイル, `.eslintrc.cjs`, `main.tsx`
- **閾値設定**: デフォルトなし（測定のみ）

カバレッジレポートの確認：

```bash
cd src/frontend
npm run test:coverage
open coverage/index.html
```

---

## トラブルシューティング

### ワークフローが失敗する

1. **ローカルで再現を試みる**

   ```bash
   ./bin/act push -W .github/workflows/[失敗したワークフロー].yml \
     -P ubuntu-latest=quay.io/jamezp/act-maven \
     --artifact-server-path /workspaces/act-artifacts
   ```

2. **ログを確認**

   - GitHub Actions の UI で詳細ログを確認
   - 特定のステップをクリックして展開

3. **キャッシュをクリア**
   - GitHub リポジトリの Settings → Actions → Caches
   - または、ワークフローファイルのキャッシュキーを変更

### docker-compose.ci.yml が起動しない

```bash
# ローカルで動作確認
docker compose -f docker-compose.ci.yml build
docker compose -f docker-compose.ci.yml up

# ログ確認
docker compose -f docker-compose.ci.yml logs

# クリーンアップ
docker compose -f docker-compose.ci.yml down -v
```

### Checkstyle エラー

```bash
cd src/backend
mvn checkstyle:check

# 詳細レポート確認
open target/checkstyle-result.xml
```

設定ファイル: `src/backend/checkstyle.xml`

### ESLint エラー

```bash
cd src/frontend
npm run lint

# 自動修正
npm run lint:fix
```

設定ファイル: `src/frontend/.eslintrc.cjs`

---

## カスタマイズ

### カバレッジ閾値の設定

#### Backend（JaCoCo）

`src/backend/pom.xml`に以下を追加：

```xml
<execution>
    <id>check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

#### Frontend（Vitest）

`src/frontend/vitest.config.ts`の`coverage`セクションに追加：

```typescript
coverage: {
  // ...
  thresholds: {
    lines: 80,
    functions: 80,
    branches: 80,
    statements: 80,
  },
}
```

### ワークフローの実行条件を変更

例：PR のみで E2E を実行しない場合

`.github/workflows/integration-e2e.yml`:

```yaml
on:
  push:
    branches: [main]
  workflow_dispatch:
  # pull_request を削除
```

---

## 参考資料

- [GitHub Actions ドキュメント](https://docs.github.com/ja/actions)
- [act - ローカルで Actions を実行](https://github.com/nektos/act)
- [JaCoCo - Java コードカバレッジ](https://www.jacoco.org/jacoco/)
- [Vitest - ユニットテストフレームワーク](https://vitest.dev/)
- [Testcontainers - 統合テスト](https://testcontainers.com/)
