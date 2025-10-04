package com.yushan.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LibraryRequestDTO {
    @NotNull(message = "progress is required")
    @Min(value = 1, message = "progress must be greater than or equal to 1")
    private Integer progress;
}
