package com.yushan.backend.common;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    private Result(ResultStatus resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    private Result(ResultStatus resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(String message) {
        this.message = message;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultStatus.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultStatus.SUCCESS, data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResultStatus.ERROR.getCode(), message);
    }

    public static <T> Result<T> noAuth() {
        return new Result<>(ResultStatus.NO_AUTH);
    }

    public static <T> Result<T> forbidden() {
        return new Result<>(ResultStatus.FORBIDDEN);
    }
}