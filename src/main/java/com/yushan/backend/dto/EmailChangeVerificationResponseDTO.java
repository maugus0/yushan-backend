package com.yushan.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class EmailChangeVerificationResponseDTO {

    @Schema(example = "true")
    private boolean success;

    @Schema(example = "Verification code sent successfully")
    private String message;

    public EmailChangeVerificationResponseDTO() {}

    public EmailChangeVerificationResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


