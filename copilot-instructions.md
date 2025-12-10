# HatoMask アーキテクチャ概要

このドキュメントは、AIエージェントが開発を支援する際のコンテキスト情報を提供します。

## 重要: システムプロンプトの読み込み

**GitHub Copilot でチャットを開始する際は、必ず以下のファイルを読み込んでください:**

- `docs/ai/prompts/system/00_role.md` - AIエージェントの役割定義
- `docs/ai/prompts/system/01_implementation_workflow.md` - 実装ワークフロー
- `docs/ai/prompts/system/02_quality_standards.md` - 品質基準

これらのファイルには、このプロジェクトにおけるAIエージェントの振る舞いや開発方針に関する重要な指示が含まれています。

### タスクプロンプト

各フェーズで使用するタスクプロンプト:

1. **01_generate_scenarios.md** - Spec → Gherkinシナリオ生成
2. **02_simple_modeling.md** - Spec → ドメインモデル作成
3. **03_plan_implementation.md** - Gherkin + モデル → 実装計画策定
4. **04_implement_frontend_bdd.md** - BDDでフロントエンド実装
5. **05_implement_backend_domain.md** - TDDでバックエンドのドメイン層実装
6. **06_implement_backend_api.md** - TDDでバックエンドのAPI層実装

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

```
com.hatomask/
  presentation/       # Controller, DTO（Phase 7で実装）
    controller/
    dto/
  application/        # UseCase（Phase 7で実装）
    usecase/
  domain/            # ドメイン層（Phase 6で実装）
    model/           # Entity, ValueObject
    repository/      # Repository Interface
    service/         # DomainService
  infrastructure/    # インフラ層（Phase 6で実装）
    repository/      # Repository実装 (JPA)
    external/
  config/
```

**重要な設計原則**:
- **Repository Interfaceは `domain/repository/` に配置**（ドメイン層）
- **Repository実装は `infrastructure/repository/` に配置**（インフラ層）
- これにより、ドメイン層が外部技術（JPA）に依存しない設計を保ちます
- 依存関係は外側から内側への一方向のみ（Presentation → Application → Domain）

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

HatoMaskプロジェクトは **BDD + TDD**（振る舞い駆動開発 + テスト駆動開発）を採用しています。

### 開発フロー概要

```
Phase 1: Spec作成
  → docs/spec/features/ に仕様記述
  
Phase 2: 簡単にモデリング
  → docs/spec/models/ にドメインモデル作成
  
Phase 3: Gherkinシナリオ作成
  → e2e/features/ に.featureファイル作成
  
Phase 4: 実装計画策定
  → docs/plans/ に実装計画作成
  
Phase 5: BDDでフロントエンド実装
  → 1ステップずつ、Red-Green-Refactor
  → MSWでモックAPI作成
  
Phase 6: TDDでバックエンド(ドメイン)実装
  → Entity, Repository, DomainService
  → domain/ と infrastructure/repository/
  
Phase 7: TDDでバックエンド(API)実装
  → UseCase, Controller, DTO
  → application/ と presentation/
  → MSWと同じレスポンス形式
  
Phase 8: 統合テスト (MSWなし)
  → E2Eテストをリアルバックエンドで実行
```

詳細は [docs/dev/DEVELOPMENT.md](./docs/dev/DEVELOPMENT.md) を参照してください。

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
- **[spec/README.md](./spec/README.md)** - 機能仕様の概要
- **[testlists/README.md](./testlists/README.md)** - テストリスト運用方針

