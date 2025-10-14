package com.yushan.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentBulkSpoilerUpdateRequestDTO {
    @NotEmpty(message = "commentIds must not be empty")
    private List<Integer> commentIds;

    @NotNull(message = "isSpoiler must not be null")
    private Boolean isSpoiler;

    public List<Integer> getCommentIds() {
        return commentIds != null ? new ArrayList<>(commentIds) : null;
    }

    public void setCommentIds(List<Integer> commentIds) {
        this.commentIds = commentIds != null ? new ArrayList<>(commentIds) : null;
    }
}
