# HatoMask App

写真にある顔をハトマスクに入れ替えるWebアプリケーション

## 概要

HatoMask Appは、アップロードされた写真内の顔を自動検出し、ハト（鳩）のマスク画像に置き換えるWebアプリケーションです。  
プライバシー保護や面白いコンテンツ作成に活用できます。

## ビジョン

写真にある顔をハトマスクに入れ替える、直感的で使いやすいアプリケーションを提供します。

## 主要機能

プロジェクトは以下の5つの主要機能を提供予定です：

1. 写真のアップロード
2. 顔の自動検出
3. ハトマスクの適用
4. プレビューと調整
5. 処理済み画像のダウンロード

## 技術スタック

### フロントエンド
- React
- TypeScript

### バックエンド
- Spring Boot
- PostgreSQL

### アーキテクチャ
- Clean Architecture
- API: 統一レスポンス形式
- エラーレスポンス: RFC 9457（Problem Details）仕様に準拠

## 技術要件

- **リアルタイム処理**: 高速な顔検出と画像処理
- **PWA対応**: Progressive Web Appとしてモバイルデバイスでも快適に動作
- **国際化 (i18n)**: 多言語対応

## 非機能要件

- **応答時間**: 1秒以内のレスポンス

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
- **[docs/ai/](./docs/ai/)** - AI開発コンテキストとプロンプト集
    - **[docs/ai/prompts/tasks/](./docs/ai/prompts/tasks/)** - AIタスク用プロンプト
        - `01_generate_scenarios.md`: 仕様書からGherkinシナリオを生成
        - `02_plan_implementation.md`: シナリオから実装計画を策定
        - `03_implement_bdd_step.md`: BDDステップの実装（E2E/Frontend）
        - `04_implement_backend_tdd.md`: バックエンドTDD実装

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

このプロジェクトは現在開発中です。

## ライセンス

MIT

## 貢献

TBD
