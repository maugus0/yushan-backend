package com.yushan.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmailVerificationRequestDTO {

    @Schema(description = "New email to verify before changing",
            example = "newuser@example.com")
    private String email;
}


