import { useState } from 'react'
import { PhotoResponse } from '../types/photo'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

export function usePhotoUpload() {
  const [uploadedPhoto, setUploadedPhoto] = useState<PhotoResponse | null>(null)
  const [isUploading, setIsUploading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const uploadPhoto = async (file: File) => {
    setIsUploading(true)
    setError(null)

    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await fetch(`${API_BASE_URL}/api/v1/photos`, {
        method: 'POST',
        body: formData,
      })

      if (!response.ok) {
        const errorData = await response.json().catch(() => null)
        throw new Error(errorData?.detail || 'アップロードに失敗しました')
      }

      const data: PhotoResponse = await response.json()
      setUploadedPhoto(data)
      return data
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'アップロードに失敗しました'
      setError(errorMessage)
      throw err
    } finally {
      setIsUploading(false)
    }
  }

  return {
    uploadedPhoto,
    isUploading,
    error,
    uploadPhoto,
  }
}
