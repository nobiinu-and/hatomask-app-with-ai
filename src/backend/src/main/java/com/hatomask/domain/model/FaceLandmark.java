package com.hatomask.domain.model;

public record FaceLandmark(String name, double x, double y) {

    public FaceLandmark {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (x < 0.0 || x > 1.0) {
            throw new IllegalArgumentException("x must be between 0.0 and 1.0");
        }
        if (y < 0.0 || y > 1.0) {
            throw new IllegalArgumentException("y must be between 0.0 and 1.0");
        }
    }
}
