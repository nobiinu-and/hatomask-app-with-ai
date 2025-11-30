import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'
import {
  Container,
  Box,
  Typography,
  Card,
  CardContent,
  CircularProgress,
  Alert,
  ThemeProvider,
  createTheme,
  CssBaseline,
} from '@mui/material'
import { useState, useEffect } from 'react'
import PhotoUploader from './components/PhotoUploader'

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
  const [uploadedPhoto, setUploadedPhoto] = useState<{
    id: string
    fileName: string
    fileSize: number
    mimeType: string
    createdAt: string
  } | null>(null)
  const [isUploading, setIsUploading] = useState<boolean>(false)
  // file input is handled by PhotoUploader component

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
          
          {/* 写真選択コンポーネント（PhotoUploader に切り出し） */}
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 3 }}>
            {/* PhotoUploader は input をオフスクリーンにしてボタンで開く実装を持つ */}
            {/* onFileSelected の最小実装: 選択されたファイル情報を保持してプレビューを表示する */}
            <PhotoUploader
              setIsUploading={setIsUploading}
              onUploadSuccess={(photo) => {
                setUploadedPhoto(photo)
              }}
            />

            {isUploading && (
              <Box sx={{ mt: 2 }}>
                <CircularProgress size={24} />
              </Box>
            )}

            {/* アップロード成功を示す最小プレビュー表示（テストは img[src*="/api/v1/photos/"] を探す） */}
            {uploadedPhoto && (
              <Box sx={{ mt: 2, width: '100%', maxWidth: 400 }}>
                <img
                  src={`/api/v1/photos/${uploadedPhoto.id}`}
                  alt={uploadedPhoto.fileName}
                  style={{ width: '100%', height: 'auto', maxHeight: 400, objectFit: 'contain' }}
                />
              </Box>
            )}
          </Box>

          <Typography variant="body2" color="text.secondary" sx={{ mt: 4 }}>
            写真にある顔をハトマスクに入れ替えるアプリケーション
          </Typography>
        </Box>
      </Container>
    </ThemeProvider>
  )
}

export default App
