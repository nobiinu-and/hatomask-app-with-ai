package com.hatomask.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 写真アップロード成功時のレスポンスDTO.
 * OpenAPI: PhotoResponse schema
 */
public record PhotoResponse(
        String id,
        String originalFileName,
        String contentType,
        Long fileSize,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
}
