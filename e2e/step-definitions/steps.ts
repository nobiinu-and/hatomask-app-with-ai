import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { Page } from '@playwright/test';
import path from 'path';

interface CustomWorld {
  page: Page;
  uploadPhotoResponseStatus?: number;
  faceDetectionResponseStatus?: number;
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
  const fixturePath = path.resolve(__dirname, '..', 'fixtures', 'photo_5mb.jpg');
  const [response] = await Promise.all([
    this.page.waitForResponse(
      (res) => res.url().includes('/api/v1/photos') && res.request().method() === 'POST',
      { timeout: 60000 },
    ),
    this.page.getByTestId('photo-file-input').setInputFiles(fixturePath),
  ]);
  this.uploadPhotoResponseStatus = response.status();
});

Then('写真アップロードが成功する', { timeout: 60000 }, async function (this: CustomWorld) {
  expect(this.uploadPhotoResponseStatus).toBe(201);
});

Then('プレビューエリアに選択した画像が表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  await expect(this.page.getByTestId('photo-preview-image')).toBeVisible({ timeout: 10000 });
});

Then('「顔検出を実行」ボタンが有効になる', { timeout: 60000 }, async function (this: CustomWorld) {
  await expect(this.page.getByRole('button', { name: '顔検出を実行' })).toBeEnabled({ timeout: 10000 });
});

When('ユーザーが「顔検出を実行」ボタンをクリックする', { timeout: 60000 }, async function (this: CustomWorld) {
  const [response] = await Promise.all([
    this.page.waitForResponse(
      (res) => res.url().includes('/api/v1/photos/') && res.url().includes('/face-detections') && res.request().method() === 'POST',
      { timeout: 60000 },
    ),
    this.page.getByRole('button', { name: '顔検出を実行' }).click(),
  ]);
  this.faceDetectionResponseStatus = response.status();
});

Then('顔検出が成功する', { timeout: 60000 }, async function (this: CustomWorld) {
  expect(this.faceDetectionResponseStatus).toBe(200);
  await expect(this.page.getByText('状態: 検出成功')).toBeVisible({ timeout: 10000 });
});

Then('プレビュー上にランドマークが重ねて表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  await expect(this.page.getByTestId('face-landmarks-overlay')).toBeVisible({ timeout: 10000 });
});
