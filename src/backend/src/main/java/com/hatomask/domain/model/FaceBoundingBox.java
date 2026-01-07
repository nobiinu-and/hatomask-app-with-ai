package com.hatomask.domain.model;

public record FaceBoundingBox(double xMin, double yMin, double width, double height) {

    public FaceBoundingBox {
        validateZeroToOne("xMin", xMin);
        validateZeroToOne("yMin", yMin);
        validateZeroToOne("width", width);
        validateZeroToOne("height", height);
        validateNotExceed("xMin + width", xMin + width);
        validateNotExceed("yMin + height", yMin + height);
    }

    private static void validateZeroToOne(String name, double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }

    private static void validateNotExceed(String name, double value) {
        if (value > 1) {
            throw new IllegalArgumentException(name + " must be <= 1");
        }
    }
}
