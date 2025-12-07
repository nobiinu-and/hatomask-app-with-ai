---
description: バックエンド機能をTDDで実装するための専用プロンプトテンプレート
---

# バックエンドTDD実装 (MSW互換)

## 🎯 目的
**モックAPI(MSW)の動作を本物のバックエンドで再現する**

## 前提条件チェック
- [ ] Frontend実装(Phase 1)が完了している
- [ ] E2EテストがMSWで通っている
- [ ] 実装計画の該当ステップが存在する (`docs/plans/{feature_name}/`)
- [ ] MSWハンドラー(`src/frontend/src/mocks/handlers.ts`)でレスポンス形式を確認できる

**注意**: テストリストは実装開始時に作成します

## 依頼内容
テストリストに基づき、以下のバックエンド機能をTDDサイクルで実装してください。

## 対象機能
- **Feature**: `[Featureファイル名]`
- **Gherkinステップ**: `[ステップ名]` (例: `Then アップロードが成功する`)
- **実装計画**: `docs/plans/[feature_name]/[plan_file].md`
- **テストリスト**: `docs/plans/[feature_name]/backend-testlist/[step_type]_[step_description].md` (作成する)
- **開始Layer**: Repository層から

---

## テストリストとテストコードの関係

### テストリストの役割: 仕様の概要
テストリストは**実装すべき振る舞いの概要**を記述します。
これは設計段階で作成し、「何を作るべきか」を明確にするためのものです。

**記述フォーマット**:
```
- [ ] {条件} を受け取ると、{期待する振る舞い}
```

**例**:
```markdown
- [ ] 有効なJPEGファイルを受け取ると、ユニークなIDで保存してメタデータを返す
- [ ] 空のファイルを受け取ると、"File is empty" エラーで拒否する
```

### テストコードの役割: 動く仕様書
テストコードは**仕様の詳細を実行可能な形式**で表現します。
**テストリストに含める内容**:

1. **対象機能の説明**
   ```markdown
   ## Service層: PhotoService
   
   **仕様**: ユーザーが選択した写真ファイルをサーバーに保存し、メタデータを返す
   ```

2. **振る舞いの列挙**
   ```markdown
   #### 正常系の振る舞い
   - [ ] 有効なJPEGファイル(5MB)を受け取ると、ユニークなIDで保存してメタデータを返す
   - [ ] 有効なPNGファイル(3MB)を受け取ると、ユニークなIDで保存してメタデータを返す
   
   #### 異常系の振る舞い
   - [ ] 空のファイルを受け取ると、"File is empty" エラーで拒否する
   - [ ] 10MBを超えるファイルを受け取ると、"File size exceeds maximum limit" エラーで拒否する
   ```

3. **検証項目の明示**
   ```markdown
   **テストで確認すること**:
   - ファイルが `uploads/photos/{UUID}.{ext}` に物理的に保存されている
   - DBにメタデータが保存されている
   - レスポンスがMSWと同じ形式である
   - エラー時は適切な例外が投げられる
   ```

4. **MSWレスポンス形式の参照**
   ```markdown
   ### MSWレスポンス形式 (確認必須)
   **ファイル**: `src/frontend/src/mocks/handlers.ts`
   
   [レスポンス形式を記載]
   ```
**Arrange-Act-Assert パターン**:
```java
@Test
@DisplayName("{テストリストの項目}") // 何をテストするか
void {method}_should{結果}_when{条件}() {
    // Arrange (準備): テストに必要なデータやモックを準備
    // 何を準備するのか、なぜその値なのかをコメントで説明
    
    // Act (実行): テスト対象のメソッドを実行
    // 何を実行するのかを明確に
    
    // Assert (検証): 期待する結果を検証
    // すべての期待値を網羅的に検証
    // 副作用（ファイル保存、DB更新など）も確認
}
```

---

## 作業手順 (Red-Green-Refactor)

### Step 1: テストリストの作成

**📋 テストリスト命名規則**:
- 配置場所: `docs/plans/{feature_name}/backend-testlist/`
- ファイル名: `{step_type}_{step_description}.md`
  - `{step_type}`: `given`, `when`, `and`, `then` (小文字)
  - `{step_description}`: ステップ内容を英語で簡潔に表現 (スネークケース)
  
