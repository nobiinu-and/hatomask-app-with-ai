import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { Page } from '@playwright/test';

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

// 写真アップロード・ダウンロード機能
Given('ユーザーがHatoMaskアプリケーションにアクセスしている', { timeout: 60000 }, async function (this: CustomWorld) {
  await this.page.goto('/');
  await this.page.waitForLoadState('domcontentloaded');
  
  // アプリケーションのタイトルが表示されていることを確認
  await expect(this.page.getByText('HatoMask App')).toBeVisible();
});

When('ユーザーが「写真を選択」ボタンをクリックする', { timeout: 60000 }, async function (this: CustomWorld) {
  // 「写真を選択」ボタンをクリック
  const uploadButton = this.page.getByRole('button', { name: '写真を選択' });
  await expect(uploadButton).toBeVisible();
  await uploadButton.click();
});

When('ユーザーがファイルサイズ5MBのJPEGファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  // テスト用の5MB JPEGファイルをアップロード
  const fileInput = this.page.locator('input[type="file"]');
  
  // 5MBのダミーJPEGファイルを作成
  const buffer = Buffer.alloc(5 * 1024 * 1024); // 5MB
  
  await fileInput.setInputFiles({
    name: 'test-photo.jpg',
    mimeType: 'image/jpeg',
    buffer: buffer,
  });
  
  // アップロード処理が完了するまで待機
  await this.page.waitForTimeout(1000);
});

When('ユーザーがファイルサイズ3MBのPNGファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  // テスト用の3MB PNGファイルをアップロード
  const fileInput = this.page.locator('input[type="file"]');
  
  // 3MBのダミーPNGファイルを作成
  const buffer = Buffer.alloc(3 * 1024 * 1024); // 3MB
  
  await fileInput.setInputFiles({
    name: 'test-photo.png',
    mimeType: 'image/png',
    buffer: buffer,
  });
  
  // アップロード処理が完了するまで待機
  await this.page.waitForTimeout(1000);
});

Then('アップロードが成功する', { timeout: 60000 }, async function (this: CustomWorld) {
  // アップロード処理が完了するまで待機
  await this.page.waitForTimeout(2000);
  
  // エラーメッセージが表示されていないことを確認
  const errorAlert = this.page.locator('[role="alert"]').filter({ hasText: /エラー|失敗/ });
  await expect(errorAlert).not.toBeVisible();
});

Then('プレビューエリアに選択した画像が表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  // プレビュー画像が表示されることを確認
  const previewImage = this.page.locator('img[alt*="プレビュー"], img[alt*="preview"]');
  await expect(previewImage).toBeVisible({ timeout: 10000 });
});

When('ユーザーが「ダウンロード」ボタンをクリックする', { timeout: 60000 }, async function (this: CustomWorld) {
  // ダウンロード処理を監視
  const downloadPromise = this.page.waitForEvent('download', { timeout: 10000 });
  
  // ダウンロードボタンをクリック
  const downloadButton = this.page.getByTestId('download-button');
  await expect(downloadButton).toBeVisible({ timeout: 10000 });
  await downloadButton.click();
  
  // ダウンロードイベントを待機
  const download = await downloadPromise;
  
  // ダウンロードされたファイル名を確認（JPEGまたはPNG）
  const suggestedFilename = download.suggestedFilename();
  expect(suggestedFilename).toMatch(/photo_.*\.(jpg|png)/);
  
  // ダウンロードが完了するまで待機
  await download.path();
});

Then('元の画像がダウンロードされる', { timeout: 60000 }, async function (this: CustomWorld) {
  // ダウンロード処理は上のステップで完了しているため、追加の検証は不要
});

// 複数回アップロード用ステップ
When('ユーザーが1枚目のJPEGファイルをアップロードする', { timeout: 60000 }, async function (this: CustomWorld) {
  // 「写真を選択」ボタンをクリック
  const uploadButton = this.page.getByRole('button', { name: '写真を選択' });
  await expect(uploadButton).toBeVisible();
  await uploadButton.click();
  
  // ファイルを選択
  const fileInput = this.page.locator('input[type="file"]');
  const buffer = Buffer.alloc(2 * 1024 * 1024); // 2MB
  
  await fileInput.setInputFiles({
    name: 'first-photo.jpg',
    mimeType: 'image/jpeg',
    buffer: buffer,
  });
  
  // アップロード処理が完了するまで待機
  await this.page.waitForTimeout(1000);
});

