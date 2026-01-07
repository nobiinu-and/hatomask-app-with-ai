package com.hatomask.application.usecase;

import com.hatomask.application.exception.FaceNotDetectedException;
import com.hatomask.application.exception.PhotoNotFoundException;
import com.hatomask.domain.model.FaceBoundingBox;
import com.hatomask.domain.model.FaceDetectionResult;
import com.hatomask.domain.model.FaceLandmark;
import com.hatomask.domain.model.StoredPhoto;
import com.hatomask.domain.repository.StoredPhotoRepository;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Point2fVector;
import org.bytedeco.opencv.opencv_core.Point2fVectorVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_face.Facemark;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.global.opencv_face;
import org.bytedeco.opencv.global.opencv_objdetect;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Clock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.bytedeco.opencv.global.opencv_core.CV_8U;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.equalizeHist;

@Service
public class DetectFaceLandmarksUseCase {

    private static final String HAAR_CASCADE_RESOURCE = "/org/bytedeco/opencv/data/haarcascades/haarcascade_frontalface_default.xml";

    private static final String CASCADE_PATH_PROPERTY = "hatomask.opencv.cascadePath";
    private static final String CASCADE_PATH_ENV = "HATOMASK_OPENCV_CASCADE_PATH";

    private static final String LBF_MODEL_PATH_PROPERTY = "hatomask.opencv.lbfModelPath";
    private static final String LBF_MODEL_PATH_ENV = "HATOMASK_OPENCV_LBF_MODEL_PATH";

    private static final String LANDMARK_METHOD_PROPERTY = "hatomask.faceDetection.landmarkMethod";
    private static final String LANDMARK_METHOD_ENV = "HATOMASK_FACE_DETECTION_LANDMARK_METHOD";

    private static final int LANDMARKS_DUMMY_COUNT = 5;
    private static final int LANDMARKS_LBF_COUNT = 68;

    private static final double LEFT_EYE_X_RATIO = 0.30;
    private static final double RIGHT_EYE_X_RATIO = 0.70;
    private static final double EYES_Y_RATIO = 0.35;
    private static final double NOSE_X_RATIO = 0.50;
    private static final double NOSE_Y_RATIO = 0.55;
    private static final double LEFT_MOUTH_X_RATIO = 0.35;
    private static final double RIGHT_MOUTH_X_RATIO = 0.65;
    private static final double MOUTH_Y_RATIO = 0.75;

    private static final double DEFAULT_CONFIDENCE = 0.5;

    private final StoredPhotoRepository storedPhotoRepository;
    private final Clock clock;

    private final CascadeClassifier faceCascade;
    private final LandmarkMethod landmarkMethod;
    private final Facemark facemark;

    private enum LandmarkMethod {
        DUMMY_5,
        LBF_68
    }

    public DetectFaceLandmarksUseCase(StoredPhotoRepository storedPhotoRepository, Clock clock) {
        this.storedPhotoRepository = storedPhotoRepository;
        this.clock = clock;

        // Fail fast: face detection is a mandatory feature.
        // If native OpenCV is not available in the runtime environment, the application
        // should not start.
        Loader.load(opencv_objdetect.class);
        this.faceCascade = loadCascadeClassifier();

        this.landmarkMethod = readLandmarkMethod();
        if (landmarkMethod == LandmarkMethod.LBF_68) {
            Loader.load(opencv_face.class);
            this.facemark = loadFacemarkLbf();
        } else {
            this.facemark = null;
        }
    }

    public FaceDetectionResult execute(UUID photoId) {
        StoredPhoto storedPhoto = storedPhotoRepository.findValidById(photoId, clock)
                .orElseThrow(() -> new PhotoNotFoundException("photo not found"));

        int imageWidth = storedPhoto.dimensions().width();
        int imageHeight = storedPhoto.dimensions().height();

        byte[] bytes = storedPhoto.bytes();
        Mat decoded = decodeImage(bytes);

        Rect face = detectLargestFace(faceCascade, decoded);
        if (face == null) {
            throw new FaceNotDetectedException("face not detected");
        }

        FaceBoundingBox boundingBox = toNormalizedBoundingBox(face, imageWidth, imageHeight);
        List<FaceLandmark> landmarks = switch (landmarkMethod) {
            case DUMMY_5 -> createFivePointLandmarks(boundingBox);
            case LBF_68 -> detect68Landmarks(decoded, face, imageWidth, imageHeight);
        };

        validateLandmarkCount(landmarks);
        return new FaceDetectionResult(landmarks, boundingBox, DEFAULT_CONFIDENCE);
    }

