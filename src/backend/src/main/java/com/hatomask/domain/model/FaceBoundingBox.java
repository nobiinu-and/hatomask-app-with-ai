package com.hatomask.domain.model;

public record FaceBoundingBox(double xMin, double yMin, double width, double height) {

    public FaceBoundingBox {
        requireBetweenZeroAndOneInclusive("xMin", xMin);
        requireBetweenZeroAndOneInclusive("yMin", yMin);
        requireBetweenZeroExclusiveAndOneInclusive("width", width);
        requireBetweenZeroExclusiveAndOneInclusive("height", height);
        requireAtMostOne("xMin + width", xMin + width);
        requireAtMostOne("yMin + height", yMin + height);
    }

    private static void requireBetweenZeroAndOneInclusive(String name, double value) {
        if (value < 0.0) {
            throw new IllegalArgumentException(name + " must be between 0.0 and 1.0");
        }
        if (value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0.0 and 1.0");
        }
    }

    private static void requireBetweenZeroExclusiveAndOneInclusive(String name, double value) {
        if (value <= 0.0) {
            throw new IllegalArgumentException(name + " must be between (0.0, 1.0]");
        }
        if (value > 1.0) {
            throw new IllegalArgumentException(name + " must be between (0.0, 1.0]");
        }
    }

    private static void requireAtMostOne(String name, double value) {
        if (value > 1.0) {
            throw new IllegalArgumentException(name + " must be <= 1.0");
        }
    }
}
