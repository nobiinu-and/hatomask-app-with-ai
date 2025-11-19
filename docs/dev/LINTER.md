````markdown
# Linter設定ガイド

このドキュメントでは、HatoMaskプロジェクトのLinter設定と使用方法について説明します。

Linterは[品質基準](./QUALITY_STANDARDS.md)を自動的に強制するためのツールです。コード品質の維持、セキュリティリスクの低減、チーム全体での一貫性確保に役立ちます。

## 目次

- [フロントエンド (ESLint)](#フロントエンド-eslint)
- [バックエンド (Checkstyle)](#バックエンド-checkstyle)
- [CI/CD統合](#cicd統合)
- [トラブルシューティング](#トラブルシューティング)
- [ルールのカスタマイズ](#ルールのカスタマイズ)

---

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
- **プライベート変数**: `_`プレフィックス (必要に応じて)

#### TypeScript規約
- ✅ `strict: true` を維持
- ❌ `any` 型の使用禁止 (どうしても必要な場合は `unknown` を使用)
- ✅ 型推論を活用 (明らかな場合は型注釈を省略)
- ✅ Optional Chaining (`?.`) とNullish Coalescing (`??`) を活用
- ✅ ユニオン型、交差型を適切に使用
- ✅ ジェネリクスで型安全性を確保

#### コード品質
- ❌ `console.log` は警告 (`console.warn`, `console.error` はOK)
- ❌ `debugger` 禁止
- ✅ `const` を優先、`let` は必要な場合のみ、`var` 禁止
- ✅ テンプレートリテラル推奨
- ✅ アロー関数を優先 (ただし `this` のコンテキストが必要な場合は通常の関数)
- ✅ 分割代入を活用
- ✅ スプレッド構文で配列・オブジェクトをコピー

#### React固有の規約
- ✅ フック依存配列を正確に指定 (`exhaustive-deps`)
- ✅ コンポーネントは関数コンポーネントで実装
- ✅ Propsの型は `interface` で定義
- ❌ 未使用の変数・インポート禁止
- ✅ `key` プロパティを適切に設定 (配列のインデックスは避ける)

#### セキュリティ規約
- ❌ `dangerouslySetInnerHTML` の使用は要レビュー
- ❌ `eval()` 禁止
- ✅ ユーザー入力は必ずバリデーション
- ✅ 機密情報をコードに直接記述しない

#### アクセシビリティ規約 (eslint-plugin-jsx-a11y)
- ✅ `img` 要素には `alt` 属性必須
- ✅ フォーム要素には適切な `label` またはARIA属性
- ✅ インタラクティブ要素にはキーボード操作サポート
- ✅ セマンティックHTML要素を使用

#### パフォーマンス規約
- ⚠️ 大きなコンポーネントは `React.memo` や `useMemo` を検討
- ⚠️ 高コストな計算は `useMemo` でメモ化
- ⚠️ コールバックは `useCallback` でメモ化
- ✅ 不要な再レンダリングを避ける

### コマンド

```bash
cd src/frontend

# Lintチェック実行
npm run lint

# 自動修正可能なエラーを修正
npm run lint:fix

# 特定のファイルをチェック
npm run lint -- src/components/PhotoCard.tsx

# 警告も含めてチェック
npm run lint -- --max-warnings 0
```

### VS Code統合

`.vscode/settings.json`に以下を追加すると、保存時に自動修正されます:

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
  ],
  "eslint.workingDirectories": [
    "src/frontend"
  ]
}
```

### 推奨ESLintプラグイン

プロジェクトで使用すべきプラグイン:

- `@typescript-eslint/eslint-plugin`: TypeScript固有のルール
- `eslint-plugin-react`: React固有のルール
- `eslint-plugin-react-hooks`: Reactフックのルール
- `eslint-plugin-jsx-a11y`: アクセシビリティルール
- `eslint-plugin-import`: インポート順序・整理
- `eslint-plugin-security`: セキュリティ脆弱性の検出

---

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
- ✅ 1行1文 (セミコロンで複数文を書かない)

#### Javadoc
- ⚠️ publicクラスとメソッドにはJavadoc推奨 (警告レベル)
- ✅ `@param`, `@return`, `@throws` を適切に記述
- ✅ クラスの責務と使用方法を記述

#### セキュリティ規約
- ❌ ハードコードされたパスワード・トークン禁止
- ✅ 入力バリデーションは必須
- ✅ SQLインジェクション対策 (PreparedStatement使用)
- ✅ ファイルパスのサニタイズ

#### パフォーマンス規約
- ✅ N+1問題の回避 (JOIN FETCHを使用)
- ✅ ページネーションの実装
- ✅ 適切なインデックスの使用
- ⚠️ ループ内でのDB操作は要レビュー

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

# 警告レベルで実行
mvn checkstyle:check -Dcheckstyle.violationSeverity=warning

# 特定のファイルのみチェック
mvn checkstyle:check -Dcheckstyle.includes=**/PhotoService.java
```

### Maven統合

Checkstyleは`validate`フェーズで自動実行されます。
エラーがある場合、ビルドは失敗します。

`pom.xml`の設定例:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <consoleOutput>true</consoleOutput>
        <failsOnError>true</failsOnError>
        <violationSeverity>warning</violationSeverity>
    </configuration>
    <executions>
        <execution>
            <id>validate</id>
            <phase>validate</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### IntelliJ IDEA統合

1. **Checkstyle-IDEAプラグインをインストール**
   - Settings → Plugins → "Checkstyle-IDEA"を検索してインストール

2. **設定ファイルを指定**
   - Settings → Tools → Checkstyle
   - Configuration Fileに`checkstyle.xml`を追加

3. **リアルタイムチェック有効化**
   - "Scan Scope"を"All sources including tests"に設定
   - "Treat Checkstyle errors as warnings"をオフ (エラーとして扱う)

---

## CI/CD統合

### GitHub Actions

`.github/workflows/lint.yml`にワークフローを追加することで、プルリクエスト時に自動チェックできます:

```yaml
name: Lint

on:
  pull_request:
    branches: [main, develop]
  push:
    branches: [main, develop]

jobs:
  frontend-lint:
    name: Frontend Lint (ESLint)
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: src/frontend
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: src/frontend/package-lock.json
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run ESLint
        run: npm run lint -- --max-warnings 0
      
      - name: Run TypeScript check
        run: npm run type-check

  backend-lint:
    name: Backend Lint (Checkstyle)
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: src/backend
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Run Checkstyle
        run: mvn checkstyle:check
      
      - name: Generate Checkstyle report
        if: failure()
        run: mvn checkstyle:checkstyle
      
      - name: Upload Checkstyle report
        if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: checkstyle-report
          path: src/backend/target/site/checkstyle.html
```

### プルリクエスト時の自動レビュー

GitHub Actionsの結果をプルリクエストにコメントとして追加することもできます:

```yaml
- name: Comment PR (ESLint)
  if: failure()
  uses: actions/github-script@v7
  with:
    script: |
      github.rest.issues.createComment({
        issue_number: context.issue.number,
        owner: context.repo.owner,
        repo: context.repo.repo,
        body: '❌ ESLint check failed. Please fix the errors and push again.'
      })
```

---

## トラブルシューティング

### フロントエンド

#### Q: ESLintでパースエラーが出る

```
Error: ESLint couldn't parse the file
```

**解決策:**
- `tsconfig.json`のパスが正しいか確認
- `.eslintrc.cjs`の`parserOptions.project`を確認
- `tsconfig.json`の`include`にファイルが含まれているか確認

```javascript
// .eslintrc.cjs
parserOptions: {
  project: './tsconfig.json',
  tsconfigRootDir: __dirname,
}
```

#### Q: any型エラーが大量に出る

**解決策:**
1. 段階的に修正する
2. 一時的に`@typescript-eslint/no-explicit-any`を無効化
3. 長期的には型定義を追加

```typescript
// 一時的な回避 (推奨しない)
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const data: any = await fetchData();

// 推奨: unknown を使用して型ガードを実装
const data: unknown = await fetchData();
if (isValidData(data)) {
  // 型安全に使用
}
```

#### Q: exhaustive-depsの警告が出る

```
React Hook useEffect has a missing dependency
```

**解決策:**
- 依存配列に必要な変数を追加
- 不要な依存を削除するためにコードをリファクタリング
- どうしても必要な場合は`useCallback`/`useMemo`でメモ化

```typescript
// ❌ 警告が出る
useEffect(() => {
  fetchData(userId);
}, []);

// ✅ 修正
useEffect(() => {
  fetchData(userId);
}, [userId]);

// または無限ループを避けるためにメモ化
const fetchDataCallback = useCallback(() => {
  fetchData(userId);
}, [userId]);

useEffect(() => {
  fetchDataCallback();
}, [fetchDataCallback]);
```

### バックエンド

#### Q: Checkstyleエラーでビルドが失敗する

**解決策:**
1. まずは警告レベルで確認

```bash
mvn checkstyle:check -Dcheckstyle.violationSeverity=warning
```

2. レポートを生成して詳細を確認

```bash
mvn checkstyle:checkstyle
# target/site/checkstyle.html をブラウザで開く
```

3. 段階的に修正

#### Q: 既存コードでエラーが多すぎる

**解決策:**
1. `checkstyle.xml`の一部ルールを一時的に無効化
2. 新規コードから適用開始
3. 段階的に既存コードを修正

```xml
<!-- 一時的に無効化 -->
<module name="JavadocMethod">
  <property name="severity" value="warning"/>
</module>
```

#### Q: ファイル長エラーが出るが、どうしても超えてしまう

**解決策:**
- クラスの責務が多すぎる可能性 → 分割を検討
- どうしても必要な場合は suppressions.xml で除外

```xml
<!-- suppressions.xml -->
<suppressions>
  <suppress checks="FileLength" files="LegacyService.java"/>
</suppressions>
```

#### Q: Javadoc警告を抑制したい

**解決策:**
1. package-info.javaには必ずJavadocを記述
2. 内部クラスは`@SuppressWarnings("javadoc")`で抑制可能
3. checkstyle.xmlでprivate/protectedメソッドは除外

```java
// パッケージレベルのJavadoc
/**
 * This package contains domain models for photo management.
 */
package com.hatomask.domain.model;
```

---

## ルールのカスタマイズ

プロジェクトの状況に応じて、設定ファイルを調整できます。

### フロントエンド (.eslintrc.cjs)

```javascript
module.exports = {
  rules: {
    // 既存ルールの厳格度を変更
    'no-console': ['warn', { allow: ['warn', 'error'] }],
    
    // ルールを無効化 (非推奨)
    '@typescript-eslint/no-explicit-any': 'off',
    
    // カスタムルールを追加
    'max-lines': ['error', { max: 300, skipBlankLines: true }],
    'max-lines-per-function': ['error', { max: 50 }],
    'complexity': ['error', { max: 10 }],
  },
};
```

### バックエンド (checkstyle.xml)

```xml
<!-- ルールの厳格度を変更 -->
<module name="JavadocMethod">
  <property name="severity" value="warning"/>
</module>

<!-- ルールを無効化 -->
<!--
<module name="MagicNumber"/>
-->

<!-- カスタムルールを追加 -->
<module name="MethodLength">
  <property name="max" value="50"/>
</module>
```

### チーム全体での合意

**重要:** ルールを変更する場合は、必ずチーム全体で合意を取ってください。

1. 変更理由を明確にする
2. プルリクエストで提案
3. チームレビューを経て承認
4. ドキュメント更新

---

## 品質基準との関連

このLinter設定は、以下の品質基準を自動的に強制します:

- **可読性**: 命名規則、コードスタイルの統一
- **保守性**: コードの複雑度制限、ファイル長制限
- **セキュリティ**: 危険なパターンの検出
- **パフォーマンス**: ベストプラクティスの強制
- **アクセシビリティ**: ARIA属性、セマンティックHTML

詳細は[品質基準](./QUALITY_STANDARDS.md)を参照してください。

---

## 関連ドキュメント

- [品質基準](./QUALITY_STANDARDS.md) - コード品質の基準とベストプラクティス
- [コーディング規約](./CODING_STANDARDS.md) - 命名規則、フォーマット、言語固有の規約
- [開発プロセス](./DEVELOPMENT.md) - BDD/TDD開発フロー

````
