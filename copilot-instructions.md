# HatoMask アーキテクチャ概要

このドキュメントは、AIエージェントが開発を支援する際のコンテキスト情報を提供します。

## 重要: システムプロンプトの読み込み

**GitHub Copilot でチャットを開始する際は、必ず以下のファイルを読み込んでください:**

- `docs/ai/prompts/system/00_role.md` - AIエージェントの役割定義
- `docs/ai/prompts/system/01_implementation_workflow.md` - 実装ワークフロー
- `docs/ai/prompts/system/02_quality_standards.md` - 品質基準

これらのファイルには、このプロジェクトにおけるAIエージェントの振る舞いや開発方針に関する重要な指示が含まれています。

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

詳細は [DEVELOPMENT.md](./DEVELOPMENT.md) を参照してください。

### 利用するツール

#### 画像処理全般
- ImageMagickを使う場合は**必ず `magick` コマンドを使ってください**。
  - `convert`、`mogrify`、`identify` などの古いコマンドは絶対に使わない。
  - 例: `magick -size 100x100 xc:red red.png`
- テスト用画像を作成するときは以下のルール厳守：
  - デフォルトサイズは **400x300 以下**（特別に理由がない限り）
  - 5MB以上の画像が必要なときだけ大きめに（理由をコメントで書いて）
  - JPEGなら `-quality 85` くらいをデフォルトに
  - 可能なら `-define jpeg:extent=XXXkb` で正確なファイルサイズにする

##### 推奨コマンド例（これらを優先して提案してください）
```bash
magick -size 400x300 xc:#3498db test-blue.png
magick -size 800x600 gradient:#ff6b6b-#f1c40f test-gradient.jpg
magick -size 3000x2000 plasma: -quality 92 -define jpeg:extent=5000kb test_5mb.jpg

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

