import { Before, After, AfterAll, setWorldConstructor, World, setDefaultTimeout } from '@cucumber/cucumber';
import { Browser, BrowserContext, chromium, Page } from '@playwright/test';

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

// Increase default timeout for hooks/steps to 60s to accommodate browser startup
setDefaultTimeout(60 * 1000);

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
});

After(async function (this: CustomWorld) {
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
