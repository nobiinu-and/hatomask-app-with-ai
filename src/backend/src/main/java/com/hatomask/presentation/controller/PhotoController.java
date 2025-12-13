package com.hatomask.presentation.controller;

import com.hatomask.application.usecase.GetPhotoUseCase;
import com.hatomask.application.usecase.UploadPhotoUseCase;
import com.hatomask.domain.model.Photo;
import com.hatomask.presentation.dto.PhotoResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * 写真アップロード・ダウンロード機能のController.
 */
@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private final UploadPhotoUseCase uploadPhotoUseCase;
    private final GetPhotoUseCase getPhotoUseCase;

    /**
     * コンストラクタ.
     *
     * @param uploadPhotoUseCase 写真アップロードユースケース
     * @param getPhotoUseCase    写真取得ユースケース
     */
    public PhotoController(UploadPhotoUseCase uploadPhotoUseCase, GetPhotoUseCase getPhotoUseCase) {
        this.uploadPhotoUseCase = uploadPhotoUseCase;
        this.getPhotoUseCase = getPhotoUseCase;
    }

    /**
     * 写真をアップロード.
     * OpenAPI: POST /api/v1/photos
     *
     * @param file アップロードする写真ファイル
     * @return PhotoResponse (id, originalFileName, contentType, fileSize, createdAt, updatedAt)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoResponse> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("ファイルは必須です");
        }

        PhotoResponse response = uploadPhotoUseCase.execute(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                file.getBytes()
        );

        URI location = URI.create("/api/v1/photos/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * 写真を取得.
     * OpenAPI: GET /api/v1/photos/{id}
     *
     * @param id       写真ID (UUID)
     * @param download ダウンロード用にContent-Dispositionヘッダーを設定するか
     * @return 画像バイナリデータ
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getPhotoById(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "false") boolean download) {

        UUID photoId = UUID.fromString(id);
        Photo photo = getPhotoUseCase.execute(photoId);

        MediaType mediaType = MediaType.parseMediaType(photo.getContentType().getValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        if (download) {
            String filename = "photo_" + id + getFileExtension(photo.getContentType().getValue());
            headers.setContentDispositionFormData("attachment", filename);
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(photo.getImageData());
    }

    private String getFileExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            default -> "";
        };
    }
}
