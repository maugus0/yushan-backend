package com.yushan.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchRequestDTO {
    @NotEmpty(message = "ids can not be empty")
    private List<Integer> ids;
}
