package com.hatomask.application.usecase;

import com.hatomask.application.exception.FaceNotDetectedException;
import com.hatomask.domain.model.UploadedPhotoReference;
import com.hatomask.infrastructure.repository.InMemoryStoredPhotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DetectFaceLandmarksUseCase OpenCV スモークテスト")
class DetectFaceLandmarksUseCaseOpenCvSmokeTest {

    @Test
    @DisplayName("OpenCV のネイティブロードとカスケード読み込みに成功し、実行時にクラッシュしない")
    void openCvLoadAndExecuteDoesNotCrash() throws Exception {
        System.setProperty("hatomask.faceDetection.landmarkMethod", "DUMMY_5");
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        InMemoryStoredPhotoRepository repository = new InMemoryStoredPhotoRepository();
        UploadPhotoUseCase uploadPhotoUseCase = new UploadPhotoUseCase(clock, repository);

        byte[] pngBytes = createPngBytes(64, 64);
        MockMultipartFile file = new MockMultipartFile("file", "blank.png", "image/png", pngBytes);
        UploadedPhotoReference uploaded = uploadPhotoUseCase.execute(file);

        DetectFaceLandmarksUseCase useCase = new DetectFaceLandmarksUseCase(repository, clock);

        // blank image => no face (but OpenCV path should run)
        assertThrows(FaceNotDetectedException.class, () -> useCase.execute(uploaded.photoId()));
    }

    private static byte[] createPngBytes(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean ok = ImageIO.write(image, "png", out);
        if (!ok) {
            throw new IllegalStateException("failed to create png bytes");
        }
        return out.toByteArray();
    }
}
