#!/bin/bash

# テスト実行スクリプト
# このスクリプトは全てのテストを実行します

set -e

echo "================================================"
echo "HatoMask - 全テスト実行"
echo "================================================"

# 色の定義
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 結果を保存する変数
BACKEND_RESULT=0
FRONTEND_RESULT=0
E2E_RESULT=0

# バックエンドテスト
echo -e "\n${YELLOW}[1/3] バックエンドテストを実行中...${NC}"
cd src/backend
if mvn test; then
    echo -e "${GREEN}✓ バックエンドテスト: 成功${NC}"
else
    echo -e "${RED}✗ バックエンドテスト: 失敗${NC}"
    BACKEND_RESULT=1
fi
cd ../..

# フロントエンドテスト
echo -e "\n${YELLOW}[2/3] フロントエンドテストを実行中...${NC}"
cd src/frontend
if npm test -- --run; then
    echo -e "${GREEN}✓ フロントエンドテスト: 成功${NC}"
else
    echo -e "${RED}✗ フロントエンドテスト: 失敗${NC}"
    FRONTEND_RESULT=1
fi
cd ../..

# E2Eテスト（オプション）
if [ "$1" == "--with-e2e" ]; then
    echo -e "\n${YELLOW}[3/3] E2Eテストを実行中...${NC}"
    echo "アプリケーションが起動していることを確認してください"
    cd e2e
    if npm run test:e2e; then
        echo -e "${GREEN}✓ E2Eテスト: 成功${NC}"
    else
        echo -e "${RED}✗ E2Eテスト: 失敗${NC}"
        E2E_RESULT=1
    fi
    cd ..
else
    echo -e "\n${YELLOW}[3/3] E2Eテストをスキップ（--with-e2e オプションで実行可能）${NC}"
fi

# 結果のサマリー
echo -e "\n================================================"
echo "テスト結果サマリー"
echo "================================================"

if [ $BACKEND_RESULT -eq 0 ]; then
    echo -e "バックエンド: ${GREEN}✓ 成功${NC}"
else
    echo -e "バックエンド: ${RED}✗ 失敗${NC}"
fi

if [ $FRONTEND_RESULT -eq 0 ]; then
    echo -e "フロントエンド: ${GREEN}✓ 成功${NC}"
else
    echo -e "フロントエンド: ${RED}✗ 失敗${NC}"
fi

if [ "$1" == "--with-e2e" ]; then
    if [ $E2E_RESULT -eq 0 ]; then
        echo -e "E2E: ${GREEN}✓ 成功${NC}"
    else
        echo -e "E2E: ${RED}✗ 失敗${NC}"
    fi
fi

echo "================================================"

# 失敗があればエラーコードを返す
TOTAL_RESULT=$((BACKEND_RESULT + FRONTEND_RESULT + E2E_RESULT))
if [ $TOTAL_RESULT -ne 0 ]; then
    echo -e "${RED}いくつかのテストが失敗しました${NC}"
    exit 1
else
    echo -e "${GREEN}全てのテストが成功しました！${NC}"
    exit 0
fi
