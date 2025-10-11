package com.yushan.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportResolutionRequestDTO {
    @NotNull(message = "action must not be null")
    @Pattern(regexp = "^(RESOLVED|DISMISSED)$", message = "action must be either RESOLVED or DISMISSED")
    private String action; // RESOLVED, DISMISSED

    @NotBlank(message = "adminNotes must not be blank")
    @Size(max = 1000, message = "adminNotes must be at most 1000 characters")
    private String adminNotes;
}