    private static LandmarkMethod readLandmarkMethod() {
        String raw = System.getProperty(LANDMARK_METHOD_PROPERTY);
        if (raw == null || raw.isBlank()) {
            raw = System.getenv(LANDMARK_METHOD_ENV);
        }
        if (raw == null || raw.isBlank()) {
            return LandmarkMethod.LBF_68;
        }

        String normalized = raw.trim().toUpperCase();
        return switch (normalized) {
            case "DUMMY_5", "DUMMY", "FIVE", "5" -> LandmarkMethod.DUMMY_5;
            case "LBF_68", "LBF", "SIXTY_EIGHT", "68" -> LandmarkMethod.LBF_68;
            default -> throw new IllegalStateException(
                    "invalid landmark method: " + raw + ". Use DUMMY_5 or LBF_68 via -D" + LANDMARK_METHOD_PROPERTY
                            + " or env " + LANDMARK_METHOD_ENV);
        };
    }

    private void validateLandmarkCount(List<FaceLandmark> landmarks) {
        if (landmarks == null) {
            throw new IllegalStateException("landmarks must not be null");
        }
        int expected = landmarkMethod == LandmarkMethod.DUMMY_5 ? LANDMARKS_DUMMY_COUNT : LANDMARKS_LBF_COUNT;
        if (landmarks.size() != expected) {
            throw new IllegalStateException(
                    "unexpected landmark count: " + landmarks.size() + " (expected " + expected + ") for method "
                            + landmarkMethod);
        }
    }

    private static CascadeClassifier loadCascadeClassifier() {
        String configuredPath = readConfiguredCascadePath();
        if (configuredPath != null) {
            return createClassifierOrThrow(configuredPath, "configured path");
        }

        String classpathExtracted = tryExtractCascadeFromClasspath();
        if (classpathExtracted != null) {
            return createClassifierOrThrow(classpathExtracted, "classpath resource");
        }

        for (String candidate : commonSystemCascadePaths()) {
            if (Files.exists(Path.of(candidate))) {
                return createClassifierOrThrow(candidate, "system path");
            }
        }

        throw new IllegalStateException(
                "face cascade not found. Set -D" + CASCADE_PATH_PROPERTY + "=<path> or env " + CASCADE_PATH_ENV
                        + ". Tried classpath resource: " + HAAR_CASCADE_RESOURCE);
    }

