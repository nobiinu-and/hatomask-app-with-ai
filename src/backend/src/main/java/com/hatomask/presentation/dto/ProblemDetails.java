package com.hatomask.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetails {

    private static final String TYPE_ABOUT_BLANK = "about:blank";

    private static final String TITLE_BAD_REQUEST = "Bad Request";
    private static final String TITLE_UNPROCESSABLE_ENTITY = "Unprocessable Entity";
    private static final String TITLE_INTERNAL_SERVER_ERROR = "Internal Server Error";

    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_UNPROCESSABLE_ENTITY = 422;
    private static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    private String type;
    private String title;
    private int status;
    private String detail;

    public static ProblemDetails badRequest(String detail) {
        return new ProblemDetails(TYPE_ABOUT_BLANK, TITLE_BAD_REQUEST, STATUS_BAD_REQUEST, detail);
    }

    public static ProblemDetails unprocessableEntity(String detail) {
        return new ProblemDetails(TYPE_ABOUT_BLANK, TITLE_UNPROCESSABLE_ENTITY, STATUS_UNPROCESSABLE_ENTITY, detail);
    }

    public static ProblemDetails serverError(String detail) {
        return new ProblemDetails(TYPE_ABOUT_BLANK, TITLE_INTERNAL_SERVER_ERROR, STATUS_INTERNAL_SERVER_ERROR, detail);
    }
}
