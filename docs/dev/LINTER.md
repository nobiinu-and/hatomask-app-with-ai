# Linter設定ガイド

このドキュメントでは、HatoMaskプロジェクトのLinter設定と使用方法について説明します。

## フロントエンド (ESLint)

### 設定ファイル

- `.eslintrc.cjs`: ESLint設定ファイル
- `.eslintignore`: ESLintで除外するファイル

### 主な規約

#### 命名規則
- **コンポーネント/型/インターフェース**: PascalCase (`PhotoCard`, `UserProfile`)
- **関数/変数**: camelCase (`uploadPhoto`, `imageUrl`)
- **定数**: UPPER_SNAKE_CASE (`MAX_FILE_SIZE`, `API_BASE_URL`)
- **カスタムフック**: `use`プレフィックス (`usePhotoUpload`)

#### TypeScript規約
- ✅ `strict: true` を維持
- ❌ `any` 型の使用禁止
- ✅ 型推論を活用（明らかな場合は型注釈を省略）
- ✅ Optional Chaining (`?.`) とNullish Coalescing (`??`) を活用

#### コード品質
- ❌ `console.log` は警告（`console.warn`, `console.error`はOK）
- ❌ `debugger` 禁止
- ✅ `const` を優先、`var` 禁止
- ✅ テンプレートリテラル推奨

### コマンド

```bash
cd src/frontend

# Lintチェック実行
npm run lint

# 自動修正可能なエラーを修正
npm run lint:fix
```

### VS Code統合

`.vscode/settings.json`に以下を追加すると、保存時に自動修正されます：

```json
{
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "eslint.validate": [
    "javascript",
    "javascriptreact",
    "typescript",
    "typescriptreact"
  ]
}
```

## バックエンド (Checkstyle)

### 設定ファイル

- `checkstyle.xml`: Checkstyle設定ファイル

### 主な規約

#### 命名規則
- **クラス名**: PascalCase (`PhotoUploadUseCase`, `UserEntity`)
- **メソッド/変数**: camelCase (`uploadPhoto`, `imageData`)
- **定数**: UPPER_SNAKE_CASE (`MAX_IMAGE_SIZE`, `DEFAULT_PAGE_SIZE`)
- **パッケージ**: 小文字 (`com.hatomask.domain.model`)

#### コード制限
- **ファイルサイズ**: 最大500行
- **メソッド長**: 最大50行
- **パラメータ数**: 最大7個
- **行の長さ**: 最大120文字
- **循環的複雑度**: 最大10

#### コードスタイル
- ✅ インデント: 4スペース
- ✅ if/else/for/while には中括弧必須
- ✅ switch文には`default`必須
- ❌ 空の`catch`ブロック禁止
- ❌ ワイルドカードインポート禁止
- ❌ 未使用のインポート禁止

#### Javadoc
- ⚠️ publicクラスとメソッドにはJavadoc推奨（警告レベル）

### コマンド

```bash
cd src/backend

# Checkstyle実行（mvn validateで自動実行）
mvn checkstyle:check

# ビルド時にも自動実行される
mvn clean install

# Checkstyleレポート生成
mvn checkstyle:checkstyle
# レポート: target/site/checkstyle.html
```

### Maven統合

Checkstyleは`validate`フェーズで自動実行されます。
エラーがある場合、ビルドは失敗します。

### IntelliJ IDEA統合

1. **Checkstyle-IDEAプラグインをインストール**
   - Settings → Plugins → "Checkstyle-IDEA"を検索してインストール

2. **設定ファイルを指定**
   - Settings → Tools → Checkstyle
   - Configuration Fileに`checkstyle.xml`を追加

3. **リアルタイムチェック有効化**
   - "Scan Scope"を"All sources including tests"に設定

## CI/CD統合

### GitHub Actions

`.github/workflows/`にワークフローを追加することで、プルリクエスト時に自動チェックできます：

```yaml
name: Lint

on: [pull_request]

jobs:
  frontend-lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: cd src/frontend && npm ci
      - run: cd src/frontend && npm run lint

  backend-lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: cd src/backend && mvn checkstyle:check
```

## トラブルシューティング

### フロントエンド

**Q: ESLintでパースエラーが出る**
```
A: tsconfig.jsonのパスが正しいか確認してください。
   .eslintrc.cjsの`parserOptions.project`を確認。
```

**Q: any型エラーが大量に出る**
```
A: 段階的に修正するか、一時的に
   // eslint-disable-next-line @typescript-eslint/no-explicit-any
   を使用。ただし、最終的には型を定義すべき。
```

### バックエンド

**Q: Checkstyleエラーでビルドが失敗する**
```
A: まずは警告レベルで実行して確認：
   mvn checkstyle:check -Dcheckstyle.violationSeverity=warning
```

**Q: 既存コードでエラーが多い**
```
A: checkstyle.xmlの一部ルールを一時的にコメントアウトし、
   段階的に修正していく。
```

## ルールのカスタマイズ

プロジェクトの状況に応じて、設定ファイルを調整できます：

- **フロントエンド**: `.eslintrc.cjs`の`rules`セクションを編集
- **バックエンド**: `checkstyle.xml`の各`<module>`を編集

ただし、チーム全体で合意を取ってから変更してください。
