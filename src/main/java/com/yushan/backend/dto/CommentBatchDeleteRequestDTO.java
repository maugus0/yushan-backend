package com.yushan.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentBatchDeleteRequestDTO {
    @NotEmpty(message = "commentIds must not be empty")
    private List<Integer> commentIds;

    private String reason; // Optional: reason for deletion

    public List<Integer> getCommentIds() {
        return commentIds != null ? new ArrayList<>(commentIds) : null;
    }

    public void setCommentIds(List<Integer> commentIds) {
        this.commentIds = commentIds != null ? new ArrayList<>(commentIds) : null;
    }
}