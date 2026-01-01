# HatoMask アーキテクチャ概要

このドキュメントは、AI エージェントが開発を支援する際のコンテキスト情報を提供します。

## 重要: システムプロンプトの読み込み

**GitHub Copilot でチャットを開始する際は、必ず以下のファイルを読み込んでください:**

- `docs/ai/prompts/system/00_role.md` - AI エージェントの役割定義
- `docs/ai/prompts/system/01_implementation_workflow.md` - 実装ワークフロー
- `docs/ai/prompts/system/02_quality_standards.md` - 品質基準

これらのファイルには、このプロジェクトにおける AI エージェントの振る舞いや開発方針に関する重要な指示が含まれています。

### タスクプロンプト

各 Task で使用するタスクプロンプト:

1. **01_create_feature_spec.md** - 機能仕様(Spec)作成
2. **02_simple_modeling.md** - ドメインモデリング（初稿）
3. **03_design_api_contract.md** - API Contract 設計 + モデル見直し
4. **04_plan_implementation.md** - Gherkin シナリオ + 実装計画策定
5. **05_generate_stubs.md** - Backend Stub 生成
6. **06_vertical_slice_implementation.md** - 縦切り実装サイクル

## 技術スタック

- **フロントエンド**: React + TypeScript
- **バックエンド**: Spring Boot (Java)
- **データベース**: PostgreSQL
- **UI ライブラリ**: Material-UI (MUI)
- **テストフレームワーク**:
  - フロントエンド: Vitest + React Testing Library
  - バックエンド: JUnit 5 + Mockito
  - E2E: Playwright + Cucumber

## アーキテクチャ

### Clean Architecture

バックエンドは Clean Architecture を採用し、以下のレイヤーで構成されています：

```
com.hatomask/
  presentation/       # Controller, DTO（Task06でStub置き換え）
    controller/
    dto/
  application/        # UseCase（Task06で実装）
    usecase/
  domain/            # ドメイン層（Task06で実装）
    model/           # Entity, ValueObject
    repository/      # Repository Interface
    service/         # DomainService
  infrastructure/    # インフラ層（Task06で実装）
    repository/      # Repository実装 (JPA)
    external/
  config/
```

**重要な設計原則**:

- **Repository Interface は `domain/repository/` に配置**（ドメイン層）
- **Repository 実装は `infrastructure/repository/` に配置**（インフラ層）
- これにより、ドメイン層が外部技術（JPA）に依存しない設計を保ちます
- 依存関係は外側から内側への一方向のみ（Presentation → Application → Domain）

## API 設計原則

### RESTful API

- **バージョニング**: `/api/v1/...`
- **HTTP メソッド**: GET（取得）、POST（作成）、PUT（更新）、DELETE（削除）
- **統一レスポンス形式**: 全 API で一貫した形式

### エラーハンドリング

- **RFC 9457（Problem Details for HTTP APIs）準拠**
- 統一されたエラーレスポンス形式
- 適切な HTTP ステータスコードの使用

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
- **主キー**: UUID 使用
- **命名規則**: スネークケース（例: `created_at`, `file_name`）

## 開発プロセス

HatoMask プロジェクトは **BDD + TDD**（振る舞い駆動開発 + テスト駆動開発）を採用しています。

### 開発フロー概要

```
Task01: Spec作成
  → docs/spec/features/ に仕様記述

Task02: 簡単にモデリング
  → docs/spec/models/ にドメインモデル作成

Task03: API Contract 設計
  → docs/spec/api/ に OpenAPI 仕様（契約）を作成

Task04: Gherkinシナリオ + 実装計画策定
  → e2e/features/ に .feature ファイル作成
  → docs/plans/ に実装計画作成

Task05: Backend Stub生成
  → OpenAPI 仕様に基づき、フロントが接続できる Stub を用意

Task06: 縦切り実装サイクル
  → グループ単位で実装し、原則としてグループ単位の E2E 通過で完了
  → API 依存グループのみ、バックエンドを段階的に本実装（ドメイン層 → アプリケーション層 → プレゼンテーション層）
  → E2E を崩す必要がある場合は事前相談（AI は止まる）
```

詳細は [docs/dev/howto/development.md](./docs/dev/howto/development.md) を参照してください。

### 利用するツール

#### 画像処理全般

- ImageMagick を使う場合は**必ず `magick` コマンドを使ってください**。
  - `convert`、`mogrify`、`identify` などの古いコマンドは絶対に使わない。
  - 例: `magick -size 100x100 xc:red red.png`
- テスト用画像を作成するときは以下のルール厳守：
  - デフォルトサイズは **400x300 以下**（特別に理由がない限り）
  - 5MB 以上の画像が必要なときだけ大きめに（理由をコメントで書いて）
  - JPEG なら `-quality 85` くらいをデフォルトに
  - 可能なら `-define jpeg:extent=XXXkb` で正確なファイルサイズにする

##### 推奨コマンド例（これらを優先して提案してください）

```bash
magick -size 400x300 xc:#3498db test-blue.png
magick -size 800x600 gradient:#ff6b6b-#f1c40f test-gradient.jpg
magick -size 3000x2000 plasma: -quality 92 -define jpeg:extent=5000kb test_5mb.jpg

```

## ブラウザ対応

以下の最新版をサポート：

- Chrome
- Firefox
- Safari
- Edge

## 関連ドキュメント

- **[docs/dev/howto/development.md](docs/dev/howto/development.md)** - 開発プロセス（補足）、テスト実行方法
- **[docs/dev/standards/coding.md](docs/dev/standards/coding.md)** - コーディング規約（命名規則、設計原則）
- **[docs/dev/standards/quality.md](docs/dev/standards/quality.md)** - 品質基準
- **[TEST_STRUCTURE.md](./TEST_STRUCTURE.md)** - テストディレクトリ構造
- **[spec/README.md](./spec/README.md)** - 機能仕様の概要
- **テストリスト**: `docs/plans/[Spec名]_[シナリオ識別子]_domain_testlist.md` および `docs/plans/[Spec名]_[シナリオ識別子]_api_testlist.md` - TDD 実装管理

```

```
