# 実装時の注意事項

このドキュメントは、AI アシスタントが`docs/dev/DEVELOPMENT.md`に定義された開発プロセスを実行する際の注意事項を定義します。

## 基本方針

**開発プロセスは`docs/dev/DEVELOPMENT.md`に従ってください。**

このドキュメントには、AI アシスタント特有の実行時の注意点のみを記載します。

## タスク開始時の確認事項

### 1. 必ず参照すべきドキュメント

実装を開始する前に、必ず以下のドキュメントを確認してください:

```
1. docs/dev/DEVELOPMENT.md
   → BDD/TDDの開発プロセス全体を理解する

2. docs/spec/models/{feature_name}.md
   → ドメインモデルを確認する（Phase 2で作成）

3. docs/spec/features/{feature_name}.md
   → 実装する機能の仕様と受け入れ基準を確認する

4. docs/dev/CODING_STANDARDS.md
   → コーディング規約を確認する

5. docs/plans/[Spec名]_[シナリオ識別子].md (実装計画)
   → 実装計画を確認する
```

### 2. 実装前のコード調査

新機能を実装する前に、必ず以下を調査してください:

- **類似機能の実装**: 同じようなパターンがないか探す
- **既存のユーティリティ**: 再利用できるコードがないか確認
- **関連するコンポーネント/クラス**: 影響を受ける箇所を特定
- **テストの構造**: 既存のテストパターンを理解する

**調査方法:**

```
- semantic_search を使って関連コードを探す
- grep_search を使って特定のパターンを探す
- list_code_usages を使って使用箇所を確認する
```

## AI 特有の実行ルール

### テスト実行の徹底

**必ずテストを実行してから次に進んでください:**

```bash
# フロントエンド実装時
cd src/frontend
npm test                    # 各変更後に実行
npm test -- --coverage     # 実装完了後に実行

# バックエンド実装時
cd src/backend
mvn test                    # 各変更後に実行
mvn test -Dtest=ClassName#methodName  # 特定のテストのみ実行

# E2Eテスト
cd e2e
npm test -- --name "シナリオ名"  # 各ステップ完了後に実行
```

**重要:** テスト結果を確認せずに次のステップに進まないでください。

### コマンド実行時の注意

1. **作業ディレクトリを意識する**

   ```bash
   # 絶対パスを使用する
   cd /workspaces/hatomask-app-with-ai/src/frontend

   # または現在のディレクトリを確認してから実行
   pwd
   cd src/frontend
   ```

2. **エラーが発生したら必ず確認する**

   ```bash
   # エラーメッセージを読む
   # ログを確認する
   # 原因を特定してから修正する
   ```

3. **長時間実行されるコマンド**

   ```bash
   # サーバー起動などはバックグラウンドで実行
   mvn spring-boot:run &

   # または別のターミナルで実行
   ```

### コード生成時の注意

1. **完全なコードを生成する**

   - 省略記号（`...`, `// 既存のコード`, `/* ... */`）は使用しない
   - すべてのコードを明示的に記述する

2. **インポート文を忘れない**

   ```typescript
   // ✅ Good - インポート文を含む
   import React, { useState } from "react";
   import { Button } from "@mui/material";

   export const PhotoUpload: React.FC = () => {
     // コンポーネント実装
   };
   ```

3. **型定義を明確にする**

   ```typescript
   // ✅ Good - 明確な型定義
   interface PhotoUploadProps {
     onUpload: (file: File) => Promise<void>;
     maxSize?: number;
   }

   // ❌ Bad - 型が曖昧
   const PhotoUpload = (props) => {
     // ...
   };
   ```

### エラーハンドリングの実装

**すべての API 呼び出しにエラーハンドリングを追加してください:**

