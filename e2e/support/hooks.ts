import { Before, After, AfterAll, setWorldConstructor, World } from '@cucumber/cucumber';
import { Browser, BrowserContext, chromium, Page, Response } from '@playwright/test';

let browser: Browser | null = null;

interface CustomWorld extends World {
  page: Page;
  context: BrowserContext;
  // テスト実行中にキャプチャしたレスポンスを格納する（任意）
  lastResponses?: Array<{ url: string; status: number; body?: string | null }>;
}

class CustomWorldImpl extends World implements CustomWorld {
  page!: Page;
  context!: BrowserContext;
  lastResponses?: Array<{ url: string; status: number; body?: string | null }>;
}

setWorldConstructor(CustomWorldImpl);

Before(async function (this: CustomWorld) {
  // 最初のテストでブラウザを起動
  if (!browser) {
    browser = await chromium.launch({
      headless: true,
    });
  }
  
  this.context = await browser.newContext({
    baseURL: 'http://localhost:3000',
  });
  this.page = await this.context.newPage();

  // レスポンスキャプチャ用配列を初期化
  this.lastResponses = [];

  // テストごとの trace を開始（失敗時に trace を保存するため）
  try {
    // screenshots/snapshots を有効にして詳細な trace を取得
    await this.context.tracing.start({ screenshots: true, snapshots: true });
  } catch (e) {
    // tracing が利用できない環境でも動作するようにエラーは無視
  }

  // アップロードなど特定のエンドポイントに対するレスポンスを収集するリスナ
  this.page.on('response', async (response: Response) => {
    try {
      const url = response.url();
      // 必要に応じて他のエンドポイントも追加可能
      if (url.includes('/api/v1/photos/upload')) {
        const status = response.status();
        const text = await response.text().catch(() => null);
        this.lastResponses!.push({ url, status, body: text });
      }
    } catch (e) {
      // リスナ内のエラーは無視してテストへの影響を避ける
    }
  });
});

After(async function (this: CustomWorld, { result }: { result?: { status?: string } } = {}) {
  // シナリオが失敗した場合、lastResponses を出力して原因把握を助ける
  try {
    if (result && result.status === 'FAILED') {
      // 失敗時は tracing を停止してファイルに保存する（可能なら）
      let tracePath: string | null = null;
      try {
        const ts = new Date().toISOString().replace(/[:.]/g, '-');
        tracePath = `e2e/test-results/trace-${ts}.zip`;
        // ディレクトリがない場合は作成
        await import('fs').then(fs => fs.promises.mkdir('e2e/test-results', { recursive: true }));
        await this.context.tracing.stop({ path: tracePath }).catch(() => null);
      } catch (e) {
        tracePath = null;
      }

      // lastResponses を Cucumber レポートに添付する
      try {
        const summary = (this.lastResponses && this.lastResponses.length > 0)
          ? this.lastResponses.map(r => `URL: ${r.url}\nstatus: ${r.status}`).join('\n---\n')
          : 'No lastResponses captured for this scenario';
        // Cucumber のレポートに添付（テキスト）
        // @ts-ignore - attach は World に存在するメソッド
        await (this as any).attach(`lastResponses:\n${summary}`, 'text/plain');
        if (tracePath) {
          // トレースファイルのパスも添付
          await (this as any).attach(`trace: ${tracePath}`, 'text/plain');
        }
      } catch (e) {
        // attach に失敗しても続行
      }
    } else {
      // 正常終了時は tracing を停止して破棄する（ファイルは作らない）
      try {
        await this.context.tracing.stop();
      } catch (e) {
        // ignore
      }
    }
  } catch (e) {
    // 出力処理で例外が出てもテストの後処理を妨げない
  }

  if (this.page) {
    await this.page.close();
  }
  if (this.context) {
    await this.context.close();
  }
});

AfterAll(async function () {
  if (browser) {
    await browser.close();
    browser = null;
  }
});
