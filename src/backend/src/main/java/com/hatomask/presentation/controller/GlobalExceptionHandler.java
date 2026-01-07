package com.hatomask.presentation.controller;

import com.hatomask.application.exception.ImageDecodingException;
import com.hatomask.application.exception.InvalidFileException;
import com.hatomask.application.exception.PayloadTooLargeException;
import com.hatomask.application.exception.FaceNotDetectedException;
import com.hatomask.application.exception.PhotoNotFoundException;
import com.hatomask.application.exception.UnsupportedMediaTypeException;
import com.hatomask.presentation.dto.ProblemDetails;
import com.hatomask.presentation.dto.ProblemFieldError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ProblemDetails> handleMissingPart(
            MissingServletRequestPartException ex,
            HttpServletRequest request) {
        return badRequest(ex.getRequestPartName(), "file is required", request);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ProblemDetails> handleInvalidFile(
            InvalidFileException ex,
            HttpServletRequest request) {
        return badRequest("file", ex.getMessage(), request);
    }

    @ExceptionHandler(ImageDecodingException.class)
    public ResponseEntity<ProblemDetails> handleImageDecoding(
            ImageDecodingException ex,
            HttpServletRequest request) {
        return badRequest("file", ex.getMessage(), request);
    }

    @ExceptionHandler(PayloadTooLargeException.class)
    public ResponseEntity<ProblemDetails> handlePayloadTooLarge(PayloadTooLargeException ex,
            HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Payload Too Large",
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                ex.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .contentType(PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ProblemDetails> handleUnsupportedMediaType(
            UnsupportedMediaTypeException ex,
            HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Unsupported Media Type",
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                ex.getMessage(),
                request.getRequestURI(),
                List.of(new ProblemFieldError("file", ex.getMessage())));

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .contentType(PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(PhotoNotFoundException.class)
    public ResponseEntity<ProblemDetails> handlePhotoNotFound(
            PhotoNotFoundException ex,
            HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(FaceNotDetectedException.class)
    public ResponseEntity<ProblemDetails> handleFaceNotDetected(
            FaceNotDetectedException ex,
            HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Unprocessable Entity",
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleGeneric(Exception ex, HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                request.getRequestURI(),
                null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(PROBLEM_JSON)
                .body(problem);
    }

    private static ResponseEntity<ProblemDetails> badRequest(
            String field,
            String message,
            HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getRequestURI(),
                List.of(new ProblemFieldError(field, message)));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(PROBLEM_JSON)
                .body(problem);
    }
}
