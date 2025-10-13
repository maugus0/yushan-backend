package com.yushan.backend.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class LibraryRequestDTO {
    @Min(value = 1, message = "progress must be greater than or equal to 1")
    private Integer progress;
}
