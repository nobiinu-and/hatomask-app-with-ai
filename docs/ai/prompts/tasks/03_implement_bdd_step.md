---
description: BDDの1ステップを実装するためのプロンプトテンプレート(AI協働プロトコル準拠)
---

# BDDステップ実装 (1ステップ厳守)

## 依頼内容
現在実装中のシナリオにおける、**以下の1ステップのみ**を実装してください。

## 対象ステップ
- **Featureファイル**: `[Featureファイルのパス]`
- **現在のステップ**:
  ```gherkin
  [Given/When/Then] [ステップのテキスト]
  ```

## 作業手順 (Red-Green-Refactor)
以下の手順を順番に実行し、各段階で報告してください。

1.  **ステップ定義の作成 (Red)**
        - `e2e/step-definitions/` にステップ定義を追加する。
        - E2Eテストを実行し、**Red（失敗）であることを確認して報告**する。失敗を作るために無理やり `throw new Error(...)` を書くのは避けること。
        - ステップ定義は「プロダクトが実装されている」前提で現実的なセレクタ／ラベルを使うこと（`to be determined` のような曖昧なプレースホルダは使わない）。
            - セマンティックなクエリを優先する：`getByRole`、`getByLabelText`、`getByText` など。
            - Gherkin の文言をそのままラベルに反映する（例: ステップに `写真を選択` があれば `getByRole('button', { name: '写真を選択' })` を使う）。
        - 例（NG / OK）:
            - NG: `this.page.getByRole('button', { name: 'to be determined' });`
            - OK: `await this.page.getByRole('button', { name: '写真を選択' }).click(); // TODO: implement button (features/photo_upload.feature:12)`
        - **注意**: ステップ作成時点ではプロダクトの実装コードを書かないこと。

2.  **モック/ユニットテスト作成 (Red)** (必要な場合)
    - フロントエンドのユニットテストを作成する。
    - テストが失敗することを確認して報告する。ここでも任意で `throw` を使うのではなく、未実装であることが明確な失敗（未定義関数の呼び出し／期待要素の不在など）を作る。

3.  **実装 (Green)**
    - テストを通すための**最小限の実装**を行う。
    - ユニットテストとE2Eテスト(このステップまで)が通ることを確認する。

4.  **リファクタリング**
    - コードを整理する。

## 厳守事項 (AI協働プロトコル)
- 🚫 **先読み禁止**: 依頼されたステップ以外のコード(次のステップなど)は一切書かないこと。
- 🛑 **確認義務**: Red状態、Green状態になった時点で必ず停止し、ユーザーの確認を求めること。Redを作る際は、
    - 無理に例外を投げて失敗させるのではなく、上の「優先する Red の作り方」に従ってください。
- 📝 **最小実装**: 必要以上の機能を実装しないこと。

## ファイルアップロード実装時の前提と注意

- **e2e 配下のリソース前提**: ファイルアップロードに関するステップを実装する際は、テスト用のリソースファイルがリポジトリの `e2e/fixtures` のようなディレクトリに存在することを前提にしてください。新たにバイナリを生成したり、ユーザ入力の代わりに Buffer を用いてバイナリを埋め込む実装は行わないでください。
- **Buffer を使わない**: サンプルファイルを扱う際は、Node の `Buffer` に直接読み込む方法を避け、テストランナー／ブラウザ自動化（Playwrightなど）のファイル入力 API にファイルパスを渡す方法を使ってください。

例（Playwright + Cucumber のステップ定義 TypeScript）:

```ts
// e2e/fixtures にある sample.jpg をアップロードする例
await this.page.locator('input[type="file"]').setInputFiles('e2e/fixtures/sample.jpg');

// 複数ファイルをセットする場合
await this.page.locator('input[type="file"]').setInputFiles([
    'e2e/fixtures/sample.jpg',
    'e2e/fixtures/sample2.jpg'
]);
```

ポイント:
- `setInputFiles` などの API はファイルパスを受け取り、内部で適切にファイルを扱うため Buffer を使う必要はありません。
- サンプルファイルはテスト専用リソース（`e2e/fixtures`）に置き、ステップ実装ではそのパスを直接参照するだけにしてください。
- CI 環境でテストが実行されることを考慮し、相対パスはテストルートから有効なものを使う（必要なら `path.join(process.cwd(), 'e2e/fixtures/sample.jpg')` を使用）。
