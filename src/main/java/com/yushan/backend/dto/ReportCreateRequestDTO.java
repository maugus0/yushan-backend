package com.yushan.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportCreateRequestDTO {
    @NotNull(message = "reportType must not be null")
    private String reportType;

    @NotBlank(message = "reason must not be blank")
    @Size(max = 1000, message = "reason must be at most 1000 characters")
    private String reason;
}
