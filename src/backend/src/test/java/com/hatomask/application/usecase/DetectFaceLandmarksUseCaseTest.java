package com.hatomask.application.usecase;

import com.hatomask.application.exception.FaceNotDetectedException;
import com.hatomask.application.exception.PhotoNotFoundException;
import com.hatomask.domain.model.FileSizeBytes;
import com.hatomask.domain.model.FaceBoundingBox;
import com.hatomask.domain.model.FaceDetectionResult;
import com.hatomask.domain.model.FaceLandmark;
import com.hatomask.domain.model.ImageDimensions;
import com.hatomask.domain.model.MimeType;
import com.hatomask.domain.model.UploadedPhotoData;
import com.hatomask.domain.repository.UploadedPhotoDataRepository;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("DetectFaceLandmarksUseCase 単体テスト")
class DetectFaceLandmarksUseCaseTest {

    private static final String CASCADE_PATH_PROPERTY = "hatomask.opencv.cascadePath";
    private static final String CASCADE_PATH_ENV = "HATOMASK_OPENCV_CASCADE_PATH";

    private static final String LBF_MODEL_PATH_PROPERTY = "hatomask.opencv.lbfModelPath";
    private static final String LBF_MODEL_PATH_ENV = "HATOMASK_OPENCV_LBF_MODEL_PATH";

    private static Path findRepoRoot() {
        Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        for (int i = 0; i < 8; i++) {
            if (Files.exists(dir.resolve("e2e/fixtures/face-multiple.png"))) {
                return dir;
            }
            Path parent = dir.getParent();
            if (parent == null) {
                break;
            }
            dir = parent;
        }

        // devcontainer でのデフォルト。見つからない場合の最終フォールバック。
        return Path.of("/workspaces/hatomask-app-with-ai");
    }

    private static Path fixtureFaceMultiplePng() {
        return findRepoRoot().resolve("e2e/fixtures/face-multiple.png");
    }

    private static String resolveLbfModelPathForTest() {
        String configured = System.getProperty(LBF_MODEL_PATH_PROPERTY);
        if (configured == null || configured.isBlank()) {
            configured = System.getenv(LBF_MODEL_PATH_ENV);
        }
        if (configured == null || configured.isBlank()) {
            configured = findRepoRoot().resolve("dev/opencv-models/lbfmodel.yaml").toString();
        }
        return configured;
    }

    private static CascadeClassifier loadCascadeClassifierForTest() {
        String configured = System.getProperty(CASCADE_PATH_PROPERTY);
        if (configured == null || configured.isBlank()) {
            configured = System.getenv(CASCADE_PATH_ENV);
        }

        if (configured == null || configured.isBlank()) {
            String[] candidates = {
                    "/usr/share/opencv4/haarcascades/haarcascade_frontalface_default.xml",
                    "/usr/local/share/opencv/haarcascades/haarcascade_frontalface_default.xml",
                    "/usr/share/opencv/haarcascades/haarcascade_frontalface_default.xml"
            };
            for (String candidate : candidates) {
                if (Files.exists(Path.of(candidate))) {
                    configured = candidate;
                    break;
                }
            }
        }

        if (configured == null || configured.isBlank()) {
            throw new IllegalStateException(
                    "cascade file not found for test. Set -D" + CASCADE_PATH_PROPERTY + " or " + CASCADE_PATH_ENV);
        }

        if (!Files.exists(Path.of(configured))) {
            throw new IllegalStateException("cascade file not found: " + configured);
        }

        CascadeClassifier classifier = new CascadeClassifier(configured);
        if (classifier.empty()) {
            throw new IllegalStateException("failed to load cascade classifier: " + configured);
        }

        return classifier;
    }

    @Test
    @DisplayName("execute() は face-multiple.png から DUMMY_5 の検出結果を返す")
    void execute_FixturePng_Dummy5_Succeeds() throws Exception {
        UploadedPhotoDataRepository repo = mock(UploadedPhotoDataRepository.class);
        UUID photoId = UUID.randomUUID();

        Path fixturePath = fixtureFaceMultiplePng();
        Assumptions.assumeTrue(Files.exists(fixturePath), "fixture not found: " + fixturePath);

        byte[] bytes = Files.readAllBytes(fixturePath);
        UploadedPhotoData data = new UploadedPhotoData(
                photoId,
                OffsetDateTime.parse("2026-01-07T00:05:00Z"),
                MimeType.IMAGE_PNG,
                new ImageDimensions(1, 1),
                new FileSizeBytes(bytes.length),
                bytes);

        when(repo.findByPhotoId(photoId)).thenReturn(Optional.of(data));

        CascadeClassifier classifier = loadCascadeClassifierForTest();
        DetectFaceLandmarksUseCase useCase = new DetectFaceLandmarksUseCase(
                repo,
                classifier,
                DetectFaceLandmarksUseCase.LandmarkMethod.DUMMY_5,
                null);

        FaceDetectionResult result = useCase.execute(photoId);

        assertThat(result.confidence()).isEqualTo(0.5);
        assertThat(result.landmarks()).hasSize(5);
        assertThat(result.landmarks())
                .extracting(FaceLandmark::name)
                .containsExactly("left_eye", "right_eye", "nose", "left_mouth", "right_mouth");

        FaceBoundingBox bb = result.boundingBox();
        double xMax = bb.xMin() + bb.width();
        double yMax = bb.yMin() + bb.height();

        assertThat(bb.xMin()).isBetween(0.0, 1.0);
        assertThat(bb.yMin()).isBetween(0.0, 1.0);
        assertThat(bb.width()).isBetween(0.0, 1.0);
        assertThat(bb.height()).isBetween(0.0, 1.0);
        assertThat(xMax).isBetween(0.0, 1.0);
        assertThat(yMax).isBetween(0.0, 1.0);

        double eps = 1e-9;
        for (FaceLandmark lm : result.landmarks()) {
            assertThat(lm.x()).isBetween(bb.xMin() - eps, xMax + eps);
            assertThat(lm.y()).isBetween(bb.yMin() - eps, yMax + eps);
        }
    }

