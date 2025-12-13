import { Before, After, AfterAll, setWorldConstructor, World, Status } from '@cucumber/cucumber';
import { Browser, BrowserContext, chromium, Page } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

let browser: Browser | null = null;

interface CustomWorld extends World {
  page: Page;
  context: BrowserContext;
}

class CustomWorldImpl extends World implements CustomWorld {
  page!: Page;
  context!: BrowserContext;
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
    // ビデオ録画を有効化
    recordVideo: {
      dir: 'test-results/videos',
      size: { width: 1280, height: 720 }
    },
    // トレース記録を開始
  });
  
  // トレース記録を開始（失敗時のみ保存）
  await this.context.tracing.start({ 
    screenshots: true, 
    snapshots: true, 
    sources: true 
  });
  
  this.page = await this.context.newPage();
  
  // コンソールメッセージをキャプチャ
  this.page.on('console', msg => {
    const type = msg.type();
    if (type === 'error' || type === 'warning') {
      console.log(`[Browser ${type.toUpperCase()}]:`, msg.text());
    }
  });
  
  // ページエラーをキャプチャ
  this.page.on('pageerror', error => {
    console.log('[Page Error]:', error.message);
  });
});

After(async function (this: CustomWorld, scenario) {
  const scenarioName = scenario.pickle.name.replace(/[^a-z0-9]/gi, '_');
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
  
  // テスト失敗時にスクリーンショットとトレースを保存
  if (scenario.result?.status === Status.FAILED) {
    const screenshotDir = 'test-results/screenshots';
    const traceDir = 'test-results/traces';
    
    // ディレクトリ作成
    if (!fs.existsSync(screenshotDir)) {
      fs.mkdirSync(screenshotDir, { recursive: true });
    }
    if (!fs.existsSync(traceDir)) {
      fs.mkdirSync(traceDir, { recursive: true });
    }
    
    // スクリーンショット保存
    const screenshotPath = path.join(screenshotDir, `${scenarioName}_${timestamp}.png`);
    await this.page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`📸 Screenshot saved: ${screenshotPath}`);
    
    // トレース保存
    const tracePath = path.join(traceDir, `${scenarioName}_${timestamp}.zip`);
    await this.context.tracing.stop({ path: tracePath });
    console.log(`🔍 Trace saved: ${tracePath}`);
    console.log(`   View with: npx playwright show-trace ${tracePath}`);
    
    // ページのHTMLをダンプ
    const htmlPath = path.join(screenshotDir, `${scenarioName}_${timestamp}.html`);
    const content = await this.page.content();
    fs.writeFileSync(htmlPath, content);
    console.log(`📄 HTML saved: ${htmlPath}`);
  } else {
    // 成功時はトレースを破棄
    await this.context.tracing.stop();
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
