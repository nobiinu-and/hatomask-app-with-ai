package com.hatomask.presentation.controller;

import com.hatomask.application.usecase.UploadPhotoUseCase;
import com.hatomask.presentation.dto.PhotoUploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private final UploadPhotoUseCase uploadPhotoUseCase;

    public PhotoController(UploadPhotoUseCase uploadPhotoUseCase) {
        this.uploadPhotoUseCase = uploadPhotoUseCase;
    }

    @PostMapping("/upload")
    public ResponseEntity<PhotoUploadResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = uploadPhotoUseCase.store(file);
        PhotoUploadResponse res = new PhotoUploadResponse(filename, "アップロードに成功しました");
        return ResponseEntity.ok(res);
    }
}
