---
description: 柔軟な粒度で縦切り実装を行うためのプロンプト（AIによる粒度提案と相談プロトコル）
---

# 縦切り実装サイクル (Task06)

## 目的

**1 シナリオを柔軟な粒度で縦切り実装し、API 依存ステップのみバックエンド実装を行う。**

### 縦切り実装とは

**UI 層からドメイン層、データアクセス層まで、機能の一部を垂直に貫通して実装する手法**です。

- **従来の水平分割**: フロントエンド全体 → バックエンド全体を順に実装
- **縦切り（Vertical Slice）**: 1 つの機能単位で UI→API→ ドメイン →DB まで一気に実装

**メリット**:

- 早期にエンドツーエンドで動作確認できる
- フロント・バックエンド間の API 契約ミスマッチを早期発見
- 段階的なフィードバックと修正が可能

### Task06 の特徴

実装粒度は状況に応じて選択：ステップ単位/API グループ単位/シナリオ単位。
AI が依存関係を分析し、3 パターンを提案、人間が決定します。

## 前提条件チェック

- [ ] Task04 で実装計画が作成されている
- [ ] Task05 で Backend Stub が作成されている
- [ ] 実装計画の「このシナリオで採用する仕様（選択結果）」で、利用する仕様(Model, API)が確定している
- [ ] Task06 の実装・テスト・修正は **実装計画で採用されたモデル/API** を参照元とする（計画にないモデル/API を勝手に追加しない、追加が必要になった場合は相談をする）

## 実装粒度相談プロトコル

### AI の責務: 対話ベースの依存関係分析と粒度提案

Task06 では、**事前ドキュメント生成は原則省略**し、チャット対話の中で依存関係分析と粒度提案を行います。

AI は、実装計画（Task04）と対象シナリオ（Gherkin）を参照し、まず以下を **チャット上で簡潔に提示**してください。

#### 1. ステップの分類（表）

- 各ステップを「フロントのみ」「API 依存」に分類
- 状態依存（UI 状態 / DB 状態）の有無を明記

#### 2. 粒度パターンの提案（A/B/C）

- パターン A/B/C のうち推奨を 1 つ提示し、理由を 1〜3 行で説明
- 具体的なグルーピング案（グループ番号、含めるステップ範囲、関係する API）を提示

#### 3. 注意点（最小）

- E2E の状態依存（テストデータ準備、Stub 継続可否など）でリスクがある点だけを列挙
- 詳細な実装戦略は `docs/dev/guidelines/vertical-slice.md` を参照

#### （任意）テンプレを用いたドキュメント化

以下に該当する場合のみ、必要に応じてテンプレを使ってドキュメント化してもよい（必須ではない）。

- 状態依存が複雑で、口頭説明だと抜け漏れが出やすい
- 複数 API が絡み、グルーピングの合意形成が難しい
- 途中で方針変更が入り、経緯を記録として残したい

テンプレート:

- `docs/plans/templates/step-dependency-analysis.template.md`
- `docs/plans/templates/implementation-granularity-proposal.template.md`

### 人間の責務: 粒度決定

AI の提案を確認し、以下を決定してください：

1. **実装粒度の選択**: パターン A/B/C のいずれか
2. **調整指示**: 必要に応じてグルーピングをカスタマイズ
3. **実装開始承認**: 決定した粒度で実装サイクル開始

**決定例**:

```
「パターンBで進めてください。ただし、グループ2はステップ4-6まで含めてください」
```

## 実装サイクル詳細

### グループ完了条件（原則）

- **完了の定義**: 対象グループの E2E が通っていること
- **フロントのみグループ**: バックエンド Stub 接続で、対象ステップまで通過
- **API 依存グループ**: 実装済みバックエンドで、対象ステップまで通過
- **例外**: 上記を満たせずに先へ進めたい場合、AI は実装を続けずに停止し、理由・代替案・戻し方を添えて人間へ事前相談すること

### フロントのみステップ

```
1. Step Definition作成（e2e/step-definitions/）
   → Red確認
2. Unit Test作成（src/frontend/src/test/、必要な場合）
   → Red確認
3. Implementation（src/frontend/src/）
   → Green確認
4. Refactor
5. E2E実行（バックエンドStub接続）
   → このステップまで通過確認
```

### API 依存ステップ（詳細フロー）

#### ステージ 1: フロント実装

```
1. Step Definition作成
   - `e2e/step-definitions/steps.ts` に追加
   - Red確認（テスト失敗）

2. Unit Test作成（必要な場合）
   - Reactコンポーネントのテスト
   - カスタムフックのテスト
   - Red確認

4. Implementation
   - コンポーネント実装
   - API呼び出し実装
   - State管理実装

5. E2E実行（バックエンドStub接続）
   - `cd e2e && npm test`
   - 該当ステップまで通過確認
```

#### ステージ 2: バックエンド実装（TDD）

