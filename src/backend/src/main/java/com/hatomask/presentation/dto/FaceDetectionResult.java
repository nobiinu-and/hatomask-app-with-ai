package com.hatomask.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceDetectionResult {
    private List<FaceLandmark> landmarks;
    private FaceBoundingBox boundingBox;
    private Double confidence;
}
