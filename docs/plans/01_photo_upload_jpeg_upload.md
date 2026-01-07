# 実装計画: JPEGファイルを選択するとプレビューが表示される

## 基本情報

- **Feature**: 写真アップロード（静止画）
- **Scenario**: JPEGファイルを選択するとプレビューが表示される
- **Milestone**: m0
- **Spec**: `docs/spec/features/01_photo_upload.md`
- **Architecture**: `docs/spec/architecture/m0.md`
- **E2E Feature**: `e2e/features/photo_upload_jpeg_upload.feature`

## 入力（候補一覧）

このシナリオに関係しうるドキュメントを列挙します（全てを使う必要はありません）。

### ドメインモデル（候補）

- `docs/spec/models/photo_upload.md`

### OpenAPI 仕様（候補）

- `docs/spec/api/photos.yaml`

## このシナリオで採用する仕様（選択結果）

### ドメインモデル

- **Primary**: `docs/spec/models/photo_upload.md`
- **Related（任意）**: なし

### OpenAPI

- **Primary**: `docs/spec/api/photos.yaml`
- **Related（任意）**: なし

## OpenAPI 仕様

参照（このシナリオで採用する OpenAPI）:

- Primary: `docs/spec/api/photos.yaml`

### エンドポイント一覧

| Spec(OpenAPI)                          | エンドポイント | メソッド | 概要               | 使用ステップ |
| -------------------------------------- | -------------- | -------- | ------------------ | ------------ |
| `docs/spec/api/photos.yaml` (Primary)  | /photos        | POST     | 写真をアップロード | Then: 写真アップロードが成功する |

参照リンク（OpenAPI 内の一意箇所）:

- JSON Pointer: `docs/spec/api/photos.yaml#/paths/~1photos/post`
- JSONPath: `$.paths["/photos"].post`

### 主要スキーマ

- **UploadPhotoRequest**: multipart/form-data で `file`（binary）を送信
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/UploadPhotoRequest`
  - JSONPath: `$.components.schemas.UploadPhotoRequest`
- **PhotoUploadResponse**: `photoId`, `mimeType`, `fileSizeBytes`, `dimensions`, `expiresAt`
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/PhotoUploadResponse`
  - JSONPath: `$.components.schemas.PhotoUploadResponse`
- **ProblemDetails**: RFC 9457 準拠のエラーレスポンス
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/ProblemDetails`
  - JSONPath: `$.components.schemas.ProblemDetails`

## ステップ別実装分類

縦切り実装サイクル（Task06）のため、各ステップを分類します。

| ステップ | 分類 | API | 状態依存 | 備考 |
| --- | --- | --- | --- | --- |
| 前提: ユーザーがブラウザを開いている | フロントのみ | - | なし | Playwright の起動（hooks） |
| もし: トップページにアクセスする | フロントのみ | - | なし | `page.goto('/')` |
| かつ: ユーザーが「写真を選択」ボタンをクリックする | フロントのみ | - | UI 状態 | file input を開く |
| かつ: ユーザーがファイルサイズ5MBのJPEGファイルを選択する | フロントのみ | - | UI 状態 | ローカル検証（JPEG/PNG、<=10MB）・デコード/正規化 |
| ならば: 写真アップロードが成功する | **API 依存** | POST /photos | UI 状態 | OpenAPI: `docs/spec/api/photos.yaml#/paths/~1photos/post` |
| かつ: プレビューエリアに選択した画像が表示される | フロントのみ | - | UI 状態 | SelectedPhoto を表示 |
| かつ: 「顔検出を実行」ボタンが有効になる | フロントのみ | - | UI 状態 | SelectedPhoto が存在すること |

### 凡例

- **フロントのみ**: API 呼び出しなし、フロントエンド実装のみ
- **API 依存**: バックエンド API を呼び出す（フロント+バックエンド両方実装必要）
- **状態依存**:
  - UI 状態: フロントエンドの React State 等に依存
  - DB 状態: 前ステップで DB に保存したデータに依存

