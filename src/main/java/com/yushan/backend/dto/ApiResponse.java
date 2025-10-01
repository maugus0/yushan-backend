package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yushan.backend.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // Static factory methods for common responses
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), "Success", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), message, data);
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), message, null);
    }
    
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }
    
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.getCode(), message, null);
    }
    
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message, T data) {
        return new ApiResponse<>(errorCode.getCode(), message, data);
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
    
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}
