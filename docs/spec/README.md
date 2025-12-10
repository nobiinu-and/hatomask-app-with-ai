# 仕様 (Spec) Overview

このディレクトリには、HatoMask アプリケーションの機能仕様を記述します。

## 構成
- `features/` - 各機能の詳細仕様（受け入れ基準を含む）
- `models/` - ドメインモデル定義（Entity, ValueObject, Repository Interface等）
- `templates/` - 仕様記述のテンプレート

## ワークフロー

```
1. features/ に機能仕様を記述
   ↓
2. models/ にドメインモデルを作成（Phase 2: 簡単にモデリング）
   ↓
3. Gherkinシナリオを作成（Phase 3）
   ↓
4. 実装計画を策定（Phase 4）
   ↓
5. 実装（Phase 5-7）
```

### Phase 2: 簡単にモデリング

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
- Core Features: 5つの主要機能
- Technical Requirements: リアルタイム、PWA、i18n
- Non-functional: 応答1sec以内

