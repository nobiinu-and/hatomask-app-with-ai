package com.hatomask.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 写真を表すEntityクラス.
 */
public class Photo {

    private static final int MAX_FILE_NAME_LENGTH = 255;

    private final UUID id;
    private final String originalFileName;
    private final ContentType contentType;
    private final FileSize fileSize;
    private final byte[] imageData;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * Photoエンティティを生成する.
     *
     * @param id               写真のUUID
     * @param originalFileName 元のファイル名
     * @param contentType      コンテンツタイプ
     * @param fileSize         ファイルサイズ
     * @param imageData        画像データ
     * @param createdAt        作成日時
     * @param updatedAt        更新日時
     * @throws IllegalArgumentException バリデーションエラーの場合
     */
    public Photo(UUID id, String originalFileName, ContentType contentType, FileSize fileSize,
                 byte[] imageData, LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateOriginalFileName(originalFileName);
        validateContentType(contentType);
        validateFileSize(fileSize);
        validateImageData(imageData);

        this.id = id;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.imageData = imageData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private void validateOriginalFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("originalFileName must not be null or empty");
        }
        if (fileName.length() > MAX_FILE_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "originalFileName must not exceed " + MAX_FILE_NAME_LENGTH + " characters");
        }
    }

    private void validateContentType(ContentType contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("contentType must not be null");
        }
    }

    private void validateFileSize(FileSize fileSize) {
        if (fileSize == null) {
            throw new IllegalArgumentException("fileSize must not be null");
        }
    }

    private void validateImageData(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("imageData must not be null or empty");
        }
    }

    /**
     * 写真のUUIDを取得する.
     *
     * @return UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 元のファイル名を取得する.
     *
     * @return ファイル名
     */
    public String getOriginalFileName() {
        return originalFileName;
    }

    /**
     * コンテンツタイプを取得する.
     *
     * @return ContentType
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * ファイルサイズを取得する.
     *
     * @return FileSize
     */
    public FileSize getFileSize() {
        return fileSize;
    }

    /**
     * 画像データを取得する.
     *
     * @return 画像バイト配列
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * 作成日時を取得する.
     *
     * @return 作成日時
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 更新日時を取得する.
     *
     * @return 更新日時
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Photo photo = (Photo) o;
        return Objects.equals(id, photo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Photo{" + "id=" + id + ", originalFileName='" + originalFileName + '\'' + ", contentType=" 
                + contentType + ", fileSize=" + fileSize + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt 
                + '}';
    }
}
