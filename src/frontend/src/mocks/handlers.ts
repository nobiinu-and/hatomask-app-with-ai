// MSW v2 handlers using the `http` API
import { http, HttpResponse } from 'msw'

export const handlers = [
  http.post('/api/v1/photos', async (req: any) => {
    const id = typeof crypto !== 'undefined' && (crypto as any).randomUUID ? (crypto as any).randomUUID() : `local-${Date.now()}`

    let fileName = 'mocked-uploaded.jpg'
    let fileSize = 0
    let mimeType = 'image/jpeg'
    try {
      const form = await req.formData()
      const file = form.get('file') as any | null
      if (file) {
        fileName = file.name || fileName
        fileSize = file.size || 0
        mimeType = file.type || mimeType
      }
    } catch (e) {
      // ignore; some environments may not expose formData parsing
    }

    return HttpResponse.json(
      {
        id,
        fileName,
        fileSize,
        mimeType,
        createdAt: new Date().toISOString(),
      },
      { status: 201 }
    )
  }),
]

export default handlers
