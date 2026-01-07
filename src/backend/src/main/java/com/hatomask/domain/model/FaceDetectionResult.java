
package com.hatomask.domain.model;

import java.util.List;

public record FaceDetectionResult(
        List<FaceLandmark> landmarks,
        FaceBoundingBox boundingBox,
        Double confidence) {

    public FaceDetectionResult {
        if (landmarks == null || landmarks.isEmpty()) {
            throw new IllegalArgumentException("landmarks is required");
        }
        if (boundingBox == null) {
            throw new IllegalArgumentException("boundingBox is required");
        }
        if (confidence != null && (confidence < 0 || confidence > 1)) {
            throw new IllegalArgumentException("confidence must be between 0 and 1");
        }
    }
}
