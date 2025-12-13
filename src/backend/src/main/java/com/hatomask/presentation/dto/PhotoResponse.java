package com.hatomask.presentation.dto;

/**
 * 写真アップロード成功時のレスポンスDTO
 * 
 * OpenAPI: PhotoResponse schema
 */
public record PhotoResponse(
    String id,
    String originalFileName,
    String contentType,
    Long fileSize,
    String createdAt,
    String updatedAt
) {}
