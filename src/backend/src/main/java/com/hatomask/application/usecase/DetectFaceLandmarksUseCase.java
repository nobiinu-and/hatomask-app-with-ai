package com.hatomask.application.usecase;

import com.hatomask.application.exception.FaceNotDetectedException;
import com.hatomask.application.exception.PhotoNotFoundException;
import com.hatomask.domain.model.FaceBoundingBox;
import com.hatomask.domain.model.FaceDetectionResult;
import com.hatomask.domain.model.FaceLandmark;
import com.hatomask.domain.model.UploadedPhotoData;
import com.hatomask.domain.repository.UploadedPhotoDataRepository;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Point2fVector;
import org.bytedeco.opencv.opencv_core.Point2fVectorVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_face.Facemark;
import org.bytedeco.opencv.opencv_face.FacemarkLBF;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.bytedeco.opencv.global.opencv_core.CV_8U;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.equalizeHist;

@Service
public class DetectFaceLandmarksUseCase {

    private static final String CASCADE_PATH_PROPERTY = "hatomask.opencv.cascadePath";
    private static final String CASCADE_PATH_ENV = "HATOMASK_OPENCV_CASCADE_PATH";

    private static final String LANDMARK_METHOD_PROPERTY = "hatomask.faceDetection.landmarkMethod";
    private static final String LANDMARK_METHOD_ENV = "HATOMASK_FACE_DETECTION_LANDMARK_METHOD";

    private static final String LBF_MODEL_PATH_PROPERTY = "hatomask.opencv.lbfModelPath";
    private static final String LBF_MODEL_PATH_ENV = "HATOMASK_OPENCV_LBF_MODEL_PATH";

    private static final double DEFAULT_CONFIDENCE = 0.5;

    private static final int EXPECTED_LBF_LANDMARK_COUNT = 68;

    private static final double LEFT_EYE_X_RATIO = 0.30;
    private static final double EYES_Y_RATIO = 0.35;
    private static final double RIGHT_EYE_X_RATIO = 0.70;
    private static final double NOSE_X_RATIO = 0.50;
    private static final double NOSE_Y_RATIO = 0.55;
    private static final double LEFT_MOUTH_X_RATIO = 0.35;
    private static final double MOUTH_Y_RATIO = 0.75;
    private static final double RIGHT_MOUTH_X_RATIO = 0.65;

    private final UploadedPhotoDataRepository uploadedPhotoDataRepository;
    private final CascadeClassifier classifier;
    private final LandmarkMethod landmarkMethod;

    private final String lbfModelPath;
    private volatile Facemark facemark;

    public DetectFaceLandmarksUseCase(UploadedPhotoDataRepository uploadedPhotoDataRepository) {
        this.uploadedPhotoDataRepository = uploadedPhotoDataRepository;
        this.landmarkMethod = readLandmarkMethod();
        this.classifier = loadCascadeClassifier();
        this.lbfModelPath = readLbfModelPath().orElse(null);
    }

    public FaceDetectionResult execute(UUID photoId) {
        Optional<UploadedPhotoData> dataOpt = uploadedPhotoDataRepository.findByPhotoId(photoId);
        UploadedPhotoData data = dataOpt.orElseThrow(() -> new PhotoNotFoundException("photo not found"));

        Mat decoded = decodeImage(data.bytes());
        int imageWidth = decoded.cols();
        int imageHeight = decoded.rows();

        Rect face = clampRectToImageBounds(detectLargestFace(decoded), imageWidth, imageHeight);

        FaceBoundingBox boundingBox = new FaceBoundingBox(
                face.x() / (double) imageWidth,
                face.y() / (double) imageHeight,
                face.width() / (double) imageWidth,
                face.height() / (double) imageHeight);

        List<FaceLandmark> landmarks = switch (landmarkMethod) {
            case DUMMY_5 -> dummy5Landmarks(face, imageWidth, imageHeight);
            case LBF_68 -> lbf68Landmarks(decoded, face, imageWidth, imageHeight);
        };

        return new FaceDetectionResult(landmarks, boundingBox, DEFAULT_CONFIDENCE);
    }

    private Rect detectLargestFace(Mat decodedBgr) {
        Mat gray = new Mat();
        cvtColor(decodedBgr, gray, COLOR_BGR2GRAY);
        equalizeHist(gray, gray);

        RectVector faces = new RectVector();
        classifier.detectMultiScale(gray, faces);

        if (faces.size() == 0) {
            throw new FaceNotDetectedException("face not detected");
        }

        Rect best = faces.get(0);
        long bestArea = area(best);
        for (long i = 1; i < faces.size(); i++) {
            Rect candidate = faces.get(i);
            long candidateArea = area(candidate);
            if (candidateArea > bestArea) {
                best = candidate;
                bestArea = candidateArea;
            }
        }

        return best;
    }

    private static long area(Rect r) {
        return (long) r.width() * (long) r.height();
    }

    private static Rect clampRectToImageBounds(Rect r, int imageWidth, int imageHeight) {
        int x = Math.max(0, r.x());
        int y = Math.max(0, r.y());
        int w = r.width();
        int h = r.height();

        if (x >= imageWidth || y >= imageHeight) {
            throw new FaceNotDetectedException("face rectangle is out of image bounds");
        }

        int maxW = imageWidth - x;
        int maxH = imageHeight - y;
        w = Math.min(w, maxW);
        h = Math.min(h, maxH);

        if (w <= 0 || h <= 0) {
            throw new FaceNotDetectedException("face rectangle is invalid");
        }

        return new Rect(x, y, w, h);
    }

