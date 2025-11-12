# ビルドステージ
FROM node:20-alpine AS build

# 作業ディレクトリを設定
WORKDIR /app

# package.jsonとpackage-lock.jsonをコピー
COPY package*.json ./

# 依存関係をインストール
RUN npm ci

# ソースコードをコピー
COPY . .

# アプリケーションをビルド
RUN npm run build

# 実行ステージ - Nginxで静的ファイルを配信
FROM nginx:alpine

# ビルドされたファイルをNginxのディレクトリにコピー
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx設定ファイルをコピー
COPY nginx.conf /etc/nginx/conf.d/default.conf

# ポートを公開
EXPOSE 80

# Nginxを起動
CMD ["nginx", "-g", "daemon off;"]
