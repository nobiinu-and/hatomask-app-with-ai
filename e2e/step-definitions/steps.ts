import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { Page } from '@playwright/test';
import * as fs from 'node:fs';
import * as path from 'node:path';

interface CustomWorld {
  page: Page;
}

Given('ユーザーがブラウザを開いている', { timeout: 60000 }, async function (this: CustomWorld) {
  // hooksでpageが設定されている
});

Given('ビューポートサイズを {int}x{int} に設定する', { timeout: 60000 }, async function (this: CustomWorld, width: number, height: number) {
  await this.page.setViewportSize({ width, height });
});

When('トップページにアクセスする', { timeout: 60000 }, async function (this: CustomWorld) {
  await this.page.goto('/');
});

When('バックエンドとの接続が確立される', { timeout: 60000 }, async function (this: CustomWorld) {
  await this.page.waitForLoadState('networkidle');
});

When('バックエンドAPIからデータを取得する', { timeout: 60000 }, async function (this: CustomWorld) {
  await this.page.waitForLoadState('networkidle');
});

When('ページが完全に読み込まれる', { timeout: 60000 }, async function (this: CustomWorld) {
  await this.page.waitForLoadState('domcontentloaded');
});

Then('タイトル {string} が表示される', { timeout: 60000 }, async function (this: CustomWorld, title: string) {
  await expect(this.page.getByText(title)).toBeVisible();
});

Then('説明文 {string} が表示される', { timeout: 60000 }, async function (this: CustomWorld, description: string) {
  await expect(this.page.getByText(description)).toBeVisible();
});

Then('{string} セクションが表示される', { timeout: 60000 }, async function (this: CustomWorld, sectionName: string) {
  await expect(this.page.getByText(sectionName)).toBeVisible({ timeout: 10000 });
});

Then('バックエンドからのメッセージが表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  await expect(
    this.page.getByText(/Hello, World from HatoMask Backend!|Hello, World from HatoMask Frontend!/)
  ).toBeVisible({ timeout: 10000 });
});

Then('コンテンツ {string} が表示される', { timeout: 60000 }, async function (this: CustomWorld, content: string) {
  await expect(this.page.getByText(content)).toBeVisible();
});

Then('Material-UIのカードコンポーネントが表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  const card = this.page.locator('[class*="MuiCard-root"]').first();
  await expect(card).toBeVisible();
});

When('ユーザーが「写真を選択」ボタンをクリックする', { timeout: 60000 }, async function (this: CustomWorld) {
  await this.page.getByRole('button', { name: '写真を選択' }).click();
});

When('ユーザーがファイルサイズ5MBのJPEGファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  const input = this.page.locator('input[type="file"]');

  const fixturePath = path.resolve(__dirname, '..', 'fixtures', 'sample.jpg');
  if (!fs.existsSync(fixturePath)) {
    throw new Error(`fixturesに sample.jpg が見つかりません: ${fixturePath}`);
  }

  const stats = fs.statSync(fixturePath);
  if (stats.size <= 0) {
    throw new Error(`fixturesの sample.jpg が空です: ${fixturePath}`);
  }
  if (stats.size > 10 * 1024 * 1024) {
    throw new Error(`fixturesの sample.jpg が10MBを超えています: ${stats.size} bytes`);
  }

  await input.setInputFiles(fixturePath);
});

When('ユーザーがファイルサイズ11MBのJPEGファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  const input = this.page.locator('input[type="file"]');

  const fixturePath = path.resolve(__dirname, '..', 'fixtures', 'sample_11mb.jpg');
  if (!fs.existsSync(fixturePath)) {
    throw new Error(`fixturesに sample_11mb.jpg が見つかりません: ${fixturePath}`);
  }

  const stats = fs.statSync(fixturePath);
  if (stats.size <= 10 * 1024 * 1024) {
    throw new Error(`fixturesの sample_11mb.jpg が10MB以下です（サイズ超過シナリオ用）: ${stats.size} bytes`);
  }

  await input.setInputFiles(fixturePath);
});

When('ユーザーがPNGファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  const input = this.page.locator('input[type="file"]');

  const fixturePath = path.resolve(__dirname, '..', 'fixtures', 'sample.png');
  if (!fs.existsSync(fixturePath)) {
    throw new Error(`fixturesに sample.png が見つかりません: ${fixturePath}`);
  }

  const stats = fs.statSync(fixturePath);
  if (stats.size <= 0) {
    throw new Error(`fixturesの sample.png が空です: ${fixturePath}`);
  }
  if (stats.size > 10 * 1024 * 1024) {
    throw new Error(`fixturesの sample.png が10MBを超えています: ${stats.size} bytes`);
  }
  await input.setInputFiles(fixturePath);
});

When('ユーザーがGIFファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  const input = this.page.locator('input[type="file"]');

  const fixturePath = path.resolve(__dirname, '..', 'fixtures', 'sample.gif');
  if (!fs.existsSync(fixturePath)) {
    throw new Error(`fixturesに sample.gif が見つかりません: ${fixturePath}`);
  }

  const stats = fs.statSync(fixturePath);
  if (stats.size <= 0) {
    throw new Error(`fixturesの sample.gif が空です: ${fixturePath}`);
  }
  if (stats.size > 10 * 1024 * 1024) {
    throw new Error(`fixturesの sample.gif が10MBを超えています: ${stats.size} bytes`);
  }

  await input.setInputFiles(fixturePath);
});

Then('プレビューエリアに選択した画像が表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  const image = this.page.locator('img[alt="選択済み画像"]');
  await expect(image).toBeVisible({ timeout: 20000 });
  await this.page.waitForFunction(
    (img) => Boolean((img as any).complete) && Number((img as any).naturalWidth) > 0,
    await image.elementHandle(),
    { timeout: 20000 }
  );
});

Then('エラーメッセージ {string} が表示される', { timeout: 60000 }, async function (this: CustomWorld, message: string) {
  const alert = this.page.locator('[role="alert"]').filter({ hasText: message });
  await expect(alert).toBeVisible({ timeout: 10000 });
});

Then('エラーメッセージ {string} が表示されない', { timeout: 60000 }, async function (this: CustomWorld, message: string) {
  const alert = this.page.locator('[role="alert"]').filter({ hasText: message });
  await expect(alert).toHaveCount(0, { timeout: 10000 });
});

Then('プレビュー画像が表示されない', { timeout: 60000 }, async function (this: CustomWorld) {
  const image = this.page.locator('img[alt="選択済み画像"]');
  await expect(image).toHaveCount(0);
});

Then('「顔検出を実行」ボタンが有効になる', { timeout: 60000 }, async function (this: CustomWorld) {
  await expect(this.page.getByRole('button', { name: '顔検出を実行' })).toBeEnabled({ timeout: 10000 });
});

Then('「顔検出を実行」ボタンが無効になる', { timeout: 60000 }, async function (this: CustomWorld) {
  await expect(this.page.getByRole('button', { name: '顔検出を実行' })).toBeDisabled({ timeout: 10000 });
});
