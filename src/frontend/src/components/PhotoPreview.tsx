import { Box, Button, CircularProgress } from '@mui/material'
import DownloadIcon from '@mui/icons-material/Download'

interface PhotoPreviewProps {
  photoId: string | null
  isLoading?: boolean
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

export function PhotoPreview({ photoId, isLoading }: PhotoPreviewProps) {
  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <CircularProgress />
      </Box>
    )
  }

  if (!photoId) {
    return null
  }

  const imageUrl = `${API_BASE_URL}/api/v1/photos/${photoId}`
  const downloadUrl = `${API_BASE_URL}/api/v1/photos/${photoId}?download=true`

  const handleDownload = () => {
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = `photo_${photoId}.jpg`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  return (
    <Box sx={{ mt: 3 }}>
      <img
        src={imageUrl}
        alt="アップロードされた写真のプレビュー"
        style={{
          maxWidth: '100%',
          maxHeight: '400px',
          objectFit: 'contain',
        }}
      />
      <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
        <Button
          variant="contained"
          startIcon={<DownloadIcon />}
          onClick={handleDownload}
          data-testid="download-button"
        >
          写真をダウンロード
        </Button>
      </Box>
    </Box>
  )
}
