import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'
import {
  Container,
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Alert,
  ThemeProvider,
  createTheme,
  CssBaseline,
} from '@mui/material'
import { useState, useEffect, useRef } from 'react'

interface HelloResponse {
  message: string
}

interface PhotoUploadResponse {
  photoId: string
  mimeType: string
  fileSizeBytes: number
  dimensions: { width: number; height: number }
  expiresAt: string
}

interface ProblemDetails {
  type: string
  title: string
  status: number
  detail?: string
}

interface FaceLandmarkDto {
  x: number
  y: number
}

interface FaceBoundingBoxDto {
  xMin: number
  yMin: number
  width: number
  height: number
}

interface FaceDetectionResultDto {
  landmarks: FaceLandmarkDto[]
  boundingBox: FaceBoundingBoxDto
  confidence?: number
}

interface FaceDetectionResponseDto {
  result: FaceDetectionResultDto
}

type FaceDetectionStatus = 'idle' | 'loading' | 'success' | 'error'

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
  const [uploading, setUploading] = useState<boolean>(false)
  const [previewUrl, setPreviewUrl] = useState<string>('')
  const [photoId, setPhotoId] = useState<string>('')
  const [faceDetectionStatus, setFaceDetectionStatus] = useState<FaceDetectionStatus>('idle')
  const [faceDetectionError, setFaceDetectionError] = useState<string>('')
  const [faceDetectionResult, setFaceDetectionResult] = useState<FaceDetectionResultDto | null>(
    null,
  )

  const imgRef = useRef<HTMLImageElement | null>(null)
  const canvasRef = useRef<HTMLCanvasElement | null>(null)

  const maxFileSizeBytes = 10 * 1024 * 1024
  const allowedMimeTypes = ['image/jpeg', 'image/png']

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

  const clearPreview = () => {
    setPreviewUrl((prev) => {
      if (prev) URL.revokeObjectURL(prev)
      return ''
    })
  }

  const resetFaceDetection = () => {
    setFaceDetectionStatus('idle')
    setFaceDetectionError('')
    setFaceDetectionResult(null)
  }

  const drawLandmarks = () => {
    const img = imgRef.current
    const canvas = canvasRef.current
    if (!img || !canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const width = img.clientWidth
    const height = img.clientHeight

    canvas.width = width
    canvas.height = height
    canvas.style.width = `${width}px`
    canvas.style.height = `${height}px`

    ctx.clearRect(0, 0, width, height)

    if (!faceDetectionResult) return

    const dotRadius = 3
    ctx.fillStyle = theme.palette.secondary.main

    for (const p of faceDetectionResult.landmarks) {
      const x = p.x * width
      const y = p.y * height
      ctx.beginPath()
      ctx.arc(x, y, dotRadius, 0, Math.PI * 2)
      ctx.fill()
    }
  }

  const handleDetectFace = async () => {
    if (!photoId) return

    setFaceDetectionError('')
    setFaceDetectionStatus('loading')
    setFaceDetectionResult(null)

    try {
      const response = await fetch(`/api/v1/photos/${photoId}/face-detections`, { method: 'POST' })

      if (!response.ok) {
        let detail = ''
        try {
          const problem = (await response.json()) as ProblemDetails
          detail = problem.detail ?? ''
        } catch {
          detail = ''
        }

        setFaceDetectionStatus('error')
        setFaceDetectionError(detail || '顔を検出できませんでした')
        return
      }

      const data = (await response.json()) as FaceDetectionResponseDto
      setFaceDetectionResult(data.result)
      setFaceDetectionStatus('success')
    } catch {
      setFaceDetectionStatus('error')
      setFaceDetectionError('顔検出に失敗しました')
    }
  }

  useEffect(() => {
    drawLandmarks()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [faceDetectionResult, previewUrl])

  const handlePhotoFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    setError('')
    setPhotoId('')
    resetFaceDetection()

    if (file.size > maxFileSizeBytes) {
      setError('ファイルサイズは10MB以下にしてください')
      clearPreview()
      event.target.value = ''
      return
    }

    if (!allowedMimeTypes.includes(file.type)) {
      setError('JPEG または PNG ファイルを選択してください')
      clearPreview()
      event.target.value = ''
      return
    }

    try {
      setUploading(true)
      const objectUrl = URL.createObjectURL(file)
      setPreviewUrl((prev) => {
        if (prev) URL.revokeObjectURL(prev)
        return objectUrl
      })

      const formData = new FormData()
      formData.append('file', file)

      const response = await fetch('/api/v1/photos', {
        method: 'POST',
        body: formData,
      })

      if (!response.ok) {
        let detail = ''
        try {
          const problem = (await response.json()) as ProblemDetails
          detail = problem.detail ?? ''
        } catch {
          detail = ''
        }

        setError(detail || '写真アップロードに失敗しました')
        setPhotoId('')
        return
      }

      const data = (await response.json()) as PhotoUploadResponse
      setPhotoId(data.photoId)
    } catch {
      setError('写真アップロードに失敗しました')
      setPhotoId('')
    } finally {
      setUploading(false)
      event.target.value = ''
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

                  <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center' }}>
                    <Button variant="contained" component="label">
                      写真を選択
                      <input
                        hidden
                        type="file"
                        accept="image/jpeg,image/png"
                        data-testid="photo-file-input"
                        disabled={uploading}
                        onChange={(event) => {
                          void handlePhotoFileChange(event)
                        }}
                      />
                    </Button>
                  </Box>

                  <Box sx={{ mt: 3 }}>
                    <Typography variant="subtitle1" component="h3" sx={{ mb: 1 }}>
                      プレビューエリア
                    </Typography>
                    <Box
                      sx={{
                        width: '100%',
                        aspectRatio: '16 / 9',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        overflow: 'hidden',
                      }}
                    >
                      {previewUrl ? (
                        <Box sx={{ position: 'relative', display: 'inline-block' }}>
                          <Box
                            component="img"
                            ref={imgRef}
                            src={previewUrl}
                            alt="選択した画像"
                            data-testid="photo-preview-image"
                            onLoad={() => {
                              drawLandmarks()
                            }}
                            sx={{
                              maxWidth: '100%',
                              maxHeight: '100%',
                              objectFit: 'contain',
                              display: 'block',
                            }}
                          />
                          <Box
                            component="canvas"
                            ref={canvasRef}
                            data-testid="face-landmarks-overlay"
                            sx={{
                              position: 'absolute',
                              top: 0,
                              left: 0,
                              pointerEvents: 'none',
                            }}
                          />
                        </Box>
                      ) : (
                        <Typography variant="body2" color="text.secondary">
                          写真が選択されていません
                        </Typography>
                      )}
                    </Box>
                  </Box>

                  <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center' }}>
                    <Button
                      variant="contained"
                      disabled={!previewUrl || uploading || !photoId || faceDetectionStatus === 'loading'}
                      onClick={() => {
                        void handleDetectFace()
                      }}
                    >
                      顔検出を実行
                    </Button>
                  </Box>

                  <Box sx={{ mt: 2, textAlign: 'center' }}>
                    <Typography variant="body2" color="text.secondary">
                      状態:{' '}
                      {faceDetectionStatus === 'idle'
                        ? '未実行'
                        : faceDetectionStatus === 'loading'
                          ? '検出中'
                          : faceDetectionStatus === 'success'
                            ? '検出成功'
                            : '検出失敗'}
                    </Typography>
                  </Box>

                  {faceDetectionStatus === 'loading' && (
                    <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
                      <CircularProgress size={24} />
                    </Box>
                  )}

                  {faceDetectionError && (
                    <Alert severity="error" sx={{ mt: 2 }}>
                      {faceDetectionError}
                    </Alert>
                  )}

                  <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center', gap: 2 }}>
                    <Button
                      variant="outlined"
                      disabled={!photoId || uploading || faceDetectionStatus === 'loading'}
                      onClick={() => {
                        void handleDetectFace()
                      }}
                    >
                      再検出
                    </Button>
                    <Button
                      variant="outlined"
                      disabled={uploading || faceDetectionStatus === 'loading'}
                      onClick={() => {
                        clearPreview()
                        setPhotoId('')
                        resetFaceDetection()
                      }}
                    >
                      別の写真を選ぶ
                    </Button>
                  </Box>
                </>
              )}
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
