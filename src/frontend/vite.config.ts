/// <reference types="vitest" />
import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // loadEnv を使って VITE_* 環境変数を安全に読み込む
  const env = loadEnv(mode, process.cwd(), '')
  const apiProxy = (env.VITE_API_PROXY as string) || 'http://localhost:8080'

  return {
    plugins: [react()],
    server: {
      port: 3000,
      proxy: {
        '/api': {
          target: apiProxy,
          changeOrigin: true,
        },
      },
    },
    test: {
      globals: true,
      environment: 'jsdom',
      setupFiles: './src/test/setup.ts',
      css: true,
      coverage: {
        provider: 'v8',
        reporter: ['text', 'json', 'html', 'lcov'],
        exclude: [
          'node_modules/',
          'src/test/',
          '**/*.d.ts',
          '**/*.config.*',
          '**/mockData',
          '**/*.test.{ts,tsx}',
          '*.config.{js,ts,cjs,mjs}',
          '.eslintrc.{js,cjs}',
          'src/main.tsx', // Entry point - not typically tested
        ],
      }
    }
  }
})