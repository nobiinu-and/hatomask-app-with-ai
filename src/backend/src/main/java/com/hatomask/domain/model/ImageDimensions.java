package com.hatomask.domain.model;

public record ImageDimensions(int width, int height) {

    public ImageDimensions {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be > 0");
        }
    }

    public static ImageDimensions of(int width, int height) {
        return new ImageDimensions(width, height);
    }
}
