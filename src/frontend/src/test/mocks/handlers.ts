import { http, HttpResponse } from 'msw'

export const handlers = [
  // GET /api/v1/hello のモック
  http.get('/api/v1/hello', () => {
    return HttpResponse.json({
      message: 'Hello, World from HatoMask Backend!',
    })
  }),
]
