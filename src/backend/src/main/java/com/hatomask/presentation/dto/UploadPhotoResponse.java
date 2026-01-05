package com.hatomask.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadPhotoResponse {
    private String mimeType;
    private Long sizeBytes;
    private Integer widthPx;
    private Integer heightPx;
}
