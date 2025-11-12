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
- UIコンポーネント: Material-UI (MUI)
- HTTP クライアント: fetch API または axios

#### 実装方針
- コンポーネントベースの設計
- Material-UIを使用した統一されたUI/UX
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

## テスト戦略

### 全体方針

- **テストピラミッド**: 単体テスト > 統合テスト > E2Eテストの比率で実装
- **自動化**: CI/CDパイプラインでの自動テスト実行
- **カバレッジ目標**: 単体テスト 80%以上、重要な機能は 90%以上

### フロントエンドテスト

#### 単体テスト
- **フレームワーク**: Vitest + React Testing Library
- **対象**:
  - コンポーネントの振る舞い
  - カスタムフック
  - ユーティリティ関数
- **方針**:
  - ユーザーの操作をシミュレートしたテスト
  - 実装の詳細ではなく、動作に焦点を当てる
  - モック/スタブを適切に使用

#### 統合テスト
- API連携を含むコンポーネントのテスト
- MSW（Mock Service Worker）を使用したAPIモック

### バックエンドテスト

#### 単体テスト
- **フレームワーク**: JUnit 5 + Mockito
- **対象**:
  - UseCase層のビジネスロジック
  - Domain層のドメインロジック
  - ユーティリティクラス
- **方針**:
  - 依存関係をモック化
  - 境界値テスト、エッジケースのカバー
  - テストデータビルダーパターンの活用

#### 統合テスト
- **フレームワーク**: Spring Boot Test + Testcontainers
- **対象**:
  - Repository層（実際のDB接続）
  - Controller層（REST APIエンドポイント）
  - Infrastructure層
- **方針**:
  - Testcontainersで実際のPostgreSQLコンテナを使用
  - @SpringBootTestまたは@DataJpaTestを使用
  - トランザクションのロールバックで各テストを独立化

### E2Eテスト

#### フレームワーク
- **Playwright**: クロスブラウザ対応、高速で信頼性の高いE2Eテスト

#### 対象
- 重要なユーザーフロー（クリティカルパス）
- 画像アップロード・ダウンロードのフロー
- エラーハンドリング

#### 方針
- ユーザーの視点でのシナリオテスト
- テストデータのセットアップ・クリーンアップ
- 本番に近い環境での実行
- スクリーンショット・動画記録

### テストデータ管理

- **バックエンド**: テストフィクスチャ、ファクトリーパターン
- **フロントエンド**: モックデータファクトリー
- **E2E**: シードデータスクリプト

### CI/CD統合

- プルリクエストでの自動テスト実行
- mainブランチへのマージ前に全テスト合格が必須
- テストカバレッジレポートの生成・可視化

## コーディング規約

### 共通規約

#### 命名規則
- **わかりやすさ優先**: 略語よりも完全な単語を使用
- **一貫性**: プロジェクト全体で統一された命名パターン
- **ビジネス用語**: ドメイン用語を正確に反映

#### コメント
- **コードで表現できないことを書く**: Whyを説明、Whatは書かない
- **日本語OK**: チーム内での理解を優先
- **TODOコメント**: `// TODO: [担当者名] 説明` の形式

#### バージョン管理
- **コミットメッセージ**: Conventional Commits形式
  - `feat: 新機能の説明`
  - `fix: バグ修正の説明`
  - `refactor: リファクタリングの説明`
  - `test: テスト追加・修正`
  - `docs: ドキュメント更新`
- **ブランチ戦略**: GitHub Flow
  - `main`: 本番環境用
  - `feature/*`: 機能開発用
  - `fix/*`: バグ修正用

### フロントエンド（React + TypeScript）

#### ファイル・ディレクトリ構成
```
src/
  components/     # 再利用可能なコンポーネント
  pages/          # ページコンポーネント
  hooks/          # カスタムフック
  services/       # API通信
  types/          # 型定義
  utils/          # ユーティリティ関数
  test/           # テスト関連
```

#### 命名規則
- **コンポーネント**: PascalCase（例: `PhotoUploadForm.tsx`）
- **関数・変数**: camelCase（例: `uploadPhoto`, `imageUrl`）
- **定数**: UPPER_SNAKE_CASE（例: `MAX_FILE_SIZE`）
- **型・インターフェース**: PascalCase（例: `PhotoData`, `UserProfile`）
- **カスタムフック**: `use`プレフィックス（例: `usePhotoUpload`）

#### コンポーネント設計
- **関数コンポーネント**: 常に関数コンポーネントを使用
- **1ファイル1コンポーネント**: 原則として1ファイルに1つのエクスポートコンポーネント
- **Props型定義**: インターフェースで明示的に定義
  ```typescript
  interface PhotoCardProps {
    photoId: string;
    imageUrl: string;
    onDelete: (id: string) => void;
  }
  ```
- **デフォルトエクスポート**: コンポーネントは名前付きエクスポートを推奨

