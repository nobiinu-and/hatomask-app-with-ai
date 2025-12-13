package com.hatomask.presentation.exception;

import com.hatomask.application.usecase.PhotoNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;

/**
 * グローバル例外ハンドラー.
 * RFC 9457 (Problem Details for HTTP APIs) に準拠したエラーレスポンスを返す。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * バリデーションエラーのハンドラー.
     *
     * @param ex IllegalArgumentException
     * @return ProblemDetail (400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleValidationError(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Bad Request");
        return problemDetail;
    }

    /**
     * 写真が見つからない場合のハンドラー.
     *
     * @param ex PhotoNotFoundException
     * @return ProblemDetail (404 Not Found)
     */
    @ExceptionHandler(PhotoNotFoundException.class)
    public ProblemDetail handlePhotoNotFound(PhotoNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Not Found");
        return problemDetail;
    }

    /**
     * ファイルサイズ超過エラーのハンドラー.
     *
     * @param ex MaxUploadSizeExceededException
     * @return ProblemDetail (400 Bad Request)
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "ファイルサイズが最大許容サイズ10MBを超えています");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Bad Request");
        return problemDetail;
    }

    /**
     * その他の例外のハンドラー.
     *
     * @param ex Exception
     * @return ProblemDetail (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleInternalServerError(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "内部サーバーエラーが発生しました");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }
}
