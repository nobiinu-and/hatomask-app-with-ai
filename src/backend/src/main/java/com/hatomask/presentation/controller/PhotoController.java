package com.hatomask.presentation.controller;

import com.hatomask.application.usecase.UploadPhotoUseCase;
import com.hatomask.domain.model.UploadedPhotoReference;
import com.hatomask.presentation.dto.ImageDimensions;
import com.hatomask.presentation.dto.PhotoUploadResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private final UploadPhotoUseCase uploadPhotoUseCase;

    public PhotoController(UploadPhotoUseCase uploadPhotoUseCase) {
        this.uploadPhotoUseCase = uploadPhotoUseCase;
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
}
