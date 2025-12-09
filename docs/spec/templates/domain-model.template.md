# [機能名] ドメインモデル

## 概要
[この機能のドメインを1-2文で説明]
[Specに含まれる全ての機能範囲を明記]

## エンティティ

### [エンティティ名1]
**説明**: [エンティティの説明]

**プロパティ**:
- id: UUID (自動生成)
- [propertyName]: [Type] ([必須/任意]、[制約条件])
- createdAt: LocalDateTime (自動生成)
- updatedAt: LocalDateTime (自動更新)

**バリデーションルール**:
- [propertyName]: [バリデーション条件]

### [エンティティ名2]
**説明**: [エンティティの説明]

**プロパティ**:
- id: UUID (自動生成)
- [propertyName]: [Type] ([必須/任意]、[制約条件])
- createdAt: LocalDateTime (自動生成)

**関連**:
- [エンティティ名1] との関連を説明

### [その他、Specに関連する全てのエンティティ]
...

## エンティティ関連図

```
[Entity1] --1:N--> [Entity2]
[Entity2] --N:M--> [Entity3]
```

## バリューオブジェクト

### [バリューオブジェクト名]
**説明**: [バリューオブジェクトの説明]

**プロパティ**:
- [propertyName]: [Type]

**バリデーションルール**:
- [バリデーション条件]

## リポジトリインターフェース

### [リポジトリ名1]
**説明**: [Entity1] エンティティの永続化を担当

**メソッド**:
- save([entity]: [Entity1]): [Entity1]
  - [Entity1] を保存し、ID と createdAt が設定されたインスタンスを返す
- findById(id: UUID): Optional<[Entity1]>
  - ID で [Entity1] を検索する
- [その他必要なメソッド]

### [リポジトリ名2]
**説明**: [Entity2] エンティティの永続化を担当

**メソッド**:
- save([entity]: [Entity2]): [Entity2]
  - [Entity2] を保存する
- findById(id: UUID): Optional<[Entity2]>
  - ID で [Entity2] を検索する
- [その他必要なメソッド]

### [その他、必要な全てのリポジトリ]
...

## ドメインサービス

### [サービス名]
**説明**: [ドメインサービスの説明]

**メソッド**:
- [methodName]([params]): [ReturnType]
  - [メソッドの説明]

（必要な場合のみ定義）

## 補足・注意事項
[モデリング時の考慮点やドメイン知識など]
[Specに記載された機能範囲の確認]
