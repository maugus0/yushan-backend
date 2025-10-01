package com.yushan.backend.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class NovelDetailResponseDTO {
    // Primary key ID
    private Integer id;
    // UUID identifier
    private UUID uuid;
    // Title of the novel
    private String title;

    // Author basic info
    private java.util.UUID authorId;
    private String authorUsername;

    // Category basic info
    private Integer categoryId;
    private String categoryName;

    // Short summary
    private String synopsis;
    // Cover image URL
    private String coverImgUrl;
    // Status string (DRAFT/PUBLISHED/ARCHIVED)
    private String status;
    // Whether the novel is completed
    private Boolean isCompleted;
    // Derived counters
    private Integer chapterCnt;
    private Long wordCnt;
    private Float avgRating;
    private Integer reviewCnt;
    private Long viewCnt;
    private Integer voteCnt;
    private Float yuanCnt;
    // Timestamps
    private Date publishTime;
    private Date createTime;
    private Date updateTime;

    public Date getPublishTime() {
        return publishTime == null ? null : new Date(publishTime.getTime());
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime == null ? null : new Date(publishTime.getTime());
    }

    public Date getCreateTime() {
        return createTime == null ? null : new Date(createTime.getTime());
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime == null ? null : new Date(createTime.getTime());
    }

    public Date getUpdateTime() {
        return updateTime == null ? null : new Date(updateTime.getTime());
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime == null ? null : new Date(updateTime.getTime());
    }
}


