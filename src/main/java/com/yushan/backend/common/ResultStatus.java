package com.yushan.backend.common;

import lombok.Getter;

@Getter
public enum ResultStatus {
    SUCCESS(200, "success"),
    ERROR(500, "error"),
    NO_AUTH(401, "no_auth"),
    FORBIDDEN(403, "forbidden");

    private final Integer code;
    private final String message;

    ResultStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}