package com.hatomask.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoUploadResponse {
    private UUID photoId;
    private String mimeType;
    private Long fileSizeBytes;
    private ImageDimensions dimensions;
    private OffsetDateTime expiresAt;
}
