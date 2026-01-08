package com.hatomask.application.exception;

public class FaceNotDetectedException extends RuntimeException {
    public FaceNotDetectedException(String message) {
        super(message);
    }
}
