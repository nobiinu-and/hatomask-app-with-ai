# Test Image Generator

Hatomaskアプリケーションのテスト用画像を生成するNode.jsツールです。

## 機能

- 様々なサイズの画像を自動生成（小・中・大サイズ）
- PNG/JPEG形式をサポート
- 画像サイズやファイル名を画像内に表示
- カスタム設定による柔軟な画像生成

## 生成される画像

デフォルトで以下の13種類のテスト画像が生成されます:

### 小サイズ
- `small-square.png` (100×100)
- `small-portrait.png` (100×150)
- `small-landscape.png` (150×100)

### 中サイズ
- `medium-square.png` (500×500)
- `medium-portrait.png` (400×600)
- `medium-landscape.png` (600×400)

### 大サイズ
- `large-square.jpeg` (1000×1000)
- `large-portrait.jpeg` (800×1200)
- `large-landscape.jpeg` (1200×800)

### 特殊サイズ
- `very-small.png` (50×50) - 最小サイズ
- `very-large.jpeg` (2000×2000) - 超大サイズ
- `ultra-wide.jpeg` (1920×500) - 超ワイド
- `jpeg-sample.jpeg` (640×480) - JPEG形式サンプル

## セットアップ

```bash
cd scripts/generate-test-images
npm install
```

## 使い方

### 基本的な使用方法

```bash
npm run generate
```

デフォルトでは `./generated` ディレクトリに13種類の画像が生成されます。

### サイズとフォーマットを指定して生成

```bash
# PNG画像を生成
npm run generate -- --width 800 --height 600 --format png

# JPEG画像を生成（ファイル名も指定）
npm run generate -- --width 1920 --height 1080 --format jpeg --name thumbnail

# デフォルト画像を生成せず、指定した画像のみ生成
npm run generate -- --width 500 --height 500 --format png --no-default
```

### 出力先を指定

```bash
npm run generate -- --output ../../e2e/fixtures/images
```

### カスタム画像を生成（JSON形式）

```bash
npm run generate -- --custom '[{"name":"custom","width":300,"height":200,"format":"png","text":"Custom Image","backgroundColor":"#ff0000","textColor":"#ffffff"}]'
```

### ビルドして実行

```bash
npm run build
npm start
```

## オプション

- `-o, --output <dir>` - 出力ディレクトリを指定（デフォルト: `./generated`）
- `-w, --width <number>` - 画像の幅を指定（--height, --formatと併用）
- `-h, --height <number>` - 画像の高さを指定（--width, --formatと併用）
- `-f, --format <type>` - 画像フォーマットを指定: `png` または `jpeg`
- `-n, --name <name>` - 画像名を指定（拡張子なし、省略時は `custom-{width}x{height}`）
- `--no-default` - デフォルト画像の生成をスキップ
- `-c, --custom <json>` - カスタム画像設定をJSON形式で指定

## カスタム画像設定のフォーマット

```json
{
  "name": "画像名",
  "width": 幅,
  "height": 高さ,
  "format": "png" | "jpeg",
  "text": "画像内に表示するテキスト",
  "backgroundColor": "#色コード",
  "textColor": "#色コード"
}
```

## 使用例

### E2Eテストのfixturesに生成

```bash
cd scripts/generate-test-images
npm run generate -- --output ../../e2e/fixtures/images
```

### 特定サイズの画像のみ生成

```bash
# 1920x1080のJPEG画像のみを生成
npm run generate -- --width 1920 --height 1080 --format jpeg --name hd-image --no-default

# 複数サイズを生成（カスタムJSONで）
npm run generate -- --custom '[
  {"name":"small","width":320,"height":240,"format":"png"},
  {"name":"medium","width":640,"height":480,"format":"png"},
  {"name":"large","width":1280,"height":720,"format":"jpeg"}
]' --no-default
```

### バックエンドのテスト用に生成

```bash
cd scripts/generate-test-images
npm run generate -- --output ../../src/backend/src/test/resources/images --width 500 --height 500 --format jpeg
```

### フロントエンドのテスト用に生成

```bash
cd scripts/generate-test-images
npm run generate -- --output ../../src/frontend/src/test/fixtures/images --width 100 --height 100 --format png
```

## 依存関係

- **sharp**: 高速な画像生成・変換ライブラリ
- **commander**: CLIオプション解析
- **typescript**: TypeScript実行環境
- **tsx**: TypeScript実行ツール

## ライセンス

MIT
