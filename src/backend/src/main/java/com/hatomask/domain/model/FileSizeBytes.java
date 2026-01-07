package com.hatomask.domain.model;

public record FileSizeBytes(long value) {

    public static final long MAX_BYTES = 10L * 1024L * 1024L;

    public FileSizeBytes {
        if (value <= 0) {
            throw new IllegalArgumentException("fileSizeBytes must be > 0");
        }
        if (value > MAX_BYTES) {
            throw new IllegalArgumentException("fileSizeBytes must be <= " + MAX_BYTES);
        }
    }

    public static FileSizeBytes of(long value) {
        return new FileSizeBytes(value);
    }
}
