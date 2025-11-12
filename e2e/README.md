# E2Eテスト

このディレクトリには Playwright を使用した E2E テストが含まれています。

## セットアップ

```bash
cd e2e
npm install
npm run playwright:install
```

## テスト実行

### 全テスト実行
```bash
npm run test:e2e
```

### UIモードで実行
```bash
npm run test:e2e:ui
```

### ヘッドモードで実行（ブラウザを表示）
```bash
npm run test:e2e:headed
```

### デバッグモード
```bash
npm run test:e2e:debug
```

## 前提条件

E2Eテストを実行する前に、以下を確認してください：

1. フロントエンドが http://localhost:3000 で起動している
2. バックエンドが http://localhost:8080 で起動している

Docker Composeで全体を起動する場合：
```bash
cd /workspaces/hatomask-app-with-ai
docker-compose -f docker-compose.dev.yml up
```

## テスト結果

- HTMLレポート: `playwright-report/index.html`
- JSONレポート: `test-results/results.json`
- スクリーンショット: `test-results/` 配下に保存
- ビデオ: `test-results/` 配下に保存
