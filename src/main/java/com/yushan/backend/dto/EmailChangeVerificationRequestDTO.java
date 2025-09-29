package com.yushan.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class EmailChangeVerificationRequestDTO {

    @Schema(description = "New email to verify before changing",
            example = "newuser@example.com")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


