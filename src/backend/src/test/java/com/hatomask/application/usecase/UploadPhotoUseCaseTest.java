package com.hatomask.application.usecase;

import com.hatomask.application.exception.ImageDecodingException;
import com.hatomask.application.exception.InvalidFileException;
import com.hatomask.application.exception.PayloadTooLargeException;
import com.hatomask.application.exception.UnsupportedMediaTypeException;
import com.hatomask.domain.model.FileSizeBytes;
import com.hatomask.domain.model.MimeType;
import com.hatomask.domain.model.UploadedPhotoReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UploadPhotoUseCase 単体テスト")
class UploadPhotoUseCaseTest {

    @Test
    @DisplayName("execute() は有効な PNG を受け取ると UploadedPhotoReference を返す")
    void execute_ValidPng_ReturnsReference() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);
        UploadPhotoUseCase useCase = new UploadPhotoUseCase(clock);

        byte[] pngBytes = createPngBytes(2, 3);
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", pngBytes);

        UploadedPhotoReference ref = useCase.execute(file);

        assertThat(ref.photoId()).isNotNull();
        assertThat(ref.mimeType()).isEqualTo(MimeType.IMAGE_PNG);
        assertThat(ref.fileSizeBytes().value()).isEqualTo(pngBytes.length);
        assertThat(ref.dimensions().width()).isEqualTo(2);
        assertThat(ref.dimensions().height()).isEqualTo(3);
        assertThat(ref.expiresAt()).isEqualTo(OffsetDateTime.parse("2026-01-07T00:05:00Z"));
    }

    @Test
    @DisplayName("execute() は file が null の場合、InvalidFileException が発生する")
    void execute_FileNull_Throws() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);
        UploadPhotoUseCase useCase = new UploadPhotoUseCase(clock);

        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(InvalidFileException.class);
    }

    @Test
    @DisplayName("execute() は empty file の場合、InvalidFileException が発生する")
    void execute_EmptyFile_Throws() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);
        UploadPhotoUseCase useCase = new UploadPhotoUseCase(clock);

        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> useCase.execute(file))
                .isInstanceOf(InvalidFileException.class);
    }

    @Test
    @DisplayName("execute() はサイズ超過の場合、PayloadTooLargeException が発生する")
    void execute_TooLarge_Throws() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);
        UploadPhotoUseCase useCase = new UploadPhotoUseCase(clock);

        byte[] bytes = new byte[(int) (FileSizeBytes.MAX_BYTES + 1)];
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", bytes);

        assertThatThrownBy(() -> useCase.execute(file))
                .isInstanceOf(PayloadTooLargeException.class);
    }

    @Test
    @DisplayName("execute() は未対応の contentType の場合、UnsupportedMediaTypeException が発生する")
    void execute_UnsupportedMediaType_Throws() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);
        UploadPhotoUseCase useCase = new UploadPhotoUseCase(clock);

        MockMultipartFile file = new MockMultipartFile("file", "photo.gif", "image/gif", new byte[] { 1, 2, 3 });

        assertThatThrownBy(() -> useCase.execute(file))
                .isInstanceOf(UnsupportedMediaTypeException.class);
    }

    @Test
    @DisplayName("execute() はデコードできない場合、ImageDecodingException が発生する")
    void execute_DecodeFail_Throws() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-07T00:00:00Z"), ZoneOffset.UTC);
        UploadPhotoUseCase useCase = new UploadPhotoUseCase(clock);

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[] { 1, 2, 3 });

        assertThatThrownBy(() -> useCase.execute(file))
                .isInstanceOf(ImageDecodingException.class);
    }

    private static byte[] createPngBytes(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        boolean ok = ImageIO.write(image, "png", out);
        assertThat(ok).isTrue();

        return out.toByteArray();
    }
}
