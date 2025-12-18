# ドメイン層テストリスト: {ステップ名}

**Feature**: {対応する Gherkin 機能の参照}  
**Domain Model**: `docs/spec/models/{feature_name}.md`  
**Plan**: `docs/plans/{feature_id}/`

---

## テストリストの目的

Phase 6 で実装するドメイン層（Entity, Repository, DomainService）のテストケースを列挙します。

---

## 1. Entity/ValueObject

### テストリスト構造

- {Entity 名}
  - {機能や振る舞いの説明}
    - {具体的なテストケース（正常系）}
      - {さらに具体的な入力と期待結果の例}
    - {具体的なテストケース（異常系・バリデーション）}
      - {さらに具体的な入力と期待結果の例}

### 記入例: Photo Entity

```markdown
- Photo Entity
  - 有効なプロパティを受け取ると Photo エンティティが生成される
    - [ ] ファイル名、ファイルサイズ、MIME タイプを受け取ると正常に生成される
      - [ ] sample.jpg を受け取ると Photo が生成される
      - [ ] photo.png を受け取ると Photo が生成される
    - [ ] 生成されたエンティティは正しいプロパティ値を持つ
      - [ ] getFileName()は設定したファイル名を返す
      - [ ] getFileSize()は設定したファイルサイズを返す
      - [ ] getMimeType()は設定した MIME タイプを返す
  - バリデーションエラーでエンティティ生成が失敗する
    - [ ] ファイル名が空の場合、IllegalArgumentException が発生する
      - [ ] 空文字列を受け取ると例外が発生する
      - [ ] null を受け取ると例外が発生する
    - [ ] ファイルサイズが不正な場合、IllegalArgumentException が発生する
      - [ ] 0 バイトを受け取ると例外が発生する
      - [ ] 負の値を受け取ると例外が発生する
      - [ ] 10MB を超える値を受け取ると例外が発生する
    - [ ] MIME タイプが許可されていない場合、IllegalArgumentException が発生する
      - [ ] "image/gif" を受け取ると例外が発生する
      - [ ] "text/plain" を受け取ると例外が発生する
      - [ ] 空文字列を受け取ると例外が発生する
```

### JUnit テストコードへの対応

上記のテストリストは以下のような構造に対応します：

```java
class PhotoEntityTest {  // 第1階層: テストクラス名
    @Nested
    class 有効なプロパティを受け取るとPhotoエンティティが生成される {  // 第2階層
        @Nested
        class ファイル名_ファイルサイズ_MIMEタイプを受け取ると正常に生成される {  // 第3階層
            @Test
            void sample_jpg_1024000_image_jpegを受け取るとPhotoが生成される() {  // 第4階層
                // テスト実装
            }
        }
    }

    @Nested
    class バリデーションエラーでエンティティ生成が失敗する {  // 第2階層
        @Nested
        class ファイル名が空の場合_IllegalArgumentExceptionが発生する {  // 第3階層
            @Test
            void 空文字列を受け取ると例外が発生する() {  // 第4階層
                // テスト実装
            }
        }
    }
}
```

**構造化のポイント**:

- 第 1 階層: テストクラス名（例: `Photo Entity` → `PhotoEntityTest.java`）
- 第 2 階層: `@Nested`クラス名（機能や振る舞いの説明）
- 第 3 階層: `@Nested`クラスまたは`@Test`メソッド名（具体的なテストケース）
- 第 4 階層: `@Test`メソッド名（さらに具体的な入力と期待結果）
- **重要**: 第 2 階層以降の文言は、そのままクラス名やメソッド名として使えるよう日本語で記述する

---

## 2. Repository Interface & 実装

### テストリスト構造

- {Repository 名}
  - {基本操作の説明}
    - {具体的なテストケース}
      - {さらに具体的な入力と期待結果の例}
  - {クエリメソッドの説明}
    - {具体的なテストケース}
      - {さらに具体的な入力と期待結果の例}

### 記入例: PhotoRepository

```markdown
- PhotoRepository
  - エンティティの保存操作が正常に動作する
    - [ ] Photo エンティティを保存すると、ID と createdAt が自動生成される
      - [ ] 新しい Photo を保存すると ID が採番される
      - [ ] 新しい Photo を保存すると createdAt に現在時刻が設定される
    - [ ] 保存されたエンティティは DB から取得できる
      - [ ] 保存後、同じ ID で検索すると同じ内容の Photo が返る
  - ID による検索が正常に動作する
    - [ ] 存在する ID で検索すると、保存済みの Photo が取得できる
      - [ ] ID=1 で保存した Photo を ID=1 で検索すると取得できる
    - [ ] 存在しない ID で検索すると、空の Optional が返る
      - [ ] 存在しない ID=999 で検索すると Optional.empty()が返る
  - ファイル名による存在確認が正常に動作する
    - [ ] 存在するファイル名で確認すると true が返る
      - [ ] "sample.jpg"で保存後、"sample.jpg"で存在確認すると true が返る
    - [ ] 存在しないファイル名で確認すると false が返る
      - [ ] "notfound.jpg"で存在確認すると false が返る
  - 複数件の検索が正常に動作する
    - [ ] ユーザー ID で検索すると、該当する Photo 一覧が返る
      - [ ] userID=1 の Photo2 件を保存後、userID=1 で検索すると 2 件返る
      - [ ] userID=1 で検索時、userID=2 の Photo は含まれない
```

