package com.yushan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class CommentListResponseDTO {
    private List<CommentResponseDTO> comments;
    private long totalCount;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public CommentListResponseDTO(List<CommentResponseDTO> comments, long totalCount, int totalPages, int currentPage, int pageSize) {
        this.comments = comments != null ? new ArrayList<>(comments) : null;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public List<CommentResponseDTO> getComments() {
        return comments != null ? new ArrayList<>(comments) : null;
    }

    public void setComments(List<CommentResponseDTO> comments) {
        this.comments = comments != null ? new ArrayList<>(comments) : null;
    }

    public static class CommentListResponseDTOBuilder {
        private List<CommentResponseDTO> comments;

        public CommentListResponseDTOBuilder comments(List<CommentResponseDTO> comments) {
            this.comments = comments != null ? new ArrayList<>(comments) : null;
            return this;
        }
    }
}
