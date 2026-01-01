# HatoMask Application - Docker 実行ガイド

## 使い分け

- **開発**: `docker-compose.yml`
- **CI**: `docker-compose.ci.yml`

## アプリケーションの起動方法

### 1. 開発環境の起動

```bash
docker compose up -d
```

### 2. ログを確認

```bash
# 全サービスのログを表示
docker compose logs -f

# 特定のサービスのみ表示
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f db
```

### 3. アプリケーションにアクセス

- **フロントエンド**: http://localhost:3000
- **バックエンド API**: http://localhost:8080
- **データベース**: localhost:5432

### 4. サービスの停止

```bash
# 停止（データは保持）
docker compose stop

# 停止してコンテナを削除
docker compose down

# データベースのボリュームも削除する場合
docker compose down -v
```

## サービス構成

### データベース (PostgreSQL)

- **イメージ**: postgres:15-alpine
- **ポート**: 5432
- **データベース名**: hatomask
- **ユーザー名**: postgres
- **パスワード**: postgres

### バックエンド (Spring Boot)

- **ポート**: 8080
- **Java**: 17
- **データベース接続**: 環境変数で自動設定

### フロントエンド (Vite dev server)

- **ポート**: 3000
- **備考**: `docker-compose.yml` では `npm run dev -- --host 0.0.0.0` で起動します

## トラブルシューティング

### コンテナの状態を確認

```bash
docker compose ps
```

### 特定のサービスを再起動

```bash
docker compose restart backend
```

### イメージを再ビルド

```bash
# 全サービスを再ビルド
docker compose build

# 特定のサービスのみ再ビルド
docker compose build backend

# キャッシュを使わずに再ビルド
docker compose build --no-cache
```

### コンテナ内でコマンドを実行

```bash
# バックエンドのコンテナに入る
docker compose exec backend sh

# データベースに接続
docker compose exec db psql -U postgres -d hatomask
```

## 開発時の注意事項

- コードを変更した場合は、該当するサービスを再ビルドする必要があります
- データベースのデータは`postgres_data`ボリュームに永続化されます
- CORS は`localhost:3000`と`localhost:80`に対して許可されています

## 環境変数のカスタマイズ

`docker-compose.yml` の environment セクションで以下の設定を変更できます：

- データベース接続情報
- CORS 許可オリジン
- その他の Spring Boot 設定

## CI での起動（参考）

CI では `docker-compose.ci.yml` を使用します。

```bash
docker compose -f docker-compose.ci.yml up --build
```
