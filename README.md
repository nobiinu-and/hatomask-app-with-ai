# AI協働開発テンプレート（HatoMask App）

[![PR Checks](https://github.com/nobiinu-and/hatomask-app-with-ai/actions/workflows/pr-checks.yml/badge.svg)](https://github.com/nobiinu-and/hatomask-app-with-ai/actions/workflows/pr-checks.yml)
[![Build & Coverage](https://github.com/nobiinu-and/hatomask-app-with-ai/actions/workflows/build.yml/badge.svg)](https://github.com/nobiinu-and/hatomask-app-with-ai/actions/workflows/build.yml)
[![Integration & E2E Tests](https://github.com/nobiinu-and/hatomask-app-with-ai/actions/workflows/integration-e2e.yml/badge.svg)](https://github.com/nobiinu-and/hatomask-app-with-ai/actions/workflows/integration-e2e.yml)

[![GitHub issues](https://img.shields.io/github/issues/nobiinu-and/hatomask-app-with-ai)](https://github.com/nobiinu-and/hatomask-app-with-ai/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/nobiinu-and/hatomask-app-with-ai)](https://github.com/nobiinu-and/hatomask-app-with-ai/pulls)
[![License](https://img.shields.io/github/license/nobiinu-and/hatomask-app-with-ai)](./LICENSE)

AIと協働で**再現可能**かつ**高品質**なソフトウェア開発を実現することを目指したテンプレートリポジトリです。

## このリポジトリについて

このリポジトリは、 **HatoMask App**（写真の顔をハトマスクに入れ替えるWebアプリ）を題材に、AI協働開発の考え方やプロセスを試行しながら理解するためのテンプレートです。

**活用方法**:
- このリポジトリをテンプレートとして使用し、独自のプロジェクトを開始できます
- AIプロンプト集（`docs/ai/prompts/`）を自分のプロジェクトに適用できます
- 開発フロー（`docs/dev/`）やテンプレート（`docs/spec/templates/`）をそのまま活用できます

**対象者**:
- AIとの協働開発で品質と効率を両立させたい開発者・チーム
- GitHub Copilot、Claude、ChatGPTなどのAIツールを活用したい方
- ドキュメント駆動開発、TDD、Clean Architectureに興味がある方

## サンプルアプリケーション：HatoMask App

このテンプレートで開発するサンプルアプリケーションは、写真の顔をハトマスクに入れ替えるWebアプリです。

**主要機能**:

1. 写真のアップロード
2. 顔の自動検出
3. ハトマスクの適用
4. プレビューと調整
5. 処理済み画像のダウンロード

これらの機能を通じて、AI協働開発の各フェーズ（仕様作成、モデリング、API設計、実装、テスト）を実践的に学べます。

**技術スタック**: React + TypeScript / Spring Boot + PostgreSQL / Clean Architecture

詳細な仕様や要件は [docs/spec/README.md](./docs/spec/README.md) を参照してください。

## CI/CD

このプロジェクトは、GitHub Actionsによる3段階のCIパイプラインを採用しています。

### パイプライン構成

1. **PR Checks** (3分) - 静的解析 + ユニットテスト
2. **Build & Coverage** (3〜5分) - ビルド + カバレッジ測定（閾値80%）
3. **Integration & E2E Tests** (5〜10分) - 統合テスト + E2Eテスト

**カバレッジ閾値**: バックエンド・フロントエンド共に **80%** を設定
- カバレッジが閾値を下回るとビルドが失敗します
- PR時、カバレッジレポートが自動的にコメントされます

詳細は [CI/CDガイド](docs/dev/CI.md) を参照してください。

### ローカルでのCIテスト

```bash
# actを使用
./bin/act push -W .github/workflows/pr-checks.yml \
  -P ubuntu-latest=quay.io/jamezp/act-maven \
  --artifact-server-path /workspaces/act-artifacts

# または手動スクリプト
./scripts/test-ci-locally.sh
```

## このテンプレートの使い方

1. **リポジトリをテンプレートとして使用**: GitHubの「Use this template」ボタンで新規リポジトリを作成
2. **プロジェクト固有の情報を更新**: `README.md`、`docs/spec/`配下の機能仕様をあなたのプロジェクトに合わせて書き換え
3. **AIプロンプトをカスタマイズ**: `docs/ai/prompts/`を必要に応じて調整
4. **開発開始**: [開発ガイド](./docs/dev/DEVELOPMENT.md)に従ってAIと協働で開発を進める

## AI協働開発の手法

このテンプレートの核となる考え方です。AIを単なるコード生成ツールではなく、**構造化された開発プロセスの協働パートナー**として位置づけています。

以下の3つの柱で、AI開発における典型的な課題を解決します：

- **AIの出力が不安定**: プロンプトが曖昧だと、毎回異なる結果が返ってくる → **ドキュメント駆動**で明確な指示を与える
- **スコープ逸脱**: AIが意図しないコードを大量に生成し、品質が低下 → **段階的アプローチ**で小刻みに制御
- **統合時に問題発覚**: フロントとバックを別々に作り、最後に繋いで初めて問題に気づく → **縦切り開発**で早期に統合

### 3つの柱

#### 1. ドキュメント駆動 × AI協働

**何をするか**: 仕様書→ドメインモデル→API仕様→実装という順序で、段階的にドキュメントを作成しながら開発を進めます。AIには「システムプロンプト」でプロジェクトの共通ルールを、「タスクプロンプト」で各作業の具体的な手順を伝えます。

**メリット**:
- 仕様が明確になってから実装するため、手戻りが少ない
- AIに毎回同じ説明をする必要がなく、効率的に協働できる
- 開発の過程が記録されるため、後から参加するメンバーも理解しやすい

#### 2. 段階的アプローチ（1ステップずつ確実に）

**何をするか**: 「1ステップ1プロンプト」の原則に従い、AIには一度に1つのことだけを依頼します。テスト駆動開発（TDD）のRed（失敗）→Green（成功）→Refactor（改善）サイクルを守り、各ステップで動作確認をしながら進めます。

**メリット**:
- 問題が起きてもどこで発生したか特定しやすい
- AIが勝手に先回りして余計なコードを書くことを防げる
- 小さな成功を積み重ねることで、着実に品質を確保できる

#### 3. 縦切り開発（小さな単位で完結させる）

**何をするか**: 「フロントエンドを全部作ってからバックエンド」ではなく、小さな実装単位（例：「ログインボタンを押す」という1つの操作）でUI→API→ビジネスロジック→データベースまで一気通貫で動くようにしてから、次の単位に進みます。

**メリット**:
- 小さく動くものを積み重ねるため、早い段階で動作確認できる
- フロントとバックの連携ミスを早期に発見できる
- 問題が起きても影響範囲が小さいので、修正しやすい

---

### AIと人間の役割分担

| 役割 | 人間が担当 | AIが担当 |
|------|-----------|----------|
| **意思決定** | 実装の粒度選択、リスク判断、アーキテクチャ決定 | - |
| **分析・提案** | 最終判断 | 依存関係分析、実装計画策定、テストケース生成 |
| **実装** | レビュー、承認 | コード生成、テスト実装、リファクタリング案の提示 |
| **検証** | 最終確認、受け入れテスト | ユニットテスト、静的解析、規約チェック |

この分担により、AIを「自動生成ツール」ではなく「品質と効率を支えるエンジニアリングパートナー」として迎えることができると考えています。

詳細は [AI協働開発手法](./docs/AI_COLLABORATION.md) を参照してください。

## プロジェクト構造

```
.
├── docs/                     # ドキュメント
│   ├── ai/                   # AI開発コンテキスト
│   │   ├── prompts/          # AIプロンプト集
│   │   └── logs/             # AI開発ログ
│   ├── dev/                  # 開発ガイド
│   │   ├── DEVELOPMENT.md    # 開発ガイド
│   │   ├── CODING_STANDARDS.md  # コーディング規約
│   │   ├── DOCKER.md         # Docker実行ガイド
│   │   ├── LINTER.md         # Linter設定
│   │   └── QUALITY_STANDARDS.md # 品質基準
│   ├── spec/                 # 仕様書
│   │   ├── README.md         # プロジェクト概要
│   │   ├── features/         # 機能仕様
│   │   └── templates/        # 仕様テンプレート
├── e2e/                      # E2Eテスト (Playwright + Cucumber)
│   ├── features/             # Cucumberフィーチャーファイル
│   ├── step-definitions/     # ステップ定義
│   └── support/              # テストサポートファイル
├── src/
│   ├── backend/              # Spring Boot バックエンド
│   │   └── src/
│   │       ├── main/java/    # アプリケーションコード
│   │       └── test/java/    # テストコード
│   └── frontend/             # React フロントエンド
│       └── src/
│           ├── constants/    # 定数定義
│           ├── hooks/        # カスタムフック
│           ├── services/     # APIクライアント
│           ├── types/        # 型定義
│           ├── utils/        # ユーティリティ関数
│           └── test/         # テストコード
├── testlists/                # テストリスト（TDD実装管理）
├── docker-compose.yml        # Docker Compose設定
└── README.md                 # このファイル
```

## ドキュメント構成

プロジェクトの理解と開発を進めるために、以下のドキュメントが用意されています：

### 開発プロセス
- **[docs/dev/DEVELOPMENT.md](./docs/dev/DEVELOPMENT.md)** - TDD開発フロー、テスト実行方法
- **[docs/dev/CODING_STANDARDS.md](./docs/dev/CODING_STANDARDS.md)** - コーディング規約（命名規則、設計原則）

### 仕様・設計
- **[docs/spec/README.md](./docs/spec/README.md)** - プロジェクト概要と機能仕様
- **[docs/spec/features/](./docs/spec/features/)** - 機能別の詳細仕様

### テスト・品質
- **[docs/dev/LINTER.md](./docs/dev/LINTER.md)** - コード品質チェックツールの使い方
- **[docs/dev/QUALITY_STANDARDS.md](./docs/dev/QUALITY_STANDARDS.md)** - 品質基準とベストプラクティス

### 環境・運用
- **[docs/dev/DOCKER.md](./docs/dev/DOCKER.md)** - Docker環境でのアプリケーション起動方法

### AI開発
- **[docs/AI_COLLABORATION.md](./docs/AI_COLLABORATION.md)** - AI協働開発の思想、原則、7段階プロセス
- **[docs/ai/](./docs/ai/)** - AI開発コンテキストとプロンプト集
    - **[docs/ai/prompts/tasks/](./docs/ai/prompts/tasks/)** - AIタスク用プロンプト
        - `01_create_feature_spec.md`: 機能仕様(Spec)を対話的に作成
        - `02_simple_modeling.md`: ドメインモデリング
        - `03_design_api_contract.md`: API Contract設計
        - `04_plan_implementation.md`: シナリオから実装計画を策定
        - `05_generate_stubs.md`: Backend Stub生成
        - `06_vertical_slice_implementation.md`: 縦切り実装サイクル

## 開発

詳細は [docs/dev/DEVELOPMENT.md](./docs/dev/DEVELOPMENT.md) を参照してください。

### TDD開発フロー

このプロジェクトでは、Outside-In TDD（E2E → Frontend → Backend）を採用しています。
詳しくは [docs/dev/DEVELOPMENT.md](./docs/dev/DEVELOPMENT.md#tdd開発フロー) を参照してください。

### セットアップ

```bash
# Docker Composeで起動
docker-compose up

# または個別に起動

# バックエンド
cd src/backend
mvn spring-boot:run

# フロントエンド
cd src/frontend
npm install
npm run dev
```

### テスト

詳細は [docs/dev/DEVELOPMENT.md](./docs/dev/DEVELOPMENT.md#テスト実行) を参照してください。

#### 個別にテスト実行

```bash
# バックエンドテスト
cd src/backend
mvn test

# フロントエンドテスト
cd src/frontend
npm test

# E2Eテスト（アプリケーション起動後）
cd e2e
npm run test:e2e
```

## 開発状況

HatoMask Appのサンプル実装は現在開発中です。AI協働開発の各フェーズを実践しながら、ドキュメントとコードを進化させています。

## ライセンス

MIT

## 貢献

イシューやプルリクエストを歓迎します。AI協働開発の改善提案、新しいプロンプトテンプレート、ドキュメントの改善などをお待ちしています。
