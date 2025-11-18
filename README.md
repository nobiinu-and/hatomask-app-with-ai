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
├── .ai/              # AI開発コンテキスト
├── e2e/              # E2Eテスト (Playwright + Cucumber)
├── runbooks/         # 運用ドキュメント
├── scripts/          # ユーティリティスクリプト
├── spec/             # 仕様書
│   ├── OVERVIEW.md   # プロジェクト概要
│   └── features/     # 機能仕様
├── src/
│   ├── backend/      # Spring Boot バックエンド
│   └── frontend/     # React フロントエンド
├── testlists/        # テストリスト（TDD実装管理）
├── CODING_STANDARDS.md  # コーディング規約
├── DEVELOPMENT.md    # 開発ガイド
├── DOCKER.md         # Docker実行ガイド
├── LINTER.md         # Linter設定
├── TEST_STRUCTURE.md # テスト構造
└── README.md         # このファイル
```

## ドキュメント構成

プロジェクトの理解と開発を進めるために、以下のドキュメントが用意されています：

### 開発プロセス
- **[DEVELOPMENT.md](./DEVELOPMENT.md)** - TDD開発フロー、テスト実行方法
- **[CODING_STANDARDS.md](./CODING_STANDARDS.md)** - コーディング規約（命名規則、設計原則）
- **[testlists/README.md](./testlists/README.md)** - テストリスト運用方針

### 仕様・設計
- **[spec/OVERVIEW.md](./spec/OVERVIEW.md)** - プロジェクト概要と機能仕様
- **[.ai/context.md](./.ai/context.md)** - アーキテクチャ概要と技術スタック

### テスト・品質
- **[TEST_STRUCTURE.md](./TEST_STRUCTURE.md)** - テストディレクトリ構造
- **[LINTER.md](./LINTER.md)** - コード品質チェックツールの使い方

### 環境・運用
- **[DOCKER.md](./DOCKER.md)** - Docker環境でのアプリケーション起動方法
- **[runbooks/](./runbooks/)** - 運用ドキュメント

## 開発

詳細は [DEVELOPMENT.md](./DEVELOPMENT.md) を参照してください。

### TDD開発フロー

このプロジェクトでは、Outside-In TDD（E2E → Frontend → Backend）を採用しています。
詳しくは [DEVELOPMENT.md](./DEVELOPMENT.md#tdd開発フロー) を参照してください。

### セットアップ

```bash
# Docker Composeで起動
docker-compose -f docker-compose.dev.yml up

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

詳細は [DEVELOPMENT.md](./DEVELOPMENT.md#テスト実行) を参照してください。

#### 全テスト実行

```bash
./scripts/run-all-tests.sh
```

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