```typescript
// フロントエンド
const uploadPhoto = async (file: File): Promise<PhotoResponse> => {
  try {
    const response = await photoService.upload(file);
    return response;
  } catch (error) {
    // ユーザーフレンドリーなエラーメッセージ
    if (error instanceof FileSizeExceededException) {
      throw new Error("ファイルサイズは10MB以下にしてください");
    }
    // 汎用的なエラー
    throw new Error("アップロードに失敗しました。もう一度お試しください");
  }
};
```

```java
// バックエンド
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

## 実装中の進捗報告

### 長時間かかる作業の場合

複数のステップを実装する場合は、中間報告を行ってください:

```
✅ ステップ1完了: ステップ定義の作成
   - E2Eテスト: Red状態確認済み

🔄 ステップ2実行中: モックAPIの作成
   - handlers.tsにPOST /api/v1/photosを追加中

⏸️  ステップ3-7: 未実装
```

### 問題が発生した場合

問題が発生したら、以下を報告してください:

1. **何をしようとしたか**
2. **何が起きたか**(エラーメッセージ)
3. **原因の推測**
4. **試した解決策**(あれば)
5. **次に試すこと**

## セルフチェックリスト

実装完了後、以下を確認してください:

### すべての実装で

- [ ] テストが書かれ、すべて通っている
- [ ] エラーハンドリングが実装されている
- [ ] ログ出力が適切(機密情報を含まない)
- [ ] コーディング規約に従っている

### フロントエンド実装で

- [ ] 型安全か(`any`を使っていない)
- [ ] アクセシビリティが考慮されている
- [ ] エラーメッセージがユーザーフレンドリー
- [ ] レスポンシブデザインに対応している

### バックエンド実装で

- [ ] 入力バリデーションが実装されている
- [ ] エラーレスポンスが RFC 9457 に準拠している
- [ ] N+1 問題が発生していない
- [ ] テストカバレッジが 80%以上

### E2E テストで

- [ ] ユーザー視点で書かれている
- [ ] 固定時間の待機を使っていない
- [ ] アクセシビリティを考慮したセレクタを使用している

## よくある問題と対処法

### テストが通らない

1. **エラーメッセージを注意深く読む**
2. **テストコードと実装コードを比較する**
3. **デバッグログを追加して状態を確認する**
4. **類似のテストがどう実装されているか確認する**

### モック API とリアル API で動作が異なる

1. **レスポンス形式を比較する**

   ```typescript
   // モックのレスポンスをログ出力
   console.log("Mock response:", mockResponse);

   // リアルAPIのレスポンスをログ出力
   console.log("Real response:", realResponse);
   ```

2. **日付フォーマット、null 値の扱いなどを確認する**

3. **必要に応じてバックエンドを修正する**

### 既存のテストが失敗する

1. **何を変更したか確認する**
2. **変更の影響範囲を特定する**
3. **破壊的変更の場合は、ユーザーに確認する**
4. **テストを更新するか、実装を元に戻す**

## 重要な原則

### ドキュメントファースト

実装前に必ずドキュメントを確認してください:

1. `docs/dev/DEVELOPMENT.md` で全体のフローを理解
2. `docs/spec/features/` で仕様を確認
3. `docs/dev/CODING_STANDARDS.md` で規約を確認

### 段階的実装

大きな機能を小さな単位に分解して、1 つずつ確実に実装してください:

1. **1 シナリオずつ**: 複数のシナリオを同時に実装しない
2. **1 ステップずつ**: Given/When/Then を 1 つずつ実装
3. **Red-Green-Refactor**: 必ずこのサイクルを回す

### 常に Green を維持

変更ごとにテストを実行し、すべてのテストが通ることを確認してください:

- **作業開始時**: Green 状態であることを確認
- **作業中**: 各変更後にテストを実行
- **作業終了時**: すべてのテストが Green 状態であることを確認

## 参照

詳細な実装手順は、以下のドキュメントを参照してください:

- **開発プロセス**: `docs/dev/DEVELOPMENT.md`
- **コーディング規約**: `docs/dev/CODING_STANDARDS.md`
- **品質基準**: `docs/ai/prompts/system/02_quality_standards.md`
