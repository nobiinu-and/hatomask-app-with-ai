---
description: OpenAPI仕様からSpring Boot Controllerのスタブを生成するプロンプト
---

# Backend Stub生成 (Phase 5)

## 目的

**OpenAPI仕様に基づき、バックエンド（Spring Boot Controller）のスタブを生成する。**

Phase 6の縦切り実装に入る前に、フロントエンドが直接接続できるAPI基盤を整えます。

## 依頼内容

以下のOpenAPI仕様を基に、Spring Boot Controllerスタブを作成してください。

## 入力情報

- **OpenAPI仕様**: `docs/spec/api/{feature_name}.yaml`
- **実装計画**: `docs/plans/[Spec名]_[シナリオ識別子].md`
- **ドメインモデル**: `docs/spec/models/{feature_name}.md`

## 作業手順

### 1. Backend Controller Stub作成

#### 1.1 ファイル配置

**パッケージ**: `com.hatomask.presentation.controller`

**ファイル名**: `{Resource}Controller.java`

例: `PhotoController.java`

#### 1.2 Controller実装

```java
package com.hatomask.presentation.controller;

import com.hatomask.presentation.dto.PhotoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    // TODO: Replace stub implementation
    // Phase 6でドメイン層・UseCase実装後に置き換える
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @RequestParam("file") MultipartFile file) {
        
        // スタブ実装: 固定レスポンスを返す
        UUID photoId = UUID.randomUUID();
        PhotoResponse response = new PhotoResponse(
            photoId.toString(),
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType(),
            LocalDateTime.now().toString()
        );
        
        URI location = URI.create("/api/v1/photos/" + photoId);
        return ResponseEntity.created(location).body(response);
    }

    // TODO: Replace stub implementation
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String id) {
        
        // スタブ実装: 空のレスポンス
        // 実装時は実際の画像バイナリを返す
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(new byte[0]);
    }
}
```

#### 1.3 DTO作成

**パッケージ**: `com.hatomask.presentation.dto`

```java
package com.hatomask.presentation.dto;

public record PhotoResponse(
    String id,
    String fileName,
    Long fileSize,
    String mimeType,
    String createdAt
) {}
```

#### 1.4 スタブ実装のポイント

**TODOコメント必須**:
```java
// TODO: Replace stub implementation
// Phase 6でドメイン層・UseCase実装後に置き換える
```

**OpenAPI準拠**:
- エンドポイントパス、HTTPメソッドを正確に
- リクエストパラメータ/ボディと完全一致
- レスポンススキーマと完全一致
- ステータスコード、ヘッダーも含める

**固定データを返す**:
```java
// ✅ 良い例: 固定データ
UUID photoId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
return ResponseEntity.ok(new PhotoResponse(photoId, "sample.jpg", 5242880L, "image/jpeg", "2024-01-15T10:30:00Z"));

// ❌ 悪い例: 実装を先走る
Photo photo = photoRepository.save(new Photo(...));  // Phase 6で実装する内容
```

**例外ハンドリングは後回し**:
```java
// Phase 6で@ControllerAdviceによるグローバルハンドリング実装
// スタブ段階では省略可
```

### 2. CORS設定

**ファイル**: `src/backend/src/main/java/com/hatomask/config/WebConfig.java`

```java
package com.hatomask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")  // フロントエンド開発サーバー
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### 3. Stub動作確認

#### 3.1 Backend Stub起動

```bash
cd src/backend
mvn spring-boot:run
```

#### 3.2 curlで確認

```bash
# POST /api/v1/photos
curl -X POST http://localhost:8080/api/v1/photos \
  -F "file=@sample.jpg" \
  -v

# GET /api/v1/photos/{id}
curl http://localhost:8080/api/v1/photos/550e8400-e29b-41d4-a716-446655440000 \
  -v
```

#### 3.3 確認ポイント

- [ ] ステータスコードがOpenAPI通り（201, 200等）
- [ ] レスポンスボディがOpenAPIスキーマ通り
- [ ] ヘッダー（Location, Content-Type等）が正確
- [ ] CORSヘッダーが設定されている（Access-Control-Allow-Origin）

### 4. フロントエンドからの接続確認

#### 4.1 フロントエンド設定

**vite.config.ts** でバックエンドへのプロキシ設定（必要に応じて）:

```typescript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

