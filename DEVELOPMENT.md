# 開発ガイド

このドキュメントでは、HatoMaskアプリケーションの開発プロセス、TDD開発フロー、テスト実行方法について説明します。

## 目次

- [実装方針](#実装方針)
- [TDD開発フロー](#tdd開発フロー)
- [開発の進め方](#開発の進め方)
- [テスト実行](#テスト実行)
- [テストのベストプラクティス](#テストのベストプラクティス)
- [CI/CD統合](#cicd統合)
- [トラブルシューティング](#トラブルシューティング)
- [コーディング規約](#コーディング規約)

---

## 実装方針

### 全体方針

- **最小実装からスタート**: 複雑な機能は後回しにし、基本機能から段階的に実装
- **シンプルさ優先**: 過度な抽象化を避け、理解しやすいコードを書く
- **段階的な改善**: 動作する最小限のコードを書いてから、リファクタリングで改善

### フロントエンド

- **コンポーネントベース設計**: 再利用可能なコンポーネントを構築
- **Material-UI使用**: 統一されたUI/UXのため、MUIコンポーネントを活用
- **シンプルな状態管理**: useState/useEffectから開始、必要に応じて拡張
- **型安全性重視**: TypeScriptの型システムを最大限活用

### バックエンド

- **Clean Architecture**: レイヤー分離による保守性の向上
- **依存性注入**: テスタビリティとモジュール性の確保
- **ドメイン駆動**: ビジネスロジックをドメイン層に集約

詳細なコーディング規約は [CODING_STANDARDS.md](./CODING_STANDARDS.md) を参照してください。

---

## TDD開発フロー

HatoMaskプロジェクトでは、**Outside-In TDD**（外側から内側へのテスト駆動開発）を採用しています。

### 開発フローの全体像

```
Spec（仕様） → テストリスト（実装順序） → 3段階TDD実装
```

### 3段階TDD実装プロセス

#### ステップ1: E2Eテスト追加（Red）

ユーザー視点のE2Eテストを1つ追加し、失敗することを確認します。

**実装場所**: `e2e/features/*.feature` (Gherkin形式)

**例**:
```gherkin
# e2e/features/photo-upload.feature
Scenario: JPEGファイルのアップロードとダウンロード
  Given ユーザーがHatoMaskアプリケーションにアクセスしている
  When ユーザーが「写真を選択」ボタンをクリックする
  And ユーザーがファイルサイズ5MBのJPEGファイルを選択する
  Then アップロードが成功する
  And プレビューエリアに選択した画像が表示される
```

**ステップ定義**: `e2e/step-definitions/steps.ts`

**実行コマンド**:
```bash
cd e2e
npm run test:cucumber
```

**期待結果**: テストが失敗する（Red）

---

#### ステップ2: フロントエンド実装（TDD）

E2Eテストをパスさせるために、フロントエンドをTDDで実装します。
**バックエンドAPIはMSWでモック化**します。

##### 2-1. フロントエンド統合テスト（Red）

**実装場所**: `src/frontend/src/test/*.test.tsx`

**例**:
```typescript
// src/frontend/src/test/PhotoUpload.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { PhotoUpload } from '../components/PhotoUpload';

describe('PhotoUpload', () => {
  it('写真を選択ボタンが表示される', () => {
    render(<PhotoUpload />);
    expect(screen.getByText('写真を選択')).toBeInTheDocument();
  });

  it('写真を選択すると画像がプレビュー表示される', async () => {
    // テストコード
  });
});
```

**実行コマンド**:
```bash
cd src/frontend
npm test -- --watch
```

##### 2-2. MSWでAPIモック設定

**実装場所**: `src/frontend/src/test/mocks/handlers.ts`

**例**:
```typescript
import { http, HttpResponse } from 'msw';

export const handlers = [
  http.post('/api/v1/photos', async () => {
    return HttpResponse.json({
      id: 'test-uuid',
      fileName: 'test.jpg',
      fileSize: 5242880,
      mimeType: 'image/jpeg',
      createdAt: '2025-11-14T00:00:00Z'
    });
  }),
];
```

##### 2-3. コンポーネント実装（Green）

テストがパスする最小限のコードを実装します。

**実装場所**: `src/frontend/src/components/PhotoUpload.tsx`

##### 2-4. リファクタリング

テストをパスしたまま、コードを改善します。

**確認**:
```bash
npm test
npm run lint
```

---

#### ステップ3: バックエンド実装（TDD）

フロントエンドの実装が完了したら、バックエンドをTDDで実装します。

##### 3-1. バックエンド統合テスト（Red）

**実装場所**: `src/backend/src/test/java/com/hatomask/presentation/controller/*IntegrationTest.java`

**例**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class PhotoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/photos - 写真アップロードが成功する")
    void uploadPhoto_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/photos").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.fileName").value("test.jpg"));
    }
}
```

**実行コマンド**:
```bash
cd src/backend
mvn test -Dtest=PhotoControllerIntegrationTest
```

##### 3-2. 単体テスト（Red → Green）

各層のテストを書いて実装を進めます。

**実装順序**:
1. **Controller層**: `PhotoControllerTest.java`
2. **UseCase層**: `UploadPhotoUseCaseTest.java`, `GetPhotoUseCaseTest.java`
3. **Repository層**: `PhotoRepositoryTest.java`
4. **Infrastructure層**: `FileStorageServiceTest.java`

**例（UseCase層）**:
```java
class UploadPhotoUseCaseTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UploadPhotoUseCase uploadPhotoUseCase;

    @Test
    @DisplayName("写真アップロードが成功する")
    void execute_success() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        
        // Act & Assert
        assertDoesNotThrow(() -> uploadPhotoUseCase.execute(file));
        verify(photoRepository, times(1)).save(any(Photo.class));
    }
}
```

##### 3-3. 実装（Green）

テストがパスする実装を追加します。

**実装場所**:
- `src/backend/src/main/java/com/hatomask/presentation/controller/PhotoController.java`
- `src/backend/src/main/java/com/hatomask/application/UploadPhotoUseCase.java`
- `src/backend/src/main/java/com/hatomask/domain/repository/PhotoRepository.java`
- `src/backend/src/main/java/com/hatomask/infrastructure/FileStorageService.java`

##### 3-4. リファクタリング

テストをパスしたまま、コードを改善します。

**確認**:
```bash
mvn test
mvn checkstyle:check
```

---

#### ステップ4: E2Eテスト確認（Green）

すべての実装が完了したら、E2Eテストをパスすることを確認します。

**実行コマンド**:
```bash
# アプリケーション起動
docker-compose -f docker-compose.dev.yml up -d

# E2Eテスト実行
cd e2e
npm run test:cucumber
```

**期待結果**: すべてのテストがパスする（Green）

---

## 開発の進め方

### 1. 仕様書（Spec）を確認

実装する機能の仕様を確認します。

**場所**: `spec/features/*.md`

**内容**:
- 機能概要
- ユースケース
- 技術要件
- API仕様
- 受け入れ基準（Gherkin形式）

### 2. テストリストを作成

Specの受け入れ基準を基に、実装順序を決めたテストリストを作成します。

**場所**: `testlists/*.md`

**形式**: Kent BeckのTDD手法に基づくチェックリスト

**例**:
```markdown
### Scenario 1: JPEGファイルのアップロードとダウンロード

- **Given** ユーザーがHatoMaskアプリケーションにアクセスしている
  - [ ] ページのタイトルが「HatoMask App」である
  - [ ] 「写真を選択」ボタンが表示されている
- **When** ユーザーが「写真を選択」ボタンをクリックする
  - [ ] ファイル選択ダイアログが開く
- **And** ユーザーがファイルサイズ5MBのJPEGファイルを選択する
  - [ ] アップロードが成功する
  - [ ] プレビューエリアに画像が表示される
```

### 3. 3段階TDDで実装

上記の[TDD開発フロー](#tdd開発フロー)に従って実装します。

### 4. テストリストを更新

実装が完了したら、テストリストのチェックボックスを更新します。

```markdown
- [x] ページのタイトルが「HatoMask App」である
- [x] 「写真を選択」ボタンが表示されている
```

---

## テスト実行

### セットアップ

#### バックエンドテスト

依存関係は既に `pom.xml` に含まれています。

```bash
cd src/backend
mvn clean install
```

#### フロントエンドテスト

依存関係をインストールします：

```bash
cd src/frontend
npm install
```

#### E2Eテスト

Playwright をセットアップします：

```bash
cd e2e
npm install
npm run playwright:install
```

### 各層のテスト実行

#### バックエンドテスト

```bash
cd src/backend

# 全テスト実行
mvn test

# 特定のテストクラスを実行
mvn test -Dtest=PhotoControllerTest

# 特定のテストメソッドを実行
mvn test -Dtest=PhotoControllerTest#uploadPhoto_success

# カバレッジレポート生成
mvn test jacoco:report
# レポート: target/site/jacoco/index.html
```

#### フロントエンドテスト

```bash
cd src/frontend

# 全テスト実行
npm test

# ウォッチモード
npm test -- --watch

# UIモード
npm run test:ui

# カバレッジレポート生成
npm run test:coverage
# レポート: coverage/index.html
```

#### E2Eテスト

```bash
# 前提: アプリケーションが起動している必要があります
docker-compose -f docker-compose.dev.yml up -d

cd e2e

# Cucumber E2Eテスト実行
npm run test:cucumber

# Playwright E2Eテスト実行
npm run test:e2e

# UIモード
npm run test:e2e:ui

# ヘッドモード（ブラウザ表示）
npm run test:e2e:headed

# デバッグモード
npm run test:e2e:debug

# 特定のブラウザのみ
npx playwright test --project=chromium
```

### 全テスト実行

```bash
# ルートディレクトリから
./scripts/run-all-tests.sh

# E2Eも含める
./scripts/run-all-tests.sh --with-e2e
```

## CI/CD統合

### GitHub Actions 設定例

```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run backend tests
        run: |
          cd src/backend
          mvn test
      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./src/backend/target/site/jacoco/jacoco.xml

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install dependencies
        run: |
          cd src/frontend
          npm ci
      - name: Run frontend tests
        run: |
          cd src/frontend
          npm test
          npm run test:coverage
      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./src/frontend/coverage/coverage-final.json

  e2e-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Start services
        run: docker-compose -f docker-compose.dev.yml up -d
      - name: Wait for services
        run: sleep 30
      - name: Install Playwright
        run: |
          cd e2e
          npm ci
          npm run playwright:install
      - name: Run E2E tests
        run: |
          cd e2e
          npm run test:cucumber
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: cucumber-report
          path: e2e/test-results/
```

## テストのベストプラクティス

### バックエンド

- ✅ **ビジネスロジックは単体テストでカバー**
- ✅ **外部依存（DB、ファイルシステム）はモック化**
- ✅ **統合テストでは実際のDBを使用**（H2 または Testcontainers）
- ✅ **`@DisplayName`で日本語の説明を記述**
- ✅ **AAA（Arrange-Act-Assert）パターンを使用**
- ❌ **テスト間で状態を共有しない**

**例**:
```java
@Test
@DisplayName("ファイルサイズが10MBを超える場合はエラー")
void uploadPhoto_fileSizeExceeded_throwsException() {
    // Arrange
    MultipartFile file = createMockFile(11 * 1024 * 1024); // 11MB
    
    // Act & Assert
    assertThrows(FileSizeExceededException.class, 
        () -> uploadPhotoUseCase.execute(file));
}
```

### フロントエンド

- ✅ **ユーザーの視点でテストを書く**
- ✅ **実装の詳細ではなく、動作をテスト**
- ✅ **MSWでAPIレスポンスをモック**
- ✅ **アクセシビリティを考慮したセレクタを使用**
- ✅ **`screen.getByRole`や`screen.getByLabelText`を優先**
- ❌ **`data-testid`は最終手段**

**例**:
```typescript
it('写真を選択すると画像がプレビュー表示される', async () => {
  const user = userEvent.setup();
  render(<PhotoUpload />);
  
  const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
  const input = screen.getByLabelText('写真を選択');
  
  await user.upload(input, file);
  
  const preview = await screen.findByRole('img', { name: /プレビュー/ });
  expect(preview).toBeInTheDocument();
});
```

### E2E

- ✅ **クリティカルなユーザーフローに焦点を当てる**
- ✅ **テストの独立性を保つ**
- ✅ **テストデータのセットアップとクリーンアップを適切に行う**
- ✅ **フレイキーテストを避ける**（適切な待機処理）
- ✅ **Gherkin形式で可読性を高める**
- ❌ **実装の詳細（CSSセレクタ等）に依存しない**

**例**:
```gherkin
Scenario: ファイルサイズ超過エラー
  Given ユーザーがHatoMaskアプリケーションにアクセスしている
  When ユーザーがファイルサイズ11MBのJPEGファイルを選択する
  Then エラーメッセージ「ファイルサイズは10MB以下にしてください」が表示される
  And 画像はアップロードされない
```

## トラブルシューティング

### フロントエンドテストで型エラーが出る

依存関係を再インストール：
```bash
cd src/frontend
rm -rf node_modules package-lock.json
npm install
```

### E2Eテストが失敗する

1. **サービスが起動しているか確認**
   ```bash
   docker-compose -f docker-compose.dev.yml ps
   ```

2. **ポートが正しいか確認**
   - フロントエンド: http://localhost:3000
   - バックエンド: http://localhost:8080

3. **ブラウザが正しくインストールされているか確認**
   ```bash
   cd e2e
   npm run playwright:install
   ```

4. **ログを確認**
   ```bash
   docker-compose -f docker-compose.dev.yml logs -f
   ```

### バックエンドテストでDB接続エラー

テストプロファイルが正しく読み込まれているか確認：
```bash
cd src/backend
mvn test -Dspring.profiles.active=test
```

### MSWモックが動作しない

1. **MSWサーバーが起動しているか確認**
   - `src/frontend/src/test/setup.ts`で`server.listen()`が呼ばれているか確認

2. **ハンドラーが正しく定義されているか確認**
   ```typescript
   // src/frontend/src/test/mocks/handlers.ts
   export const handlers = [
     http.post('/api/v1/photos', async () => {
       return HttpResponse.json({ /* ... */ });
     }),
   ];
   ```

3. **テスト後のクリーンアップが実行されているか確認**
   ```typescript
   afterEach(() => server.resetHandlers());
   afterAll(() => server.close());
   ```

---

## テストカバレッジの目標

- **バックエンド**: 全体で 80%以上、ビジネスロジック（UseCase層）は 90%以上
- **フロントエンド**: 全体で 80%以上、重要なコンポーネントは 90%以上
- **E2E**: 主要なユーザーフロー（クリティカルパス）を 100%カバー

---

## コーディング規約

HatoMaskプロジェクトのコーディング規約については、[CODING_STANDARDS.md](./CODING_STANDARDS.md) を参照してください。

以下の内容が含まれています：

- **共通規約**: 命名規則、コメント、バージョン管理
- **フロントエンド規約**: React/TypeScriptの設計パターン、テストの書き方
- **バックエンド規約**: Spring Boot/Javaの設計パターン、レイヤー構成、テストの書き方

---

## 関連ドキュメント

- [コーディング規約](./CODING_STANDARDS.md) - 命名規則、設計原則、テストの書き方
- [テスト構造](./TEST_STRUCTURE.md) - テストディレクトリ構造の詳細
- [仕様書](./spec/OVERVIEW.md) - 機能仕様の概要
- [テストリスト](./testlists/README.md) - テストリストの運用方針
- [Linter設定](./LINTER.md) - コード品質チェックツールの使い方
- [Docker実行ガイド](./DOCKER.md) - Docker環境でのアプリケーション起動方法
- [アーキテクチャ](./.ai/context.md) - アーキテクチャ概要と技術スタック
