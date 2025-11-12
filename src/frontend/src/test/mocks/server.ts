import { setupServer } from 'msw/node'
import { handlers } from './handlers'

// モックサーバーのセットアップ
export const server = setupServer(...handlers)