### JUnit テストコードへの対応

```java
@DataJpaTest  // Repository統合テスト用
class PhotoRepositoryTest {
    @Nested
    class エンティティの保存操作が正常に動作する {
        @Nested
        class Photoエンティティを保存すると_IDとcreatedAtが自動生成される {
            @Test
            void 新しいPhotoを保存するとIDが採番される() {
                // テスト実装
            }
        }
    }

    @Nested
    class IDによる検索が正常に動作する {
        @Test
        void 存在するIDで検索すると_保存済みのPhotoが取得できる() {
            // 第3階層に第4階層がない場合は@Testメソッドに
        }
    }
}
```

**注意**: Repository 実装テストは `@DataJpaTest` を使用した統合テストで実行します

---

## 3. DomainService (必要な場合)

### テストリスト構造

- {Service 名}
  - {サービスの振る舞いの説明}
    - {具体的なテストケース（正常系）}
      - {さらに具体的な入力と期待結果の例}
    - {具体的なテストケース（異常系）}
      - {さらに具体的な入力と期待結果の例}

### 記入例: PhotoValidationService

```markdown
- PhotoValidationService
  - ファイルサイズの検証が正常に動作する
    - [ ] ファイルサイズが制限以下の場合、検証が成功する
      - [ ] 1MB のファイルで検証が成功する
      - [ ] 10MB のファイルで検証が成功する
    - [ ] ファイルサイズが制限を超える場合、FileSizeLimitExceededException が発生する
      - [ ] 10MB+1 バイトのファイルで例外が発生する
      - [ ] 100MB のファイルで例外が発生する
  - MIME タイプの検証が正常に動作する
    - [ ] 許可された MIME タイプの場合、検証が成功する
      - [ ] "image/jpeg" で検証が成功する
      - [ ] "image/png" で検証が成功する
    - [ ] 許可されていない MIME タイプの場合、UnsupportedFileTypeException が発生する
      - [ ] "image/gif" で例外が発生する
      - [ ] "text/plain" で例外が発生する
      - [ ] 空文字列で例外が発生する
  - 重複ファイル名のチェックが正常に動作する
    - [ ] ファイル名が重複していない場合、検証が成功する
      - [ ] 新規ファイル名 "newfile.jpg" で検証が成功する
    - [ ] ファイル名が重複している場合、DuplicateFileNameException が発生する
      - [ ] 既存ファイル名 "existing.jpg" で例外が発生する
```

### JUnit テストコードへの対応

```java
class PhotoValidationServiceTest {
    @Nested
    class ファイルサイズの検証が正常に動作する {
        @Nested
        class ファイルサイズが制限以下の場合_検証が成功する {
            @Test
            void 1MBのファイルで検証が成功する() {
                // テスト実装
            }
        }
    }
}
```

**注意**: DomainService テストは Repository をモック化した単体テストで実行します

---

## 実装順序の推奨

### Phase 6: ドメイン層の実装フロー

1. **Entity/ValueObject のバリデーションロジック**

   - テスト: 正常系の最もシンプルなケース → 異常系
   - 実装: Red → Green → Refactor
   - 注意: 純粋な Java オブジェクトとしてテスト（JPA 不要）

2. **Repository Interface の定義**

   - インターフェースのみ定義（テストなし）
   - 配置: `domain/repository/` パッケージ
   - ドメイン層が外部技術（JPA）に依存しないように

3. **Repository 実装**

   - テスト: `@DataJpaTest` で統合テスト
   - 実装: Red → Green → Refactor
   - 配置: `infrastructure/repository/` パッケージ
   - 注意: H2 インメモリ DB で実行

4. **DomainService** (必要な場合)
   - テスト: 単体テスト（Repository モック）
   - 実装: Red → Green → Refactor
   - 配置: `domain/service/` パッケージ

---

## 進捗管理

実装を進める際:

1. **最も簡単なテストから始める** (通常は正常系の最もシンプルなケース)
2. **完了したら項目を`[x]` に、未完了は `[ ]` のままにする**
3. **実装中に気づいた新しいケースを追加する**
4. **リファクタリングが必要になったら別項目として追加する**

**進捗表示の例**:

```markdown
- Photo Entity
  - 有効なプロパティを受け取ると Photo エンティティが生成される
    - [x] ファイル名、ファイルサイズ、MIME タイプを受け取ると正常に生成される
      - [x] "sample.jpg", 1024000, "image/jpeg" を受け取ると Photo が生成される
      - [x] "photo.png", 512000, "image/png" を受け取ると Photo が生成される
    - [ ] 生成されたエンティティは正しいプロパティ値を持つ ← 現在実装中
      - [ ] getFileName()は設定したファイル名を返す
      - [ ] getFileSize()は設定したファイルサイズを返す
  - バリデーションエラーでエンティティ生成が失敗する
    - [ ] ファイル名が空の場合、IllegalArgumentException が発生する
```

---

## References

- Domain Model: `docs/spec/models/{feature_name}.md`
- Implementation Plan: `docs/plans/[Spec名]_[シナリオ識別子].md`
- Domain TDD Prompt: `docs/ai/prompts/tasks/05_implement_backend_domain.md`
