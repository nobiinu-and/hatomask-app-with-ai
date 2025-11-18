# HatoMask アーキテクチャ概要

このドキュメントは、AIエージェントが開発を支援する際のコンテキスト情報を提供します。

## 技術スタック

- **フロントエンド**: React + TypeScript
- **バックエンド**: Spring Boot (Java)
- **データベース**: PostgreSQL
- **UIライブラリ**: Material-UI (MUI)
- **テストフレームワーク**: 
  - フロントエンド: Vitest + React Testing Library
  - バックエンド: JUnit 5 + Mockito
  - E2E: Playwright + Cucumber

## アーキテクチャ

### Clean Architecture

バックエンドはClean Architectureを採用し、以下のレイヤーで構成されています：

- **Presentation層（Controller）**: REST APIエンドポイント、リクエスト/レスポンスの変換
- **Application層（UseCase）**: ビジネスロジック、ユースケースの実装
- **Domain層**: エンティティ、ドメインロジック
- **Infrastructure層**: データベースアクセス、外部サービス連携、ファイルシステムアクセス

依存関係は外側から内側への一方向のみ（Presentation → Application → Domain）。

## API設計原則

### RESTful API

- **バージョニング**: `/api/v1/...`
- **HTTPメソッド**: GET（取得）、POST（作成）、PUT（更新）、DELETE（削除）
- **統一レスポンス形式**: 全APIで一貫した形式

### エラーハンドリング

- **RFC 9457（Problem Details for HTTP APIs）準拠**
- 統一されたエラーレスポンス形式
- 適切なHTTPステータスコードの使用

**例**:
```json
{
  "type": "/errors/file-size-exceeded",
  "title": "File Size Exceeded",
  "status": 400,
  "detail": "ファイルサイズは10MB以下にしてください",
  "instance": "/api/v1/photos"
}
```

## データベース設計

- **RDBMS**: PostgreSQL
- **マイグレーション**: Flyway
- **主キー**: UUID使用
- **命名規則**: スネークケース（例: `created_at`, `file_name`）

## 開発プロセス

HatoMaskプロジェクトは **Outside-In TDD**（外側から内側へのテスト駆動開発）を採用しています。

### 開発フロー

```
Spec（仕様） → テストリスト（実装順序） → 3段階TDD実装
  1. E2Eテスト（Red）
  2. フロントエンド実装（TDD with MSW）
  3. バックエンド実装（TDD）
  4. E2Eテスト確認（Green）
```

詳細は [DEVELOPMENT.md](./DEVELOPMENT.md) を参照してください。

## ブラウザ対応

以下の最新版をサポート：
- Chrome
- Firefox
- Safari
- Edge

## 関連ドキュメント

- **[DEVELOPMENT.md](./DEVELOPMENT.md)** - TDD開発フロー、テスト実行方法
- **[CODING_STANDARDS.md](./CODING_STANDARDS.md)** - コーディング規約（命名規則、設計原則）
- **[TEST_STRUCTURE.md](./TEST_STRUCTURE.md)** - テストディレクトリ構造
- **[spec/OVERVIEW.md](./spec/OVERVIEW.md)** - 機能仕様の概要
- **[testlists/README.md](./testlists/README.md)** - テストリスト運用方針

