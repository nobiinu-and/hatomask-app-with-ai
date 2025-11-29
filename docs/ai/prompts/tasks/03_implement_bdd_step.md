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
        - E2Eテストを実行し、**Red（失敗）であることを確認して報告**する。 ただし、失敗を作るために無理やり `throw new Error(...)` を書くことは避けること。
        - 優先する Red の作り方（推奨順）:
            1. ステップを未実装（未定義）にして、テストフレームワークが自然に未定義/未実装として失敗させる。
            2. プロダクト側が未実装のために自然に失敗するような**意味のあるアサーション**を書く（たとえば、まだ存在しない UI セレクタの存在を期待するなど）。失敗メッセージは必ず実装不足を示す説明を付ける。
            3. フレームワークがサポートする「保留／pending」メカニズムを使う（例: `pending()`、`this.pending()`、あるいはテストフレームワークの明示的な保留表現）。
        - どの場合でも、失敗理由に「未実装（TODO: implement <thing>）」と分かりやすく書き残すこと。
        - **注意**: まだプロダクトの実装コードは書かないこと。

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

## 追加: AIへの具体的指示テンプレート（例）
- **NG（しないでください）**: "Implement step by throwing an Error('not implemented') so test is red."
- **OK（推奨）**: "Create the step definition file and mark it pending or add a minimal assertion that will fail naturally because the product code is missing. Include a clear TODO comment linking to the feature and what should be implemented. After confirming the test fails (Red), stop and ask for confirmation before implementing." 

## 追加: ステップ実装例（テンプレート）
- **コメント版（保留を使う場合）**: `// TODO: implement header component (see feature X)
    // pending: step intentionally left pending until product code implemented`
- **アサーション版（自然に失敗させる場合）**: `expect(await page.$('selector-for-new-ui')).not.toBeNull(); // currently fails because component not created — TODO implement`

これらをAIプロンプトに明示すれば、AI実装者は無理に `throw` して Red を作るのではなく、プロダクト未実装であることを示す自然な失敗を残すようになります。
