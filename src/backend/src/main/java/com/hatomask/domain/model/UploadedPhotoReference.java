package com.hatomask.domain.model;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UploadedPhotoReference(
        UUID photoId,
        OffsetDateTime expiresAt,
        MimeType mimeType,
        ImageDimensions dimensions,
        FileSizeBytes fileSizeBytes) {

    public UploadedPhotoReference {
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
    }

    public static UploadedPhotoReference create(
            UUID photoId,
            OffsetDateTime expiresAt,
            MimeType mimeType,
            ImageDimensions dimensions,
            FileSizeBytes fileSizeBytes,
            Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("clock is required");
        }
        OffsetDateTime now = OffsetDateTime.now(clock);
        if (expiresAt == null) {
            throw new IllegalArgumentException("expiresAt is required");
        }
        if (expiresAt.isBefore(now)) {
            throw new IllegalArgumentException("expiresAt must not be in the past");
        }

        return new UploadedPhotoReference(photoId, expiresAt, mimeType, dimensions, fileSizeBytes);
    }

    public static UploadedPhotoReference create(
            UUID photoId,
            OffsetDateTime expiresAt,
            MimeType mimeType,
            ImageDimensions dimensions,
            FileSizeBytes fileSizeBytes) {
        return create(photoId, expiresAt, mimeType, dimensions, fileSizeBytes, Clock.systemUTC());
    }
}
