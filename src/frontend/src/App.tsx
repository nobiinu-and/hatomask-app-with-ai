import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'
import {
  Container,
  Box,
  Button,
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
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string | null>(null)
  const [uploading, setUploading] = useState<boolean>(false)
  const [uploaded, setUploaded] = useState<boolean>(false)
  const [uploadMessage, setUploadMessage] = useState<string | null>(null)

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

          {/* アップロードUI: 写真を選択ボタン（テストで使用） */}
          <Box sx={{ mt: 4, display: 'flex', gap: 2, alignItems: 'center', flexDirection: 'column' }}>
            <input
              id="photo-input"
              type="file"
              accept="image/*"
              style={{ display: 'none' }}
              onChange={(e) => {
                const files = (e.target as HTMLInputElement).files
                if (files && files[0]) {
                  const file = files[0]
                  setSelectedFile(file)
                  const url = URL.createObjectURL(file)
                  setPreviewUrl(url)
                }
              }}
            />
              <Box sx={{ display: 'flex', gap: 2 }}>
              <Button variant="contained" color="primary" onClick={() => {
                const input = document.getElementById('photo-input') as HTMLInputElement | null;
                input?.click();
              }}>
                写真を選択
              </Button>
              <Button variant="outlined" color="primary" disabled={!selectedFile || uploading} onClick={async () => {
                if (!selectedFile) return
                setUploading(true)
                setUploadMessage(null)
                try {
                  const fd = new FormData()
                  fd.append('file', selectedFile)
                  const resp = await fetch('/api/v1/photos/upload', {
                    method: 'POST',
                    body: fd,
                  })
                  const data = await resp.json().catch(() => ({}))
                  if (resp.ok) {
                    setUploaded(true)
                    setUploadMessage((data && data.message) ? data.message : 'アップロードに成功しました')
                  } else {
                    setUploadMessage((data && data.message) ? data.message : 'アップロードに失敗しました')
                  }
                } catch (e) {
                  console.error('upload error', e)
                  setUploadMessage('アップロード中にエラーが発生しました')
                } finally {
                  setUploading(false)
                }
              }}>
                アップロード
              </Button>
              <Button variant="contained" color="secondary" disabled={!uploaded} onClick={() => {
                if (!previewUrl) return
                const a = document.createElement('a')
                a.href = previewUrl
                a.download = selectedFile?.name ?? 'photo.jpg'
                document.body.appendChild(a)
                a.click()
                a.remove()
              }}>
                ダウンロード
              </Button>
            </Box>

            {uploading && (
              <Typography variant="body2" sx={{ mt: 1 }}>アップロード中...</Typography>
            )}

            {uploadMessage && (
              <Typography id="upload-success" variant="body1" color={uploaded ? 'success.main' : 'error.main'} sx={{ mt: 1 }}>
                {uploadMessage}
              </Typography>
            )}

            {previewUrl && (
              <Box sx={{ mt: 2 }}>
                <img id="photo-preview" src={previewUrl} alt="プレビュー" style={{ maxWidth: 300, maxHeight: 300 }} />
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