    private static Mat decodeImage(byte[] bytes) {
        BytePointer pointer = new BytePointer(bytes);
        Mat buffer = new Mat(1, bytes.length, CV_8U, pointer);
        Mat decoded = imdecode(buffer, IMREAD_COLOR);
        if (decoded == null || decoded.empty()) {
            throw new FaceNotDetectedException("failed to decode image");
        }
        return decoded;
    }

    private static List<FaceLandmark> dummy5Landmarks(Rect face, int imageWidth, int imageHeight) {
        double xMin = face.x() / (double) imageWidth;
        double yMin = face.y() / (double) imageHeight;
        double w = face.width() / (double) imageWidth;
        double h = face.height() / (double) imageHeight;

        return List.of(
                new FaceLandmark("left_eye", xMin + w * LEFT_EYE_X_RATIO, yMin + h * EYES_Y_RATIO),
                new FaceLandmark("right_eye", xMin + w * RIGHT_EYE_X_RATIO, yMin + h * EYES_Y_RATIO),
                new FaceLandmark("nose", xMin + w * NOSE_X_RATIO, yMin + h * NOSE_Y_RATIO),
                new FaceLandmark("left_mouth", xMin + w * LEFT_MOUTH_X_RATIO, yMin + h * MOUTH_Y_RATIO),
                new FaceLandmark("right_mouth", xMin + w * RIGHT_MOUTH_X_RATIO, yMin + h * MOUTH_Y_RATIO));
    }

    private List<FaceLandmark> lbf68Landmarks(Mat decodedBgr, Rect face, int imageWidth, int imageHeight) {
        if (lbfModelPath == null || lbfModelPath.isBlank()) {
            throw new IllegalStateException(
                    "LBF_68 requires an LBF model file; set -D" + LBF_MODEL_PATH_PROPERTY
                            + " or " + LBF_MODEL_PATH_ENV);
        }

        Facemark fm = getOrCreateFacemark();

        Mat gray = new Mat();
        cvtColor(decodedBgr, gray, COLOR_BGR2GRAY);

        RectVector faces = new RectVector(face);
        Point2fVectorVector landmarks = new Point2fVectorVector();

        boolean ok = fm.fit(gray, faces, landmarks);
        if (!ok || landmarks.size() == 0) {
            throw new FaceNotDetectedException("failed to detect face landmarks");
        }

        Point2fVector points = landmarks.get(0);
        long count = points.size();
        if (count != EXPECTED_LBF_LANDMARK_COUNT) {
            throw new IllegalStateException("unexpected LBF landmark count: " + count);
        }

        return toNormalizedLandmarks(points, imageWidth, imageHeight);
    }

    private Facemark getOrCreateFacemark() {
        Facemark current = facemark;
        if (current != null) {
            return current;
        }

        synchronized (this) {
            if (facemark != null) {
                return facemark;
            }

            if (!Files.exists(Path.of(lbfModelPath))) {
                throw new IllegalStateException("LBF model file not found: " + lbfModelPath);
            }

            Facemark created = FacemarkLBF.create();
            created.loadModel(lbfModelPath);
            facemark = created;
            return created;
        }
    }

    private static List<FaceLandmark> toNormalizedLandmarks(Point2fVector points, int imageWidth, int imageHeight) {
        long count = points.size();
        return java.util.stream.LongStream.range(0, count)
                .mapToObj(i -> {
                    Point2f p = points.get(i);
                    double x = p.x() / (double) imageWidth;
                    double y = p.y() / (double) imageHeight;
                    return new FaceLandmark("point_" + i, x, y);
                })
                .toList();
    }

    private static CascadeClassifier loadCascadeClassifier() {
        String configured = System.getProperty(CASCADE_PATH_PROPERTY);
        if (configured == null || configured.isBlank()) {
            configured = System.getenv(CASCADE_PATH_ENV);
        }

        if (configured == null || configured.isBlank()) {
            configured = "/usr/share/opencv4/haarcascades/haarcascade_frontalface_default.xml";
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

    private static LandmarkMethod readLandmarkMethod() {
        String value = System.getProperty(LANDMARK_METHOD_PROPERTY);
        if (value == null || value.isBlank()) {
            value = System.getenv(LANDMARK_METHOD_ENV);
        }

        if (value == null || value.isBlank()) {
            return LandmarkMethod.LBF_68;
        }

        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "DUMMY_5", "DUMMY", "FIVE", "5" -> LandmarkMethod.DUMMY_5;
            case "LBF_68", "LBF", "SIXTY_EIGHT", "68" -> LandmarkMethod.LBF_68;
            default -> throw new IllegalStateException("unsupported landmark method: " + value);
        };
    }

    enum LandmarkMethod {
        DUMMY_5,
        LBF_68
    }

    private static Optional<String> readLbfModelPath() {
        String value = System.getProperty(LBF_MODEL_PATH_PROPERTY);
        if (value == null || value.isBlank()) {
            value = System.getenv(LBF_MODEL_PATH_ENV);
        }
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }
}
