package com.hatomask.application.usecase;

import com.hatomask.application.exception.PhotoNotFoundException;
import com.hatomask.domain.model.FaceBoundingBox;
import com.hatomask.domain.model.FaceDetectionResult;
import com.hatomask.domain.model.FaceLandmark;
import com.hatomask.domain.model.StoredPhoto;
import com.hatomask.domain.repository.StoredPhotoRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
public class DetectFaceLandmarksUseCase {

    private static final double DEFAULT_BOX_X_MIN = 0.30;
    private static final double DEFAULT_BOX_Y_MIN = 0.20;
    private static final double DEFAULT_BOX_WIDTH = 0.40;
    private static final double DEFAULT_BOX_HEIGHT = 0.50;

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

    public DetectFaceLandmarksUseCase(StoredPhotoRepository storedPhotoRepository, Clock clock) {
        this.storedPhotoRepository = storedPhotoRepository;
        this.clock = clock;
    }

    public FaceDetectionResult execute(UUID photoId) {
        StoredPhoto storedPhoto = storedPhotoRepository.findValidById(photoId, clock)
                .orElseThrow(() -> new PhotoNotFoundException("photo not found"));

        // M0: プレースホルダ実装（縦切り確認が目的）。
        // 実検出ロジックは差し替え可能にする。
        FaceBoundingBox boundingBox = new FaceBoundingBox(
                DEFAULT_BOX_X_MIN,
                DEFAULT_BOX_Y_MIN,
                DEFAULT_BOX_WIDTH,
                DEFAULT_BOX_HEIGHT);

        double xMin = boundingBox.xMin();
        double yMin = boundingBox.yMin();
        double w = boundingBox.width();
        double h = boundingBox.height();

        List<FaceLandmark> landmarks = List.of(
                new FaceLandmark(xMin + w * LEFT_EYE_X_RATIO, yMin + h * EYES_Y_RATIO),
                new FaceLandmark(xMin + w * RIGHT_EYE_X_RATIO, yMin + h * EYES_Y_RATIO),
                new FaceLandmark(xMin + w * NOSE_X_RATIO, yMin + h * NOSE_Y_RATIO),
                new FaceLandmark(xMin + w * LEFT_MOUTH_X_RATIO, yMin + h * MOUTH_Y_RATIO),
                new FaceLandmark(xMin + w * RIGHT_MOUTH_X_RATIO, yMin + h * MOUTH_Y_RATIO));

        return new FaceDetectionResult(landmarks, boundingBox, DEFAULT_CONFIDENCE);
    }
}
