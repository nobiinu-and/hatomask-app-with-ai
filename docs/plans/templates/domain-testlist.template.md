# ドメイン層テストリスト: {ステップ名}

**Feature**: {対応するGherkin機能の参照}  
**Domain Model**: `docs/spec/models/{feature_name}.md`  
**Plan**: `docs/plans/{feature_id}/`

---

## テストリストの目的

Phase 6で実装するドメイン層（Entity, Repository, DomainService）のテストケースを列挙します。

---

## 1. Entity/ValueObject

### テストリスト構造

* {Entity名}
  * {機能や振る舞いの説明}
    * {具体的なテストケース（正常系）}
      * {さらに具体的な入力と期待結果の例}
    * {具体的なテストケース（異常系・バリデーション）}
      * {さらに具体的な入力と期待結果の例}

### 記入例: Photo Entity

```markdown
* Photo Entity
  * 有効なプロパティを受け取るとPhotoエンティティが生成される
    * [ ] ファイル名、ファイルサイズ、MIMEタイプを受け取ると正常に生成される
      * [ ] sample.jpgを受け取るとPhotoが生成される
      * [ ] photo.pngを受け取るとPhotoが生成される
    * [ ] 生成されたエンティティは正しいプロパティ値を持つ
      * [ ] getFileName()は設定したファイル名を返す
      * [ ] getFileSize()は設定したファイルサイズを返す
      * [ ] getMimeType()は設定したMIMEタイプを返す
  * バリデーションエラーでエンティティ生成が失敗する
    * [ ] ファイル名が空の場合、IllegalArgumentExceptionが発生する
      * [ ] 空文字列を受け取ると例外が発生する
      * [ ] nullを受け取ると例外が発生する
    * [ ] ファイルサイズが不正な場合、IllegalArgumentExceptionが発生する
      * [ ] 0バイトを受け取ると例外が発生する
      * [ ] 負の値を受け取ると例外が発生する
      * [ ] 10MBを超える値を受け取ると例外が発生する
    * [ ] MIMEタイプが許可されていない場合、IllegalArgumentExceptionが発生する
      * [ ] "image/gif" を受け取ると例外が発生する
      * [ ] "text/plain" を受け取ると例外が発生する
      * [ ] 空文字列を受け取ると例外が発生する
```

### JUnitテストコードへの対応

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
- 第1階層: テストクラス名（例: `Photo Entity` → `PhotoEntityTest.java`）
- 第2階層: `@Nested`クラス名（機能や振る舞いの説明）
- 第3階層: `@Nested`クラスまたは`@Test`メソッド名（具体的なテストケース）
- 第4階層: `@Test`メソッド名（さらに具体的な入力と期待結果）
- **重要**: 第2階層以降の文言は、そのままクラス名やメソッド名として使えるよう日本語で記述する

---

## 2. Repository Interface & 実装

### テストリスト構造

* {Repository名}
  * {基本操作の説明}
    * {具体的なテストケース}
      * {さらに具体的な入力と期待結果の例}
  * {クエリメソッドの説明}
    * {具体的なテストケース}
      * {さらに具体的な入力と期待結果の例}

### 記入例: PhotoRepository

```markdown
* PhotoRepository
  * エンティティの保存操作が正常に動作する
    * [ ] Photoエンティティを保存すると、IDとcreatedAtが自動生成される
      * [ ] 新しいPhotoを保存するとIDが採番される
      * [ ] 新しいPhotoを保存するとcreatedAtに現在時刻が設定される
    * [ ] 保存されたエンティティはDBから取得できる
      * [ ] 保存後、同じIDで検索すると同じ内容のPhotoが返る
  * IDによる検索が正常に動作する
    * [ ] 存在するIDで検索すると、保存済みのPhotoが取得できる
      * [ ] ID=1で保存したPhotoをID=1で検索すると取得できる
    * [ ] 存在しないIDで検索すると、空のOptionalが返る
      * [ ] 存在しないID=999で検索するとOptional.empty()が返る
  * ファイル名による存在確認が正常に動作する
    * [ ] 存在するファイル名で確認するとtrueが返る
      * [ ] "sample.jpg"で保存後、"sample.jpg"で存在確認するとtrueが返る
    * [ ] 存在しないファイル名で確認するとfalseが返る
      * [ ] "notfound.jpg"で存在確認するとfalseが返る
  * 複数件の検索が正常に動作する
    * [ ] ユーザーIDで検索すると、該当するPhoto一覧が返る
      * [ ] userID=1のPhoto2件を保存後、userID=1で検索すると2件返る
      * [ ] userID=1で検索時、userID=2のPhotoは含まれない
```

