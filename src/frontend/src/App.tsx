import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'
import {
  Container,
  Box,
  Typography,
  Card,
  CardContent,
  CircularProgress,
  Alert,
  Button,
  ThemeProvider,
  createTheme,
  CssBaseline,
} from '@mui/material'
import { useState, useEffect } from 'react'

interface HelloResponse {
  message: string
}

type UploadPhotoResponse = {
  mimeType: 'image/jpeg' | 'image/png'
  sizeBytes: number
  widthPx: number
  heightPx: number
}

type ProblemDetails = {
  type: string
  title: string
  status: number
  detail?: string
}

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
})

function App() {
  const [message, setMessage] = useState<string>('')
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string>('')

  const [photoError, setPhotoError] = useState<string>('')
  const [isUploadingPhoto, setIsUploadingPhoto] = useState<boolean>(false)
  const [selectedPhotoUrl, setSelectedPhotoUrl] = useState<string>('')
  const [selectedPhotoMeta, setSelectedPhotoMeta] = useState<UploadPhotoResponse | null>(null)

  useEffect(() => {
    fetch('/api/v1/hello')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Network response was not ok')
        }
        return response.json()
      })
      .then((data: HelloResponse) => {
        setMessage(data.message)
        setLoading(false)
      })
      .catch((error) => {
        console.error('Error fetching hello:', error)
        setError('バックエンドとの接続に失敗しました')
        setMessage('Hello, World from HatoMask Frontend! (Backend not available)')
        setLoading(false)
      })
  }, [])

  useEffect(() => {
    return () => {
      if (selectedPhotoUrl) {
        URL.revokeObjectURL(selectedPhotoUrl)
      }
    }
  }, [selectedPhotoUrl])

  const handleSelectPhotoClick = () => {
    const input = document.getElementById('photo-file-input') as HTMLInputElement | null
    input?.click()
  }

  const handlePhotoSelected = async (file: File | null) => {
    if (!file) {
      return
    }

    setPhotoError('')

    const validationError = await validateSelectedFile(file)
    if (validationError) {
      setPhotoError(validationError)
      return
    }

    setIsUploadingPhoto(true)

    let normalized: Blob
    try {
      normalized = await normalizeImageForPreviewAndProcessing(file)
    } catch {
      setPhotoError('画像のデコードに失敗しました')
      setIsUploadingPhoto(false)
      return
    }

    if (normalized.size > 10 * 1024 * 1024) {
      setPhotoError('ファイルサイズは10MB以下にしてください')
      setIsUploadingPhoto(false)
      return
    }

    try {
      const response = await uploadPhoto(normalized)

      const nextUrl = URL.createObjectURL(normalized)
      setSelectedPhotoMeta(response)
      setSelectedPhotoUrl((prev) => {
        if (prev) {
          URL.revokeObjectURL(prev)
        }
        return nextUrl
      })
    } catch (exception) {
      if (exception instanceof Error) {
        setPhotoError(exception.message)
      } else {
        setPhotoError('アップロードに失敗しました')
      }
    } finally {
      setIsUploadingPhoto(false)
    }
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="md">
        <Box
          sx={{
            minHeight: '100vh',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            py: 4,
          }}
        >
          <Typography variant="h2" component="h1" gutterBottom align="center">
            🕊️ HatoMask App
          </Typography>
          
          <Card sx={{ mt: 4, width: '100%', maxWidth: 600 }}>
            <CardContent>
              {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                  <CircularProgress />
                </Box>
              ) : (
                <>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <CheckCircleOutlineIcon color="success" sx={{ mr: 1 }} />
                    <Typography variant="h6" component="h2">
                      接続状態
                    </Typography>
                  </Box>
                  
                  <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
                    {message}
                  </Typography>
                  
                  {error && (
                    <Alert severity="warning" sx={{ mt: 2 }}>
                      {error}
                    </Alert>
                  )}
                </>
              )}
            </CardContent>
          </Card>

          <Card sx={{ mt: 4, width: '100%', maxWidth: 600 }}>
            <CardContent>
              <Typography variant="h6" component="h2" sx={{ mb: 2 }}>
                写真アップロード
              </Typography>

              {photoError && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {photoError}
                </Alert>
              )}

              <input
                id="photo-file-input"
                type="file"
                accept="image/jpeg,image/png"
                style={{ display: 'none' }}
                onChange={(event) => {
                  const file = event.target.files?.[0] ?? null
                  void handlePhotoSelected(file)
                  event.target.value = ''
                }}
              />

              <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', mb: 2, flexWrap: 'wrap' }}>
                <Button
                  variant="contained"
                  onClick={handleSelectPhotoClick}
                  disabled={isUploadingPhoto}
                >
                  写真を選択
                </Button>

                <Button
                  variant="outlined"
                  disabled={!selectedPhotoMeta}
                >
                  顔検出を実行
                </Button>

                {isUploadingPhoto && (
                  <Typography variant="body2" color="text.secondary">
                    処理中...
                  </Typography>
                )}
              </Box>

              <Box
                aria-label="プレビューエリア"
                sx={{
                  width: '100%',
                  height: 320,
                  border: 1,
                  borderColor: 'divider',
                  borderRadius: 1,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  overflow: 'hidden',
                  bgcolor: 'background.default',
                }}
              >
                {selectedPhotoUrl ? (
                  <img
                    alt="選択済み画像"
                    src={selectedPhotoUrl}
                    style={{ width: '100%', height: '100%', objectFit: 'contain' }}
                  />
                ) : (
                  <Typography variant="body2" color="text.secondary" align="center" sx={{ px: 2 }}>
                    写真を選択するとここにプレビューが表示されます
                  </Typography>
                )}
              </Box>
            </CardContent>
          </Card>
          
          <Typography variant="body2" color="text.secondary" sx={{ mt: 4 }}>
            写真にある顔をハトマスクに入れ替えるアプリケーション
          </Typography>
        </Box>
      </Container>
    </ThemeProvider>
  )
}

