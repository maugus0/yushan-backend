package com.yushan.backend.dto;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class NovelRankingResponseDTO {
    // Novel basic info
    private Integer id;
    private UUID uuid;
    private String title;
    
    // Author info
    private UUID authorId;
    private String authorName;
    
    // Category info
    private Integer categoryId;
    private String categoryName;
    
    // Novel details
    private String synopsis;
    private String coverImgUrl;
    private String status;
    private Boolean isCompleted;
    
    // Ranking metrics
    private Long viewCnt;
    private Integer voteCnt;
    private Float avgRating;
    private Integer reviewCnt;
    private Integer chapterCnt;
    private Long wordCnt;
    private Float yuanCnt;
    
    // Ranking position
    private Integer ranking;
    private String sortType; // "view" or "vote"
    private Integer categoryRanking; // ranking within category
    private Integer overallRanking; // ranking overall
    
    // Timestamps
    private Date createTime;
    private Date updateTime;
    private Date publishTime;

    // Defensive copying for Date fields to prevent exposure of internal representation
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

    public Date getPublishTime() {
        return publishTime == null ? null : new Date(publishTime.getTime());
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime == null ? null : new Date(publishTime.getTime());
    }
}
