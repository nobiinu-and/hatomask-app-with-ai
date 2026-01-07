# マルチステージビルドを使用
FROM maven:3.9-eclipse-temurin-17 AS build

# 作業ディレクトリを設定
WORKDIR /app

# pom.xmlをコピーして依存関係をダウンロード
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ソースコードをコピー
COPY src ./src
COPY checkstyle.xml .

# アプリケーションをビルド
RUN mvn clean package -DskipTests

# 実行用の軽量イメージ
FROM eclipse-temurin:17-jre

USER root
RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends \
    opencv-data \
    ca-certificates \
    curl \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender1 \
    libice6 \
    libgomp1; \
    apt-get install -y --no-install-recommends libgtk2.0-0 \
    || apt-get install -y --no-install-recommends libgtk2.0-0t64; \
    rm -rf /var/lib/apt/lists/*

ENV HATOMASK_OPENCV_CASCADE_PATH=/usr/share/opencv4/haarcascades/haarcascade_frontalface_default.xml

RUN set -eux; \
    mkdir -p /opt/hatomask/models; \
    curl -fsSL -o /opt/hatomask/models/lbfmodel.yaml \
    https://raw.githubusercontent.com/kurnianggoro/GSOC2017/master/data/lbfmodel.yaml

ENV HATOMASK_OPENCV_LBF_MODEL_PATH=/opt/hatomask/models/lbfmodel.yaml

# 作業ディレクトリを設定
WORKDIR /app

# ビルドステージからJARファイルをコピー
COPY --from=build /app/target/*.jar app.jar

# ポートを公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]
