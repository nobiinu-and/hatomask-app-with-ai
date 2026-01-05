# 実装計画: JPEG ファイルを選択するとプレビューが表示される

## 基本情報

- **Feature**: 写真アップロード（静止画）
- **Scenario**: JPEG ファイルを選択するとプレビューが表示される
- **Spec**: `docs/spec/features/01_photo_upload.md`
- **Domain Model**: `docs/spec/models/photo_upload.md`
- **OpenAPI**: `docs/spec/api/photo_upload.yaml`

## OpenAPI 仕様

参照: `docs/spec/api/photo_upload.yaml`

### エンドポイント一覧

| エンドポイント | メソッド | 概要                               | 使用ステップ                                                 |
| -------------- | -------- | ---------------------------------- | ------------------------------------------------------------ |
| /api/v1/photos | POST     | 写真アップロード（検証・デコード） | And: ユーザーがファイルサイズ 5MB の JPEG ファイルを選択する |

### 主要スキーマ

- **UploadPhotoResponse**: アップロード画像のメタ情報（`mimeType`, `sizeBytes`, `widthPx`, `heightPx`）
- **ProblemDetails**: エラーレスポンス（RFC 9457 準拠）

## ステップ別実装分類

| ステップ                                                      | 分類         | API          | 状態依存 | 備考                                                                      |
| ------------------------------------------------------------- | ------------ | ------------ | -------- | ------------------------------------------------------------------------- |
| 前提: ユーザーがブラウザを開いている                          | フロントのみ | -            | なし     | Playwright hooks で page 作成済み                                         |
| もし: トップページにアクセスする                              | フロントのみ | -            | なし     | 既存ステップを流用                                                        |
| かつ: ユーザーが「写真を選択」ボタンをクリックする            | フロントのみ | -            | UI 状態  | `<input type="file">` を起動する操作                                      |
| かつ: ユーザーがファイルサイズ 5MB の JPEG ファイルを選択する | **API 依存** | POST /photos | UI 状態  | 選択したファイルを `multipart/form-data` で送信し、成功時にメタ情報を保存 |
| ならば: プレビューエリアに選択した画像が表示される            | フロントのみ | -            | UI 状態  | 画像表示（アスペクト比維持）                                              |
| かつ: 「顔検出を実行」ボタンが有効になる                      | フロントのみ | -            | UI 状態  | 画像が選択済みのときのみ有効                                              |

## 推奨実装グルーピング

縦切り実装（Task06）ではパターン B（API グループ単位）を想定。

### グループ 1: 画面の骨格（フロントのみ）

- もし: トップページにアクセスする
- かつ: 「写真を選択」ボタンが表示される（※本シナリオ外だが前提として必要）
- かつ: プレビューエリア（画像なし時の案内文）が表示される
- かつ: 「顔検出を実行」ボタンが無効である

**実装内容**:

- フロント: UI 配置（Spec のレイアウト）、初期状態（未選択）

**API**: なし

**グルーピング理由**:

- 後続ステップ（ファイル選択/プレビュー）に必要な UI 土台を先に用意する

### グループ 2: ファイル選択 → アップロード → 状態更新（API: POST /photos）

- かつ: ユーザーが「写真を選択」ボタンをクリックする
- かつ: ユーザーがファイルサイズ 5MB の JPEG ファイルを選択する

**実装内容**:

- フロント: ファイル選択ハンドリング、バリデーションエラー表示（Alert）、成功時の状態更新
- バックエンド: `POST /api/v1/photos` の受信、形式/サイズ検証、デコード（幅/高さ抽出）、RFC 9457 エラー返却

**API**:

- `POST /api/v1/photos`
  - Request(JSONPath): `$.paths['/photos'].post.requestBody.content['multipart/form-data'].schema.properties.file`
  - Response 200(JSONPath): `$.paths['/photos'].post.responses['200'].content['application/json'].schema`
  - Response 400/422(JSONPath): `$.paths['/photos'].post.responses['400'].content['application/problem+json'].schema`

**グルーピング理由**:

- UI 状態（選択ファイル）と API 結果（メタ情報）が一体なので、同グループで完結させる

### グループ 3: プレビュー表示・ボタン活性（フロントのみ）

- ならば: プレビューエリアに選択した画像が表示される
- かつ: 「顔検出を実行」ボタンが有効になる

**実装内容**:

- フロント: `SelectedImage` 相当の状態が存在する場合にプレビュー表示、ボタン活性

**API**: なし

