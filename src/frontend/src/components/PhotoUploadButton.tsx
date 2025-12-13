import { Button } from '@mui/material'
import { useRef } from 'react'

interface PhotoUploadButtonProps {
  onFileSelect?: (file: File) => void
}

export function PhotoUploadButton({ onFileSelect }: PhotoUploadButtonProps) {
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleClick = () => {
    fileInputRef.current?.click()
  }

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file && onFileSelect) {
      onFileSelect(file)
    }
  }

  return (
    <>
      <input
        ref={fileInputRef}
        type="file"
        accept="image/jpeg,image/png"
        style={{ display: 'none' }}
        onChange={handleFileChange}
      />
      <Button
        variant="contained"
        color="primary"
        onClick={handleClick}
        size="large"
      >
        写真を選択
      </Button>
    </>
  )
}
