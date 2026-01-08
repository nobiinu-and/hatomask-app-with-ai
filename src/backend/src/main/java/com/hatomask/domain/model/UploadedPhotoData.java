package com.hatomask.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UploadedPhotoData(
        UUID photoId,
        OffsetDateTime expiresAt,
        MimeType mimeType,
        ImageDimensions dimensions,
        FileSizeBytes fileSizeBytes,
        byte[] bytes) {

    public UploadedPhotoData {
        if (photoId == null) {
            throw new IllegalArgumentException("photoId is required");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }
        if (mimeType == null) {
            throw new IllegalArgumentException("mimeType is required");
        }
        if (dimensions == null) {
            throw new IllegalArgumentException("dimensions is required");
        }
        if (fileSizeBytes == null) {
            throw new IllegalArgumentException("fileSizeBytes is required");
        }
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("bytes is required");
        }
    }
}