**グルーピング理由**:

- API 完了後の UI 反映に集中できる

## Gherkin ステップごとの実装要件

### 前提 ユーザーがブラウザを開いている

**Frontend**

- 変更なし（`e2e/support/hooks.ts` で `page` 作成済み）

**Backend API**

- なし

**Database**

- なし

**Validation & Logic**

- なし

---

### もし トップページにアクセスする

**Frontend**

- 既存ステップを流用（`page.goto('/')`）

**Backend API**

- なし（ただし画面ロード時に既存の疎通 API を叩く実装があればテストが待つ）

**Database**

- なし

**Validation & Logic**

- なし

---

### かつ ユーザーが「写真を選択」ボタンをクリックする

**Frontend**

- **Components**: トップページに「写真を選択」ボタンと `input[type=file]` を配置
- **State**: 未選択状態（SelectedImage なし）を初期化
- **UI/UX**: キーボード操作でも実行できる（ボタンにフォーカス →Enter/Space）

**Backend API**

- なし

**Database**

- なし

**Validation & Logic**

- なし

---

### かつ ユーザーがファイルサイズ 5MB の JPEG ファイルを選択する

**Frontend**

- **State**: 選択ファイルを保持し、アップロード成功時にメタ情報（幅/高さ/MIME/サイズ）を保存
- **UI/UX**: バリデーション失敗時は Alert 表示し、選択済み画像は更新しない

**Backend API**

- **Endpoint**: `POST /api/v1/photos`
- **Request**: `multipart/form-data` の `file`（OpenAPI 参照: `$.paths['/photos'].post.requestBody...`）
- **Response**:
  - 200: `UploadPhotoResponse`（OpenAPI 参照: `$.paths['/photos'].post.responses['200']...`）
  - 400: 形式不一致/サイズ超過（ProblemDetails）
  - 422: デコード失敗（ProblemDetails）

**Database**

- **Tables**: なし
- **Changes**: 永続化禁止（DB/ストレージ/キャッシュ等に保存しない）

**Validation & Logic**

- MIME: `image/jpeg` / `image/png`
- サイズ: 10MB 以下
- デコードできない場合は 422
- 非ログ: 画像データ/入力ファイル名をログに出さない

**E2E テストデータ方針**

- リポジトリに大きなバイナリを追加せずに済むよう、ステップ定義側で「有効な JPEG + パディング（COM セグメント等）」を生成して 5MB 相当のファイルを作る案を採用する

---

### ならば プレビューエリアに選択した画像が表示される

**Frontend**

- **Components**: プレビュー領域に画像を表示
- **State**: 選択済み状態（SelectedImage あり）で分岐
- **UI/UX**: 表示領域に収まるように調整し、アスペクト比は維持

**Backend API**

- なし

**Database**

- なし

**Validation & Logic**

- 画像の向き（EXIF 回転など）により、プレビューと後続処理の向きが一致すること

---

### かつ 「顔検出を実行」ボタンが有効になる

**Frontend**

- **Components**: 「顔検出を実行」ボタン
- **State**: 選択済み画像が存在する場合のみ enabled

**Backend API**

- なし

**Database**

- なし

**Validation & Logic**

- 未選択時は無効のまま

## データモデル (共通)

**参照**: `docs/spec/models/photo_upload.md`

ドメインモデルで定義された `SelectedImage`（概念）と、`ImageMetadata`（幅/高さ/MIME）に相当する状態をフロントに保持する。
バックエンドは永続化せず、リクエストスコープ内で検証・デコードのみ行う。

### 追加が DB Schema

```sql
-- 追加なし（永続化しない）
```

### 追加の DTO (フロントエンド用)

```typescript
export type UploadPhotoResponse = {
  mimeType: "image/jpeg" | "image/png";
  sizeBytes: number;
  widthPx: number;
  heightPx: number;
};
```

## 技術的課題・リスク

- [ ] 画像ファイルをログに出さない（例外ログ/アクセスログ含む）
- [ ] 画像デコードで EXIF 回転をどう扱うか（プレビューと後続処理の向き一致）
- [ ] E2E で「5MB 相当」の有効 JPEG をどう用意するか（バイナリ追加なし方針）
- [ ] `POST /api/v1/photos` が現状未実装のため、Task05 でスタブ生成 →Task06 で置換が必要

## 備考

- Spec 上、Milestone 0 では「サーバ永続化は必須にしない」が、受信自体は許容される（永続化禁止/短命/非ログを必須）
