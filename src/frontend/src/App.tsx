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
import { useState, useEffect } from 'react'

interface HelloResponse {
  message: string
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
  const [previewUrl, setPreviewUrl] = useState<string>('')

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

  const handlePhotoFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    setError('')

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
      }
    } catch {
      setError('写真アップロードに失敗しました')
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
                        <Box
                          component="img"
                          src={previewUrl}
                          alt="選択した画像"
                          data-testid="photo-preview-image"
                          sx={{
                            maxWidth: '100%',
                            maxHeight: '100%',
                            objectFit: 'contain',
                          }}
                        />
                      ) : (
                        <Typography variant="body2" color="text.secondary">
                          写真が選択されていません
                        </Typography>
                      )}
                    </Box>
                  </Box>

                  <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center' }}>
                    <Button variant="contained" disabled={!previewUrl || uploading}>
                      顔検出を実行
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
