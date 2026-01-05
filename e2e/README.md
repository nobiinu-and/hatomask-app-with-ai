# Cucumber E2E テスト

このディレクトリには、Cucumber を使用した BDD（振る舞い駆動開発）スタイルの E2E テストが含まれています。

## ディレクトリ構造

```
e2e/
├── features/              # Gherkin形式のフィーチャーファイル
│   ├── top-page.feature
│   ├── responsive-design.feature
│   └── ui-theme.feature
├── step-definitions/      # ステップ定義（テストの実装）
│   └── steps.ts
├── support/              # テストのセットアップとフック
│   └── hooks.ts
├── cucumber.js           # Cucumber設定ファイル
└── tsconfig.cucumber.json # TypeScript設定
```

## テストの実行

### 前提条件

1. 依存関係のインストール:

```bash
npm install
```

2. Cucumber の最新バージョン（v12.2.0）を使用しています。

3. Playwright ブラウザのインストール:

```bash
npm run playwright:install
```

4. アプリケーションの起動（別ターミナルで）:

```bash
# フロントエンドとバックエンドを起動
docker compose up
```

5. 写真アップロードのE2Eは fixtures を使用します:

- `e2e/fixtures/sample.jpg` が必要です（テストで直接選択します）

### Cucumber テストの実行

```bash
# すべてのCucumberテストを実行
npm run test:cucumber

# ドライラン（構文チェックのみ）
npm run test:cucumber:dry
```

### Playwright テストの実行（従来のテスト）

```bash
# すべてのPlaywrightテストを実行
npm run test:e2e

# UIモードで実行
npm run test:e2e:ui

# ヘッドモードで実行（ブラウザ表示あり）
npm run test:e2e:headed

# デバッグモードで実行
npm run test:e2e:debug
```

## フィーチャーファイルの書き方

フィーチャーファイルは Gherkin 構文を使用し、日本語で記述されています：

```gherkin
# language: ja
機能: トップページの表示

  シナリオ: トップページが正しく表示される
    前提 ユーザーがブラウザを開いている
    もし トップページにアクセスする
    ならば タイトル "🕊️ HatoMask App" が表示される
```

### キーワード

- `機能`: テスト対象の機能
- `シナリオ`: 具体的なテストケース
- `シナリオアウトライン`: パラメータ化されたテストケース
- `前提` (Given): 初期状態
- `もし` (When): アクション
- `ならば` (Then): 期待される結果
- `かつ` (And): 前のステップの続き

## テストレポート

テスト実行後、以下の場所にレポートが生成されます：

- HTML: `test-results/cucumber-report.html`
- JSON: `test-results/cucumber-report.json`
- JUnit XML: `test-results/cucumber-report.xml`

## ステップ定義の追加

新しいステップを追加する場合は、`step-definitions/steps.ts`に追加してください：

```typescript
Given("新しい前提条件", async function () {
  // 実装
});

When("新しいアクション", async function () {
  // 実装
});

Then("新しい期待結果", async function () {
  // 実装
});
```

## トラブルシューティング

### エラー: `page is not defined`

`support/hooks.ts`で Page オブジェクトが正しくセットアップされているか確認してください。

### エラー: `Cannot find module`

`tsconfig.cucumber.json`の設定を確認し、必要なモジュールがインストールされているか確認してください。

### タイムアウトエラー

`step-definitions/steps.ts`の先頭で`setDefaultTimeout`の値を調整してください。
