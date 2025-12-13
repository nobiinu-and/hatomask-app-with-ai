import { Box, CircularProgress } from '@mui/material'

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
    </Box>
  )
}
