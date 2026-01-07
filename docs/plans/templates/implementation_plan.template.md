# 実装計画: [シナリオ名]

## 基本情報

- **Feature**: [Feature 名]
- **Scenario**: [シナリオ名]
- **Spec**: [機能仕様へのリンク]

## 入力（候補一覧）

このシナリオに関係しうるドキュメントを列挙します（全てを使う必要はありません）。

### ドメインモデル（候補）

- `docs/spec/models/[model].md`

### OpenAPI 仕様（候補）

- `docs/spec/api/[api].yaml`

## このシナリオで採用する仕様（選択結果）

### ドメインモデル

- **Primary**: `docs/spec/models/{model_name}.md`
- **Related（任意）**: `docs/spec/models/{related_model_name}.md`（複数可）

### OpenAPI

- **Primary**: `docs/spec/api/{api_name}.yaml`
- **Related（任意）**: `docs/spec/api/{related_api_name}.yaml`（複数可）

## OpenAPI 仕様

参照（このシナリオで採用する OpenAPI）:

- Primary: `docs/spec/api/{api_name}.yaml`
- Related（任意）: `docs/spec/api/{related_api_name}.yaml`

### エンドポイント一覧

| Spec(OpenAPI)                             | エンドポイント          | メソッド | 概要   | 使用ステップ |
| ----------------------------------------- | ----------------------- | -------- | ------ | ------------ |
| `docs/spec/api/{api_name}.yaml` (Primary) | /api/v1/[resource]      | POST     | [概要] | [ステップ名] |
| `docs/spec/api/{related_api_name}.yaml`   | /api/v1/[resource]/{id} | GET      | [概要] | [ステップ名] |

補足:

- Spec(OpenAPI) 列には、該当エンドポイントが定義されている YAML を必ず記載する
- 可能なら `#/paths/...` の JSON Pointer も併記して、参照箇所を一意にする（例: `docs/spec/api/photos.yaml#/paths/~1photos/post`）

### 主要スキーマ

- **[Resource]Response**: [説明]
- **[Resource]Request**: [説明]
- **ProblemDetails**: エラーレスポンス（RFC 9457 準拠）

## ステップ別実装分類

縦切り実装サイクル（Task06）のため、各ステップを分類します。

| ステップ          | 分類         | API                 | 状態依存    | 備考   |
| ----------------- | ------------ | ------------------- | ----------- | ------ |
| Given: [テキスト] | フロントのみ | -                   | なし        | [備考] |
| When: [テキスト]  | フロントのみ | -                   | なし        | [備考] |
| And: [テキスト]   | フロントのみ | -                   | UI 状態     | [備考] |
| Then: [テキスト]  | **API 依存** | POST /[resource]    | UI 状態     | [備考] |
| And: [テキスト]   | **API 依存** | GET /[resource]/:id | **DB 状態** | [備考] |

### 凡例

- **フロントのみ**: API 呼び出しなし、フロントエンド実装のみ
- **API 依存**: バックエンド API を呼び出す（フロント+バックエンド両方実装必要）
- **状態依存**:
  - UI 状態: フロントエンドの React State 等に依存
  - DB 状態: 前ステップで DB に保存したデータに依存

## 推奨実装グルーピング

縦切り実装サイクル（Task06）で実装する際の推奨グループ分けです。
AI との相談時に調整可能です。

### グループ 1: [グループ名]（フロントのみ）

- [ステップ 1]
- [ステップ 2]
- [ステップ 3]

**実装内容**:

- [実装する内容の概要]

**API**: なし

**グルーピング理由**:

- [理由]

### グループ 2: [グループ名]（API: [エンドポイント]）

- [ステップ 4]
- [ステップ 5]

**実装内容**:

- フロント: [概要]
- バックエンド: [概要]

**API**: [エンドポイント一覧]

**グルーピング理由**:

- [理由、特に状態依存がある場合は明記]

### グループ 3: [グループ名]

- [ステップ 6]
- [ステップ 7]

**実装内容**:

- [概要]

**API**: [エンドポイント一覧]

**グルーピング理由**:

- [理由]

## Gherkin ステップごとの実装要件

### [Given/When/Then] [ステップのテキスト]

**Frontend**

- **Components**: [変更・作成するコンポーネント]
- **State**: [状態管理の変更点]
- **UI/UX**: [表示内容、インタラクション]

**Backend API**

- **Endpoint**: `[METHOD] [PATH]`
- **Request**: [パラメータ、ボディ]
- **Response**: [ステータスコード、レスポンスボディ構造]

**Database**

- **Tables**: [関連テーブル]
- **Changes**: [スキーマ変更、クエリ内容]

**Validation & Logic**

- [バリデーションルール]
- [ビジネスロジックの要点]

---

### [Given/When/Then] [ステップのテキスト]

_(次のステップも同様に記述)_

---

## データモデル (共通)

**参照**: `docs/spec/models/{model_name}.md`

ドメインモデルで定義されたエンティティ、バリューオブジェクト、リポジトリインターフェースを使用します。

### 追加が DB Schema

```sql
-- Additional table definitions or changes (if any)
```

### 追加の DTO (フロントエンド用)

```typescript
// Additional TypeScript interfaces for frontend (if any)
```

## 技術的課題・リスク

- [ ] [課題 1]
- [ ] [課題 2]

## 備考

- [その他メモ]
