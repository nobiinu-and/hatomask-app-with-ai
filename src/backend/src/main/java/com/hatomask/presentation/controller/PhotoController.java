package com.hatomask.presentation.controller;

import com.hatomask.application.usecase.DetectFaceLandmarksUseCase;
import com.hatomask.application.usecase.UploadPhotoUseCase;
import com.hatomask.domain.model.UploadedPhotoReference;
import com.hatomask.presentation.dto.FaceBoundingBox;
import com.hatomask.presentation.dto.FaceDetectionResponse;
import com.hatomask.presentation.dto.FaceDetectionResult;
import com.hatomask.presentation.dto.FaceLandmark;
import com.hatomask.presentation.dto.ImageDimensions;
import com.hatomask.presentation.dto.PhotoUploadResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private final UploadPhotoUseCase uploadPhotoUseCase;
    private final DetectFaceLandmarksUseCase detectFaceLandmarksUseCase;

    public PhotoController(
            UploadPhotoUseCase uploadPhotoUseCase,
            DetectFaceLandmarksUseCase detectFaceLandmarksUseCase) {
        this.uploadPhotoUseCase = uploadPhotoUseCase;
        this.detectFaceLandmarksUseCase = detectFaceLandmarksUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhotoUploadResponse> uploadPhoto(@RequestPart("file") MultipartFile file) {
        UploadedPhotoReference ref = uploadPhotoUseCase.execute(file);

        PhotoUploadResponse response = new PhotoUploadResponse(
                ref.photoId(),
                ref.mimeType().value(),
                ref.fileSizeBytes().value(),
                new ImageDimensions(ref.dimensions().width(), ref.dimensions().height()),
                ref.expiresAt());

        URI location = URI.create("/api/v1/photos/" + ref.photoId());
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping(path = "/{photoId}/face-detections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FaceDetectionResponse> detectFaceLandmarks(@PathVariable("photoId") String photoId) {
        com.hatomask.domain.model.FaceDetectionResult detected = detectFaceLandmarksUseCase
                .execute(UUID.fromString(photoId));

        FaceDetectionResult result = new FaceDetectionResult(
                detected.landmarks().stream()
                        .map(lm -> new FaceLandmark(lm.name(), lm.x(), lm.y()))
                        .toList(),
                new FaceBoundingBox(
                        detected.boundingBox().xMin(),
                        detected.boundingBox().yMin(),
                        detected.boundingBox().width(),
                        detected.boundingBox().height()),
                detected.confidence());

        return ResponseEntity.ok(new FaceDetectionResponse(result));
    }
}
