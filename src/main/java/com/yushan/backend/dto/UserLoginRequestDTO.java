package com.yushan.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDTO {
    @NotBlank(message = "email cannot be empty")
    private String email;
    @NotBlank(message = "password cannot be empty")
    private String password;

    public String getEmail() {
        return email != null ? email.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }
}
