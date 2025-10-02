package com.yushan.backend.exception;

import com.yushan.backend.dto.ApiResponse;
import com.yushan.backend.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * handle authorization denied exception
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorizationDeniedException(WebRequest request) {
        ApiResponse<Object> errorResponse = ApiResponse.error(
            ErrorCode.UNAUTHORIZED, 
            "Access denied"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * handle custom exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        ApiResponse<Object> errorResponse = ApiResponse.error(
            ErrorCode.NOT_FOUND, 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(ValidationException e, WebRequest request) {
        ApiResponse<Object> errorResponse = ApiResponse.error(
            ErrorCode.VALIDATION_ERROR, 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException e, WebRequest request) {
        ApiResponse<Object> errorResponse = ApiResponse.error(
            ErrorCode.UNAUTHORIZED, 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbiddenException(ForbiddenException e, WebRequest request) {
        ApiResponse<Object> errorResponse = ApiResponse.error(
            ErrorCode.FORBIDDEN, 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * handle method argument not valid exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e, WebRequest request) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            if (errorMessage.length() > 0) {
                errorMessage.append("; ");
            }
            errorMessage.append(error.getDefaultMessage());
        });
        
        ApiResponse<Object> errorResponse = ApiResponse.error(
            ErrorCode.VALIDATION_ERROR, 
            errorMessage.toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * handle bind exception
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException e, WebRequest request) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            if (errorMessage.length() > 0) {
                errorMessage.append("; ");
            }
            errorMessage.append(error.getDefaultMessage());
        });
        
        ApiResponse<Object> errorResponse = ApiResponse.error(
            ErrorCode.VALIDATION_ERROR, 
            errorMessage.toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * handle general exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e, WebRequest request) {
        ApiResponse<Object> errorResponse = ApiResponse.error(
            ErrorCode.INTERNAL_SERVER_ERROR, 
            "System error: " + e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

