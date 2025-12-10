# HatoMask App

写真にある顔をハトマスクに入れ替えるWebアプリケーション

## 概要

HatoMask Appは、アップロードされた写真内の顔を自動検出し、ハト（鳩）のマスク画像に置き換えるWebアプリケーションです。  
プライバシー保護や面白いコンテンツ作成に活用できます。

## ビジョン

写真にある顔をハトマスクに入れ替える、直感的で使いやすいアプリケーションを提供します。

## AI協働開発について

このプロジェクトでは、AIを単なるコード生成ツールではなく、**構造化された開発プロセスの協働パートナー**として位置づけています。

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

このアプローチにより、AIとの協働で**再現可能**かつ**高品質**なソフトウェア開発を目指しています。

詳細は [AI協働開発手法](./docs/AI_COLLABORATION.md) を参照してください。

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
- **[docs/AI_COLLABORATION.md](./docs/AI_COLLABORATION.md)** - AI協働開発の思想、原則、7段階プロセス
- **[docs/ai/](./docs/ai/)** - AI開発コンテキストとプロンプト集
    - **[docs/ai/prompts/tasks/](./docs/ai/prompts/tasks/)** - AIタスク用プロンプト
        - `01_generate_scenarios.md`: 仕様書からGherkinシナリオを生成
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

このプロジェクトは現在開発中です。

## ライセンス

MIT

## 貢献

TBD