### JUnitテストコードへの対応

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

**注意**: Repository実装テストは `@DataJpaTest` を使用した統合テストで実行します

---

## 3. DomainService (必要な場合)

### テストリスト構造

* {Service名}
  * {サービスの振る舞いの説明}
    * {具体的なテストケース（正常系）}
      * {さらに具体的な入力と期待結果の例}
    * {具体的なテストケース（異常系）}
      * {さらに具体的な入力と期待結果の例}

### 記入例: PhotoValidationService

```markdown
* PhotoValidationService
  * ファイルサイズの検証が正常に動作する
    * [ ] ファイルサイズが制限以下の場合、検証が成功する
      * [ ] 1MBのファイルで検証が成功する
      * [ ] 10MBのファイルで検証が成功する
    * [ ] ファイルサイズが制限を超える場合、FileSizeLimitExceededExceptionが発生する
      * [ ] 10MB+1バイトのファイルで例外が発生する
      * [ ] 100MBのファイルで例外が発生する
  * MIMEタイプの検証が正常に動作する
    * [ ] 許可されたMIMEタイプの場合、検証が成功する
      * [ ] "image/jpeg" で検証が成功する
      * [ ] "image/png" で検証が成功する
    * [ ] 許可されていないMIMEタイプの場合、UnsupportedFileTypeExceptionが発生する
      * [ ] "image/gif" で例外が発生する
      * [ ] "text/plain" で例外が発生する
      * [ ] 空文字列で例外が発生する
  * 重複ファイル名のチェックが正常に動作する
    * [ ] ファイル名が重複していない場合、検証が成功する
      * [ ] 新規ファイル名 "newfile.jpg" で検証が成功する
    * [ ] ファイル名が重複している場合、DuplicateFileNameExceptionが発生する
      * [ ] 既存ファイル名 "existing.jpg" で例外が発生する
```

### JUnitテストコードへの対応

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

**注意**: DomainServiceテストはRepositoryをモック化した単体テストで実行します

---

## 実装順序の推奨

### Phase 6: ドメイン層の実装フロー

1. **Entity/ValueObject のバリデーションロジック**
   - テスト: 正常系の最もシンプルなケース → 異常系
   - 実装: Red → Green → Refactor
   - 注意: 純粋なJavaオブジェクトとしてテスト（JPA不要）

2. **Repository Interface の定義**
   - インターフェースのみ定義（テストなし）
   - 配置: `domain/repository/` パッケージ
   - ドメイン層が外部技術（JPA）に依存しないように

3. **Repository 実装**
   - テスト: `@DataJpaTest` で統合テスト
   - 実装: Red → Green → Refactor
   - 配置: `infrastructure/repository/` パッケージ
   - 注意: H2インメモリDBで実行

4. **DomainService** (必要な場合)
   - テスト: 単体テスト（Repositoryモック）
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
* Photo Entity
  * 有効なプロパティを受け取るとPhotoエンティティが生成される
    * [x] ファイル名、ファイルサイズ、MIMEタイプを受け取ると正常に生成される
      * [x] "sample.jpg", 1024000, "image/jpeg" を受け取るとPhotoが生成される
      * [x] "photo.png", 512000, "image/png" を受け取るとPhotoが生成される
    * [ ] 生成されたエンティティは正しいプロパティ値を持つ ← 現在実装中
      * [ ] getFileName()は設定したファイル名を返す
      * [ ] getFileSize()は設定したファイルサイズを返す
  * バリデーションエラーでエンティティ生成が失敗する
    * [ ] ファイル名が空の場合、IllegalArgumentExceptionが発生する
```

---

## References
- Domain Model: `docs/spec/models/{feature_name}.md`
- Implementation Plan: `docs/plans/{feature_name}/`
- Domain TDD Prompt: `docs/ai/prompts/tasks/05_implement_backend_domain.md`
