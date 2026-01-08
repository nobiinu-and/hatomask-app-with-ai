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
import { useRef, useState, useEffect } from 'react'

interface HelloResponse {
  message: string
}

interface PhotoUploadResponse {
  photoId: string
}

interface FaceLandmarkDto {
  name: string
  x: number
  y: number
}

interface FaceBoundingBoxDto {
  xMin: number
  yMin: number
  width: number
  height: number
}

interface FaceDetectionResponse {
  result: {
    landmarks: FaceLandmarkDto[]
    boundingBox: FaceBoundingBoxDto
    confidence: number
  }
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
  const [uploading, setUploading] = useState<boolean>(false)
  const [detecting, setDetecting] = useState<boolean>(false)
  const [previewUrl, setPreviewUrl] = useState<string>('')
  const [photoId, setPhotoId] = useState<string>('')
  const [faceDetection, setFaceDetection] = useState<FaceDetectionResponse | null>(null)

  const previewContainerRef = useRef<HTMLDivElement | null>(null)
  const previewImageRef = useRef<HTMLImageElement | null>(null)
  const [renderedImageRect, setRenderedImageRect] = useState<
    | {
        offsetX: number
        offsetY: number
        width: number
        height: number
      }
    | null
  >(null)

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
    setPhotoId('')
    setFaceDetection(null)
    setRenderedImageRect(null)
  }

  const updateRenderedImageRect = () => {
    const container = previewContainerRef.current
    const img = previewImageRef.current
    if (!container || !img) {
      setRenderedImageRect(null)
      return
    }

    const { clientWidth: containerWidth, clientHeight: containerHeight } = container
    const { naturalWidth, naturalHeight } = img

    if (containerWidth <= 0 || containerHeight <= 0 || naturalWidth <= 0 || naturalHeight <= 0) {
      setRenderedImageRect(null)
      return
    }

    const scale = Math.min(containerWidth / naturalWidth, containerHeight / naturalHeight)
    const width = naturalWidth * scale
    const height = naturalHeight * scale
    const offsetX = (containerWidth - width) / 2
    const offsetY = (containerHeight - height) / 2

    setRenderedImageRect({ offsetX, offsetY, width, height })
  }

  const toOverlayX = (x01: number) => {
    if (!renderedImageRect) return 0
    return renderedImageRect.offsetX + x01 * renderedImageRect.width
  }

  const toOverlayY = (y01: number) => {
    if (!renderedImageRect) return 0
    return renderedImageRect.offsetY + y01 * renderedImageRect.height
  }

  useEffect(() => {
    if (!previewUrl) {
      setRenderedImageRect(null)
      return
    }

    updateRenderedImageRect()
    const onResize = () => {
      updateRenderedImageRect()
    }
    window.addEventListener('resize', onResize)
    return () => {
      window.removeEventListener('resize', onResize)
    }
  }, [previewUrl])

  const handlePhotoFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    setError('')
    setPhotoId('')
    setFaceDetection(null)

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
        setError('写真アップロードに失敗しました')
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

  const handleDetectFace = async () => {
    if (!photoId) return
    setError('')
    setFaceDetection(null)

    try {
      setDetecting(true)
      const response = await fetch(`/api/v1/photos/${photoId}/face-detections`, {
        method: 'POST',
      })

      if (!response.ok) {
        setError('顔検出に失敗しました')
        return
      }

      const data = (await response.json()) as FaceDetectionResponse
      setFaceDetection(data)
      updateRenderedImageRect()
    } catch {
      setError('顔検出に失敗しました')
    } finally {
      setDetecting(false)
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
                      ref={previewContainerRef}
                      sx={{
                        width: '100%',
                        aspectRatio: '16 / 9',
                        position: 'relative',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        overflow: 'hidden',
                      }}
                    >
                      {previewUrl ? (
                        <>
                          <Box
                            component="img"
                            src={previewUrl}
                            alt="選択した画像"
                            data-testid="photo-preview-image"
                            ref={previewImageRef}
                            onLoad={() => {
                              updateRenderedImageRect()
                            }}
                            sx={{
                              width: '100%',
                              height: '100%',
                              objectFit: 'contain',
                            }}
                          />
                          {faceDetection && renderedImageRect ? (
                            <>
                              <Box
                                data-testid="face-bounding-box"
                                sx={{
                                  position: 'absolute',
                                  left: toOverlayX(faceDetection.result.boundingBox.xMin),
                                  top: toOverlayY(faceDetection.result.boundingBox.yMin),
                                  width: faceDetection.result.boundingBox.width * renderedImageRect.width,
                                  height: faceDetection.result.boundingBox.height * renderedImageRect.height,
                                  border: 2,
                                  borderColor: 'primary.main',
                                  boxSizing: 'border-box',
                                  pointerEvents: 'none',
                                }}
                              />
                              {faceDetection.result.landmarks.map((lm) => (
                                <Box
                                  key={lm.name}
                                  data-testid="face-landmark-point"
                                  sx={{
                                    position: 'absolute',
                                    left: toOverlayX(lm.x),
                                    top: toOverlayY(lm.y),
                                    width: 10,
                                    height: 10,
                                    borderRadius: '50%',
                                    bgcolor: 'secondary.main',
                                    border: 2,
                                    borderColor: 'common.white',
                                    transform: 'translate(-50%, -50%)',
                                    boxSizing: 'border-box',
                                    pointerEvents: 'none',
                                  }}
                                />
                              ))}
                            </>
                          ) : null}
                        </>
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
                      disabled={!photoId || uploading || detecting}
                      onClick={() => {
                        void handleDetectFace()
                      }}
                    >
                      {detecting ? '顔検出中...' : '顔検出を実行'}
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
