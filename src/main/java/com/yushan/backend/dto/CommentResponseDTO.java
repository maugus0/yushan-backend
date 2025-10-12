package com.yushan.backend.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
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

    public CommentResponseDTO(Integer id, UUID userId, String username, Integer chapterId, String chapterTitle, String content, Integer likeCnt, Boolean isSpoiler, Date createTime, Date updateTime, Boolean isOwnComment) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.chapterId = chapterId;
        this.chapterTitle = chapterTitle;
        this.content = content;
        this.likeCnt = likeCnt;
        this.isSpoiler = isSpoiler;
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
        this.isOwnComment = isOwnComment;
    }

    public Date getCreateTime() {
        return createTime != null ? new Date(createTime.getTime()) : null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
    }

    public Date getUpdateTime() {
        return updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
    }

    public static class CommentResponseDTOBuilder {
        private Date createTime;
        private Date updateTime;

        public CommentResponseDTOBuilder createTime(Date createTime) {
            this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
            return this;
        }

        public CommentResponseDTOBuilder updateTime(Date updateTime) {
            this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
            return this;
        }
    }
}
