# 仕様 (Spec) Overview

このディレクトリには、HatoMask アプリケーションの機能仕様を記述します。

## 構成

- `features/` - 各機能の詳細仕様（受け入れ基準を含む）
- `api/` - OpenAPI 仕様（契約）
- `models/` - ドメインモデル定義（Entity, ValueObject, Repository Interface 等）
- `templates/` - 仕様記述のテンプレート

## ワークフロー

開発タスク（Task01〜Task06）の正本は `docs/ai/prompts/tasks/`（01〜06）です。
この `docs/spec/` では、成果物（Spec/モデル/API 契約）を置きます。

推奨フロー（番号はタスク番号に合わせる）:

1. `docs/ai/prompts/tasks/01_create_feature_spec.md` に従って `features/` を作る
2. `docs/ai/prompts/tasks/02_simple_modeling.md` に従って `models/` を作る
3. `docs/ai/prompts/tasks/03_design_api_contract.md` に従って `api/` を作る
4. `docs/ai/prompts/tasks/04_plan_implementation.md` に従ってシナリオ/計画を作る
5. `docs/ai/prompts/tasks/05_generate_stubs.md` → `06_vertical_slice_implementation.md` で実装を進める

### ドメインモデル（概要）

実装前に、`models/{feature_name}.md` でドメインモデルを定義します。

**定義内容**:

- **Entity**: ライフサイクルを持ち、一意に識別されるオブジェクト
- **ValueObject**: 値そのものを表現する不変オブジェクト
- **Repository Interface**: エンティティの永続化を抽象化
- **DomainService**: 複数エンティティにまたがるロジック（必要な場合）

**目的**:

- ドメイン知識を明確化
- フロントエンド/バックエンド間でモデルを共有
- 実装の指針を提供

---

## HatoMask アプリケーション概要

- Vision: 写真にある顔をハトマスクに入れ替えるアプリ
- Core Features: 5 つの主要機能
- Technical Requirements: リアルタイム、PWA、i18n
- Non-functional: 応答 1sec 以内
