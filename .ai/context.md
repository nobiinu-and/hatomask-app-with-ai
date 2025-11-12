- Stack: React + TypeScript + Spring Boot + PostgreSQL
- Architecture: Clean Architecture
- API: 統一レスポンス形式、エラーレスポンスは、RFC 9457（Problem Details）仕様に従う

## アーキテクチャ詳細

### バックエンド: Clean Architecture

レイヤー構成：
- **Controller層（Presentation）**: REST APIエンドポイント、リクエスト/レスポンスの変換
- **UseCase層（Application）**: ビジネスロジック、ユースケースの実装
- **Domain層**: エンティティ、ドメインロジック
- **Repository層（Interface）**: データアクセスのインターフェース
- **Infrastructure層**: データベースアクセス、外部サービス連携、ファイルシステムアクセス

### エラーハンドリング

- RFC 9457（Problem Details for HTTP APIs）準拠
- 統一されたエラーレスポンス形式
- 適切なHTTPステータスコードの使用

### データベース

- PostgreSQL
- マイグレーション管理（Flyway または Liquibase推奨）
- UUID を主キーとして使用

### API設計原則

- RESTful API
- バージョニング: `/api/v1/...`
- 統一されたレスポンス形式
- 適切なHTTPメソッドの使用（GET, POST, PUT, DELETE）

## 実装方針

### 全体方針

- **最小実装からスタート**: 複雑な機能は後回しにし、基本機能から段階的に実装
- **シンプルさ優先**: 過度な抽象化を避け、理解しやすいコードを書く
- **段階的な改善**: 動作する最小限のコードを書いてから、リファクタリングで改善

### フロントエンド

#### 技術スタック
- React
- TypeScript
- HTTP クライアント: fetch API または axios

#### 実装方針
- コンポーネントベースの設計
- シンプルな状態管理（useState/useEffect から開始）
- 型安全性を重視（TypeScript の活用）

### バックエンド

#### 技術スタック
- Spring Boot
- PostgreSQL
- ファイルストレージ: 初期はローカルファイルシステム、将来的にクラウドストレージへ移行可能な設計

#### 実装方針
- Clean Architecture に基づいた層分離
- 依存性注入の活用
- テスタビリティを考慮した設計

### ブラウザ対応

以下の最新版をサポート：
- Chrome
- Firefox
- Safari
- Edge

