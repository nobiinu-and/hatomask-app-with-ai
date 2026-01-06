# API 層テストリスト: {ステップ名}

**Feature**: {対応する Gherkin 機能の参照}  
**Domain Model**: `docs/spec/models/{model_name}.md`  
**Plan**: `docs/plans/[Spec名]_[シナリオ識別子].md`  
**MSW Handler**: `src/frontend/src/mocks/handlers.ts`

---

## テストリストの目的

縦切り実装サイクル（Task06）の中で実装する API 層（UseCase, Controller, DTO）のテストケースを列挙し、MSW 互換性を確保します。

---

## 1. UseCase 層

### テストリスト構造

- {UseCase 名}
  - {ユースケースの振る舞いの説明}
    - {具体的なテストケース（正常系）}
      - {さらに具体的な入力と期待結果の例}
    - {具体的なテストケース（異常系）}
      - {さらに具体的な入力と期待結果の例}

### 記入例: UploadPhotoUseCase

```markdown
- UploadPhotoUseCase
  - 有効なファイルを受け取ると Photo を保存してメタデータを返す
    - [ ] JPEG 画像を受け取ると Photo を保存してメタデータを返す
      - [ ] "sample.jpg"を受け取ると Photo が保存される
      - [ ] 保存後、メタデータを返す
    - [ ] PNG 画像を受け取ると Photo を保存してメタデータを返す
      - [ ] "photo.png", 512000, "image/png" を受け取ると Photo が保存される
      - [ ] 保存後のメタデータには createdAt が現在時刻として設定される
    - [ ] 保存時に Repository が正しく呼び出される
      - [ ] PhotoRepository.save()が 1 回呼び出される
      - [ ] save()に渡される Photo エンティティは入力と同じプロパティを持つ
  - バリデーションエラーで Photo の保存が失敗する
    - [ ] 空のファイルを受け取ると InvalidFileException が発生する
      - [ ] ファイルサイズ 0 バイトを受け取ると例外が発生する
      - [ ] Repository.save()は呼び出されない
    - [ ] ファイルサイズが上限を超える場合 FileSizeLimitExceededException が発生する
      - [ ] 10MB+1 バイトのファイルを受け取ると例外が発生する
      - [ ] 100MB のファイルを受け取ると例外が発生する
      - [ ] Repository.save()は呼び出されない
    - [ ] 許可されていない MIME タイプの場合 UnsupportedFileTypeException が発生する
      - [ ] "image/gif" を受け取ると例外が発生する
      - [ ] "text/plain" を受け取ると例外が発生する
      - [ ] Repository.save()は呼び出されない
  - 重複ファイル名の検証が正しく動作する
    - [ ] 既存のファイル名と重複しない場合、正常に保存される
      - [ ] 新規ファイル名 "newfile.jpg" で保存が成功する
    - [ ] 既存のファイル名と重複する場合、DuplicateFileNameException が発生する
      - [ ] 既存ファイル名 "existing.jpg" で例外が発生する
      - [ ] Repository.existsByFileName()が true を返す場合、save()は呼び出されない
```

### JUnit テストコードへの対応

上記のテストリストは以下のような構造に対応します：

```java
class UploadPhotoUseCaseTest {  // 第1階層: テストクラス名
    @Nested
    class 有効なファイルを受け取るとPhotoを保存してメタデータを返す {  // 第2階層
        @Nested
        class JPEG画像を受け取るとPhotoを保存してメタデータを返す {  // 第3階層
            @Test
            void sample_jpg_1024000_image_jpegを受け取るとPhotoが保存される() {  // 第4階層
                // テスト実装（Repositoryモック使用）
            }
        }

        @Nested
        class 保存時にRepositoryが正しく呼び出される {
            @Test
            void PhotoRepository_saveが1回呼び出される() {
                // verify(repository, times(1)).save(any());
            }
        }
    }

    @Nested
    class バリデーションエラーでPhotoの保存が失敗する {  // 第2階層
        @Nested
        class 空のファイルを受け取るとInvalidFileExceptionが発生する {  // 第3階層
            @Test
            void ファイルサイズ0バイトを受け取ると例外が発生する() {  // 第4階層
                // assertThrows(InvalidFileException.class, ...);
            }

            @Test
            void Repository_saveは呼び出されない() {
                // verify(repository, never()).save(any());
            }
        }
    }
}
```

**構造化のポイント**:

- 第 1 階層: テストクラス名（例: `UploadPhotoUseCase` → `UploadPhotoUseCaseTest.java`）
- 第 2 階層: `@Nested`クラス名（ユースケースの振る舞いの説明）
- 第 3 階層: `@Nested`クラスまたは`@Test`メソッド名（具体的なテストケース）
- 第 4 階層: `@Test`メソッド名（さらに具体的な入力と期待結果）
- **重要**: 第 2 階層以降の文言は、そのままクラス名やメソッド名として使えるよう日本語で記述する
- **モック検証**: Repository 呼び出しの検証も明示的にテストケースに含める

---

## 2. Controller 層

### テストリスト構造

- {Controller 名}
  - {エンドポイントの振る舞いの説明}
    - {具体的なテストケース}
      - {さらに具体的な入力と期待結果の例}

### 記入例: PhotoController (簡潔版)

UseCase 層の構造を参考に、以下のように記述します：

