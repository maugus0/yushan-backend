package com.yushan.backend.common;

import lombok.Getter;

@Getter
public enum ResultStatus {
    SUCCESS(200, "success"),
    ERROR(500, "error"),
    NO_AUTH(401, "no_auth"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "not_found"),
    BAD_REQUEST(400, "bad_request");

    private final Integer code;
    private final String message;

    ResultStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}