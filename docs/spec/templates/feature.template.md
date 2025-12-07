# 実装計画: [機能名]

## 基本情報
- **Feature**: [機能の概要]
- **Scenario**: [シナリオ名]
- **Spec**: `docs/spec/features/[ファイル名].md`
- **テストリスト**: `docs/plans/[プラン名]/backend-testlist/`

---

## 📍 実装フェーズの原則

### Phase 1: Frontend実装 (MSW使用)
すべてのステップでフロントエンドを先に実装します。

**実装対象**:
- フロントエンドコンポーネント (`src/frontend/src/components/`)
- APIクライアント (`src/frontend/src/services/`)
- MSWモックハンドラー (`src/frontend/src/mocks/handlers.ts`)
- E2Eステップ定義 (`e2e/step-definitions/`)

**完了条件**:
- ✅ E2EテストがMSWで通ること
- ✅ フロントエンドが完全に動作すること

**禁止事項**:
- 🚫 バックエンド(`src/backend/`)は一切触らない

---

### Phase 2: Backend実装 (TDD)
フロントエンド完成後、バックエンドを実装します。

**🎯 目的**: **モックAPI(MSW)の動作を本物のバックエンドで再現する**

**実装対象**:
- バックエンドコード (`src/backend/`) のみ

**実装手順**:
1. **テストリスト作成**: Gherkinステップごとにテストリストを作成
   - 配置場所: `docs/plans/{feature_name}/backend-testlist/`
   - 命名規則: `{step_type}_{step_description}.md`
     - 例: `then_upload_success.md`, `and_preview_image.md`
2. **MSWレスポンス確認**: `src/frontend/src/mocks/handlers.ts` で形式を把握
3. **TDDサイクル**: testlist -> Red → Green → Refactor
4. **レスポンス形式厳守**: MSWと完全に同じJSON構造、ステータスコード、ヘッダーを返す

**🚫 禁止事項**:
- ❌ **E2Eステップ定義(`e2e/step-definitions/`)を変更しない**
  - フロントエンド実装でE2Eテストは既に完成している
- ❌ **フロントエンドコード(`src/frontend/`)を変更しない**
  - APIクライアントも変更しない
- ❌ **MSWハンドラー(`src/frontend/src/mocks/`)を変更しない**
  - モックAPIは既に完成している

---

## プロンプト参照

### Frontend実装時
- **プロンプト**: `docs/ai/prompts/tasks/03_implement_bdd_step.md`
- **指定**: "Phase 1 (Frontend) を実装してください"

### Backend実装時
- **プロンプト**: `docs/ai/prompts/tasks/03b_implement_backend_tdd.md`
- **前提**: フロントエンド実装が完了していること
- **開始**: テストリストの作成から始める

## Gherkinステップごとの実装要件

### Given [前提条件を記述]
**Frontend**
- **Components**: [コンポーネント名]
  - [実装内容を記述]
- **State**: [状態管理の詳細]
- **UI/UX**: [UI/UXの要件]

**Backend API**
- なし (または該当するAPIがあれば記述)

---

### When [ユーザーアクションを記述]
**Frontend**
- **Components**: [コンポーネント名]
  - [実装内容を記述]
- **UI/UX**: [UI/UXの要件]

**Backend API**
- なし (または該当するAPIがあれば記述)

---

### And [追加のアクションを記述]
**Frontend**
- **Components**: [コンポーネント名]
  - [実装内容を記述]
- **Validation**:
  - [バリデーション要件1]
  - [バリデーション要件2]

**Backend API**
- なし (または該当するAPIがあれば記述)

---

### Then [期待される結果を記述]

#### Phase 1: Frontend実装要件

**Components**: [コンポーネント名]
- [実装内容を記述]

**State**:
- [状態変数名] ([型]): [説明]

**Mock API (MSW)**:
- `handlers.ts` に [HTTPメソッド] [エンドポイント] のハンドラを追加
- レスポンス ([ステータスコード]) を返す
  ```json
  {
    "field1": "value1",
    "field2": "value2"
  }
  ```

---

#### Phase 2: Backend実装要件

**API Endpoint**: [HTTPメソッド] [エンドポイント]

**Request**: [リクエスト形式]
- [パラメータ名]: [型] ([説明])

**Response**: [ステータスコード] (MSWと同じ形式)
```json
{
  "field1": "value1",
  "field2": "value2"
}
```

**Database**
- **Tables**: [テーブル名]
- **Changes**: [変更内容]

**Validation & Logic**
- [バリデーション要件1]
- [ロジック要件1]
- [ロジック要件2]

---

### And [追加の期待結果を記述]

#### Phase 1: Frontend実装要件

**Components**: [コンポーネント名]
- [実装内容を記述]

**UI/UX**: [UI/UXの要件]

**Mock API (MSW)**:
- `handlers.ts` に [HTTPメソッド] [エンドポイント] のハンドラを追加
- [レスポンス形式を記述]

---

#### Phase 2: Backend実装要件

**API Endpoint**: [HTTPメソッド] [エンドポイント]

**Request**: [リクエスト形式]

**Response**: [ステータスコード]
- Content-Type: [コンテンツタイプ]
- Body: [ボディの説明]

**Logic**
- [ロジック要件1]
- [ロジック要件2]

## データモデル設計 (共通)

### Entities / Interfaces

**TypeScript (Frontend)**
```typescript
export interface [EntityName] {
  id: string;
  field1: string;
  field2: number;
  createdAt: string;
}

export interface ApiError {
  type: string;
  title: string;
  status: number;
  detail: string;
}
```

**Java (Backend)**
```java
// Entity
@Entity
@Table(name = "[table_name]")
public class [EntityName] {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String field1;
    
    @Column(nullable = false)
    private Long field2;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}

// DTO
public record [EntityName]Response(
    UUID id,
    String field1,
    Long field2,
    LocalDateTime createdAt
) {}
```

### DB Schema
```sql
CREATE TABLE [table_name] (
  id UUID PRIMARY KEY,
  field1 VARCHAR(255) NOT NULL,
  field2 BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 技術的課題・リスク
- [ ] **[課題1のタイトル]**: [課題の詳細と対策を記述]
- [ ] **[課題2のタイトル]**: [課題の詳細と対策を記述]
- [ ] **[課題3のタイトル]**: [課題の詳細と対策を記述]
- [ ] **CORS**: フロントエンド(Vite)とバックエンド(Spring Boot)のポートが異なるため、開発環境でのCORS設定が必要。
- [ ] **エラーハンドリング**: RFC 9457 形式のエラーレスポンスをフロントエンドで適切にパースして表示する共通処理が必要。

## 備考
- まずはHappy Path (正常系) を通すことを最優先とし、エラーハンドリングは基本的なものに留める。
- テスト駆動開発(TDD)を意識し、バックエンドはController/Service/Repositoryの単体テスト、フロントエンドはコンポーネントテストを書きながら進める。
