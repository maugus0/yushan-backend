package com.yushan.backend.enums;

public enum ErrorCode {
    // Success
    SUCCESS(0, "Success"),
    
    // Client errors (4xx)
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    VALIDATION_ERROR(400, "Validation Error"),
    
    // Server errors (5xx)
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
