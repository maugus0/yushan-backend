package com.yushan.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class EmailVerificationRequestDTO {

    @Schema(description = "New email to verify before changing",
            example = "newuser@example.com")
    private String email;

    // Getters and Setters
    public String getEmail() {
        return email != null ? email.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }
}


