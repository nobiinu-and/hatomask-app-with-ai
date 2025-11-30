export interface Photo {
  id: string
  fileName: string
  fileSize: number
  mimeType: string
  createdAt: string
}

export async function postPhoto(file: File): Promise<Photo> {
  const form = new FormData()
  form.append('file', file)

  const res = await fetch('/api/v1/photos', {
    method: 'POST',
    body: form,
  })

  if (!res.ok) {
    const text = await res.text().catch(() => '')
    throw new Error(`Upload failed: ${res.status} ${text}`)
  }

  const data = await res.json().catch(() => null)
  if (!data) throw new Error('Upload returned empty response')
  return data as Photo
}

export default { postPhoto }