```markdown
- PhotoController
  - POST /api/v1/photos は有効なリクエストを受け取ると 201 とメタデータを返す
    - [ ] 有効な JPEG 画像を受け取ると 201 Created を返す
      - [ ] レスポンスボディに id, fileName, fileSize, mimeType, createdAt が含まれる
      - [ ] Content-Type が "application/json" である
    - [ ] レスポンス形式が MSW と完全に一致する
      - [ ] JSON キーがキャメルケース（fileName, fileSize 等）である
      - [ ] 日付形式が MSW と同じ（ISO 8601 形式）である
  - POST /api/v1/photos は無効なリクエストを拒否する
    - [ ] ファイルが空の場合、400 Bad Request を返す
      - [ ] エラーメッセージが含まれる
    - [ ] ファイルサイズが上限を超える場合、400 Bad Request を返す
    - [ ] 許可されていないファイル形式の場合、400 Bad Request を返す
    - [ ] エラーレスポンス形式が MSW と一致する
```

**注意**: Controller 層は`@WebMvcTest`で単体テストを行い、UseCase はモック化します

---

## 3. 統合テスト (E2E - MSW なし)

### テストリスト構造

- {Feature 名} Integration
  - {統合テストの観点}
    - {具体的な確認項目}

### 記入例: Photo Upload Integration (簡潔版)

```markdown
- Photo Upload Integration
  - E2E テストが MSW なしで正常に動作する
    - [ ] アップロードステップが成功する
    - [ ] レスポンスの JSON 構造が MSW と完全に一致する
    - [ ] レスポンスのステータスコードが 201 である
  - データが正しく永続化される
    - [ ] Photo テーブルにレコードが追加される
    - [ ] ファイルが uploads/photos/ ディレクトリに保存される
    - [ ] ファイル名と DB のレコードが一致する
```

**注意**: 統合テストは`@SpringBootTest`で実行し、実際の DB とファイルシステムを使用します

---

## MSW レスポンス形式 (確認必須)

**ファイル**: `src/frontend/src/mocks/handlers.ts`

### {エンドポイント}

**リクエスト**:

```
{リクエストの形式をコピー}
```

**レスポンス** (正常系):

```json
{
  // MSWから返されるレスポンスをコピー
}
```

**レスポンス** (異常系):

```json
{
  // MSWから返されるエラーレスポンスをコピー
}
```

**確認ポイント**:

- ステータスコード
- JSON 構造
- プロパティ名（キャメルケース）
- ヘッダー (Content-Type, Content-Disposition 等)

---

## 実装順序の推奨

### API 層の実装フロー

1. **UseCase 層**

   - テスト: Repository モックで単体テスト
   - 実装: Red → Green → Refactor
   - 配置: `application/usecase/` パッケージ
   - 注意: ドメイン層の Repository と DomainService をモック化

2. **DTO 定義**

   - Request/Response DTO を定義
   - 配置: `presentation/dto/` パッケージ
   - 注意: 不変オブジェクト推奨（record 型や final フィールド）

3. **Controller 層**

   - テスト: `@WebMvcTest` で単体テスト
   - 実装: Red → Green → Refactor
   - 配置: `presentation/controller/` パッケージ
   - 注意: UseCase をモック化、HTTP リクエスト/レスポンスの変換のみ

4. **Exception Handling**

   - `@RestControllerAdvice` でグローバルエラーハンドリング
   - MSW と同じエラーレスポンス形式を返す
   - 配置: `presentation/exception/` パッケージ

5. **統合テスト**

   - テスト: `@SpringBootTest` で統合テスト
   - MSW なしで E2E 実行
   - 実装: Red → Green → Refactor
   - 注意: 実際の DB（H2 インメモリ）とファイルシステムを使用

6. **MSW 互換性確認**
   - E2E テストを MSW なしで実行
   - レスポンス形式の完全一致を確認
   - 差異があれば修正

---

## 進捗管理

実装を進める際:

1. **最も簡単なテストから始める** (通常は UseCase 層の正常系の最もシンプルなケース)
2. **完了したら項目を`[x]` に、未完了は `[ ]` のままにする**
3. **実装中に気づいた新しいケースを追加する**
4. **MSW との差異が見つかったら記録し、即座に修正する**
5. **リファクタリングが必要になったら別項目として追加する**

**進捗表示の例**:

```markdown
- UploadPhotoUseCase
  - 有効なファイルを受け取ると Photo を保存してメタデータを返す
    - [x] JPEG 画像を受け取ると Photo を保存してメタデータを返す
      - [x] "sample.jpg", 1024000, "image/jpeg" を受け取ると Photo が保存される
      - [x] 保存後、id, fileName, fileSize, mimeType, createdAt を含むメタデータを返す
    - [ ] PNG 画像を受け取ると Photo を保存してメタデータを返す ← 現在実装中
      - [ ] "photo.png", 512000, "image/png" を受け取ると Photo が保存される
  - バリデーションエラーで Photo の保存が失敗する
    - [ ] 空のファイルを受け取ると InvalidFileException が発生する
```

---

## References

- Domain Model: `docs/spec/models/{model_name}.md`
- MSW Handler: `src/frontend/src/mocks/handlers.ts`
- Implementation Plan: `docs/plans/[Spec名]_[シナリオ識別子].md`
- API TDD Prompt: `docs/ai/prompts/tasks/06_implement_backend_api.md`
