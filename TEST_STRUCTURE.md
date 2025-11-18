# テスト構成

> **Note**: このドキュメントはテストディレクトリの**構造**を説明します。
> 開発プロセスやテスト実行方法については [DEVELOPMENT.md](./DEVELOPMENT.md) を参照してください。

## テストディレクトリ構造

```
.
├── src/
│   ├── backend/
│   │   └── src/
│   │       └── test/
│   │           ├── java/
│   │           │   └── com/hatomask/
│   │           │       ├── HatomaskApplicationTests.java           # アプリケーション起動テスト
│   │           │       └── presentation/
│   │           │           ├── controller/
│   │           │           │   ├── HelloControllerTest.java         # Controller単体テスト
│   │           │           │   └── HelloControllerIntegrationTest.java  # Controller統合テスト
│   │           │           └── dto/
│   │           │               └── HelloResponseTest.java           # DTO単体テスト
│   │           └── resources/
│   │               └── application-test.yml                         # テスト用設定
│   │
│   └── frontend/
│       └── src/
│           └── test/
│               ├── App.test.tsx                                    # Appコンポーネントテスト
│               ├── setup.ts                                        # テストセットアップ
│               └── mocks/
│                   ├── handlers.ts                                 # MSWハンドラー
│                   └── server.ts                                   # MSWサーバー
│
├── e2e/
│   ├── tests/
│   │   └── app.spec.ts                                            # E2Eテスト
│   ├── playwright.config.ts                                        # Playwright設定
│   └── package.json                                               # E2E依存関係
│
├── scripts/
│   └── run-all-tests.sh                                           # 全テスト実行スクリプト
│
└── TESTING.md                                                     # テスト実行ガイド
```

## テストファイル一覧

### バックエンドテスト

| ファイル | 種類 | 説明 |
|---------|------|------|
| `HatomaskApplicationTests.java` | 統合テスト | アプリケーションコンテキストの起動確認 |
| `HelloControllerTest.java` | 単体テスト | HelloControllerのロジックテスト |
| `HelloControllerIntegrationTest.java` | 統合テスト | HelloController APIエンドポイントテスト |
| `HelloResponseTest.java` | 単体テスト | HelloResponse DTOのテスト |

### フロントエンドテスト

| ファイル | 種類 | 説明 |
|---------|------|------|
| `App.test.tsx` | 単体テスト | Appコンポーネントの振る舞いテスト |
| `setup.ts` | 設定 | Vitest・MSWの初期設定 |
| `mocks/handlers.ts` | モック | APIレスポンスのモック定義 |
| `mocks/server.ts` | モック | MSWモックサーバー |

### E2Eテスト

| ファイル | 種類 | 説明 |
|---------|------|------|
| `app.spec.ts` | E2Eテスト | アプリケーション全体のE2Eテスト |

## テストカバレッジ

### 現在のカバレッジ

- **バックエンド**: 基本的なController、DTO、統合テスト
- **フロントエンド**: Appコンポーネント、API連携、エラーハンドリング
- **E2E**: 基本的な画面表示、API連携、レスポンシブデザイン

### 今後追加予定のテスト

#### バックエンド
- [ ] UseCase層のビジネスロジックテスト
- [ ] Repository層の統合テスト（Testcontainers使用）
- [ ] Domain層のエンティティテスト
- [ ] バリデーションテスト
- [ ] エラーハンドリングテスト
- [ ] ファイルアップロード・ダウンロードのテスト

#### フロントエンド
- [ ] 個別コンポーネントの単体テスト
- [ ] カスタムフックのテスト
- [ ] ユーティリティ関数のテスト
- [ ] フォームバリデーションのテスト
- [ ] 画像アップロード機能のテスト

#### E2E
- [ ] 画像アップロードフロー
- [ ] 画像処理フロー
- [ ] エラーケース（ネットワークエラー、大容量ファイルなど）
- [ ] 複数ブラウザでの動作確認

## テスト実行コマンド早見表

```bash
# バックエンド
cd src/backend
mvn test                          # 全テスト実行
mvn test -Dtest=HelloControllerTest  # 特定のテスト実行
mvn test jacoco:report            # カバレッジレポート生成

# フロントエンド
cd src/frontend
npm test                          # 全テスト実行
npm test -- --watch              # ウォッチモード
npm run test:ui                  # UIモード
npm run test:coverage            # カバレッジレポート

# E2E
cd e2e
npm run test:e2e                 # 全ブラウザでテスト
npm run test:e2e:ui              # UIモード
npm run test:e2e:headed          # ブラウザ表示モード
npm run test:e2e:debug           # デバッグモード

# 全テスト
./scripts/run-all-tests.sh       # 単体・統合テストのみ
./scripts/run-all-tests.sh --with-e2e  # E2Eも含む
```

## 依存関係

### バックエンド（pom.xml）
- spring-boot-starter-test
- JUnit 5
- Mockito
- Testcontainers（PostgreSQL）
- REST Assured
- H2 Database（テスト用）

### フロントエンド（package.json）
- Vitest
- React Testing Library
- @testing-library/jest-dom
- @testing-library/user-event
- MSW (Mock Service Worker)
- jsdom

### E2E（e2e/package.json）
- @playwright/test
- @types/node

## 次のステップ

1. フロントエンドの依存関係をインストール: `cd src/frontend && npm install`
2. E2Eの依存関係をインストール: `cd e2e && npm install && npm run playwright:install`
3. バックエンドテストを実行: `cd src/backend && mvn test`
4. フロントエンドテストを実行: `cd src/frontend && npm test`
5. アプリを起動してE2Eテストを実行
