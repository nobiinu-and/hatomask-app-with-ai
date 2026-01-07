package com.hatomask.domain.model;

import java.util.Arrays;

public enum MimeType {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static MimeType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("mimeType is required");
        }

        return Arrays.stream(values())
                .filter(v -> v.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unsupported mimeType: " + value));
    }
}
