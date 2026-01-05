package com.hatomask.presentation.controller;

import com.hatomask.presentation.dto.ProblemDetails;
import com.hatomask.presentation.dto.UploadPhotoResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import javax.imageio.ImageIO;
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

    private static final String MIME_TYPE_JPEG = "image/jpeg";
    private static final String MIME_TYPE_PNG = "image/png";
    private static final long BYTES_PER_KIB = 1024L;
    private static final long MAX_FILE_SIZE_BYTES = 10L * BYTES_PER_KIB * BYTES_PER_KIB;

    // TODO: Replace stub implementation
    // Task06でドメイン層・UseCase実装後に置き換える
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) {
        long sizeBytes = file.getSize();
        if (sizeBytes > MAX_FILE_SIZE_BYTES) {
            ProblemDetails problem = ProblemDetails.badRequest("ファイルサイズは10MB以下にしてください");
            return ResponseEntity.status(problem.getStatus())
                    .contentType(MediaType.valueOf("application/problem+json"))
                    .body(problem);
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException exception) {
            ProblemDetails problem = ProblemDetails.serverError("画像の読み込みに失敗しました");
            return ResponseEntity.status(problem.getStatus())
                    .contentType(MediaType.valueOf("application/problem+json"))
                    .body(problem);
        }

        String mimeType = normalizeMimeType(file.getContentType(), bytes);
        if (!isSupportedMimeType(mimeType)) {
            ProblemDetails problem = ProblemDetails.badRequest("JPEG または PNG ファイルを選択してください");
            return ResponseEntity.status(problem.getStatus())
                    .contentType(MediaType.valueOf("application/problem+json"))
                    .body(problem);
        }

        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException exception) {
            image = null;
        }

        if (image == null) {
            ProblemDetails problem = ProblemDetails.unprocessableEntity("画像のデコードに失敗しました");
            return ResponseEntity.status(problem.getStatus())
                    .contentType(MediaType.valueOf("application/problem+json"))
                    .body(problem);
        }

        UploadPhotoResponse response = new UploadPhotoResponse(
                mimeType,
                sizeBytes,
                image.getWidth(),
                image.getHeight());

        return ResponseEntity.ok(response);
    }

    private static boolean isSupportedMimeType(String mimeType) {
        return MIME_TYPE_JPEG.equals(mimeType) || MIME_TYPE_PNG.equals(mimeType);
    }

    private static String normalizeMimeType(String contentType, byte[] bytes) {
        if (contentType != null && !contentType.isBlank()) {
            return contentType;
        }

        try {
            String detected = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
            return detected != null ? detected : "";
        } catch (IOException exception) {
            return "";
        }
    }
}
