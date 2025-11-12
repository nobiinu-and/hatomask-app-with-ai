# テスト実行ガイド

## 概要

HatoMask アプリケーションには以下の3種類のテストが用意されています：

1. **バックエンド単体・統合テスト** (JUnit 5 + Mockito + Spring Boot Test)
2. **フロントエンド単体テスト** (Vitest + React Testing Library)
3. **E2Eテスト** (Playwright)

## セットアップ

### バックエンドテスト

依存関係は既に `pom.xml` に含まれています。

```bash
cd src/backend
mvn clean install
```

### フロントエンドテスト

依存関係をインストールします：

```bash
cd src/frontend
npm install
```

### E2Eテスト

Playwright をセットアップします：

```bash
cd e2e
npm install
npm run playwright:install
```

## テスト実行

### バックエンドテスト

#### 全テスト実行
```bash
cd src/backend
mvn test
```

#### 特定のテストクラスを実行
```bash
mvn test -Dtest=HelloControllerTest
```

#### カバレッジレポート生成（JaCoCo）
```bash
mvn test jacoco:report
# レポート: target/site/jacoco/index.html
```

### フロントエンドテスト

#### 全テスト実行
```bash
cd src/frontend
npm test
```

#### ウォッチモードで実行
```bash
npm test -- --watch
```

#### UIモードで実行
```bash
npm run test:ui
```

#### カバレッジレポート生成
```bash
npm run test:coverage
# レポート: coverage/index.html
```

### E2Eテスト

#### 前提条件
アプリケーション全体が起動している必要があります：

```bash
# ルートディレクトリから
docker-compose -f docker-compose.dev.yml up
```

または個別に起動：

```bash
# バックエンド
cd src/backend
mvn spring-boot:run

# フロントエンド（別ターミナル）
cd src/frontend
npm run dev
```

#### テスト実行

```bash
cd e2e

# 全ブラウザで実行
npm run test:e2e

# UIモードで実行
npm run test:e2e:ui

# ヘッドモード（ブラウザ表示）で実行
npm run test:e2e:headed

# デバッグモード
npm run test:e2e:debug

# 特定のブラウザのみで実行
npx playwright test --project=chromium
```

## 継続的インテグレーション (CI)

### GitHub Actions での実行例

```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run backend tests
        run: |
          cd src/backend
          mvn test

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install dependencies
        run: |
          cd src/frontend
          npm ci
      - name: Run frontend tests
        run: |
          cd src/frontend
          npm test

  e2e-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Start services
        run: docker-compose -f docker-compose.dev.yml up -d
      - name: Wait for services
        run: sleep 30
      - name: Install Playwright
        run: |
          cd e2e
          npm ci
          npm run playwright:install
      - name: Run E2E tests
        run: |
          cd e2e
          npm run test:e2e
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: playwright-report
          path: e2e/playwright-report/
```

## テストのベストプラクティス

### バックエンド

- ビジネスロジックは単体テストでカバー
- 外部依存はモック化
- 統合テストでは実際のDBを使用（H2 または Testcontainers）
- `@DisplayName` で日本語の説明を記述

### フロントエンド

- ユーザーの視点でテストを書く
- 実装の詳細ではなく、動作をテスト
- MSW でAPIレスポンスをモック
- アクセシビリティを考慮したセレクタを使用

### E2E

- クリティカルなユーザーフローに焦点を当てる
- テストの独立性を保つ
- テストデータのセットアップとクリーンアップを適切に行う
- フレイキーテストを避ける（適切な待機処理）

## トラブルシューティング

### フロントエンドテストで型エラーが出る

依存関係を再インストール：
```bash
cd src/frontend
rm -rf node_modules package-lock.json
npm install
```

### E2Eテストが失敗する

1. サービスが起動しているか確認
2. ポートが正しいか確認（フロントエンド: 3000、バックエンド: 8080）
3. ブラウザが正しくインストールされているか確認
   ```bash
   cd e2e
   npm run playwright:install
   ```

### バックエンドテストでDB接続エラー

テストプロファイルが正しく読み込まれているか確認：
```bash
mvn test -Dspring.profiles.active=test
```

## テストカバレッジの目標

- **バックエンド**: 全体で 80%以上、ビジネスロジックは 90%以上
- **フロントエンド**: 全体で 80%以上、重要なコンポーネントは 90%以上
- **E2E**: 主要なユーザーフロー（クリティカルパス）を100%カバー
