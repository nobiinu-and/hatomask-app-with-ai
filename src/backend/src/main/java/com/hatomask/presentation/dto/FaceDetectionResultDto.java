package com.hatomask.presentation.dto;

import java.util.List;

public record FaceDetectionResultDto(
        List<FaceLandmarkDto> landmarks,
        FaceBoundingBoxDto boundingBox,
        Double confidence) {
}
