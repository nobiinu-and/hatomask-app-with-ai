# HatoMask Application - セットアップガイド

## プロジェクト構成

```
src/
├── backend/    # Spring Boot バックエンド
└── frontend/   # React + TypeScript フロントエンド
```

## 前提条件

- Java 17 以上
- Maven 3.6 以上
- Node.js 18 以上
- PostgreSQL 14 以上

## セットアップ手順

### 1. データベースのセットアップ

PostgreSQLでデータベースを作成：

```bash
# PostgreSQLに接続
psql -U postgres

# データベース作成
CREATE DATABASE hatomask;

# 終了
\q
```

### 2. バックエンドのセットアップと起動

```bash
cd src/backend

# 依存関係のインストールとビルド
mvn clean install

# アプリケーションの起動
mvn spring-boot:run
```

バックエンドは http://localhost:8080 で起動します。

### 3. フロントエンドのセットアップと起動

```bash
cd src/frontend

# 依存関係のインストール
npm install

# 開発サーバーの起動
npm run dev
```

フロントエンドは http://localhost:3000 で起動します。

## 動作確認

1. ブラウザで http://localhost:3000 にアクセス
2. "Hello, World from HatoMask Backend!" のメッセージが表示されることを確認

### APIエンドポイントの直接確認

```bash
curl http://localhost:8080/api/v1/hello
```

レスポンス例：
```json
{
  "message": "Hello, World from HatoMask Backend!"
}
```

## プロジェクト構成の詳細

### バックエンド (Spring Boot)

```
src/backend/
├── pom.xml
└── src/main/
    ├── java/com/hatomask/
    │   ├── HatomaskApplication.java          # メインアプリケーション
    │   ├── config/
    │   │   └── WebConfig.java                # CORS設定
    │   └── presentation/
    │       ├── controller/
    │       │   └── HelloController.java      # REST API
    │       └── dto/
    │           └── HelloResponse.java        # レスポンスDTO
    └── resources/
        └── application.yml                    # 設定ファイル
```

### フロントエンド (React + TypeScript + Material-UI)

```
src/frontend/
├── package.json
├── tsconfig.json
├── vite.config.ts
├── index.html
└── src/
    ├── main.tsx           # エントリーポイント
    ├── App.tsx            # メインコンポーネント（Material-UI使用）
    ├── App.css
    └── index.css
```

**使用しているUIライブラリ**:
- Material-UI (MUI) v5
- Material Icons

## 開発時のポイント

- フロントエンドからバックエンドへのAPIリクエストは、Viteのプロキシ設定により `/api` を経由します
- CORS設定はバックエンドの `application.yml` で管理しています
- TypeScriptの型チェックとReactの開発サーバーのホットリロードが有効です
- Material-UIを使用した統一されたデザインシステムを採用しています
- テーマ設定は `App.tsx` の `createTheme` で管理されています

## トラブルシューティング

### データベース接続エラー

`application.yml` のデータベース接続情報を確認してください：
- ホスト: localhost
- ポート: 5432
- データベース名: hatomask
- ユーザー名: postgres
- パスワード: postgres

### CORSエラー

フロントエンドとバックエンドが正しいポートで起動していることを確認してください：
- バックエンド: 8080
- フロントエンド: 3000
