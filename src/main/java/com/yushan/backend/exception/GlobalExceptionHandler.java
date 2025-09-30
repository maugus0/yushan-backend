package com.yushan.backend.exception;

import com.yushan.backend.common.Result;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * handle authorization denied exception
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public Result<?> handleAuthorizationDeniedException() {
        return Result.noAuth();
    }

    /**
     * handle runtime exception
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(NovelNotFoundException.class)
    public Result<?> handleNovelNotFound(NovelNotFoundException e) {
        return Result.notFound(e.getMessage());
    }

    /**
     * handle method argument not valid exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            if (errorMessage.length() > 0) {
                errorMessage.append("; ");
            }
            errorMessage.append(error.getDefaultMessage());
        });
        return Result.error(errorMessage.toString());
    }

    /**
     * handle bind exception
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            if (errorMessage.length() > 0) {
                errorMessage.append("; ");
            }
            errorMessage.append(error.getDefaultMessage());
        });
        return Result.error(errorMessage.toString());
    }

    /**
     * handle general exception
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.error("system error: " + e.getMessage());
    }

    //todo: handle business exception
}

