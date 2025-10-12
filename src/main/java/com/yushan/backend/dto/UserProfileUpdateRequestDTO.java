package com.yushan.backend.dto;

import com.yushan.backend.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for user profile update requests (write model)
 */
@Data
public class UserProfileUpdateRequestDTO {

    @Size(max = 20, message = "username must be at most 20 char")
    @Pattern(regexp = "^[a-zA-Z0-9_\\.\\-]*$", message = "username can only contain letters, numbers, underscores, dots, and hyphens")
    private String username;

    @Size(max = 254, message = "email length pasts limitation")
    private String email;

    @Size(max = 512, message = "avatarUrl must be at most 512 characters")
    private String avatarUrl;

    @Size(max = 1000, message = "profileDetail must be at most 1000 characters")
    private String profileDetail;

    private Gender gender;

    // Verification code for email change
    @Size(max = 6, message = "code must be at most 6 char")
    private String verificationCode;

    public void setUsername(String username) {
        // Only validate username format if username is provided and not empty
        if (username != null && !username.trim().isEmpty()) {
            if (username.length() < 3) {
                throw new IllegalArgumentException("username must be at least 3 characters");
            }
            if (!username.matches("^[a-zA-Z0-9_\\.\\-]+$")) {
                throw new IllegalArgumentException("username can only contain letters, numbers, underscores, dots, and hyphens");
            }
        }
        this.username = username;
    }

    public String getEmail() {
        return email != null ? email.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }

    public void setEmail(String email) {
        // Only validate email format if email is provided and not empty
        if (email != null && !email.trim().isEmpty()) {
            // Basic email format validation
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new IllegalArgumentException("email format is incorrect");
            }
        }
        this.email = email != null ? email.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }
}


