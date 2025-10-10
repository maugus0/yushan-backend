package com.yushan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Integer id;
    private UUID userId;
    private String username; // User's display name (from User entity)
    private Integer chapterId;
    private String chapterTitle; // Chapter title for context (optional)
    private String content;
    private Integer likeCnt;
    private Boolean isSpoiler;
    private Date createTime;
    private Date updateTime;
    private Boolean isOwnComment; // Whether the current user owns this comment
}
