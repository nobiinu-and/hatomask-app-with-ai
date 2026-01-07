package com.hatomask.presentation.controller;

import com.hatomask.application.usecase.DetectFaceLandmarksUseCase;
import com.hatomask.domain.model.FaceDetectionResult;
import com.hatomask.presentation.dto.FaceBoundingBoxDto;
import com.hatomask.presentation.dto.FaceDetectionResponse;
import com.hatomask.presentation.dto.FaceDetectionResultDto;
import com.hatomask.presentation.dto.FaceLandmarkDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/photos")
public class FaceDetectionController {

    private final DetectFaceLandmarksUseCase detectFaceLandmarksUseCase;

    public FaceDetectionController(DetectFaceLandmarksUseCase detectFaceLandmarksUseCase) {
        this.detectFaceLandmarksUseCase = detectFaceLandmarksUseCase;
    }

    @PostMapping(path = "/{photoId}/face-detections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FaceDetectionResponse> detectFace(@PathVariable UUID photoId) {
        FaceDetectionResult result = detectFaceLandmarksUseCase.execute(photoId);

        List<FaceLandmarkDto> landmarks = result.landmarks().stream()
                .map(p -> new FaceLandmarkDto(p.x(), p.y()))
                .toList();

        FaceBoundingBoxDto boundingBox = new FaceBoundingBoxDto(
                result.boundingBox().xMin(),
                result.boundingBox().yMin(),
                result.boundingBox().width(),
                result.boundingBox().height());

        FaceDetectionResultDto resultDto = new FaceDetectionResultDto(landmarks, boundingBox, result.confidence());

        return ResponseEntity.ok(new FaceDetectionResponse(resultDto));
    }
}
