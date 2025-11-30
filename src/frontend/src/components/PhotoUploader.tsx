import React, { useRef } from 'react'
import Button from '@mui/material/Button'

interface Props {
  // called when upload starts/ends
  setIsUploading?: (v: boolean) => void
  // called when upload succeeds with backend Photo response
  onUploadSuccess?: (photo: {
    id: string
    fileName: string
    fileSize: number
    mimeType: string
    createdAt: string
  }) => void
}

import { postPhoto } from '../services/api'

const MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB

const PhotoUploader: React.FC<Props> = ({ setIsUploading, onUploadSuccess }) => {
  const fileInputRef = useRef<HTMLInputElement | null>(null)

  const handleChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files && e.target.files[0]
    if (!file) return

    // basic validation: mime type and size
    if (!['image/jpeg', 'image/png'].includes(file.type)) {
      alert('サポートされているファイル形式は JPEG / PNG です')
      return
    }
    if (file.size > MAX_FILE_SIZE) {
      alert('ファイルサイズは10MB以下でしてください')
      return
    }

    try {
      setIsUploading && setIsUploading(true)

      const form = new FormData()
      form.append('file', file)

      const resp = await postPhoto(file)

      // notify parent about uploaded photo
      onUploadSuccess && onUploadSuccess(resp)
    } catch (err) {
      console.error('upload error', err)
      alert('アップロードに失敗しました')
    } finally {
      setIsUploading && setIsUploading(false)
    }
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative' }}>
      <input
        type="file"
        accept="image/jpeg, image/png"
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