```
【テストリスト作成】
1. ドメイン層テストリスト作成
   - テンプレート: `docs/plans/templates/domain-testlist.template.md`
   - 出力: `docs/plans/[機能仕様ファイル名]_[シナリオ識別子]_domain_testlist.md`
   - 内容: Entity/ValueObject、Repository のテストケース

2. API層テストリスト作成
   - テンプレート: `docs/plans/templates/api-testlist.template.md`
   - 出力: `docs/plans/[機能仕様ファイル名]_[シナリオ識別子]_api_testlist.md`
   - 内容: UseCase、Controller のテストケース

※テストリストの前提となる **対象エンドポイント / スキーマ / ドメインモデル** は、実装計画の「エンドポイント一覧」および「このシナリオで採用する仕様（選択結果）」を参照して確定させる。

【ドメイン層】
3. Entity/ValueObject TDD
   - テストリストに従ってテスト作成: `src/backend/src/test/.../domain/model/`
   - Red確認
   - 実装: `src/backend/src/main/.../domain/model/`
   - Green確認
   - Refactor
   - テストリストの該当項目にチェック

4. Repository Interface定義
   - `src/backend/src/main/.../domain/repository/`

5. Repository実装 TDD
   - テストリストに従ってテスト作成: `src/backend/src/test/.../infrastructure/repository/`
   - Red確認
   - 実装: `src/backend/src/main/.../infrastructure/repository/`
   - Green確認
   - Refactor
   - テストリストの該当項目にチェック

【アプリケーション層】
6. UseCase TDD
   - テストリストに従ってテスト作成: `src/backend/src/test/.../application/usecase/`
   - Red確認
   - 実装: `src/backend/src/main/.../application/usecase/`
   - Green確認
   - Refactor
   - テストリストの該当項目にチェック

【プレゼンテーション層】
7. Controller実装（Stub置き換え）
   - テストリストに従ってテスト作成: `src/backend/src/test/.../presentation/controller/`
   - Red確認
   - **Stub実装を本実装に置き換え**
   - TODOコメント削除
   - UseCaseを呼び出す実装
   - Green確認
   - Refactor
   - テストリストの該当項目にチェック
```

#### ステージ 3: 統合確認

```
1. バックエンド起動（本実装）
   - `cd src/backend && mvn spring-boot:run`

2. E2E実行（実装済みバックエンド）
   - `cd e2e && npm test`
   - 該当ステップまで通過確認

3. レスポンス形式確認
   - ネットワークタブでレスポンス確認
   - OpenAPI仕様と完全一致していることを検証

5. 次のグループへ
   - 全テスト通過後、次のグループの実装へ進む
```

## 出力形式

### 各サイクル完了報告

```markdown
## グループ[N]完了

### 実装範囲

- ステップ[X]-[Y]

### 作成・変更ファイル

**フロントエンド**:

- `e2e/step-definitions/steps.ts` - ステップ定義追加
- `src/frontend/src/components/[Component].tsx` - コンポーネント実装
- `src/frontend/src/hooks/[hook].ts` - カスタムフック実装

**バックエンド** (API 依存ステップの場合):

- `src/backend/.../domain/model/Photo.java` - Entity
- `src/backend/.../domain/repository/PhotoRepository.java` - Repository Interface
- `src/backend/.../infrastructure/repository/PhotoRepositoryImpl.java` - Repository 実装
- `src/backend/.../application/usecase/UploadPhotoUseCase.java` - UseCase
- `src/backend/.../presentation/controller/PhotoController.java` - Stub 置き換え

### テスト結果

- ✅ E2E: グループ[N]のステップまで通過
- ✅ ユニットテスト: 全通過
- ✅ 統合確認: （API 依存グループの場合）実装済みバックエンドで通過

### 次のアクション

- グループ[N+1]の実装へ進む
```

## 参考資料

- **縦切り戦略**: `docs/dev/guidelines/vertical-slice.md`
- **実装計画**: `docs/plans/[機能仕様ファイル名]_[シナリオ識別子].md`
- 実装計画の「このシナリオで採用する仕様（選択結果）」に記載された OpenAPI（Primary/Related）を参照
- **AI 協働プロトコル**: `docs/dev/howto/development.md`

## 注意事項

### AI 協働プロトコルの遵守

- **1 プロンプト 1 ステップ**: フロント実装時は 1 ステップずつ
- **先読み実装の禁止**: 依頼されたグループ以外は実装しない
- **完了確認の義務化**: 各グループ完了時に停止し、人間の確認を求める
- **E2E 通過の義務化**: 各グループ完了条件は「グループ単位での E2E 通過」（例外は事前相談）
- **Red 状態の提示**: テスト失敗(Red)を必ず確認・提示

### 柔軟性の確保

- 提案した粒度は絶対ではない
- 人間の判断でグルーピングを調整可能
- 状況に応じてパターン A/B/C を切り替え可能

### OpenAPI 準拠

- バックエンド実装は OpenAPI 仕様に忠実に
- フロントエンドとの完全一致を維持
- Task03 で確定した API 契約（OpenAPI）が基準