**例**:
```
docs/plans/01_photo_upload_download_basic_jpeg/backend-testlist/
├── then_upload_success.md          # Then アップロードが成功する
├── and_preview_image.md            # And プレビューエリアに選択した画像が表示される
├── when_click_download.md          # When ユーザーが「ダウンロード」ボタンをクリックする
└── then_download_original.md       # Then 元の画像がダウンロードされる
```
### Step 3: テスト作成 (Red)

1. テストファイルを作成
   - 場所: `src/backend/src/test/java/com/hatomask/[layer]/`
   - 命名: `[ClassName]Test.java`

2. テストリストの1項目に対してテストメソッドを作成
   
   **テストコードの構造**:
   ```java
   @Test
   @DisplayName("{テストリストの項目をそのままコピー}")
   void {method}_should{期待する結果}_when{条件}() {
       // Arrange: {何を準備するか}
       // 具体的な値を使ってテストデータを準備
       // モックの振る舞いを設定
       
       // Act: {何を実行するか}
       // テスト対象のメソッドを呼び出す
       
       // Assert: {何を検証するか}
       // 期待する結果をすべて検証
       // 副作用も確認 (verify, ファイル存在確認など)
   }
   ```

3. **Arrange-Act-Assertのポイント**:
   
   **Arrange (準備)**:
   - 具体的な値を使う (5MB, "sample.jpg" など)
   - なぜその値なのかをコメントで説明
   - モックの振る舞いを明確に設定
   
   **Act (実行)**:
   - テスト対象のメソッドを1つだけ呼ぶ
   - 何を実行するかをコメントで明記
### Step 4: 実装 (Green)

1. テストを通すための**最小限の実装**を行う
   - テストが要求する振る舞いを満たす
   - 過度な先読み実装をしない
   - MSWのレスポンス形式と一致させる

2. **実装のポイント**:
   - テストコードが「仕様書」、実装コードが「仕様の実現」
   - テストで検証していることを確実に実装
   - エラーメッセージはテストで期待する通りに
   - レスポンス形式はMSWと完全に一致

3. `mvn test` を実行し、**成功すること(Green)**を確認

4. **ここで停止してユーザーに報告**
4. `mvn test` を実行し、**失敗すること(Red)**を確認

5. **ここで停止してユーザーに報告**
3. 実装順序
4. MSWレスポンス形式の参照

**作成手順**:
1. 実装計画の該当ステップを確認
2. Phase 2のBackend実装要件を元にテストリストを作成
3. テストリスト作成完了後、ユーザーに報告
4. 承認後、実装開始

### Step 2: MSWレスポンス形式の確認

1. `src/frontend/src/mocks/handlers.ts` を開く
2. 該当エンドポイントのハンドラーを確認
3. 以下を把握する:
   - レスポンスのJSON構造
   - ステータスコード
   - ヘッダー (Content-Type, Content-Disposition等)
4. **バックエンドはこれと完全に同じ形式を返すこと**

### Step 3: テスト作成 (Red)

1. テストファイルを作成
   - 場所: `src/backend/src/test/java/com/hatomask/[layer]/`
   - 命名: `[ClassName]Test.java`
2. テストリストの1項目に対してテストメソッドを作成
### Step 5: リファクタリング

1. コードの重複を排除
2. 可読性を向上
3. 変数名やメソッド名を仕様を反映したものに
4. `mvn test` でテストが引き続き成功することを確認

**リファクタリングのポイント**:
- テストが保証する仕様は変えない
- テストコード自体もリファクタリング対象
- Given/When/Then (Arrange/Act/Assert) が明確か確認
1. テストを通すための**最小限の実装**を行う
2. MSWのレスポンス形式と一致させる
3. `mvn test` を実行し、**成功すること(Green)**を確認
4. **ここで停止してユーザーに報告**

### Step 5: リファクタリング

1. コードの重複を排除
2. 可読性を向上
3. `mvn test` でテストが引き続き成功することを確認

### Step 6: 次のテストケースへ

