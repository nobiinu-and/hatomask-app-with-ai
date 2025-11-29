import path from 'path'

export function fixturePath(fileName: string) {
  return path.join(process.cwd(), 'fixtures', fileName)
}