## 推奨実装グルーピング

縦切り実装サイクル（Task06）で実装する際の推奨グループ分けです。
AI との相談時に調整可能です。

### グループ 1: ローカル選択〜プレビュー（フロントのみ）

- 前提: ユーザーがブラウザを開いている
- もし: トップページにアクセスする
- かつ: ユーザーが「写真を選択」ボタンをクリックする
- かつ: ユーザーがファイルサイズ5MBのJPEGファイルを選択する
- かつ: プレビューエリアに選択した画像が表示される
- かつ: 「顔検出を実行」ボタンが有効になる

**実装内容**:

- フロント: ファイル選択 UI、バリデーション（形式/サイズ）、デコード/向き正規化、プレビュー表示、ボタン有効化
- E2E: file chooser 操作とプレビュー可視性の検証（fixture 画像が必要）

**API**: なし

**グルーピング理由**:

- まず「端末内でプレビューできる」最短の価値を作り、以降の API 統合を安全に進められる

### グループ 2: アップロード API 統合（API: POST /photos）

- ならば: 写真アップロードが成功する

**実装内容**:

- フロント: `POST /api/v1/photos` へ multipart アップロードし、`photoId` 等を状態として保持
- バックエンド: multipart 受信、バリデーション、画像メタ情報抽出（MIME/サイズ/縦横）、短命 `photoId` 発行、RFC9457 エラー

**API**:

- `POST /api/v1/photos`（OpenAPI: `docs/spec/api/photos.yaml#/paths/~1photos/post`）

**グルーピング理由**:

- Protected Photo Data を扱う境界（非ログ/短命 TTL/非永続）を含み、リスクが高いので単独で検証しやすくする

## Gherkin ステップごとの実装要件

### 前提 ユーザーがブラウザを開いている

**Frontend**

- **Components**: 変更なし（E2E 実行基盤）
- **State**: なし
- **UI/UX**: なし

**Backend API**

- なし

**Database**

- なし（m0 は永続化禁止）

**Validation & Logic**

- なし

---

### もし トップページにアクセスする

**Frontend**

- **Components**: ルーティング/トップページが表示されること
- **State**: なし
- **UI/UX**: なし

**Backend API**

- なし

**Database**

- なし

**Validation & Logic**

- なし

---

### かつ ユーザーが「写真を選択」ボタンをクリックする

**Frontend**

- **Components**: 写真選択ボタン + hidden file input
- **State**: `SelectedPhoto` を更新する準備（未更新）
- **UI/UX**: キーボード操作でも実行できる（機能仕様の要件）

**Backend API**

- なし

**Database**

- なし

**Validation & Logic**

- file input の accept を `image/jpeg,image/png` とする（ブラウザ補助。最終判断はアプリ側検証）

---

### かつ ユーザーがファイルサイズ5MBのJPEGファイルを選択する

**Frontend**

- **Components**: ファイル選択ハンドラ、プレビュー表示領域
- **State**:
  - 成功時: `SelectedPhoto`（正規化済み画像参照、mimeType、dimensions）をセット
  - 失敗時: `PhotoSelectionError` をセットし、`SelectedPhoto` は更新しない
- **UI/UX**: アスペクト比を維持して表示領域に収まる

**Backend API**

- なし（この時点ではローカル処理）

**Database**

- なし

**Validation & Logic**

- 形式: JPEG/PNG のみ（`image/jpeg` or `image/png`）
- サイズ: `<= 10 * 1024 * 1024`（10MB）
- デコード/向き正規化: プレビューと後続処理の向きが一致すること

---

### ならば 写真アップロードが成功する

**Frontend**

- **Components**: アップロード実行（サービス/フック）
- **State**: `UploadedPhotoReference`（`photoId`, `expiresAt`, `mimeType`, `dimensions`, `fileSizeBytes`）を保持
- **UI/UX**: 失敗時は RFC9457 の `ProblemDetails.detail` を中心にエラー表示（非ログ方針を尊重し、ファイル名等は出さない）

**Backend API**

