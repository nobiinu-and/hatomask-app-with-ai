# Cucumber E2Eテスト

このディレクトリには、Cucumberを使用したBDD（振る舞い駆動開発）スタイルのE2Eテストが含まれています。

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

2. Cucumberの最新バージョン（v12.2.0）を使用しています。

3. Playwrightブラウザのインストール:
```bash
npm run playwright:install
```

4. アプリケーションの起動（別ターミナルで）:
```bash
# フロントエンドとバックエンドを起動
docker-compose -f docker-compose.dev.yml up
```

### Cucumberテストの実行

```bash
# すべてのCucumberテストを実行
npm run test:cucumber

# ドライラン（構文チェックのみ）
npm run test:cucumber:dry
```

### Playwrightテストの実行（従来のテスト）

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

フィーチャーファイルはGherkin構文を使用し、日本語で記述されています：

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

## デバッグ機能

### テスト失敗時の自動保存

テストが失敗すると、以下のファイルが自動的に保存されます：

#### 1. スクリーンショット
- 保存先: `test-results/screenshots/`
- フルページスクリーンショット（PNG形式）
- ファイル名: `{シナリオ名}_{タイムスタンプ}.png`

#### 2. Playwrightトレース
- 保存先: `test-results/traces/`
- 実行履歴の完全な記録（ZIP形式）
- ファイル名: `{シナリオ名}_{タイムスタンプ}.zip`
- **表示方法**: 
  ```bash
  npx playwright show-trace test-results/traces/{ファイル名}.zip
  ```
- トレースビューアでは以下を確認できます：
  - 各ステップのスクリーンショット
  - DOMスナップショット
  - ネットワークリクエスト
  - コンソールログ
  - ソースコード

#### 3. HTMLダンプ
- 保存先: `test-results/screenshots/`
- 失敗時のページHTML（HTML形式）
- ファイル名: `{シナリオ名}_{タイムスタンプ}.html`

#### 4. ビデオ録画
- 保存先: `test-results/videos/`
- テスト実行中の画面録画（WebM形式）
- すべてのテストで自動録画

### ブラウザコンソール監視

テスト実行中、ブラウザのエラーと警告がターミナルに出力されます：
- `[Browser ERROR]: {メッセージ}` - JavaScriptエラー
- `[Browser WARNING]: {メッセージ}` - 警告
- `[Page Error]: {メッセージ}` - ページエラー

## ステップ定義の追加

新しいステップを追加する場合は、`step-definitions/steps.ts`に追加してください：

```typescript
Given('新しい前提条件', async function () {
  // 実装
});

When('新しいアクション', async function () {
  // 実装
});

Then('新しい期待結果', async function () {
  // 実装
});
```

## トラブルシューティング

### エラー: `page is not defined`

`support/hooks.ts`でPageオブジェクトが正しくセットアップされているか確認してください。

### エラー: `Cannot find module`

`tsconfig.cucumber.json`の設定を確認し、必要なモジュールがインストールされているか確認してください。

### タイムアウトエラー

`step-definitions/steps.ts`の先頭で`setDefaultTimeout`の値を調整してください。
