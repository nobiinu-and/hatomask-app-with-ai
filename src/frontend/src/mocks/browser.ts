// Browser worker for MSW (msw v2)
import { setupWorker } from 'msw/browser'
import handlers from './handlers'

export const worker = setupWorker(...handlers)
