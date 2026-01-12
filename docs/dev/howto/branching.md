# ブランチ戦略ガイド

このドキュメントでは、HatoMask プロジェクトにおけるブランチ戦略について詳細に説明します。

## 基本方針

本プロジェクトでは **GitHub Flow** をベースとしたシンプルなブランチ戦略を採用しています。AI 協働開発においても、人間の開発者と同じルールに従います。

## ブランチの種類

### main ブランチ

- **役割**: 本番環境にデプロイ可能な状態を常に維持
- **直接プッシュ**: 禁止（PR 経由のみ）
- **保護設定**: CI パス必須、レビュー承認必須

### feature ブランチ

- **役割**: 新機能の開発
- **命名規則**: `feature/{機能番号}-{簡潔な説明}`
- **例**:
  - `feature/01-photo-upload`
  - `feature/02-face-detection`
  - `feature/03-mask-rendering`

### fix ブランチ

- **役割**: バグ修正
- **命名規則**: `fix/{Issue番号またはバグの簡潔な説明}`
- **例**:
  - `fix/123-upload-timeout`
  - `fix/file-size-validation`

### docs ブランチ

- **役割**: ドキュメントの追加・修正
- **命名規則**: `docs/{ドキュメントの簡潔な説明}`
- **例**:
  - `docs/add-branch-strategy`
  - `docs/update-api-spec`

### refactor ブランチ

- **役割**: リファクタリング（機能変更なし）
- **命名規則**: `refactor/{対象の簡潔な説明}`
- **例**:
  - `refactor/photo-service`
  - `refactor/clean-architecture`

### experiment ブランチ

- **役割**: 実験的な変更、PoC
- **命名規則**: `experiment/{実験内容}`
- **例**:
  - `experiment/opencv-integration`
  - `experiment/new-mask-algorithm`

## AI エージェント用ブランチ

AI エージェント（Devin、Copilot など）が作成するブランチには、識別しやすいプレフィックスを付けます。

### 目的

- **作業の追跡**: どの AI エージェントがどの作業を行ったかを明確にする
- **レビューの効率化**: AI 生成コードであることを認識し、適切なレビュー観点を持てる
- **履歴の管理**: AI 協働開発の履歴を後から振り返りやすくする

### 命名規則

```
{ai-agent}/{timestamp}-{簡潔な説明}
```

### 例

- `devin/1768183963-add-branch-strategy`
- `copilot/1768184000-fix-upload-bug`

### タイムスタンプの生成

```bash
date +%s
```

## Task01〜06 に対応したブランチ運用

AI 協働開発の各タスクに対応したブランチ運用のパターンです。

### 基本方針: 1 機能 = 1 ブランチ

Task01〜06 のすべてのタスクは、**1 つの feature ブランチ**で扱います。

```
feature/{機能番号}-{機能名}
```

**例**: `feature/01-photo-upload`

### ブランチに含まれる成果物

1 つの feature ブランチには、ドキュメントとコードの両方がコミットされます:

- Task01: 機能仕様（`docs/spec/features/`）
- Task02: ドメインモデル（`docs/spec/models/`）
- Task03: API Contract（`docs/spec/api/`）
- Task04: Gherkin シナリオ + 実装計画（`e2e/features/`, `docs/plans/`）
- Task05: Backend Stub（`src/backend/`）
- Task06: 実装コード（`src/frontend/`, `src/backend/`）

### 大きな機能の場合

機能が大きい場合は、**ブランチを分割するのではなく、機能（ストーリー）を分割**します。

**例**: 「写真アップロード」機能が大きい場合

- `feature/01a-photo-upload-basic` - 基本的なアップロード機能
- `feature/01b-photo-upload-validation` - バリデーション機能
- `feature/01c-photo-upload-preview` - プレビュー機能

それぞれの feature ブランチで Task01〜06 を完結させます。

## ブランチ作成から PR マージまでの流れ

### 1. ブランチの作成

```bash
git checkout main
git pull origin main
git checkout -b feature/01-photo-upload
```

### 2. 作業とコミット

