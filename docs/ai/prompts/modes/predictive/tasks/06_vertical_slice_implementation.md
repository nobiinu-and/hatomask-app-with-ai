---
description: 柔軟な粒度で縦切り実装を行うためのプロンプト（AIによる粒度提案と相談プロトコル）
---

# 縦切り実装サイクル (Phase 6)

## 目的

**1シナリオを柔軟な粒度で縦切り実装し、API依存ステップのみバックエンド実装を行う。**

### 縦切り実装とは

**UI層からドメイン層、データアクセス層まで、機能の一部を垂直に貫通して実装する手法**です。

- **従来の水平分割**: フロントエンド全体→バックエンド全体を順に実装
- **縦切り（Vertical Slice）**: 1つの機能単位でUI→API→ドメイン→DBまで一気に実装

**メリット**:
- 早期にエンドツーエンドで動作確認できる
- フロント・バックエンド間のAPI契約ミスマッチを早期発見
- 段階的なフィードバックと修正が可能

### Phase 6の特徴

実装粒度は状況に応じて選択：ステップ単位/APIグループ単位/シナリオ単位。
AIが依存関係を分析し、3パターンを提案、人間が決定します。

## 前提条件チェック

- [ ] Phase 4で実装計画が作成されている
- [ ] Phase 5でBackend Stubが作成されている
- [ ] OpenAPI仕様が確定している (`docs/spec/api/{feature_name}.yaml`)
- [ ] ドメインモデルが確定している (`docs/spec/models/{feature_name}.md`)

## 実装粒度相談プロトコル

### AIの責務: 依存関係分析と粒度提案

Phase 6開始前に、以下のドキュメントを作成してください。

#### 1. ステップ依存関係分析

**テンプレート**: `docs/plans/templates/step-dependency-analysis.template.md`

**作成ファイル**: `docs/plans/[Spec名]_[シナリオ識別子]_dependency.md`

**内容**:
- Gherkinシナリオの各ステップの分類（フロントのみ/API依存）
- 状態依存の分析（UI状態/DB状態）
- Mermaid図による依存関係の可視化
- 重要な依存関係の詳細説明

#### 2. 実装粒度提案

**テンプレート**: `docs/plans/templates/implementation-granularity-proposal.template.md`

**作成ファイル**: `docs/plans/[Spec名]_[シナリオ識別子]_granularity.md`

**記載内容**:
- **選択パターンと理由**: パターンA/B/Cから最適なものを選択し、理由を明記
- **実装グルーピング**: 具体的なグループ分け（ステップ番号、API、理由）
- **実装上の注意点**: E2E状態依存、Stub継続判断、テストデータ準備方針

**ポイント**:
- 通常はパターンB（APIグループ単位）を推奨
- 依存関係分析結果を基に、自然なグループ分けを提案
- 詳細な実装戦略は`docs/dev/VERTICAL_SLICE_STRATEGY.md`を参照

### 人間の責務: 粒度決定

AIの提案を確認し、以下を決定してください：

1. **実装粒度の選択**: パターンA/B/Cのいずれか
2. **調整指示**: 必要に応じてグルーピングをカスタマイズ
3. **実装開始承認**: 決定した粒度で実装サイクル開始

**決定例**:
```
「パターンBで進めてください。ただし、グループ2はステップ4-6まで含めてください」
```

## 実装サイクル詳細

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

### API依存ステップ（詳細フロー）

#### フェーズ1: フロント実装

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

#### フェーズ2: バックエンド実装（TDD）

