package com.hatomask.presentation.controller;

import com.hatomask.presentation.dto.UploadPhotoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private static final int DEFAULT_WIDTH_PX = 1920;
    private static final int DEFAULT_HEIGHT_PX = 1080;

    // TODO: Replace stub implementation
    // Task06でドメイン層・UseCase実装後に置き換える
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadPhotoResponse> uploadPhoto(@RequestParam("file") MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "image/jpeg";
        }

        UploadPhotoResponse response = new UploadPhotoResponse(
                mimeType,
                file.getSize(),
                DEFAULT_WIDTH_PX,
                DEFAULT_HEIGHT_PX);

        return ResponseEntity.ok(response);
    }
}