コミットメッセージは [Conventional Commits](https://www.conventionalcommits.org/ja/v1.0.0/) 形式を使用します。

```bash
git add <変更ファイル>
git commit -m "feat: 写真アップロード機能の実装"
```

### 3. リモートへのプッシュ

```bash
git push origin feature/01-photo-upload
```

### 4. PR の作成

- GitHub 上で PR を作成
- PR テンプレートに従って記述
- レビュアーをアサイン

### 5. レビューと修正

- レビューコメントに対応
- 追加コミットをプッシュ

### 6. マージ

- CI がすべてパス
- レビュー承認を取得
- **Create a merge commit** を使用（詳細な履歴を保持）

### 7. ブランチの削除

マージ後、リモートブランチは自動削除（GitHub 設定）。ローカルブランチは手動で削除。

```bash
git checkout main
git pull origin main
git branch -d feature/01-photo-upload
```

## マージ戦略

### 推奨: Create a Merge Commit

- **理由**: 詳細な履歴を残し、各コミットの文脈を保持する
- **適用場面**: すべての feature/fix ブランチ
- **メリット**: 
  - 各コミットが [Conventional Commits](https://www.conventionalcommits.org/ja/v1.0.0/) 形式で残る
  - 後から変更の経緯を追跡しやすい

### 代替: Squash and Merge

- **理由**: 履歴をシンプルにしたい場合
- **適用場面**: 小さな修正、WIP コミットが多い場合
- **注意**: 個々のコミット履歴が失われる

### 非推奨: Rebase and Merge

- **理由**: コンフリクト解決が複雑になる可能性
- **注意**: AI エージェントは rebase を使用しない

## コンフリクト解決

### 基本方針

1. **早期発見**: 定期的に main ブランチの変更を取り込む
2. **小さな単位**: ブランチの寿命を短く保つ
3. **コミュニケーション**: 同じファイルを編集する場合は事前に調整

### 解決手順

```bash
git checkout feature/01-photo-upload
git fetch origin
git merge origin/main

# コンフリクトが発生した場合
# 1. コンフリクトファイルを編集して解決
# 2. 解決後にコミット
git add <解決したファイル>
git commit -m "fix: mainブランチとのコンフリクトを解決"
git push origin feature/01-photo-upload
```

### AI エージェントのコンフリクト対応

AI エージェントがコンフリクトに遭遇した場合:

1. コンフリクトの内容を人間に報告
2. 自動解決を試みない（意図しない変更を防ぐ）
3. 人間の指示を待つ

## ブランチ保護ルール

### main ブランチの保護設定（推奨）

- [ ] Require a pull request before merging
- [ ] Require approvals (1 人以上)
- [ ] Require status checks to pass before merging
  - PR Checks
  - Build & Coverage
- [ ] Require branches to be up to date before merging
- [ ] Do not allow bypassing the above settings

## よくある質問

### Q: 1 つの機能に複数のシナリオがある場合、ブランチはどう分けるべき？

**A**: ブランチを分割するのではなく、**機能（ストーリー）を分割**することを推奨します。

大きな機能は複数の小さなストーリーに分割し、それぞれのストーリーを 1 つの feature ブランチで完結させます。これにより、各 PR が小さく保たれ、レビューしやすくなります。

**例**: 「写真アップロード」機能が大きい場合

- `feature/01a-photo-upload-basic` - 基本的なアップロード機能
- `feature/01b-photo-upload-validation` - バリデーション機能
- `feature/01c-photo-upload-preview` - プレビュー機能

### Q: AI エージェントが作成したブランチを人間が引き継ぐ場合は？

**A**: そのまま作業を継続できます。ブランチ名の変更は不要です。

### Q: 実験的なブランチはいつ削除すべき？

**A**: 以下のタイミングで削除を検討:

- 実験が完了し、結果が記録された
- 実験が中止された
- 3 ヶ月以上更新がない

## 関連ドキュメント

- [コーディング規約](../standards/coding.md) - コミットメッセージの形式
- [CI/CD ガイド](./ci.md) - CI パイプラインの詳細
- [開発プロセスガイド](./development.md) - Task01〜06 の詳細
