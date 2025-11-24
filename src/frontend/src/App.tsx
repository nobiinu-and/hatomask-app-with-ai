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
          <Box sx={{ mt: 4, display: 'flex', gap: 2, alignItems: 'center' }}>
            <input
              id="photo-input"
              type="file"
              accept="image/*"
              style={{ display: 'none' }}
            />
            <Button variant="contained" color="primary" onClick={() => {
              const input = document.getElementById('photo-input') as HTMLInputElement | null;
              input?.click();
            }}>
              写真を選択
            </Button>
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
