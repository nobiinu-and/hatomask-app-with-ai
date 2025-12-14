# 品質基準

このドキュメントでは、HatoMaskプロジェクトにおけるコード品質の基準とベストプラクティスを定義します。

## 目次

- [コード品質の基本原則](#コード品質の基本原則)
- [テスト品質基準](#テスト品質基準)
- [エラーハンドリング基準](#エラーハンドリング基準)
- [セキュリティ基準](#セキュリティ基準)
- [パフォーマンス基準](#パフォーマンス基準)
- [アクセシビリティ基準](#アクセシビリティ基準)
- [コードレビュー チェックリスト](#コードレビュー-チェックリスト)

---

## コード品質の基本原則

### 1. 可読性優先

コードは書くよりも読む時間の方が長い。他の開発者が理解しやすいコードを書く。

**良い例:**
```typescript
const isValidImageFile = (file: File): boolean => {
  const validTypes = ['image/jpeg', 'image/png'];
  const maxSize = 10 * 1024 * 1024; // 10MB
  
  return validTypes.includes(file.type) && file.size <= maxSize;
};
```

**悪い例:**
```typescript
const v = (f: File): boolean => {
  return ['image/jpeg', 'image/png'].includes(f.type) && f.size <= 10485760;
};
```

**ポイント:**
- 意味のある変数名・関数名を使用する
- マジックナンバーは定数化する
- 複雑な条件は関数に抽出する

### 2. 単一責任の原則 (Single Responsibility Principle)

1つのクラス/関数は1つの責任だけを持つ。

**良い例:**
```typescript
// コンポーネントは表示のみを担当
export const PhotoCard: React.FC<PhotoCardProps> = ({ photo, onDelete }) => {
  return (
    <Card>
      <CardMedia image={photo.imageUrl} />
      <Button onClick={() => onDelete(photo.id)}>削除</Button>
    </Card>
  );
};

// ビジネスロジックはカスタムフックに分離
export const usePhotoManagement = () => {
  const [photos, setPhotos] = useState<Photo[]>([]);
  
  const deletePhoto = async (id: string) => {
    await photoService.delete(id);
    setPhotos(photos.filter(p => p.id !== id));
  };
  
  return { photos, deletePhoto };
};
```

**悪い例:**
```typescript
// コンポーネントにビジネスロジックが混在
export const PhotoCard: React.FC = () => {
  const [photos, setPhotos] = useState<Photo[]>([]);
  
  const deletePhoto = async (id: string) => {
    await fetch(`/api/photos/${id}`, { method: 'DELETE' });
    setPhotos(photos.filter(p => p.id !== id));
  };
  
  return (
    <Card>
      <CardMedia image={photo.imageUrl} />
      <Button onClick={() => deletePhoto(photo.id)}>削除</Button>
    </Card>
  );
};
```

### 3. DRY原則 (Don't Repeat Yourself)

同じコードを複数箇所に書かない。共通処理は関数/クラスに抽出する。

**良い例:**
```java
@Service
@RequiredArgsConstructor
public class FileValidator {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Set<String> VALID_TYPES = Set.of("image/jpeg", "image/png");
    
    public void validate(MultipartFile file) {
        validateFileSize(file);
        validateFileType(file);
    }
    
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("File size exceeds 10MB");
        }
    }
    
    private void validateFileType(MultipartFile file) {
        if (!VALID_TYPES.contains(file.getContentType())) {
            throw new InvalidFileTypeException("Only JPEG and PNG are allowed");
        }
    }
}
```

**悪い例:**
```java
// 各コントローラーでバリデーションロジックを重複して記述
@PostMapping("/photos")
public void uploadPhoto(MultipartFile file) {
    if (file.getSize() > 10 * 1024 * 1024) {
        throw new RuntimeException("File too large");
    }
    if (!file.getContentType().equals("image/jpeg") && 
        !file.getContentType().equals("image/png")) {
        throw new RuntimeException("Invalid file type");
    }
    // ...
}
```

---

## テスト品質基準

### ユニットテストの原則

#### FIRST原則

- **F**ast: 高速に実行できる(外部依存を最小限に)
- **I**ndependent: 他のテストに依存しない
- **R**epeatable: 何度実行しても同じ結果
- **S**elf-validating: 自動的に成否を判定できる
- **T**imely: 実装前にテストを書く(TDD)

#### AAA パターン

すべてのテストは以下の構造に従う:

- **A**rrange: テストの準備(データ、モックの設定)
- **A**ct: テスト対象のアクションを実行
- **A**ssert: 結果の検証

**例:**
```java
@Test
@DisplayName("ファイルサイズが10MBを超える場合はエラー")
void validate_fileSizeExceeds_throwsException() {
    // Arrange
    MultipartFile file = createMockFile(11 * 1024 * 1024);
    FileValidator validator = new FileValidator();
    
    // Act & Assert
    assertThrows(FileSizeExceededException.class, 
        () -> validator.validate(file));
}
```

#### テストカバレッジの目標

- **全体**: 80%以上（CI/CDで自動チェック ✅）
- **重要なビジネスロジック**: 90%以上
- **ユーティリティ関数**: 100%

**閾値適用状況:**
- **バックエンド（JaCoCo）**: 80%閾値を設定済み（Instructions, Lines, Methods）
- **フロントエンド（Vitest）**: 80%閾値を設定済み（Statements, Branches, Functions, Lines）
- 閾値を下回るとビルドが失敗します

**測定方法:**
```bash
# フロントエンド（閾値チェック含む）
cd src/frontend
npm run test:coverage

# バックエンド（閾値チェック含む）
cd src/backend
mvn clean verify
```

### E2Eテストの原則

#### 1. ユーザー視点でテスト

実装の詳細ではなく、ユーザーの行動と期待結果をテストする。

**良い例:**
```typescript
// アクセシビリティを考慮したセレクタ
await page.getByRole('button', { name: '写真を選択' }).click();
await page.getByLabelText('写真ファイル').setInputFiles('test.jpg');
await expect(page.getByText('アップロード完了')).toBeVisible();
```

**悪い例:**
```typescript
// 実装詳細に依存(CSSクラス、ID)
await page.locator('#upload-button').click();
await page.locator('.file-input').setInputFiles('test.jpg');
await page.locator('.success-message').waitFor();
```

#### 2. 適切な待機

固定時間の待機は避け、要素の状態を待つ。

**良い例:**
```typescript
await expect(page.getByText('アップロード完了')).toBeVisible();
```

**悪い例:**
```typescript
await page.waitForTimeout(3000);
```

---

## エラーハンドリング基準

### フロントエンド

#### 1. ユーザーフレンドリーなエラーメッセージ

技術的な詳細ではなく、ユーザーが理解できるメッセージを表示する。

```typescript
const uploadPhoto = async (file: File) => {
  try {
    const response = await photoService.upload(file);
    return response;
  } catch (error) {
    if (error instanceof FileSizeExceededException) {
      // ユーザーにわかりやすいメッセージ
      throw new Error('ファイルサイズは10MB以下にしてください');
    }
    if (error instanceof NetworkError) {
      throw new Error('ネットワークエラーが発生しました。もう一度お試しください');
    }
    // 予期しないエラー
    throw new Error('エラーが発生しました。しばらくしてからお試しください');
  }
};
```

#### 2. エラーバウンダリーの使用

予期しないエラーをキャッチして、アプリ全体のクラッシュを防ぐ。

```typescript
class ErrorBoundary extends React.Component<Props, State> {
  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    // エラーログサービスに送信
  }
  
  render() {
    if (this.state.hasError) {
      return <ErrorFallback />;
    }
    return this.props.children;
  }
}
```

### バックエンド

#### 1. カスタム例外の定義

ドメイン固有の例外を定義する。

```java
public class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException(String message) {
        super(message);
    }
}

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}

public class PhotoNotFoundException extends RuntimeException {
    public PhotoNotFoundException(UUID id) {
        super("Photo not found: " + id);
    }
}
```

#### 2. グローバル例外ハンドラー (RFC 9457準拠)

すべての例外を統一的に処理し、Problem Details形式でレスポンスを返す。

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
        problemDetail.setProperty("maxSize", "10MB");
        
        return ResponseEntity.badRequest().body(problemDetail);
    }
    
    @ExceptionHandler(PhotoNotFoundException.class)
    public ResponseEntity<ProblemDetail> handlePhotoNotFound(
        PhotoNotFoundException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setTitle("Photo Not Found");
        problemDetail.setType(URI.create("/errors/photo-not-found"));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
        Exception ex
    ) {
        log.error("Unexpected error occurred", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("/errors/internal-server-error"));
        
        return ResponseEntity.internalServerError().body(problemDetail);
    }
}
```

#### 3. 適切なログレベルの使用

```java
@Slf4j
@Service
public class PhotoService {
    public Photo uploadPhoto(MultipartFile file) {
        log.info("Photo upload started: fileName={}", file.getOriginalFilename());
        
        try {
            // 処理
            log.debug("Photo saved: id={}", photo.getId());
            return photo;
        } catch (FileSizeExceededException e) {
            log.warn("File size exceeded: fileName={}, size={}", 
                file.getOriginalFilename(), file.getSize());
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload photo: fileName={}", 
                file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload photo", e);
        }
    }
}
```

**ログレベルの使い分け:**
- `ERROR`: システムエラー、予期しない例外
- `WARN`: 警告、復旧可能なエラー
- `INFO`: 重要な処理の開始・終了
- `DEBUG`: 開発時のデバッグ情報

**注意事項:**
- 個人情報はログに出力しない
- パスワード、トークン等の機密情報は出力しない

---

## セキュリティ基準

### 入力バリデーション

すべてのユーザー入力は信頼せず、必ずバリデーションを実施する。

#### フロントエンド

```typescript
const validateFile = (file: File): ValidationResult => {
  const errors: string[] = [];
  
  // ファイルサイズチェック
  if (file.size > 10 * 1024 * 1024) {
    errors.push('ファイルサイズは10MB以下にしてください');
  }
  
  // ファイル形式チェック
  const validTypes = ['image/jpeg', 'image/png'];
  if (!validTypes.includes(file.type)) {
    errors.push('JPEG または PNG 形式のファイルを選択してください');
  }
  
  // ファイル名チェック(XSS対策)
  if (/<script|javascript:/i.test(file.name)) {
    errors.push('無効なファイル名です');
  }
  
  return { isValid: errors.length === 0, errors };
};
```

#### バックエンド

フロントエンドのバリデーションは信頼せず、バックエンドでも必ず検証する。

```java
@Component
public class FileValidator {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Set<String> VALID_MIME_TYPES = 
        Set.of("image/jpeg", "image/png");
    
    public void validate(MultipartFile file) {
        // ファイルの存在確認
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // ファイルサイズチェック
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException(
                String.format("File size exceeds %d bytes", MAX_FILE_SIZE)
            );
        }
        
        // MIMEタイプチェック
        String contentType = file.getContentType();
        if (!VALID_MIME_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException(
                "Only JPEG and PNG files are allowed"
            );
        }
        
        // ファイル内容の検証(マジックナンバーチェック)
        validateFileContent(file);
    }
    
    private void validateFileContent(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            is.read(header);
            
            // JPEG: FF D8 FF
            // PNG: 89 50 4E 47
            if (!isValidImageHeader(header)) {
                throw new InvalidFileTypeException("Invalid image file");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to validate file content", e);
        }
    }
}
```

### SQLインジェクション対策

#### Spring Data JPAを使用(推奨)

```java
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    // パラメータは自動的にエスケープされる
    List<Photo> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    @Query("SELECT p FROM Photo p WHERE p.fileName LIKE %:keyword%")
    List<Photo> searchByFileName(@Param("keyword") String keyword);
}
```

#### 生SQLを使う場合は必ずPreparedStatementを使用

```java
// ❌ 絶対にやってはいけない
String sql = "SELECT * FROM photos WHERE user_id = '" + userId + "'";

// ✅ PreparedStatementを使用
String sql = "SELECT * FROM photos WHERE user_id = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, userId);
```

### 機密情報の管理

機密情報はコードに直接書かず、環境変数または設定ファイルで管理する。

```java
// ❌ コードに直接書かない
private static final String DB_PASSWORD = "password123";

// ✅ 環境変数から取得
@Value("${spring.datasource.password}")
private String dbPassword;
```

```typescript
// ❌ コードに直接書かない
const API_KEY = 'sk_live_1234567890';

// ✅ 環境変数から取得
const API_KEY = import.meta.env.VITE_API_KEY;
```

---

## パフォーマンス基準

### フロントエンド

#### 1. 遅延読み込み (Lazy Loading)

初期ロード時間を短縮するため、必要になるまでコンポーネントを読み込まない。

```typescript
import { lazy, Suspense } from 'react';

// コンポーネントの遅延読み込み
const PhotoGallery = lazy(() => import('./components/PhotoGallery'));

function App() {
  return (
    <Suspense fallback={<Loading />}>
      <PhotoGallery />
    </Suspense>
  );
}
```

#### 2. メモ化 (Memoization)

高コストな計算やコールバックをキャッシュする。

```typescript
// 高コストな計算のメモ化
const filteredPhotos = useMemo(() => {
  return photos.filter(photo => photo.userId === currentUserId);
}, [photos, currentUserId]);

// コールバックのメモ化
const handleDelete = useCallback((id: string) => {
  deletePhoto(id);
}, [deletePhoto]);
```

#### 3. 仮想スクロール (大量データ)

大量のリストを表示する場合は、仮想スクロールを使用する。

```typescript
import { FixedSizeList } from 'react-window';

const PhotoList = ({ photos }: Props) => {
  return (
    <FixedSizeList
      height={600}
      itemCount={photos.length}
      itemSize={200}
      width="100%"
    >
      {({ index, style }) => (
        <div style={style}>
          <PhotoCard photo={photos[index]} />
        </div>
      )}
    </FixedSizeList>
  );
};
```

### バックエンド

#### 1. N+1問題の回避

関連エンティティを取得する際は、JOIN FETCHを使用する。

```java
// ❌ N+1問題
@GetMapping
public List<PhotoResponse> getPhotos() {
    List<Photo> photos = photoRepository.findAll();
    // 各photoのuserを取得するために追加のクエリが発行される
    return photos.stream()
        .map(photo -> PhotoResponse.from(photo, photo.getUser()))
        .toList();
}

// ✅ JOIN FETCHで解決
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    @Query("SELECT p FROM Photo p LEFT JOIN FETCH p.user")
    List<Photo> findAllWithUser();
}
```

#### 2. ページネーション

大量データは必ずページングする。

```java
@GetMapping
public Page<PhotoResponse> getPhotos(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Photo> photos = photoRepository.findAll(pageable);
    return photos.map(PhotoResponse::from);
}
```

#### 3. キャッシュの活用

頻繁にアクセスされるデータはキャッシュする。

```java
@Service
@CacheConfig(cacheNames = "photos")
public class PhotoService {
    
    @Cacheable(key = "#id")
    public Photo getPhoto(UUID id) {
        return photoRepository.findById(id)
            .orElseThrow(() -> new PhotoNotFoundException(id));
    }
    
    @CacheEvict(key = "#id")
    public void deletePhoto(UUID id) {
        photoRepository.deleteById(id);
    }
}
```

---

## アクセシビリティ基準

### セマンティックHTML

意味のあるHTML要素を使用する。

```typescript
// ✅ セマンティックHTML
<nav aria-label="メインナビゲーション">
  <ul>
    <li><a href="/">ホーム</a></li>
    <li><a href="/photos">写真一覧</a></li>
  </ul>
</nav>

<main>
  <h1>写真アップロード</h1>
  <form aria-label="写真アップロードフォーム">
    {/* フォーム内容 */}
  </form>
</main>

// ❌ 非セマンティック
<div class="nav">
  <div class="nav-item">ホーム</div>
  <div class="nav-item">写真一覧</div>
</div>

<div>
  <div class="title">写真アップロード</div>
  <div class="form">
    {/* フォーム内容 */}
  </div>
</div>
```

### キーボード操作

すべての機能がキーボードで操作できるようにする。

```typescript
const PhotoCard = ({ photo, onDelete }: Props) => {
  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      onDelete(photo.id);
    }
  };
  
  return (
    <Card>
      <CardMedia image={photo.imageUrl} alt={photo.fileName} />
      <Button 
        onClick={() => onDelete(photo.id)}
        onKeyDown={handleKeyDown}
        aria-label={`${photo.fileName}を削除`}
      >
        削除
      </Button>
    </Card>
  );
};
```

### ARIA属性

適切なARIA属性を使用して、スクリーンリーダーのサポートを強化する。

```typescript
const FileUpload = () => {
  const [uploading, setUploading] = useState(false);
  
  return (
    <div>
      <input
        type="file"
        id="file-input"
        aria-label="写真ファイルを選択"
        aria-describedby="file-help"
      />
      <span id="file-help">
        JPEG または PNG 形式のファイルを選択してください(最大10MB)
      </span>
      
      {uploading && (
        <div 
          role="status" 
          aria-live="polite"
          aria-busy="true"
        >
          アップロード中...
        </div>
      )}
    </div>
  );
};
```

---

## コードレビュー チェックリスト

実装完了後、以下の項目をチェックしてください。

### 全般
- [ ] コーディング規約に従っているか
- [ ] テストが書かれ、すべて通っているか
- [ ] エラーハンドリングは適切か
- [ ] ログ出力は適切か(機密情報を含まないか)
- [ ] コメントは必要最小限か(コードで表現できることは書かない)

### フロントエンド
- [ ] 型安全か(`any`を使っていないか)
- [ ] アクセシビリティは考慮されているか
- [ ] レスポンシブデザインに対応しているか
- [ ] パフォーマンス上の問題はないか
- [ ] ユーザーフレンドリーなエラーメッセージか

### バックエンド
- [ ] 入力バリデーションは適切か
- [ ] SQLインジェクション対策されているか
- [ ] N+1問題はないか
- [ ] トランザクション境界は適切か
- [ ] エラーレスポンスはRFC 9457に準拠しているか

### テスト
- [ ] ユニットテストカバレッジは80%以上か（CI/CDで自動チェック）
- [ ] E2Eテストはユーザー視点で書かれているか
- [ ] エッジケースがテストされているか
- [ ] エラーケースがテストされているか

### セキュリティ
- [ ] 入力値は検証されているか
- [ ] 機密情報はコードに含まれていないか
- [ ] 認証・認可は適切か
- [ ] CORS設定は適切か

---

## 関連ドキュメント

- [コーディング規約](./CODING_STANDARDS.md) - 命名規則、フォーマット、言語固有の規約
- [開発プロセス](./DEVELOPMENT.md) - BDD/TDD開発フロー
- [Linter設定](./LINTER.md) - 自動コード品質チェック
