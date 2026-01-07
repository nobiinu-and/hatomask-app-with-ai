package com.hatomask.presentation.controller;

import com.hatomask.presentation.dto.ImageDimensions;
import com.hatomask.presentation.dto.PhotoUploadResponse;
import com.hatomask.presentation.dto.ProblemDetails;
import com.hatomask.presentation.dto.ProblemFieldError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final UUID STUB_PHOTO_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final OffsetDateTime STUB_EXPIRES_AT = OffsetDateTime.parse("2026-01-07T12:34:56Z");
    private static final int STUB_IMAGE_WIDTH_PX = 1920;
    private static final int STUB_IMAGE_HEIGHT_PX = 1080;

    // TODO: Replace stub implementation
    // Task06でドメイン層・UseCase実装後に置き換える
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadPhoto(
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request) {

        if (file == null || file.isEmpty()) {
            return badRequest("file", "file is required", request);
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            ProblemDetails problem = new ProblemDetails(
                    "about:blank",
                    "Payload Too Large",
                    HttpStatus.PAYLOAD_TOO_LARGE.value(),
                    "payload too large",
                    request.getRequestURI(),
                    null);
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .contentType(MediaType.valueOf("application/problem+json"))
                    .body(problem);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            ProblemDetails problem = new ProblemDetails(
                    "about:blank",
                    "Unsupported Media Type",
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                    "unsupported media type",
                    request.getRequestURI(),
                    List.of(new ProblemFieldError("file", "only image/jpeg or image/png is supported")));
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .contentType(MediaType.valueOf("application/problem+json"))
                    .body(problem);
        }

        PhotoUploadResponse response = new PhotoUploadResponse(
                STUB_PHOTO_ID,
                contentType,
                file.getSize(),
                new ImageDimensions(STUB_IMAGE_WIDTH_PX, STUB_IMAGE_HEIGHT_PX),
                STUB_EXPIRES_AT);

        URI location = URI.create("/api/v1/photos/" + STUB_PHOTO_ID);
        return ResponseEntity.created(location).body(response);
    }

    private static ResponseEntity<ProblemDetails> badRequest(
            String field,
            String message,
            HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                "validation error",
                request.getRequestURI(),
                List.of(new ProblemFieldError(field, message)));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(problem);
    }
}
