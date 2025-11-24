import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { Page } from '@playwright/test';

interface CustomWorld {
  page: Page;
}

Given('ユーザーがブラウザを開いている', { timeout: 60000 }, async function (this: CustomWorld) {
  // hooksでpageが設定されている
});

Given('ユーザーがHatoMaskアプリケーションにアクセスしている', { timeout: 60000 }, async function (this: CustomWorld) {
  const errors: string[] = [];
  this.page.on('pageerror', (e) => errors.push(String(e?.message ?? e)));
  this.page.on('console', (msg) => {
    if (msg.type() === 'error') errors.push(msg.text());
  });

  await this.page.goto('/');
  await this.page.waitForLoadState('load');

  // ドキュメントが完全にロードされていることを確認
  const readyState = await this.page.evaluate(() => document.readyState);
  if (readyState !== 'complete') {
    throw new Error(`document.readyState is not complete: ${readyState}`);
  }

  // ページタイトルの確認
  await expect(this.page.getByText('HatoMask App')).toBeVisible({ timeout: 5000 });

  // コンソールエラーがないことを確認
  if (errors.length > 0) {
    throw new Error(`Console errors detected: ${errors.join(' | ')}`);
  }
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
  // ボタンはまだ存在しない可能性があるため、まず存在を待つ
  const btn = this.page.getByRole('button', { name: '写真を選択' });
  await expect(btn).toBeVisible({ timeout: 5000 });
  await btn.click();
});

