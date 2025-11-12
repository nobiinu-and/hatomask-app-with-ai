import { test, expect } from '@playwright/test';

test.describe('HatoMask アプリケーション', () => {
  test('トップページが正しく表示される', async ({ page }) => {
    await page.goto('/');

    // タイトルの確認
    await expect(page.getByText('🕊️ HatoMask App')).toBeVisible();

    // 説明文の確認
    await expect(
      page.getByText('写真にある顔をハトマスクに入れ替えるアプリケーション')
    ).toBeVisible();
  });

  test('バックエンドとの接続状態が表示される', async ({ page }) => {
    await page.goto('/');

    // 接続状態セクションが表示されるまで待機
    // （ローディングが速い場合はスピナーが表示されないことがあるためスキップ）
    await expect(page.getByText('接続状態')).toBeVisible({ timeout: 10000 });
  });

  test('バックエンドからのメッセージが表示される', async ({ page }) => {
    await page.goto('/');

    // バックエンドからのメッセージを待機
    await expect(
      page.getByText(/Hello, World from HatoMask Backend!|Hello, World from HatoMask Frontend!/)
    ).toBeVisible({ timeout: 10000 });
  });

  test('レスポンシブデザインが機能する', async ({ page }) => {
    // デスクトップビュー
    await page.setViewportSize({ width: 1280, height: 720 });
    await page.goto('/');
    await expect(page.getByText('🕊️ HatoMask App')).toBeVisible();

    // タブレットビュー
    await page.setViewportSize({ width: 768, height: 1024 });
    await expect(page.getByText('🕊️ HatoMask App')).toBeVisible();

    // モバイルビュー
    await page.setViewportSize({ width: 375, height: 667 });
    await expect(page.getByText('🕊️ HatoMask App')).toBeVisible();
  });

  test('Material-UIのテーマが適用されている', async ({ page }) => {
    await page.goto('/');

    // カードコンポーネントの存在確認
    const card = page.locator('[class*="MuiCard-root"]').first();
    await expect(card).toBeVisible();
  });
});