または、フロントエンドのAPIクライアントで直接指定:

```typescript
const API_BASE_URL = 'http://localhost:8080';

export const uploadPhoto = async (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch(`${API_BASE_URL}/api/v1/photos`, {
    method: 'POST',
    body: formData,
  });
  
  return response.json();
};
```

#### 4.2 動作確認

```bash
# ターミナル1: バックエンド起動
cd src/backend
mvn spring-boot:run

# ターミナル2: フロントエンド起動
cd src/frontend
npm run dev
```

ブラウザで http://localhost:5173 を開き:
1. ファイル選択
2. アップロードボタンクリック
3. DevToolsのNetworkタブでレスポンス確認

### 5. 成果物確認

作成したStubについて：

- [ ] Backend Controllerが全エンドポイント分ある
- [ ] DTOが定義されている
- [ ] TODOコメントが記載されている
- [ ] OpenAPI仕様と完全一致している
- [ ] CORS設定が正しい
- [ ] curlで動作確認済み
- [ ] フロントエンドから接続確認済み

## 出力形式

### 1. Backend Controller

**ファイルパス**: `src/backend/src/main/java/com/hatomask/presentation/controller/{Resource}Controller.java`

### 2. DTO

**ファイルパス**: `src/backend/src/main/java/com/hatomask/presentation/dto/{Resource}Response.java`

### 3. CORS設定

**ファイルパス**: `src/backend/src/main/java/com/hatomask/config/WebConfig.java`

### 4. Phase 5完了報告

```markdown
## Phase 5完了

### 作成ファイル

**バックエンド**:
- `src/backend/.../PhotoController.java` (Stub実装、TODO付き)
- `src/backend/.../PhotoResponse.java` (DTO)
- `src/backend/.../WebConfig.java` (CORS設定)

### Stub動作確認

- ✅ Backend Stub動作確認済み (curl)
- ✅ CORS設定確認済み
- ✅ フロントエンドから接続確認済み

### 次のアクション
- Phase 6: 縦切り実装サイクル開始
- 実装粒度をAIと相談
```

## 参考資料

- **OpenAPI仕様**: `docs/spec/api/{feature_name}.yaml`
- **Spring Boot**: https://spring.io/projects/spring-boot
- **CORS設定**: https://spring.io/guides/gs/rest-service-cors/

## 注意事項

### OpenAPI準拠の重要性

- **Phase 3で確定したAPI契約に忠実**に実装
- スキーマ、ステータスコード、ヘッダーすべて一致させる
- Phase 6で本実装に置き換える際もOpenAPIが基準

### TODOコメントの意義

- スタブ実装であることを明示
- Phase 6でどこを置き換えるか明確化
- レビュー時の確認ポイント

### エラーハンドリング

- スタブ段階では基本的な400/404のみ実装
- 詳細なエラーハンドリングはPhase 6で実装
- RFC 9457形式は守る

### CORS設定の重要性

- ローカル開発時、フロントエンド（localhost:5173）からバックエンド（localhost:8080）へのアクセスにはCORS設定が必須
- 本番環境では適切に制限すること

## トラブルシューティング

### Backend Stubが404

**症状**: curlでアクセスすると404

**確認事項**:
- `@RestController` アノテーションがあるか
- `@RequestMapping("/api/v1/photos")` パスが正確か
- Spring Bootが起動しているか

### CORS エラー

**症状**: フロントエンドから接続すると `CORS policy: No 'Access-Control-Allow-Origin'`

**確認事項**:
- WebConfig.javaのallowedOriginsにlocalhost:5173が含まれているか
- Spring Bootが再起動されているか
- ブラウザのキャッシュをクリア

### レスポンス形式不一致

**症状**: OpenAPIと異なるレスポンス

**対策**:
- OpenAPI仕様を再確認
- Controllerのコードを修正
- curlでレスポンスを確認し、OpenAPIとdiff比較

