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

# 作業ディレクトリを設定
WORKDIR /app

# ビルドステージからJARファイルをコピー
COPY --from=build /app/target/*.jar app.jar

# ポートを公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]
