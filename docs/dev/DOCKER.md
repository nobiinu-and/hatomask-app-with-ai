# HatoMask Application - Docker実行ガイド

## 作成されたファイル

以下のDockerファイルが作成されました：

```
docker-compose.dev.yml                 # 開発用のDocker Compose設定
src/backend/Dockerfile                 # Spring Bootバックエンド用
src/backend/.dockerignore              # バックエンド用の除外設定
src/frontend/Dockerfile                # Reactフロントエンド用
src/frontend/nginx.conf                # フロントエンド用のNginx設定
src/frontend/.dockerignore             # フロントエンド用の除外設定
```

## アプリケーションの起動方法

### 1. 開発環境の起動

```bash
docker-compose -f docker-compose.dev.yml up -d
```

### 2. ログを確認

```bash
# 全サービスのログを表示
docker-compose -f docker-compose.dev.yml logs -f

# 特定のサービスのみ表示
docker-compose -f docker-compose.dev.yml logs -f backend
docker-compose -f docker-compose.dev.yml logs -f frontend
docker-compose -f docker-compose.dev.yml logs -f db
```

### 3. アプリケーションにアクセス

- **フロントエンド**: http://localhost:3000
- **バックエンドAPI**: http://localhost:8080
- **データベース**: localhost:5432

### 4. サービスの停止

```bash
# 停止（データは保持）
docker-compose -f docker-compose.dev.yml stop

# 停止してコンテナを削除
docker-compose -f docker-compose.dev.yml down

# データベースのボリュームも削除する場合
docker-compose -f docker-compose.dev.yml down -v
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

### フロントエンド (React + Nginx)
- **ポート**: 3000 (80にマッピング)
- **Webサーバー**: Nginx
- **React Router対応**: SPAのフォールバック設定済み

## トラブルシューティング

### コンテナの状態を確認
```bash
docker-compose -f docker-compose.dev.yml ps
```

### 特定のサービスを再起動
```bash
docker-compose -f docker-compose.dev.yml restart backend
```

### イメージを再ビルド
```bash
# 全サービスを再ビルド
docker-compose -f docker-compose.dev.yml build

# 特定のサービスのみ再ビルド
docker-compose -f docker-compose.dev.yml build backend

# キャッシュを使わずに再ビルド
docker-compose -f docker-compose.dev.yml build --no-cache
```

### コンテナ内でコマンドを実行
```bash
# バックエンドのコンテナに入る
docker-compose -f docker-compose.dev.yml exec backend sh

# データベースに接続
docker-compose -f docker-compose.dev.yml exec db psql -U postgres -d hatomask
```

## 開発時の注意事項

- コードを変更した場合は、該当するサービスを再ビルドする必要があります
- データベースのデータは`postgres_data`ボリュームに永続化されます
- CORSは`localhost:3000`と`localhost:80`に対して許可されています

## 環境変数のカスタマイズ

`docker-compose.dev.yml`のenvironment セクションで以下の設定を変更できます：

- データベース接続情報
- CORS許可オリジン
- その他のSpring Boot設定

## 開発用と本番用の使い分け

このファイルは開発用の設定です。本番環境用には以下を作成することを推奨します：

- `docker-compose.prod.yml` - 本番環境用の設定
- 環境変数は `.env` ファイルで管理
- セキュリティ強化（パスワードの外部化など）
- ボリュームマウントの最適化
