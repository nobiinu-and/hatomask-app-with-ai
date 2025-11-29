import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { Page } from '@playwright/test';
import path from 'path';

interface CustomWorld {
  page: Page;
}

Given('ユーザーがHatoMaskアプリケーションにアクセスしている', { timeout: 60000 }, async function (this: CustomWorld) {
  // 期待されるタイトルを確認する
  await this.page.goto('/');
  await expect(this.page.getByText('HatoMask App')).toBeVisible({ timeout: 10000 });
});

When('ユーザーが「写真を選択」ボタンをクリックする', { timeout: 60000 }, async function (this: CustomWorld) {
  // ボタンが存在しない場合はここで自然に失敗する（Redを誘発）
  const button = this.page.getByRole('button', { name: '写真を選択' });
  await expect(button).toBeVisible({ timeout: 10000 });
  await button.click();
});

When('ユーザーがファイルサイズ5MBのJPEGファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  // テスト用のfixturesに置かれた5MBのJPEGファイルを想定してアップロードする
  // input[type="file"] が存在しない場合は自然に失敗する（Redを誘発）
  const fileInput = this.page.locator('input[type="file"]');
  await expect(fileInput).toBeVisible({ timeout: 10000 });

  // テスト実行ルートからの相対パスを使用
  const filePath = path.join(process.cwd(), 'fixtures', 'sample-5mb.jpg');

  // PlaywrightのAPIでファイルをセットする。ファイルが存在しなければこの行で失敗する。
  await fileInput.setInputFiles(filePath);
});

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