export default App

async function validateSelectedFile(file: File): Promise<string | null> {
  if (file.size > 10 * 1024 * 1024) {
    return 'ファイルサイズは10MB以下にしてください'
  }

  const mimeType = file.type || (await detectMimeType(file))
  if (mimeType !== 'image/jpeg' && mimeType !== 'image/png') {
    return 'JPEG または PNG ファイルを選択してください'
  }

  return null
}

async function detectMimeType(file: File): Promise<string> {
  const buffer = await file.slice(0, 16).arrayBuffer()
  const bytes = new Uint8Array(buffer)

  // JPEG: FF D8 FF
  if (bytes.length >= 3 && bytes[0] === 0xff && bytes[1] === 0xd8 && bytes[2] === 0xff) {
    return 'image/jpeg'
  }

  // PNG: 89 50 4E 47
  if (bytes.length >= 4 && bytes[0] === 0x89 && bytes[1] === 0x50 && bytes[2] === 0x4e && bytes[3] === 0x47) {
    return 'image/png'
  }

  return ''
}

async function uploadPhoto(blob: Blob): Promise<UploadPhotoResponse> {
  const formData = new FormData()
  formData.append('file', blob, blob.type === 'image/png' ? 'photo.png' : 'photo.jpg')

  const response = await fetch('/api/v1/photos', {
    method: 'POST',
    body: formData,
  })

  if (!response.ok) {
    const contentType = response.headers.get('content-type') ?? ''
    if (contentType.includes('application/problem+json')) {
      const problem = (await response.json()) as ProblemDetails
      throw new Error(problem.detail || 'アップロードに失敗しました')
    }
    throw new Error('アップロードに失敗しました')
  }

  return (await response.json()) as UploadPhotoResponse
}

async function normalizeImageForPreviewAndProcessing(file: File): Promise<Blob> {
  const mimeType = file.type || (await detectMimeType(file))

  if (mimeType !== 'image/jpeg') {
    // PNG等: Milestone 0では向きの正規化が不要なものはそのまま
    return file
  }

  const buffer = await file.arrayBuffer()
  const orientation = readJpegExifOrientation(new Uint8Array(buffer))
  if (orientation === 1) {
    return file
  }

  const imageBitmap = await createImageBitmap(new Blob([buffer], { type: 'image/jpeg' }))

  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    throw new Error('Canvas is not supported')
  }

  const shouldSwap = orientation === 5 || orientation === 6 || orientation === 7 || orientation === 8
  canvas.width = shouldSwap ? imageBitmap.height : imageBitmap.width
  canvas.height = shouldSwap ? imageBitmap.width : imageBitmap.height

  applyExifOrientationTransform(ctx, orientation, imageBitmap.width, imageBitmap.height)
  ctx.drawImage(imageBitmap, 0, 0)

  const blob = await new Promise<Blob>((resolve, reject) => {
    canvas.toBlob(
      (result) => {
        if (!result) {
          reject(new Error('Failed to create blob'))
          return
        }
        resolve(result)
      },
      'image/jpeg',
      0.92,
    )
  })

  return blob
}

