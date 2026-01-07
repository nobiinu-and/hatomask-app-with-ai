---
description: OpenAPI仕様からSpring Boot Controllerのスタブを生成するプロンプト
---

# Backend Stub 生成 (Task05)

## 目的

**OpenAPI 仕様に基づき、バックエンド（Spring Boot Controller）のスタブを生成する。**

Task06 の縦切り実装に入る前に、フロントエンドが直接接続できる API 基盤を整えます。

## 依頼内容

以下の OpenAPI 仕様を基に、Spring Boot Controller スタブを作成してください。

## 入力情報

- **実装計画**: `docs/plans/[機能仕様ファイル名]_[シナリオ識別子].md`
  - 実装計画の「このシナリオで採用する仕様（選択結果）」に記載された **ドメインモデル（Primary/Related）** と **OpenAPI（Primary/Related）** を参照し、それに基づいてスタブを作成する
  - Task05 では、実装計画で採用された OpenAPI / モデル以外を勝手に追加しない（必要なら Task04 に戻って計画を更新する）

## 作業手順

### 0. 参照元の確定（必須）

1. 実装計画 `docs/plans/[機能仕様ファイル名]_[シナリオ識別子].md` を開き、以下を転記（または明確に参照）する:

- 採用するドメインモデル: Primary / Related
- 採用する OpenAPI: Primary / Related

2. スタブ生成の対象は、原則として **実装計画の「エンドポイント一覧」** に記載されたもの（= このシナリオで使用するもの）に限定する

### 1. Backend Controller Stub 作成

#### 1.1 ファイル配置

**パッケージ**: `com.hatomask.presentation.controller`

**ファイル名**: `{Resource}Controller.java`

例: `PhotoController.java`

#### 1.2 Controller 実装

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
    // Task06でドメイン層・UseCase実装後に置き換える
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

#### 1.3 DTO 作成

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

**TODO コメント必須**:

```java
// TODO: Replace stub implementation
// Task06でドメイン層・UseCase実装後に置き換える
```

**OpenAPI 準拠**:

- エンドポイントパス、HTTP メソッドを正確に
- リクエストパラメータ/ボディと完全一致
- レスポンススキーマと完全一致
- ステータスコード、ヘッダーも含める

**固定データを返す**:

```java
// ✅ 良い例: 固定データ
UUID photoId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
return ResponseEntity.ok(new PhotoResponse(photoId, "sample.jpg", 5242880L, "image/jpeg", "2024-01-15T10:30:00Z"));

// ❌ 悪い例: 実装を先走る
Photo photo = photoRepository.save(new Photo(...));  // Task06で実装する内容
```

**例外ハンドリングは後回し**:

```java
// Task06で@ControllerAdviceによるグローバルハンドリング実装
// スタブ段階では省略可
```

### 2. CORS 設定

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

### 3. Stub 動作確認

#### 3.1 Backend Stub 起動

```bash
cd src/backend
mvn spring-boot:run
```

#### 3.2 curl で確認

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

- [ ] ステータスコードが OpenAPI 通り（201, 200 等）
- [ ] レスポンスボディが OpenAPI スキーマ通り
- [ ] ヘッダー（Location, Content-Type 等）が正確
- [ ] CORS ヘッダーが設定されている（Access-Control-Allow-Origin）

### 4. フロントエンドからの接続確認

#### 4.1 フロントエンド設定

**vite.config.ts** でバックエンドへのプロキシ設定（必要に応じて）:

```typescript
export default defineConfig({
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
```

または、フロントエンドの API クライアントで直接指定:

```typescript
const API_BASE_URL = "http://localhost:8080";

export const uploadPhoto = async (file: File) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_BASE_URL}/api/v1/photos`, {
    method: "POST",
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
3. DevTools の Network タブでレスポンス確認

### 5. 成果物確認

作成した Stub について：

- [ ] Backend Controller が全エンドポイント分ある
- [ ] DTO が定義されている
- [ ] TODO コメントが記載されている
- [ ] OpenAPI 仕様と完全一致している
- [ ] CORS 設定が正しい
- [ ] curl で動作確認済み
- [ ] フロントエンドから接続確認済み

## 出力形式

### 1. Backend Controller

**ファイルパス**: `src/backend/src/main/java/com/hatomask/presentation/controller/{Resource}Controller.java`

### 2. DTO

**ファイルパス**: `src/backend/src/main/java/com/hatomask/presentation/dto/{Resource}Response.java`

### 3. CORS 設定

**ファイルパス**: `src/backend/src/main/java/com/hatomask/config/WebConfig.java`

### 4. Task05 完了報告

```markdown
## Task05 完了

### 作成ファイル

**バックエンド**:

- `src/backend/.../PhotoController.java` (Stub 実装、TODO 付き)
- `src/backend/.../PhotoResponse.java` (DTO)
- `src/backend/.../WebConfig.java` (CORS 設定)

### Stub 動作確認

- ✅ Backend Stub 動作確認済み (curl)
- ✅ CORS 設定確認済み
- ✅ フロントエンドから接続確認済み

### 次のアクション

- Task06: 縦切り実装サイクル開始
- 実装粒度を AI と相談
```

## 参考資料

- **実装計画**: `docs/plans/[機能仕様ファイル名]_[シナリオ識別子].md`
  - 「このシナリオで採用する仕様（選択結果）」に記載の OpenAPI（Primary/Related）を参照
- **Spring Boot**: https://spring.io/projects/spring-boot
- **CORS 設定**: https://spring.io/guides/gs/rest-service-cors/

## 注意事項

### OpenAPI 準拠の重要性

- **Task03 で確定した API 契約に忠実**に実装
- スキーマ、ステータスコード、ヘッダーすべて一致させる
- Task06 で本実装に置き換える際も OpenAPI が基準

### TODO コメントの意義

- スタブ実装であることを明示
- Task06 でどこを置き換えるか明確化
- レビュー時の確認ポイント

### エラーハンドリング

- スタブ段階では基本的な 400/404 のみ実装
- 詳細なエラーハンドリングは Task06 で実装
- RFC 9457 形式は守る

### CORS 設定の重要性

- ローカル開発時、フロントエンド（localhost:5173）からバックエンド（localhost:8080）へのアクセスには CORS 設定が必須
- 本番環境では適切に制限すること

## トラブルシューティング

### Backend Stub が 404

**症状**: curl でアクセスすると 404

**確認事項**:

- `@RestController` アノテーションがあるか
- `@RequestMapping("/api/v1/photos")` パスが正確か
- Spring Boot が起動しているか

### CORS エラー

**症状**: フロントエンドから接続すると `CORS policy: No 'Access-Control-Allow-Origin'`

**確認事項**:

- WebConfig.java の allowedOrigins に localhost:5173 が含まれているか
- Spring Boot が再起動されているか
- ブラウザのキャッシュをクリア

### レスポンス形式不一致

**症状**: OpenAPI と異なるレスポンス

**対策**:

- OpenAPI 仕様を再確認
- Controller のコードを修正
- curl でレスポンスを確認し、OpenAPI と diff 比較