テストリストの次の項目に進む (Step 3に戻る)

---

## 🚫 厳守事項

### 変更禁止
- ❌ **E2Eステップ定義(`e2e/step-definitions/`)を変更しない**
  - フロントエンド実装でE2Eテストは既に完成している
- ❌ **フロントエンドコード(`src/frontend/`)を変更しない**
  - APIクライアントも変更しない
- ❌ **MSWハンドラー(`src/frontend/src/mocks/`)を変更しない**
  - モックAPIは既に完成している

### 実装対象
- ✅ **バックエンドコード(`src/backend/`)のみ実装する**
  - Entity, Repository, Service, Controller
  - Exception classes
  - DTO classes
  - Configuration (必要な場合)

### TDDサイクル
- 🔴 **Red**: テストを書いて失敗させる
- 🟢 **Green**: 最小限の実装でテストを通す
- 🔵 **Refactor**: コードを整理する
- 🛑 **各段階で停止**: ユーザーに報告してから次に進む

---

## 実装例テンプレート

### Repository層のテスト例

```java
@DataJpaTest
class PhotoRepositoryTest {
    
    @Autowired
    private PhotoRepository photoRepository;
    
    @Test
    @DisplayName("Photoエンティティを保存すると、IDとcreatedAtが自動生成されてDBに保存される")
    void save_shouldSavePhotoWithGeneratedIdAndTimestamp() {
        // Arrange: Photoエンティティを準備
        Photo photo = new Photo();
        photo.setFileName("sample.jpg");
        photo.setFileSize(5242880L); // 5MB
        photo.setMimeType("image/jpeg");
        photo.setFilePath("/uploads/photos/sample.jpg");
        
        // Act: 保存を実行
        Photo saved = photoRepository.save(photo);
        
        // Assert: IDとcreatedAtが自動生成されている
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getFileName()).isEqualTo("sample.jpg");
        assertThat(saved.getFileSize()).isEqualTo(5242880L);
        assertThat(saved.getMimeType()).isEqualTo("image/jpeg");
    }
}
```

### Service層のテスト例

```java
@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {
    
    @Mock
    private PhotoRepository photoRepository;
    
    @Mock
    private FileStorageService fileStorageService;
    
    @InjectMocks
    private PhotoService photoService;
    
    @Test
    @DisplayName("有効なJPEGファイル(5MB)を受け取ると、ユニークなIDで保存してメタデータを返す")
    void uploadPhoto_shouldSaveFileAndReturnMetadata_whenValidJpegFile() {
        // Arrange: 5MBの有効なJPEGファイルを準備
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "sample.jpg",
            "image/jpeg",
            new byte[5 * 1024 * 1024] // 5MB
        );
        
        Photo savedPhoto = new Photo();
        savedPhoto.setId(UUID.randomUUID());
        savedPhoto.setFileName("sample.jpg");
        savedPhoto.setFileSize(5242880L);
        savedPhoto.setMimeType("image/jpeg");
        savedPhoto.setCreatedAt(LocalDateTime.now());
        
        when(photoRepository.save(any(Photo.class))).thenReturn(savedPhoto);
        
        // Act: アップロードを実行
        PhotoResponse response = photoService.uploadPhoto(file);
        
        // Assert: メタデータが正しく返される
        assertThat(response.fileName()).isEqualTo("sample.jpg");
        assertThat(response.fileSize()).isEqualTo(5242880L);
        assertThat(response.mimeType()).isEqualTo("image/jpeg");
        assertThat(response.id()).isNotNull();
        assertThat(response.createdAt()).isNotNull();
        
        // Assert: ファイルが保存され、DBに記録される
        verify(fileStorageService).save(any(), any());
        verify(photoRepository).save(any(Photo.class));
    }
    
    @Test
    @DisplayName("空のファイルを受け取ると、File is empty エラーで拒否する")
    void uploadPhoto_shouldThrowInvalidFileException_whenEmptyFile() {
        // Arrange: 空のファイルを準備
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.jpg",
            "image/jpeg",
            new byte[0] // サイズ0
        );
        
        // Act & Assert: InvalidFileExceptionが投げられる
        assertThatThrownBy(() -> photoService.uploadPhoto(emptyFile))
            .isInstanceOf(InvalidFileException.class)
            .hasMessage("File is empty");
        
        // Assert: ファイルもDBも操作されない
        verify(fileStorageService, never()).save(any(), any());
        verify(photoRepository, never()).save(any());
    }
}
```

