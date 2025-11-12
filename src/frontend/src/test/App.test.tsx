import { describe, it, expect, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import App from '../App'
import { server } from './mocks/server'
import { http, HttpResponse } from 'msw'

describe('App コンポーネント', () => {
  it('ローディング中は CircularProgress が表示される', () => {
    render(<App />)
    expect(screen.getByRole('progressbar')).toBeInTheDocument()
  })

  it('バックエンドからのメッセージを表示する', async () => {
    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('Hello, World from HatoMask Backend!')).toBeInTheDocument()
    })
  })

  it('タイトルが表示される', () => {
    render(<App />)
    expect(screen.getByText('🕊️ HatoMask App')).toBeInTheDocument()
  })

  it('説明文が表示される', () => {
    render(<App />)
    expect(screen.getByText('写真にある顔をハトマスクに入れ替えるアプリケーション')).toBeInTheDocument()
  })

  it('接続状態セクションが表示される', async () => {
    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('接続状態')).toBeInTheDocument()
    })
  })

  it('APIエラー時にエラーメッセージとフォールバックメッセージを表示する', async () => {
    // エラーレスポンスをモック
    server.use(
      http.get('/api/v1/hello', () => {
        return HttpResponse.error()
      })
    )

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('バックエンドとの接続に失敗しました')).toBeInTheDocument()
      expect(
        screen.getByText('Hello, World from HatoMask Frontend! (Backend not available)')
      ).toBeInTheDocument()
    })
  })

  it('コンソールエラーが出力される（APIエラー時）', async () => {
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    server.use(
      http.get('/api/v1/hello', () => {
        return HttpResponse.error()
      })
    )

    render(<App />)

    await waitFor(() => {
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Error fetching hello:',
        expect.any(Error)
      )
    })

    consoleErrorSpy.mockRestore()
  })
})
