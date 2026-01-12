# コーディング規約

このドキュメントでは、HatoMask アプリケーションのコーディング規約について説明します。

## 目次

- [共通規約](#共通規約)
- [フロントエンド規約](#フロントエンド規約)
- [バックエンド規約](#バックエンド規約)

---

## 共通規約

### 命名規則

- **わかりやすさ優先**: 略語よりも完全な単語を使用
- **一貫性**: プロジェクト全体で統一された命名パターン
- **ビジネス用語**: ドメイン用語を正確に反映

### コメント

- **コードで表現できないことを書く**: Why を説明、What は書かない
- **日本語 OK**: チーム内での理解を優先
- **TODO コメント**: `// TODO: [担当者名] 説明` の形式

### バージョン管理

#### コミットメッセージ

Conventional Commits 形式を使用：

- `feat: 新機能の説明`
- `fix: バグ修正の説明`
- `refactor: リファクタリングの説明`
- `test: テスト追加・修正`
- `docs: ドキュメント更新`

#### ブランチ戦略

GitHub Flow を採用。詳細は [ブランチ戦略ガイド](../howto/branching.md) を参照してください。

- `main`: 本番環境用
- `feature/*`: 機能開発用
- `fix/*`: バグ修正用
- `docs/*`: ドキュメント更新用
- `refactor/*`: リファクタリング用

---

## フロントエンド規約

### 技術スタック

- React
- TypeScript
- UI コンポーネント: Material-UI (MUI)
- HTTP クライアント: fetch API または axios
- テストフレームワーク: Vitest + React Testing Library

### ファイル・ディレクトリ構成

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

### 命名規則

- **コンポーネント**: PascalCase（例: `PhotoUploadForm.tsx`）
- **関数・変数**: camelCase（例: `uploadPhoto`, `imageUrl`）
- **定数**: UPPER_SNAKE_CASE（例: `MAX_FILE_SIZE`）
- **型・インターフェース**: PascalCase（例: `PhotoData`, `UserProfile`）
- **カスタムフック**: `use`プレフィックス（例: `usePhotoUpload`）

### コンポーネント設計

#### 基本原則

- **関数コンポーネント**: 常に関数コンポーネントを使用
- **1 ファイル 1 コンポーネント**: 原則として 1 ファイルに 1 つのエクスポートコンポーネント
- **デフォルトエクスポート**: コンポーネントは名前付きエクスポートを推奨

#### Props 型定義

インターフェースで明示的に定義：

```typescript
interface PhotoCardProps {
  photoId: string;
  imageUrl: string;
  onDelete: (id: string) => void;
}

export const PhotoCard: React.FC<PhotoCardProps> = ({
  photoId,
  imageUrl,
  onDelete,
}) => {
  // コンポーネント実装
};
```

### TypeScript

- **厳格モード**: `strict: true` を維持
- **any 禁止**: 原則として`any`は使用しない（`unknown`を検討）
- **型推論活用**: 明らかな場合は型注釈を省略
- **null チェック**: Optional Chaining（`?.`）と Nullish Coalescing（`??`）を活用

**例**:

```typescript
// ✅ Good
const userName = user?.profile?.name ?? "Guest";

// ❌ Bad
const userName =
  user && user.profile && user.profile.name ? user.profile.name : "Guest";
```

### スタイリング

- **Material-UI**: コンポーネントライブラリとして使用
- **sx prop**: インラインスタイルは`sx` prop を使用
- **テーマ**: MUI のテーマシステムを活用
- **レスポンシブ**: モバイルファーストで設計

**例**:

```typescript
<Box
  sx={{
    display: "flex",
    gap: 2,
    p: { xs: 2, md: 3 }, // モバイル: 2, デスクトップ: 3
  }}
>
  {/* コンテンツ */}
</Box>
```

### 状態管理

- **ローカル状態**: `useState`で管理
- **副作用**: `useEffect`で管理、依存配列を正確に指定
- **グローバル状態**: 必要に応じて Context API を使用（初期は不要）

**例**:

```typescript
const [photos, setPhotos] = useState<Photo[]>([]);
const [loading, setLoading] = useState(false);

useEffect(() => {
  const fetchPhotos = async () => {
    setLoading(true);
    try {
      const data = await photoService.getPhotos();
      setPhotos(data);
    } finally {
      setLoading(false);
    }
  };

  fetchPhotos();
}, []); // 依存配列を明示
```

### API 通信

- **エラーハンドリング**: try-catch で適切に処理
- **ローディング状態**: ユーザーにフィードバックを提供
- **型安全性**: レスポンスの型を定義

**例**:

```typescript
interface UploadPhotoResponse {
  id: string;
  fileName: string;
  fileSize: number;
  mimeType: string;
  createdAt: string;
}

const uploadPhoto = async (file: File): Promise<UploadPhotoResponse> => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch("/api/v1/photos", {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    throw new Error("Upload failed");
  }

  return response.json();
};
```

### テスト

#### ファイル配置

- **ファイル名**: `*.test.tsx` または `*.test.ts`
- **配置**: `src/frontend/src/test/` 配下に集約

#### テストの書き方

- **アクセシビリティ**: `getByRole`, `getByLabelText`を優先
- **テスト ID**: `data-testid`属性は最終手段
- **ユーザー視点**: 実装の詳細ではなく、動作をテスト

**例**:

```typescript
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { PhotoUpload } from "../components/PhotoUpload";

describe("PhotoUpload", () => {
  it("写真を選択ボタンが表示される", () => {
    render(<PhotoUpload />);
    expect(
      screen.getByRole("button", { name: "写真を選択" })
    ).toBeInTheDocument();
  });

  it("写真を選択すると画像がプレビュー表示される", async () => {
    const user = userEvent.setup();
    render(<PhotoUpload />);

    const file = new File(["test"], "test.jpg", { type: "image/jpeg" });
    const input = screen.getByLabelText("写真を選択");

    await user.upload(input, file);

    const preview = await screen.findByRole("img", { name: /プレビュー/ });
    expect(preview).toBeInTheDocument();
  });
});
```

---

## バックエンド規約

### 技術スタック

- Spring Boot
- PostgreSQL
- ファイルストレージ: 初期はローカルファイルシステム、将来的にクラウドストレージへ移行可能な設計
- テストフレームワーク: JUnit 5 + Mockito

### パッケージ構成（Clean Architecture）

```
com.hatomask/
  presentation/       # Controller, DTO, リクエスト/レスポンス
    controller/
    dto/
  application/        # UseCase, ApplicationService
    usecase/
  domain/            # Entity, DomainService, ValueObject, Repository Interface
    model/           # Entity, ValueObject
    repository/      # Repository Interface
    service/         # DomainService
  infrastructure/    # Repository実装, 外部サービス連携
    repository/      # Repository実装 (JPA)
    external/
  config/            # 設定クラス
```

**重要**:

- Repository **Interface** は `domain/repository/` に配置（ドメイン層）
- Repository **実装** は `infrastructure/repository/` に配置（インフラ層）
- これにより、ドメイン層が外部技術（JPA）に依存しない設計を保ちます

### 命名規則

- **クラス**: PascalCase（例: `PhotoUploadUseCase`）
- **メソッド・変数**: camelCase（例: `uploadPhoto`, `imageData`）
- **定数**: UPPER_SNAKE_CASE（例: `MAX_IMAGE_SIZE`）
- **パッケージ**: 小文字（例: `com.hatomask.domain.model`）

### クラス設計

#### レイヤーごとの責務

- **Controller**: リクエスト/レスポンスの変換のみ、ビジネスロジックは持たない
- **UseCase**: ビジネスロジックを実装、1 つのユースケースを表現
- **Entity**: ドメインロジックを持つ、アノテーションは最小限
- **DTO**: データ転送用、不変オブジェクト推奨

#### 基本原則

- **単一責任の原則**: 1 クラス 1 責務
- **依存性注入**: コンストラクタインジェクション（`@RequiredArgsConstructor`推奨）
- **Lombok 活用**: `@Data`, `@Builder`, `@RequiredArgsConstructor`を活用

**例**:

```java
@Service
@RequiredArgsConstructor
public class UploadPhotoUseCase {
    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    public PhotoResponse execute(MultipartFile file) {
        // ビジネスロジック
        Photo photo = Photo.builder()
            .id(UUID.randomUUID())
            .fileName(file.getOriginalFilename())
            .fileSize(file.getSize())
            .build();

        fileStorageService.save(photo.getId(), file);
        photoRepository.save(photo);

        return PhotoResponse.from(photo);
    }
}
```

### アノテーション

- **Validation**: `@Valid`, `@NotNull`, `@Size`等でバリデーション
- **トランザクション**: `@Transactional`を適切に使用
- **ロギング**: `@Slf4j`アノテーションを使用

**例**:

```java
@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
@Slf4j
public class PhotoController {
    private final UploadPhotoUseCase uploadPhotoUseCase;

    @PostMapping
    public ResponseEntity<PhotoResponse> uploadPhoto(
        @Valid @RequestParam("file") MultipartFile file
    ) {
        log.info("Photo upload request received: {}", file.getOriginalFilename());
        PhotoResponse response = uploadPhotoUseCase.execute(file);
        return ResponseEntity.ok(response);
    }
}
```

### エラーハンドリング

- **カスタム例外**: ドメイン固有の例外を定義
- **グローバルハンドラー**: `@RestControllerAdvice`で統一的に処理
- **RFC 9457 準拠**: Problem Details 形式でレスポンス

**例**:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleFileSizeExceeded(
        FileSizeExceededException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problemDetail.setTitle("File Size Exceeded");
        problemDetail.setType(URI.create("/errors/file-size-exceeded"));

        return ResponseEntity.badRequest().body(problemDetail);
    }
}
```

### データベース

- **Repository**: Spring Data JPA のインターフェースを活用
- **命名**: `findByXxx`, `existsByXxx`等の規約に従う
- **ID**: UUID を主キーとして使用
- **N+1 問題対策**: `@EntityGraph`や`JOIN FETCH`を使用

**例**:

```java
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    List<Photo> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT p FROM Photo p LEFT JOIN FETCH p.user WHERE p.id = :id")
    Optional<Photo> findByIdWithUser(@Param("id") UUID id);
}
```

### ロギング

#### ログレベル

- `ERROR`: システムエラー、予期しない例外
- `WARN`: 警告、復旧可能なエラー
- `INFO`: 重要な処理の開始・終了
- `DEBUG`: 開発時のデバッグ情報

#### 注意事項

- **個人情報**: ログに出力しない
- **機密情報**: パスワード、トークン等は出力しない

**例**:

```java
@Slf4j
@Service
public class PhotoService {
    public void processPhoto(Photo photo) {
        log.info("Processing photo: id={}", photo.getId());

        try {
            // 処理
            log.debug("Photo processing completed: id={}", photo.getId());
        } catch (Exception e) {
            log.error("Failed to process photo: id={}", photo.getId(), e);
            throw e;
        }
    }
}
```

### テスト

#### ファイル配置

- **単体テスト**: `src/test/java` 配下、テスト対象と同じパッケージ
  - 例: `controller/PhotoControllerTest.java`
- **統合テスト**: `src/test/java` 配下の `integration/` パッケージ
  - 例: `integration/PhotoControllerIntegrationTest.java`

#### 命名規則

- 単体テスト: `*Test.java`（例: `PhotoUploadUseCaseTest.java`）
- 統合テスト: `*IntegrationTest.java`（例: `PhotoControllerIntegrationTest.java`）

#### テストの書き方

- **DisplayName**: `@DisplayName`で日本語の説明を記述
- **AAA パターン**: Arrange-Act-Assert の構造を明確に
- **モック**: Mockito を使用、`@Mock`, `@InjectMocks`

**例**:

```java
@ExtendWith(MockitoExtension.class)
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
        when(file.getSize()).thenReturn(1024L);

        // Act
        PhotoResponse response = uploadPhotoUseCase.execute(file);

        // Assert
        assertNotNull(response);
        assertEquals("test.jpg", response.getFileName());
        verify(photoRepository, times(1)).save(any(Photo.class));
        verify(fileStorageService, times(1)).save(any(UUID.class), eq(file));
    }

    @Test
    @DisplayName("ファイルサイズが10MBを超える場合はエラー")
    void execute_fileSizeExceeded_throwsException() {
        // Arrange
        MultipartFile file = createMockFile(11 * 1024 * 1024); // 11MB

        // Act & Assert
        assertThrows(FileSizeExceededException.class,
            () -> uploadPhotoUseCase.execute(file));
    }
}
```

### セキュリティ

- **入力検証**: 必ず実施
- **SQL インジェクション対策**: PreparedStatement または JPA を使用
- **パスワード**: ハッシュ化（BCrypt 推奨）
- **機密情報**: 環境変数または設定ファイルで管理、コードに埋め込まない

### パフォーマンス

- **N+1 問題**: `@EntityGraph`や`JOIN FETCH`で対策
- **ページネーション**: 大量データは必ずページングする
- **キャッシュ**: 必要に応じて`@Cacheable`を使用

---

## 関連ドキュメント

- [開発ガイド](../howto/development.md) - TDD 開発フロー、テスト実行方法
- [品質基準](./quality.md) - 品質基準とベストプラクティス
- [Linter 設定](./linting.md) - コード品質チェックツールの使い方