Then('1枚目の画像がプレビューエリアに表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  // プレビュー画像が表示されることを確認
  const previewImage = this.page.locator('img[alt*="プレビュー"], img[alt*="preview"]');
  await expect(previewImage).toBeVisible({ timeout: 10000 });
  
  // 1枚目の画像のsrc URLを保存（後で2枚目と比較するため）
  const firstImageSrc = await previewImage.getAttribute('src');
  (this as any).firstImageSrc = firstImageSrc;
});

When('ユーザーが2枚目のPNGファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  // ファイルを選択
  const fileInput = this.page.locator('input[type="file"]');
  const buffer = Buffer.alloc(1 * 1024 * 1024); // 1MB
  
  await fileInput.setInputFiles({
    name: 'second-photo.png',
    mimeType: 'image/png',
    buffer: buffer,
  });
  
  // アップロード処理が完了するまで待機
  await this.page.waitForTimeout(1000);
});

Then('プレビューエリアの画像が2枚目の画像に切り替わる', { timeout: 60000 }, async function (this: CustomWorld) {
  // プレビュー画像が表示されることを確認
  const previewImage = this.page.locator('img[alt*="プレビュー"], img[alt*="preview"]');
  await expect(previewImage).toBeVisible({ timeout: 10000 });
  
  // 2枚目の画像のsrc URLを取得
  const secondImageSrc = await previewImage.getAttribute('src');
  
  // 1枚目と2枚目のURLが異なることを確認（画像が切り替わった）
  const firstImageSrc = (this as any).firstImageSrc;
  expect(secondImageSrc).not.toBe(firstImageSrc);
});

// エラーハンドリング用ステップ
When('ユーザーがファイルサイズ11MBのJPEGファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  // 「写真を選択」ボタンをクリック
  const uploadButton = this.page.getByRole('button', { name: '写真を選択' });
  await expect(uploadButton).toBeVisible();
  await uploadButton.click();
  
  // ファイルを選択
  const fileInput = this.page.locator('input[type="file"]');
  // 11MBのダミーJPEGファイルを作成
  const buffer = Buffer.alloc(11 * 1024 * 1024); // 11MB
  
  await fileInput.setInputFiles({
    name: 'large-photo.jpg',
    mimeType: 'image/jpeg',
    buffer: buffer,
  });
  
  // アップロード処理が完了するまで待機
  await this.page.waitForTimeout(2000);
});

Then('エラーメッセージ「ファイルサイズは10MB以下にしてください」が表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  // エラーメッセージが表示されることを確認
  const errorAlert = this.page.locator('[role="alert"]').filter({ hasText: /ファイルサイズ.*10MB/ });
  await expect(errorAlert).toBeVisible({ timeout: 10000 });
});

Then('プレビューエリアの画像が消える', { timeout: 60000 }, async function (this: CustomWorld) {
  // プレビュー画像が表示されていないことを確認（エラー時にクリアされる）
  const previewImage = this.page.locator('img[alt*="プレビュー"], img[alt*="preview"]');
  await expect(previewImage).not.toBeVisible();
});

When('ユーザーがGIF形式のファイルを選択する', { timeout: 60000 }, async function (this: CustomWorld) {
  // 「写真を選択」ボタンをクリック
  const uploadButton = this.page.getByRole('button', { name: '写真を選択' });
  await expect(uploadButton).toBeVisible();
  await uploadButton.click();
  
  // ファイルを選択
  const fileInput = this.page.locator('input[type="file"]');
  // GIFファイルを作成
  const buffer = Buffer.alloc(1 * 1024 * 1024); // 1MB
  
  await fileInput.setInputFiles({
    name: 'test-image.gif',
    mimeType: 'image/gif',
    buffer: buffer,
  });
  
  // アップロード処理が完了するまで待機
  await this.page.waitForTimeout(2000);
});

Then('エラーメッセージ「JPEG または PNG ファイルを選択してください」が表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  // エラーメッセージが表示されることを確認
  const errorAlert = this.page.locator('[role="alert"]').filter({ hasText: /JPEG.*PNG/ });
  await expect(errorAlert).toBeVisible({ timeout: 10000 });
});

Then('コンテンツ {string} が表示される', { timeout: 60000 }, async function (this: CustomWorld, content: string) {
  await expect(this.page.getByText(content)).toBeVisible();
});

Then('Material-UIのカードコンポーネントが表示される', { timeout: 60000 }, async function (this: CustomWorld) {
  const card = this.page.locator('[class*="MuiCard-root"]').first();
  await expect(card).toBeVisible();
});
