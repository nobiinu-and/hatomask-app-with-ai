#!/bin/sh
set -e

echo "Starting development watcher (compile) and Spring Boot..."

# フォアグラウンドで Spring Boot を起動し、バックグラウンドでソース変更時にコンパイルを実行
# entr が変更を検知すると mvn compile が呼ばれ、spring-boot-devtools が再起動をトリガーします

# watchers: find によるファイルリストを entr に渡す
sh -c "find src -name '*.java' | entr -r mvn -q -DskipTests compile" &

# Spring Boot をフォアグラウンドで実行
mvn -DskipTests -Dspring-boot.run.fork=false spring-boot:run