    private static String readConfiguredCascadePath() {
        String fromProperty = System.getProperty(CASCADE_PATH_PROPERTY);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty.trim();
        }
        String fromEnv = System.getenv(CASCADE_PATH_ENV);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        return null;
    }

    private static String tryExtractCascadeFromClasspath() {
        try {
            File cascadeFile = Loader.extractResource(HAAR_CASCADE_RESOURCE, null, "cascade", ".xml");
            if (cascadeFile == null && HAAR_CASCADE_RESOURCE.startsWith("/")) {
                cascadeFile = Loader.extractResource(
                        HAAR_CASCADE_RESOURCE.substring(1),
                        null,
                        "cascade",
                        ".xml");
            }
            return cascadeFile == null ? null : cascadeFile.getAbsolutePath();
        } catch (Exception ex) {
            throw new IllegalStateException("failed to extract face cascade resource", ex);
        }
    }

    private static List<String> commonSystemCascadePaths() {
        return List.of(
                "/usr/share/opencv4/haarcascades/haarcascade_frontalface_default.xml",
                "/usr/share/opencv/haarcascades/haarcascade_frontalface_default.xml",
                "/usr/local/share/opencv4/haarcascades/haarcascade_frontalface_default.xml",
                "/usr/local/share/opencv/haarcascades/haarcascade_frontalface_default.xml");
    }

    private static CascadeClassifier createClassifierOrThrow(String cascadePath, String source) {
        CascadeClassifier classifier = new CascadeClassifier(cascadePath);
        if (classifier.empty()) {
            throw new IllegalStateException("failed to load face cascade classifier (" + source + "): " + cascadePath);
        }
        return classifier;
    }

    private static Rect detectLargestFace(CascadeClassifier classifier, Mat decoded) {
        Mat gray = new Mat();
        cvtColor(decoded, gray, COLOR_BGR2GRAY);
        equalizeHist(gray, gray);

        RectVector faces = new RectVector();
        classifier.detectMultiScale(gray, faces);
        if (faces.size() == 0) {
            return null;
        }

        Rect best = faces.get(0);
        for (long i = 1; i < faces.size(); i++) {
            Rect candidate = faces.get(i);
            if (area(candidate) > area(best)) {
                best = candidate;
            }
        }
        return best;
    }

    private static Mat decodeImage(byte[] bytes) {
        BytePointer bytePointer = new BytePointer(bytes);
        Mat buffer = new Mat(1, bytes.length, CV_8U, bytePointer);
        Mat decoded = imdecode(buffer, IMREAD_COLOR);
        if (decoded == null || decoded.empty()) {
            throw new FaceNotDetectedException("failed to decode image");
        }
        return decoded;
    }

    private static long area(Rect rect) {
        return (long) rect.width() * (long) rect.height();
    }

    private Facemark loadFacemarkLbf() {
        String modelPath = readConfiguredLbfModelPath();
        if (modelPath == null) {
            for (String candidate : commonSystemLbfModelPaths()) {
                if (Files.exists(Path.of(candidate))) {
                    modelPath = candidate;
                    break;
                }
            }
        }

        if (modelPath == null) {
            throw new IllegalStateException(
                    "LBF model not found. Set -D" + LBF_MODEL_PATH_PROPERTY + "=<path> or env " + LBF_MODEL_PATH_ENV);
        }

        Facemark facemarkLbf = opencv_face.createFacemarkLBF();
        facemarkLbf.loadModel(modelPath);
        return facemarkLbf;
    }

    private static String readConfiguredLbfModelPath() {
        String fromProperty = System.getProperty(LBF_MODEL_PATH_PROPERTY);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty.trim();
        }
        String fromEnv = System.getenv(LBF_MODEL_PATH_ENV);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        return null;
    }

    private static List<String> commonSystemLbfModelPaths() {
        return List.of(
                "/opt/hatomask/models/lbfmodel.yaml",
                "/usr/share/opencv4/lbfmodel.yaml",
                "/usr/share/opencv/lbfmodel.yaml");
    }

    private List<FaceLandmark> detect68Landmarks(Mat decodedBgr, Rect face, int imageWidth, int imageHeight) {
        if (facemark == null) {
            throw new IllegalStateException("facemark is not initialized");
        }
        RectVector faces = new RectVector(1);
        faces.put(0, face);

        Point2fVectorVector landmarks = new Point2fVectorVector();
        boolean ok = facemark.fit(decodedBgr, faces, landmarks);
        if (!ok || landmarks.size() == 0) {
            throw new FaceNotDetectedException("failed to estimate face landmarks");
        }

        Point2fVector points = landmarks.get(0);
        if (points == null || points.size() == 0) {
            throw new FaceNotDetectedException("failed to estimate face landmarks");
        }

        return toNormalizedLandmarks(points, imageWidth, imageHeight);
    }

    private static List<FaceLandmark> createFivePointLandmarks(FaceBoundingBox boundingBox) {
        double xMin = boundingBox.xMin();
        double yMin = boundingBox.yMin();
        double w = boundingBox.width();
        double h = boundingBox.height();

        return List.of(
                new FaceLandmark(xMin + w * LEFT_EYE_X_RATIO, yMin + h * EYES_Y_RATIO),
                new FaceLandmark(xMin + w * RIGHT_EYE_X_RATIO, yMin + h * EYES_Y_RATIO),
                new FaceLandmark(xMin + w * NOSE_X_RATIO, yMin + h * NOSE_Y_RATIO),
                new FaceLandmark(xMin + w * LEFT_MOUTH_X_RATIO, yMin + h * MOUTH_Y_RATIO),
                new FaceLandmark(xMin + w * RIGHT_MOUTH_X_RATIO, yMin + h * MOUTH_Y_RATIO));
    }

    private static List<FaceLandmark> toNormalizedLandmarks(Point2fVector points, int imageWidth, int imageHeight) {
        int size = (int) points.size();
        java.util.ArrayList<FaceLandmark> result = new java.util.ArrayList<>(size);
        for (long i = 0; i < points.size(); i++) {
            Point2f p = points.get(i);
            result.add(new FaceLandmark(p.x() / (double) imageWidth, p.y() / (double) imageHeight));
        }
        return java.util.Collections.unmodifiableList(result);
    }

    private static FaceBoundingBox toNormalizedBoundingBox(Rect rect, int imageWidth, int imageHeight) {
        double xMin = rect.x() / (double) imageWidth;
        double yMin = rect.y() / (double) imageHeight;
        double width = rect.width() / (double) imageWidth;
        double height = rect.height() / (double) imageHeight;
        return new FaceBoundingBox(xMin, yMin, width, height);
    }

}
