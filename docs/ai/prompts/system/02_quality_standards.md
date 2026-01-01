# コード生成時の品質チェック

このドキュメントは、AIアシスタントがコードを生成する際の品質チェック項目を定義します。

**プロジェクトの品質基準は`docs/dev/standards/quality.md`に定義されています。必ずそちらを参照してください。**

このドキュメントには、AIアシスタント特有のコード生成時のチェック項目のみを記載します。

---

## コード生成の基本ルール

### 1. 完全なコードを生成する

省略記号や「既存のコード」コメントは使用しない。

**❌ 悪い例:**
```typescript
export const PhotoUpload: React.FC = () => {
  // ...既存のコード...
  
  const handleUpload = async (file: File) => {
    // アップロード処理
  };
  
  // ...残りのコード...
};
```

**✅ 良い例:**
```typescript
import React, { useState } from 'react';
import { Button } from '@mui/material';

export const PhotoUpload: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  
  const handleUpload = async (file: File) => {
    setUploading(true);
    try {
      await photoService.upload(file);
    } finally {
      setUploading(false);
    }
  };
  
  return (
    <div>
      <input type="file" onChange={(e) => setFile(e.target.files?.[0] || null)} />
      <Button onClick={() => file && handleUpload(file)} disabled={uploading}>
        アップロード
      </Button>
    </div>
  );
};
```

### 2. インポート文を必ず含める

生成したコードに必要なインポート文をすべて記述する。

**✅ 必須:**
```typescript
// React関連
import React, { useState, useEffect } from 'react';

// Material-UI
import { Button, Card, CardMedia } from '@mui/material';

// カスタムフック・サービス
import { usePhotoManagement } from '../hooks/usePhotoManagement';
import { photoService } from '../services/photoService';

// 型定義
import type { Photo } from '../types/photo';
```

### 3. 型定義を明確にする

TypeScriptでは`any`を使わず、明確な型を定義する。

**❌ 避ける:**
```typescript
const handleUpload = (file: any) => {
  // ...
};
```

**✅ 推奨:**
```typescript
const handleUpload = (file: File): Promise<void> => {
  // ...
};
```

---

## テスト生成時のチェック項目

**テストの原則は`docs/dev/standards/quality.md`を参照してください。**

### ユニットテスト生成時

#### 1. AAAパターンを明示する

コメントでArrange/Act/Assertを明確に分ける。

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

#### 2. DisplayNameで日本語の説明を記述

テストの意図が明確になるよう、日本語で説明を記述する。

```java
@Test
@DisplayName("写真アップロードが成功する")
void execute_success() {
    // テスト内容
}

@Test
@DisplayName("ファイルサイズが10MBを超える場合はFileSizeExceededExceptionをスローする")
void execute_fileSizeExceeds_throwsException() {
    // テスト内容
}
```

### E2Eテスト生成時

#### 1. アクセシビリティを考慮したセレクタを使用

**優先順位:**
1. `getByRole()` - 最優先
2. `getByLabelText()` - フォーム要素
3. `getByText()` - テキストコンテンツ
4. `getByTestId()` - 最終手段

**❌ 避ける:**
```typescript
// CSSクラス、IDに依存
await page.locator('#upload-button').click();
await page.locator('.file-input').setInputFiles('test.jpg');
```

**✅ 推奨:**
```typescript
// アクセシビリティを考慮
await page.getByRole('button', { name: '写真を選択' }).click();
await page.getByLabelText('写真ファイル').setInputFiles('test.jpg');
```

#### 2. 固定時間の待機を避ける

**❌ 避ける:**
```typescript
await page.waitForTimeout(3000);
```

**✅ 推奨:**
```typescript
await expect(page.getByText('アップロード完了')).toBeVisible();
```

---

## エラーハンドリング生成時のチェック項目

**エラーハンドリングの基準は`docs/dev/standards/quality.md`を参照してください。**

### すべてのAPI呼び出しにtry-catchを追加

API呼び出しを生成する際は、必ずエラーハンドリングを含める。

**フロントエンド:**
```typescript
const uploadPhoto = async (file: File): Promise<PhotoResponse> => {
  try {
    const response = await photoService.upload(file);
    return response;
  } catch (error) {
    // ユーザーフレンドリーなエラーメッセージ
    if (error instanceof FileSizeExceededException) {
      throw new Error('ファイルサイズは10MB以下にしてください');
    }
    throw new Error('アップロードに失敗しました。もう一度お試しください');
  }
};
```

**バックエンド:**
```java
@Service
@Slf4j
public class PhotoService {
    public Photo uploadPhoto(MultipartFile file) {
        log.info("Photo upload started: fileName={}", file.getOriginalFilename());
        
        try {
            // 処理
            return photo;
        } catch (FileSizeExceededException e) {
            log.warn("File size exceeded: {}", file.getOriginalFilename());
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload photo", e);
            throw new RuntimeException("Failed to upload photo", e);
        }
    }
}
```

### ログ出力の注意事項

#### 必ずログを追加する