```
【テストリスト作成】
1. ドメイン層テストリスト作成
   - テンプレート: `docs/plans/templates/domain-testlist.template.md`
   - 出力: `testlists/[Spec名]_[シナリオ識別子]_domain_testlist.md`
   - 内容: Entity/ValueObject、Repository のテストケース

2. API層テストリスト作成
   - テンプレート: `docs/plans/templates/api-testlist.template.md`
   - 出力: `testlists/[Spec名]_[シナリオ識別子]_api_testlist.md`
   - 内容: UseCase、Controller のテストケース

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

#### フェーズ3: 統合確認

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

## 実装手順（パターンB例）

### グループ1: 初期表示（フロントのみ）

**対象ステップ**:
- Given: ユーザーがHatoMaskアプリケーションにアクセスしている
- When: ユーザーが「写真を選択」ボタンをクリックする
- And: ユーザーがファイルサイズ5MBのJPEGファイルを選択する

**実装内容**:
1. Step Definition作成（3ステップ分）
2. App.tsx初期実装
3. PhotoUploadButton.tsx実装
4. ファイル選択UI実装
5. E2E実行（バックエンドStub接続）

### グループ2: アップロード〜プレビュー（POST + GET）

**対象ステップ**:
- Then: アップロードが成功する
- And: プレビューエリアに選択した画像が表示される

**実装内容**:

#### フロント実装
1. Step Definition作成（2ステップ分）
2. usePhotoUpload hook実装
3. PhotoPreview.tsx実装
4. E2E実行（バックエンドStub接続）

#### バックエンド実装
6. Photo Entity TDD
7. PhotoRepository Interface + 実装 TDD
8. UploadPhotoUseCase TDD
9. GetPhotoUseCase TDD
10. PhotoController（POST /photos）Stub置き換え
11. PhotoController（GET /photos/:id）Stub置き換え

#### 統合確認
12. E2E実行（実装済みバックエンド、ステップ1-5通過確認）
13. レスポンス形式確認

### グループ3: ダウンロード（GET再利用）

**対象ステップ**:
- When: ユーザーが「ダウンロード」ボタンをクリックする
- Then: 元の画像がダウンロードされる

**実装内容**:

#### フロント実装
1. Step Definition作成（2ステップ分）
2. DownloadButton.tsx実装
3. usePhotoDownload hook実装
4. E2E実行（バックエンドStub接続）

#### バックエンド実装
5. DownloadPhotoUseCase TDD（GetPhotoを再利用）
6. PhotoController調整（Content-Dispositionヘッダー）

#### 統合確認
7. E2E実行（実バックエンド、ステップ1-7全通過）
8. シナリオ完結確認

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

**バックエンド** (API依存ステップの場合):
- `src/backend/.../domain/model/Photo.java` - Entity
- `src/backend/.../domain/repository/PhotoRepository.java` - Repository Interface
- `src/backend/.../infrastructure/repository/PhotoRepositoryImpl.java` - Repository実装
- `src/backend/.../application/usecase/UploadPhotoUseCase.java` - UseCase
- `src/backend/.../presentation/controller/PhotoController.java` - Stub置き換え

### テスト結果
- ✅ E2E: グループ[N]のステップまで通過
- ✅ ユニットテスト: 全通過
- ✅ 統合確認: 実装済みバックエンドで動作確認済み

### 次のアクション
- グループ[N+1]の実装へ進む
```

## 参考資料

- **縦切り戦略**: `docs/dev/VERTICAL_SLICE_STRATEGY.md`
- **実装計画**: `docs/plans/[Spec名]_[シナリオ識別子].md`
- **OpenAPI仕様**: `docs/spec/api/{feature_name}.yaml`
- **AI協働プロトコル**: `docs/dev/DEVELOPMENT.md#ai協働プロトコル`

## 注意事項

### AI協働プロトコルの遵守

- **1プロンプト1ステップ**: フロント実装時は1ステップずつ
- **先読み実装の禁止**: 依頼されたグループ以外は実装しない
- **完了確認の義務化**: 各グループ完了時に停止し、人間の確認を求める
- **Red状態の提示**: テスト失敗(Red)を必ず確認・提示

### 柔軟性の確保

- 提案した粒度は絶対ではない
- 人間の判断でグルーピングを調整可能
- 状況に応じてパターンA/B/Cを切り替え可能

### OpenAPI準拠

- バックエンド実装はOpenAPI仕様に忠実に
- フロントエンドとの完全一致を維持
- Phase 3で確定したAPI契約が基準