    @Test
    @DisplayName("execute() は face-multiple.png から LBF_68 の検出結果(68点)を返す")
    void execute_FixturePng_Lbf68_Succeeds() throws Exception {
        UploadedPhotoDataRepository repo = mock(UploadedPhotoDataRepository.class);
        UUID photoId = UUID.randomUUID();

        Path fixturePath = fixtureFaceMultiplePng();
        Assumptions.assumeTrue(Files.exists(fixturePath), "fixture not found: " + fixturePath);

        String lbfModelPath = resolveLbfModelPathForTest();
        Assumptions.assumeTrue(Files.exists(Path.of(lbfModelPath)), "LBF model not found: " + lbfModelPath);

        byte[] bytes = Files.readAllBytes(fixturePath);
        UploadedPhotoData data = new UploadedPhotoData(
                photoId,
                OffsetDateTime.parse("2026-01-07T00:05:00Z"),
                MimeType.IMAGE_PNG,
                new ImageDimensions(1, 1),
                new FileSizeBytes(bytes.length),
                bytes);

        when(repo.findByPhotoId(photoId)).thenReturn(Optional.of(data));

        CascadeClassifier classifier = loadCascadeClassifierForTest();
        DetectFaceLandmarksUseCase useCase = new DetectFaceLandmarksUseCase(
                repo,
                classifier,
                DetectFaceLandmarksUseCase.LandmarkMethod.LBF_68,
                lbfModelPath);

        FaceDetectionResult result = useCase.execute(photoId);

        assertThat(result.confidence()).isEqualTo(0.5);
        assertThat(result.landmarks()).hasSize(68);
        assertThat(result.landmarks().get(0).name()).isEqualTo("point_0");
        assertThat(result.landmarks().get(67).name()).isEqualTo("point_67");

        FaceBoundingBox bb = result.boundingBox();
        double xMax = bb.xMin() + bb.width();
        double yMax = bb.yMin() + bb.height();

        assertThat(bb.xMin()).isBetween(0.0, 1.0);
        assertThat(bb.yMin()).isBetween(0.0, 1.0);
        assertThat(bb.width()).isBetween(0.0, 1.0);
        assertThat(bb.height()).isBetween(0.0, 1.0);
        assertThat(xMax).isBetween(0.0, 1.0);
        assertThat(yMax).isBetween(0.0, 1.0);

        for (FaceLandmark lm : result.landmarks()) {
            assertThat(lm.x()).isBetween(0.0, 1.0);
            assertThat(lm.y()).isBetween(0.0, 1.0);
        }
    }

    @Test
    @DisplayName("execute() は存在しない photoId の場合 PhotoNotFoundException")
    void execute_UnknownPhotoId_Throws() {
        UploadedPhotoDataRepository repo = mock(UploadedPhotoDataRepository.class);
        UUID photoId = UUID.randomUUID();
        when(repo.findByPhotoId(photoId)).thenReturn(Optional.empty());

        DetectFaceLandmarksUseCase useCase = new DetectFaceLandmarksUseCase(
                repo,
                null,
                DetectFaceLandmarksUseCase.LandmarkMethod.DUMMY_5,
                null);

        assertThatThrownBy(() -> useCase.execute(photoId))
                .isInstanceOf(PhotoNotFoundException.class);
    }

    @Test
    @DisplayName("execute() は画像デコードに失敗する場合 FaceNotDetectedException")
    void execute_DecodeFail_Throws() {
        UploadedPhotoDataRepository repo = mock(UploadedPhotoDataRepository.class);
        UUID photoId = UUID.randomUUID();

        // UploadedPhotoData は record のため、最低限の値を満たすインスタンスを作る
        // ただしこのテストでは decodeImage() が失敗することを期待する
        com.hatomask.domain.model.UploadedPhotoData data = new com.hatomask.domain.model.UploadedPhotoData(
                photoId,
                java.time.OffsetDateTime.parse("2026-01-07T00:05:00Z"),
                MimeType.IMAGE_JPEG,
                new ImageDimensions(2, 3),
                new FileSizeBytes(3),
                new byte[] { 1, 2, 3 });

        when(repo.findByPhotoId(photoId)).thenReturn(Optional.of(data));

        DetectFaceLandmarksUseCase useCase = new DetectFaceLandmarksUseCase(
                repo,
                null,
                DetectFaceLandmarksUseCase.LandmarkMethod.DUMMY_5,
                null);

        assertThatThrownBy(() -> useCase.execute(photoId))
                .isInstanceOf(FaceNotDetectedException.class);
    }
}