重要な処理には、必ずログを追加する。

```java
log.info("Photo upload started: fileName={}", file.getOriginalFilename());
log.debug("Photo saved: id={}", photo.getId());
log.warn("File size exceeded: fileName={}, size={}", fileName, fileSize);
log.error("Failed to upload photo: fileName={}", fileName, e);
```

#### 機密情報を含めない

**❌ 避ける:**
```java
log.info("User login: email={}, password={}", email, password);
log.debug("API token: {}", apiToken);
```

**✅ 推奨:**
```java
log.info("User login: email={}", email);
log.debug("API request authenticated");
```

---

## セキュリティチェック項目

**セキュリティ基準は`docs/dev/standards/quality.md`を参照してください。**

### 入力バリデーションを必ず実装

ユーザー入力を受け取るコードを生成する際は、必ずバリデーションを含める。

**チェックリスト:**
- [ ] フロントエンドでバリデーションを実装している
- [ ] バックエンドでも同じバリデーションを実装している(二重チェック)
- [ ] エラーメッセージがユーザーフレンドリー

### 機密情報を含めない

**絶対に避けること:**
```typescript
// ❌ パスワード、APIキー、トークンをコードに含めない
const API_KEY = 'sk_live_1234567890';
const DB_PASSWORD = 'password123';
```

**必ず環境変数を使用:**
```typescript
// ✅ 環境変数から取得
const API_KEY = import.meta.env.VITE_API_KEY;
```

```java
// ✅ application.ymlから取得
@Value("${spring.datasource.password}")
private String dbPassword;
```

### SQLインジェクション対策

**必ずSpring Data JPAまたはPreparedStatementを使用:**

**❌ 絶対に生成しない:**
```java
String sql = "SELECT * FROM photos WHERE user_id = '" + userId + "'";
```

**✅ 必ず生成:**
```java
// Spring Data JPA
List<Photo> findByUserId(UUID userId);

// またはPreparedStatement
PreparedStatement stmt = connection.prepareStatement(
    "SELECT * FROM photos WHERE user_id = ?"
);
stmt.setString(1, userId);
```

---

## アクセシビリティチェック項目

**アクセシビリティ基準は`docs/dev/standards/quality.md`を参照してください。**

### UIコンポーネント生成時

#### 1. セマンティックHTMLを使用

**❌ 避ける:**
```typescript
<div class="nav">
  <div class="nav-item">ホーム</div>
</div>
```

**✅ 推奨:**
```typescript
<nav aria-label="メインナビゲーション">
  <ul>
    <li><a href="/">ホーム</a></li>
  </ul>
</nav>
```

#### 2. ARIA属性を追加

**必須のARIA属性:**
- 画像: `alt`属性
- ボタン: `aria-label`(アイコンのみの場合)
- フォーム入力: `aria-label`または`aria-describedby`
- 動的コンテンツ: `aria-live`, `role="status"`

**例:**
```typescript
<Button aria-label={`${photo.fileName}を削除`}>
  <DeleteIcon />
</Button>

<input
  type="file"
  aria-label="写真ファイルを選択"
  aria-describedby="file-help"
/>
<span id="file-help">最大10MB</span>
```

---

## コード生成後のセルフチェック

コードを生成した後、以下の項目を必ず確認してください。

### 基本チェック

- [ ] **完全なコード**: 省略記号や「既存のコード」コメントを使っていない
- [ ] **インポート文**: 必要なインポートがすべて含まれている
- [ ] **型定義**: `any`を使わず、明確な型を定義している
- [ ] **エラーハンドリング**: すべてのAPI呼び出しにtry-catchがある
- [ ] **ログ出力**: 重要な処理にログを追加している
- [ ] **機密情報**: パスワード、APIキー等を含めていない

### テストコード生成時

- [ ] **AAAパターン**: Arrange/Act/Assertが明確に分かれている
- [ ] **DisplayName**: 日本語で意図を説明している(Java)
- [ ] **セレクタ**: アクセシビリティを考慮している(E2E)
- [ ] **待機**: 固定時間待機を使っていない(E2E)

### UIコンポーネント生成時

- [ ] **セマンティックHTML**: 適切なHTML要素を使用している
- [ ] **ARIA属性**: 必要なARIA属性を追加している
- [ ] **エラーメッセージ**: ユーザーフレンドリーな文言

### バックエンドコード生成時

- [ ] **入力バリデーション**: ユーザー入力を検証している
- [ ] **SQLインジェクション対策**: JPAまたはPreparedStatementを使用
- [ ] **N+1問題**: 関連エンティティの取得にJOIN FETCHを使用
- [ ] **エラーレスポンス**: RFC 9457形式(ProblemDetail)

---

## 不明点がある場合

品質基準について不明な点がある場合は、以下を確認してください:

1. **`docs/dev/standards/quality.md`** - プロジェクトの品質基準
2. **`docs/dev/standards/coding.md`** - コーディング規約
3. **既存のコード** - 類似機能の実装例を参照

それでも不明な場合は、ユーザーに質問してください。
