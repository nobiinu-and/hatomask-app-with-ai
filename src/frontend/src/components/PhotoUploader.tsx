import React, { useRef } from 'react'
import Button from '@mui/material/Button'

interface Props {
  onFileSelected?: (file: File) => void
}

const PhotoUploader: React.FC<Props> = ({ onFileSelected }) => {
  const fileInputRef = useRef<HTMLInputElement | null>(null)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files && e.target.files[0]
    if (file && onFileSelected) onFileSelected(file)
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative' }}>
      <input
        type="file"
        accept="image/jpeg"
        aria-label="写真ファイル入力"
        ref={(el) => (fileInputRef.current = el)}
        onChange={handleChange}
        // Hide visually but keep accessible to automation
        style={{ position: 'absolute', left: '-9999px', width: '1px', height: '1px', overflow: 'hidden' }}
      />
      <Button
        variant="contained"
        color="primary"
        onClick={() => fileInputRef.current && fileInputRef.current.click()}
        aria-label="写真を選択ボタン"
      >
        写真を選択
      </Button>
    </div>
  )
}

export default PhotoUploader
