import React from 'react'
import ReactDOM from 'react-dom/client'

import App from './App.tsx'
import './index.css'

// Start MSW in development so /api/v1/photos is handled by our mock handler
// Start MSW in development or when explicitly enabled via Vite env var
const mswEnabled = Boolean(import.meta.env.DEV)
if (mswEnabled) {
  // eslint-disable-next-line @typescript-eslint/no-floating-promises
  import('./mocks/browser').then(({ worker }) => {
    // Start the worker with a service worker URL and graceful unhandled request handling.
    // NOTE: You must run `npx msw init <public_dir>` (e.g. `public/`) to generate
    // the `mockServiceWorker.js` file that the browser worker will register.
    const swOptions = {
      serviceWorker: {
        url: '/mockServiceWorker.js',
      },
      onUnhandledRequest: 'bypass',
    }
    worker.start(swOptions).then(() => console.info('[MSW] worker started'))
  })
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
