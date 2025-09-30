package com.yushan.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequestDTO {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}