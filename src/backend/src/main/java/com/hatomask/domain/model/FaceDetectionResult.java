package com.hatomask.domain.model;

import java.util.List;

public record FaceDetectionResult(
        List<FaceLandmark> landmarks,
        FaceBoundingBox boundingBox,
        double confidence) {

    private static final int DUMMY_5_LANDMARKS = 5;
    private static final int LBF_68_LANDMARKS = 68;

    public FaceDetectionResult {
        if (landmarks == null || landmarks.isEmpty()) {
            throw new IllegalArgumentException("landmarks is required");
        }
        if (!(landmarks.size() == DUMMY_5_LANDMARKS || landmarks.size() == LBF_68_LANDMARKS)) {
            throw new IllegalArgumentException("landmarks size must be "
                    + DUMMY_5_LANDMARKS
                    + " or "
                    + LBF_68_LANDMARKS);
        }
        if (boundingBox == null) {
            throw new IllegalArgumentException("boundingBox is required");
        }
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be between 0.0 and 1.0");
        }
    }
}
