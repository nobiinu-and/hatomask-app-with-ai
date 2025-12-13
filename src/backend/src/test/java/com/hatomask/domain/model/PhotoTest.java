package com.hatomask.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhotoTest {

    @Nested
    class 有効なプロパティを受け取るとPhotoエンティティが生成される {

        @Test
        void sampleJpgとContentTypeとFileSizeとバイト配列を受け取ると生成される() {
            byte[] imageData = new byte[]{1, 2, 3, 4, 5};
            ContentType contentType = new ContentType("image/jpeg");
            FileSize fileSize = new FileSize(5_242_880L);

            Photo photo = new Photo(UUID.randomUUID(), "sample.jpg", contentType, fileSize, imageData, 
                    LocalDateTime.now(), LocalDateTime.now());

            assertNotNull(photo);
        }

        @Test
        void photoPngとContentTypeとFileSizeとバイト配列を受け取ると生成される() {
            byte[] imageData = new byte[]{10, 20, 30};
            ContentType contentType = new ContentType("image/png");
            FileSize fileSize = new FileSize(1024L);

            Photo photo = new Photo(UUID.randomUUID(), "photo.png", contentType, fileSize, imageData,
                    LocalDateTime.now(), LocalDateTime.now());

            assertNotNull(photo);
        }

        @Test
        void getIdはUUIDを返す() {
            UUID id = UUID.randomUUID();
            byte[] imageData = new byte[]{1, 2, 3};
            Photo photo = new Photo(id, "test.jpg", new ContentType("image/jpeg"), 
                    new FileSize(1024L), imageData, LocalDateTime.now(), LocalDateTime.now());

            assertEquals(id, photo.getId());
        }

        @Test
        void getOriginalFileNameは設定したファイル名を返す() {
            byte[] imageData = new byte[]{1, 2, 3};
            Photo photo = new Photo(UUID.randomUUID(), "test.jpg", new ContentType("image/jpeg"),
                    new FileSize(1024L), imageData, LocalDateTime.now(), LocalDateTime.now());

            assertEquals("test.jpg", photo.getOriginalFileName());
        }

        @Test
        void getContentTypeは設定したContentTypeを返す() {
            ContentType contentType = new ContentType("image/jpeg");
            byte[] imageData = new byte[]{1, 2, 3};
            Photo photo = new Photo(UUID.randomUUID(), "test.jpg", contentType,
                    new FileSize(1024L), imageData, LocalDateTime.now(), LocalDateTime.now());

            assertEquals(contentType, photo.getContentType());
        }

        @Test
        void getFileSizeは設定したFileSizeを返す() {
            FileSize fileSize = new FileSize(5_242_880L);
            byte[] imageData = new byte[]{1, 2, 3};
            Photo photo = new Photo(UUID.randomUUID(), "test.jpg", new ContentType("image/jpeg"),
                    fileSize, imageData, LocalDateTime.now(), LocalDateTime.now());

            assertEquals(fileSize, photo.getFileSize());
        }

        @Test
        void getImageDataは設定したバイト配列を返す() {
            byte[] imageData = new byte[]{1, 2, 3, 4, 5};
            Photo photo = new Photo(UUID.randomUUID(), "test.jpg", new ContentType("image/jpeg"),
                    new FileSize(1024L), imageData, LocalDateTime.now(), LocalDateTime.now());

            assertArrayEquals(imageData, photo.getImageData());
        }

        @Test
        void getCreatedAtは現在時刻を返す() {
            LocalDateTime now = LocalDateTime.now();
            byte[] imageData = new byte[]{1, 2, 3};
            Photo photo = new Photo(UUID.randomUUID(), "test.jpg", new ContentType("image/jpeg"),
                    new FileSize(1024L), imageData, now, LocalDateTime.now());

            assertNotNull(photo.getCreatedAt());
            assertTrue(photo.getCreatedAt().isEqual(now) || photo.getCreatedAt().isAfter(now.minusSeconds(1)));
        }

        @Test
        void getUpdatedAtは現在時刻を返す() {
            LocalDateTime now = LocalDateTime.now();
            byte[] imageData = new byte[]{1, 2, 3};
            Photo photo = new Photo(UUID.randomUUID(), "test.jpg", new ContentType("image/jpeg"),
                    new FileSize(1024L), imageData, LocalDateTime.now(), now);

            assertNotNull(photo.getUpdatedAt());
            assertTrue(photo.getUpdatedAt().isEqual(now) || photo.getUpdatedAt().isAfter(now.minusSeconds(1)));
        }
    }

    @Nested
    class バリデーションエラーでエンティティ生成が失敗する {

        @Test
        void 空文字列のoriginalFileNameを受け取ると例外が発生する() {
            byte[] imageData = new byte[]{1, 2, 3};
            assertThrows(IllegalArgumentException.class, () -> {
                new Photo(UUID.randomUUID(), "", new ContentType("image/jpeg"),
                        new FileSize(1024L), imageData, LocalDateTime.now(), LocalDateTime.now());
            });
        }

        @Test
        void nullのoriginalFileNameを受け取ると例外が発生する() {
            byte[] imageData = new byte[]{1, 2, 3};
            assertThrows(IllegalArgumentException.class, () -> {
                new Photo(UUID.randomUUID(), null, new ContentType("image/jpeg"),
                        new FileSize(1024L), imageData, LocalDateTime.now(), LocalDateTime.now());
            });
        }

        @Test
        void _255文字を超えるoriginalFileNameを受け取ると例外が発生する() {
            String longFileName = "a".repeat(256);
            byte[] imageData = new byte[]{1, 2, 3};
            assertThrows(IllegalArgumentException.class, () -> {
                new Photo(UUID.randomUUID(), longFileName, new ContentType("image/jpeg"),
                        new FileSize(1024L), imageData, LocalDateTime.now(), LocalDateTime.now());
            });
        }

        @Test
        void nullのcontentTypeを受け取ると例外が発生する() {
            byte[] imageData = new byte[]{1, 2, 3};
            assertThrows(IllegalArgumentException.class, () -> {
                new Photo(UUID.randomUUID(), "test.jpg", null,
                        new FileSize(1024L), imageData, LocalDateTime.now(), LocalDateTime.now());
            });
        }

        @Test
        void nullのfileSizeを受け取ると例外が発生する() {
            byte[] imageData = new byte[]{1, 2, 3};
            assertThrows(IllegalArgumentException.class, () -> {
                new Photo(UUID.randomUUID(), "test.jpg", new ContentType("image/jpeg"),
                        null, imageData, LocalDateTime.now(), LocalDateTime.now());
            });
        }

        @Test
        void 空配列のimageDataを受け取ると例外が発生する() {
            byte[] emptyData = new byte[0];
            assertThrows(IllegalArgumentException.class, () -> {
                new Photo(UUID.randomUUID(), "test.jpg", new ContentType("image/jpeg"),
                        new FileSize(1024L), emptyData, LocalDateTime.now(), LocalDateTime.now());
            });
        }

        @Test
        void nullのimageDataを受け取ると例外が発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Photo(UUID.randomUUID(), "test.jpg", new ContentType("image/jpeg"),
                        new FileSize(1024L), null, LocalDateTime.now(), LocalDateTime.now());
            });
        }
    }
}