#### TypeScript
- **厳格モード**: `strict: true` を維持
- **any禁止**: 原則として`any`は使用しない（`unknown`を検討）
- **型推論活用**: 明らかな場合は型注釈を省略
- **nullチェック**: Optional Chaining（`?.`）とNullish Coalescing（`??`）を活用

#### スタイリング
- **Material-UI**: コンポーネントライブラリとして使用
- **sx prop**: インラインスタイルは`sx` propを使用
- **テーマ**: MUIのテーマシステムを活用
- **レスポンシブ**: モバイルファーストで設計

#### 状態管理
- **ローカル状態**: `useState`で管理
- **副作用**: `useEffect`で管理、依存配列を正確に指定
- **グローバル状態**: 必要に応じてContext APIを使用（初期は不要）

#### API通信
- **エラーハンドリング**: try-catchで適切に処理
- **ローディング状態**: ユーザーにフィードバックを提供
- **型安全性**: レスポンスの型を定義

#### テスト
- **ファイル名**: `*.test.tsx` または `*.test.ts`
- **配置**: テスト対象と同じディレクトリまたは`test/`配下
- **テストID**: `data-testid`属性を使用
- **アクセシビリティ**: `getByRole`, `getByLabelText`を優先

### バックエンド（Spring Boot + Java）

#### パッケージ構成（Clean Architecture）
```
com.hatomask/
  presentation/       # Controller, DTO, リクエスト/レスポンス
    controller/
    dto/
  application/        # UseCase, ApplicationService
    usecase/
  domain/            # Entity, DomainService, ValueObject
    model/
    service/
  infrastructure/    # Repository実装, 外部サービス連携
    repository/
    external/
  config/            # 設定クラス
```

#### 命名規則
- **クラス**: PascalCase（例: `PhotoUploadUseCase`）
- **メソッド・変数**: camelCase（例: `uploadPhoto`, `imageData`）
- **定数**: UPPER_SNAKE_CASE（例: `MAX_IMAGE_SIZE`）
- **パッケージ**: 小文字（例: `com.hatomask.domain.model`）

#### クラス設計
- **単一責任の原則**: 1クラス1責務
- **Controller**: リクエスト/レスポンスの変換のみ、ビジネスロジックは持たない
- **UseCase**: ビジネスロジックを実装、1つのユースケースを表現
- **Entity**: ドメインロジックを持つ、アノテーションは最小限
- **DTO**: データ転送用、不変オブジェクト推奨

#### アノテーション
- **依存性注入**: コンストラクタインジェクション（`@RequiredArgsConstructor`推奨）
- **Lombok**: `@Data`, `@Builder`, `@RequiredArgsConstructor`を活用
- **Validation**: `@Valid`, `@NotNull`, `@Size`等でバリデーション

#### エラーハンドリング
- **カスタム例外**: ドメイン固有の例外を定義
- **グローバルハンドラー**: `@RestControllerAdvice`で統一的に処理
- **RFC 9457準拠**: Problem Details形式でレスポンス

#### データベース
- **Repository**: Spring Data JPAのインターフェースを活用
- **命名**: `findByXxx`, `existsByXxx`等の規約に従う
- **トランザクション**: `@Transactional`を適切に使用
- **ID**: UUIDを主キーとして使用

#### ロギング
- **SLF4J**: `@Slf4j`アノテーションを使用
- **ログレベル**:
  - `ERROR`: システムエラー、予期しない例外
  - `WARN`: 警告、復旧可能なエラー
  - `INFO`: 重要な処理の開始・終了
  - `DEBUG`: 開発時のデバッグ情報
- **個人情報**: ログに出力しない

#### テスト
- **配置**: `src/test/java`配下、パッケージ構成は本番コードと同じ
- **命名**: 
  - 単体テスト: `*Test.java`（例: `PhotoUploadUseCaseTest.java`）
  - 統合テスト: `*IntegrationTest.java`（例: `PhotoControllerIntegrationTest.java`）
- **配置場所**:
  - 単体テスト: テスト対象と同じパッケージ（例: `controller/HelloControllerTest.java`）
  - 統合テスト: `integration/`パッケージ（例: `integration/HelloControllerIntegrationTest.java`）
- **DisplayName**: `@DisplayName`で日本語の説明を記述
- **Given-When-Then**: テストコードの構造を明確に
- **モック**: Mockitoを使用、`@Mock`, `@InjectMocks`

#### セキュリティ
- **入力検証**: 必ず実施
- **SQLインジェクション対策**: PreparedStatementまたはJPAを使用
- **パスワード**: ハッシュ化（BCrypt推奨）
- **機密情報**: 環境変数または設定ファイルで管理、コードに埋め込まない

#### パフォーマンス
- **N+1問題**: `@EntityGraph`や`JOIN FETCH`で対策
- **ページネーション**: 大量データは必ずページングする
- **キャッシュ**: 必要に応じて`@Cacheable`を使用

