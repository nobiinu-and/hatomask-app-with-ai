package com.hatomask.domain.model;

public record FaceLandmark(double x, double y) {

    public FaceLandmark {
        if (x < 0 || x > 1) {
            throw new IllegalArgumentException("x must be between 0 and 1");
        }
        if (y < 0 || y > 1) {
            throw new IllegalArgumentException("y must be between 0 and 1");
        }
    }
}
