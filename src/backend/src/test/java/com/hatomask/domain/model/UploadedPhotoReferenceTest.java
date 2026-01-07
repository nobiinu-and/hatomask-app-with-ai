package com.hatomask.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UploadedPhotoReference 単体テスト")
class UploadedPhotoReferenceTest {

    @Test
    @DisplayName("create() は有効な入力で生成できる")
    void create_Valid_Creates() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);

        UploadedPhotoReference ref = UploadedPhotoReference.create(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                OffsetDateTime.parse("2026-01-07T00:01:00Z"),
                MimeType.IMAGE_JPEG,
                ImageDimensions.of(1920, 1080),
                FileSizeBytes.of(1234),
                clock);

        assertThat(ref.photoId()).isNotNull();
        assertThat(ref.expiresAt()).isEqualTo(OffsetDateTime.parse("2026-01-07T00:01:00Z"));
        assertThat(ref.mimeType()).isEqualTo(MimeType.IMAGE_JPEG);
        assertThat(ref.dimensions().width()).isEqualTo(1920);
        assertThat(ref.dimensions().height()).isEqualTo(1080);
        assertThat(ref.fileSizeBytes().value()).isEqualTo(1234);
    }

    @Test
    @DisplayName("create() は photoId が null の場合、例外が発生する")
    void create_PhotoIdNull_Throws() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);

        assertThatThrownBy(() -> UploadedPhotoReference.create(
                null,
                OffsetDateTime.parse("2026-01-07T00:01:00Z"),
                MimeType.IMAGE_JPEG,
                ImageDimensions.of(1, 1),
                FileSizeBytes.of(1),
                clock))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("create() は expiresAt が null の場合、例外が発生する")
    void create_ExpiresAtNull_Throws() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);

        assertThatThrownBy(() -> UploadedPhotoReference.create(
                UUID.randomUUID(),
                null,
                MimeType.IMAGE_JPEG,
                ImageDimensions.of(1, 1),
                FileSizeBytes.of(1),
                clock))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("create() は expiresAt が過去の場合、例外が発生する")
    void create_ExpiresAtPast_Throws() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);

        assertThatThrownBy(() -> UploadedPhotoReference.create(
                UUID.randomUUID(),
                OffsetDateTime.parse("2026-01-06T23:59:59Z"),
                MimeType.IMAGE_JPEG,
                ImageDimensions.of(1, 1),
                FileSizeBytes.of(1),
                clock))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