function applyExifOrientationTransform(
  ctx: CanvasRenderingContext2D,
  orientation: number,
  width: number,
  height: number,
) {
  switch (orientation) {
    case 2: // Mirror horizontal
      ctx.translate(width, 0)
      ctx.scale(-1, 1)
      return
    case 3: // Rotate 180
      ctx.translate(width, height)
      ctx.rotate(Math.PI)
      return
    case 4: // Mirror vertical
      ctx.translate(0, height)
      ctx.scale(1, -1)
      return
    case 5: // Mirror horizontal and rotate 90 CW
      ctx.rotate(0.5 * Math.PI)
      ctx.scale(1, -1)
      return
    case 6: // Rotate 90 CW
      ctx.rotate(0.5 * Math.PI)
      ctx.translate(0, -height)
      return
    case 7: // Mirror horizontal and rotate 90 CCW
      ctx.rotate(-0.5 * Math.PI)
      ctx.scale(1, -1)
      ctx.translate(-width, 0)
      return
    case 8: // Rotate 90 CCW
      ctx.rotate(-0.5 * Math.PI)
      ctx.translate(-width, 0)
      return
    default:
      return
  }
}

function readJpegExifOrientation(bytes: Uint8Array): number {
  // 1: default
  if (bytes.length < 4 || bytes[0] !== 0xff || bytes[1] !== 0xd8) {
    return 1
  }

  let offset = 2
  while (offset + 4 < bytes.length) {
    if (bytes[offset] !== 0xff) {
      break
    }

    const marker = bytes[offset + 1]
    // EOI / SOS
    if (marker === 0xd9 || marker === 0xda) {
      break
    }

    const size = (bytes[offset + 2] << 8) + bytes[offset + 3]
    if (size < 2) {
      break
    }

    if (marker === 0xe1) {
      // APP1
      const app1Start = offset + 4
      if (app1Start + 6 <= bytes.length) {
        const exifHeader = String.fromCharCode(
          bytes[app1Start],
          bytes[app1Start + 1],
          bytes[app1Start + 2],
          bytes[app1Start + 3],
        )
        if (exifHeader === 'Exif') {
          const tiffOffset = app1Start + 6
          return readTiffOrientation(bytes, tiffOffset)
        }
      }
    }

    offset += 2 + size
  }

  return 1
}

function readTiffOrientation(bytes: Uint8Array, tiffOffset: number): number {
  if (tiffOffset + 8 > bytes.length) {
    return 1
  }

  const endianness = (bytes[tiffOffset] << 8) + bytes[tiffOffset + 1]
  const littleEndian = endianness === 0x4949

  const readUint16 = (offset: number) => {
    const a = bytes[offset]
    const b = bytes[offset + 1]
    return littleEndian ? (b << 8) + a : (a << 8) + b
  }
  const readUint32 = (offset: number) => {
    const a = bytes[offset]
    const b = bytes[offset + 1]
    const c = bytes[offset + 2]
    const d = bytes[offset + 3]
    return littleEndian
      ? (d << 24) + (c << 16) + (b << 8) + a
      : (a << 24) + (b << 16) + (c << 8) + d
  }

  const firstIfdOffset = readUint32(tiffOffset + 4)
  let ifdStart = tiffOffset + firstIfdOffset
  if (ifdStart + 2 > bytes.length) {
    return 1
  }

  const entries = readUint16(ifdStart)
  ifdStart += 2

  for (let i = 0; i < entries; i += 1) {
    const entryOffset = ifdStart + i * 12
    if (entryOffset + 12 > bytes.length) {
      return 1
    }
    const tag = readUint16(entryOffset)
    if (tag !== 0x0112) {
      continue
    }

    const valueOffset = entryOffset + 8
    const value = readUint16(valueOffset)
    return value || 1
  }

  return 1
}
