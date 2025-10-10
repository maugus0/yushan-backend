package com.yushan.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CommentBatchDeleteRequestDTO {
    @NotEmpty(message = "commentIds must not be empty")
    private List<Integer> commentIds;

    private String reason; // Optional: reason for deletion
}