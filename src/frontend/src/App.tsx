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
  Divider,
} from '@mui/material'
import { useState, useEffect } from 'react'
import { PhotoUploadButton } from './components/PhotoUploadButton'

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
          
          {/* 既存の接続確認機能 */}
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

          <Divider sx={{ my: 4, width: '100%', maxWidth: 600 }} />

          {/* 写真アップロード・ダウンロード機能 */}
          <Card sx={{ width: '100%', maxWidth: 600 }}>
            <CardContent>
              <Typography variant="h6" component="h2" gutterBottom>
                写真のアップロード
              </Typography>
              
              <Box sx={{ mt: 3, textAlign: 'center' }}>
                <PhotoUploadButton />
                
                {/* プレビューエリア: ステップ5で実装 */}
                
                {/* ダウンロードボタン: ステップ6で実装 */}
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