### Controller層のテスト例

```java
@WebMvcTest(PhotoController.class)
class PhotoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PhotoService photoService;
    
    @Test
    @DisplayName("有効な写真ファイルを受け取ると、201 Createdとメタデータを返す")
    void uploadPhoto_shouldReturn201WithMetadata_whenValidFile() throws Exception {
        // Arrange: 有効なファイルとレスポンスを準備
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "sample.jpg",
            "image/jpeg",
            new byte[5 * 1024 * 1024] // 5MB
        );
        
        PhotoResponse response = new PhotoResponse(
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
            "sample.jpg",
            5242880L,
            "image/jpeg",
            LocalDateTime.parse("2023-11-26T10:00:00")
        );
        
        when(photoService.uploadPhoto(any())).thenReturn(response);
        
        // Act & Assert: 201とJSONが返される
        mockMvc.perform(multipart("/api/v1/photos")
                .file(file))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("550e8400-e29b-41d4-a716-446655440000"))
            .andExpect(jsonPath("$.fileName").value("sample.jpg"))
            .andExpect(jsonPath("$.fileSize").value(5242880))
            .andExpect(jsonPath("$.mimeType").value("image/jpeg"))
            .andExpect(jsonPath("$.createdAt").exists());
    }
    
    @Test
    @DisplayName("バリデーションエラー時に、400 Bad Requestと詳細なエラーを返す")
    void uploadPhoto_shouldReturn400WithErrorDetail_whenFileSizeExceeded() throws Exception {
        // Arrange: サイズ超過ファイルを準備
        MockMultipartFile largeFile = new MockMultipartFile(
            "file",
            "large.jpg",
            "image/jpeg",
            new byte[11 * 1024 * 1024] // 11MB
        );
        
        when(photoService.uploadPhoto(any()))
            .thenThrow(new FileSizeExceededException("File size exceeds maximum limit of 10MB"));
        
        // Act & Assert: 400とRFC 9457形式のエラーが返される
        mockMvc.perform(multipart("/api/v1/photos")
                .file(largeFile))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.type").value("about:blank"))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("File size exceeds maximum limit of 10MB"));
    }
}
```

---

## チェックリスト

実装開始前に確認:
- [ ] テストリストを作成した (`backend-testlist/{step_type}_{step_description}.md`)
- [ ] MSWのレスポンス形式を確認した
- [ ] 実装する層(Repository/Service/Controller)を理解した
- [ ] E2Eステップ定義を変更しないことを理解した

各テストケース実装時:
- [ ] テストを書いた (Red)
- [ ] `mvn test` で失敗を確認した
- [ ] 最小限の実装をした (Green)
- [ ] `mvn test` で成功を確認した
- [ ] コードをリファクタリングした
- [ ] MSWと同じレスポンス形式になっている

---

## トラブルシューティング

### Q: E2Eテストを修正したくなった
A: **修正しないでください**。E2EテストはMSWで既に動作しています。バックエンドをMSWに合わせてください。

### Q: フロントエンドのAPIクライアントを変更したい
A: **変更しないでください**。フロントエンドは完成しています。バックエンドのレスポンス形式を合わせてください。

### Q: MSWのレスポンス形式が間違っている気がする
A: まずはMSWと同じ形式で実装してください。問題があれば別のタスクとして対応します。

### Q: テストリストにないケースを実装したい
A: テストリストに追加してから実装してください。勝手に実装を進めないでください。

### Q: テストリストのファイル名はどうすればいい?
A: `{step_type}_{step_description}.md` の形式で作成してください。
- `step_type`: `given`, `when`, `and`, `then` (小文字)
- `step_description`: ステップ内容を英語で簡潔に (スネークケース)
- 例: `then_upload_success.md`, `and_preview_image.md`
