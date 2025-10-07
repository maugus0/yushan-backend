package com.yushan.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for author upgrade request
 * Used for POST /api/author/upgrade-to-author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorUpgradeRequestDTO {
    
    @NotBlank(message = "Verification code is required")
    @Size(min = 6, max = 6, message = "Verification code must be exactly 6 characters")
    private String verificationCode;
}