- **Endpoint**: `POST /api/v1/photos`
- **OpenAPI**:
  - JSON Pointer: `docs/spec/api/photos.yaml#/paths/~1photos/post`
  - JSONPath: `$.paths["/photos"].post`
- **Request**:
  - `multipart/form-data`
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/UploadPhotoRequest`
- **Response**:
  - `201` + `PhotoUploadResponse`
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/PhotoUploadResponse`
- **Error Response**:
  - `400`, `413`, `415`, `500`
  - JSON Pointer: `docs/spec/api/photos.yaml#/components/schemas/ProblemDetails`

**Database**

- なし（m0 は永続化禁止）

**Validation & Logic**

- バックエンド側でも MIME/サイズを検証する（フロントの検証は信用しない）
- `photoId` は UUID を払い出す
- `expiresAt` を設定する（短命 TTL。最大 5 分以内を上限として設計）
- 後続 API のために、画像バイナリを短命で保持する必要がある場合:
  - in-memory で TTL 付き（または tmpfs）
  - ログ/永続化しない

---

### かつ プレビューエリアに選択した画像が表示される

**Frontend**

- **Components**: プレビュー表示（`img` / `canvas` 等）
- **State**: `SelectedPhoto` 参照を表示に反映
- **UI/UX**: 画像がない場合は案内文を出す（機能仕様のレイアウト例）

**Backend API**

- なし

**Database**

- なし

**Validation & Logic**

- 表示領域に収まるように調整し、アスペクト比を維持

---

### かつ 「顔検出を実行」ボタンが有効になる

**Frontend**

- **Components**: 顔検出ボタン
- **State**: `SelectedPhoto` が存在する場合に enabled
- **UI/UX**: 画像未選択時は disabled（仕様通り）

**Backend API**

- なし（顔検出 API は別 feature の範囲）

**Database**

- なし

**Validation & Logic**

- `SelectedPhoto` の有無で判定

---

## データモデル (共通)

**参照**: `docs/spec/models/photo_upload.md`

ドメインモデルで定義されたエンティティ/バリューオブジェクトを使用します。

- `SelectedPhoto`（フロントの「選択済み画像」状態）
- `UploadedPhotoReference`（バックエンドから受け取る短命参照）

### 追加が DB Schema

m0 は Protected Photo Data の永続化を禁止しているため、追加の DB スキーマは作らない。

```sql
-- no-op
```

### 追加の DTO (フロントエンド用)

```typescript
export type MimeType = 'image/jpeg' | 'image/png';

export interface ImageDimensions {
  width: number;
  height: number;
}

export interface UploadedPhotoReferenceDto {
  photoId: string; // uuid
  mimeType: MimeType;
  fileSizeBytes: number;
  dimensions: ImageDimensions;
  expiresAt: string; // date-time
}

export interface ProblemDetails {
  type: string;
  title: string;
  status: number;
  detail?: string;
  instance?: string;
  errors?: Array<{ field: string; message: string }>;
}
```

## 技術的課題・リスク

- [ ] Protected Photo Data の**非ログ**徹底（例外/HTTPログ/バイナリダンプ/テスト成果物）
- [ ] 短命 TTL の設計（`photoId` を後続 feature が使える期間と、保持コストのバランス）
- [ ] 画像メタ情報抽出（dimensions/mimeType）の実装差異（ブラウザ/サーバ）
- [ ] multipart アップロードの E2E 安定性（Playwright の file chooser と fixture 管理）
- [ ] 大きい画像の取り回し（メモリ使用量、タイムアウト、CI 実行時間）

## 備考

- 本計画は m0 の方針（`docs/spec/architecture/m0.md`）およびデータ取り扱い（`docs/dev/policies/data-handling.md`）を優先する。
- E2E の新規ステップ（ファイル選択、アップロード成功、プレビュー表示など）は `e2e/step-definitions/steps.ts` への追記が必要。
- E2E fixture 画像（JPEG 5MB 相当）は `e2e/fixtures/` 配下へ追加する想定。
