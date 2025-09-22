package com.yushan.backend.entity;

import java.util.Date;
import java.util.UUID;

public class Novel {
    private Integer id;

    private UUID uuid;

    private String title;

    private Integer authorId;

    private String authorName;

    private Integer categoryId;

    private String synopsis;

    private String coverImgUrl;

    private Integer status;

    private Boolean isCompleted;

    private Boolean isValid;

    private Integer chapterCnt;

    private Long wordCnt;

    private Float avgRating;

    private Integer reviewCnt;

    private Long viewCnt;

    private Integer voteCnt;

    private Float yuanCnt;

    private Date createTime;

    private Date updateTime;

    private Date publishTime;

    public Novel(Integer id, UUID uuid, String title, Integer authorId, String authorName, Integer categoryId, String synopsis, String coverImgUrl, Integer status, Boolean isCompleted, Boolean isValid, Integer chapterCnt, Long wordCnt, Float avgRating, Integer reviewCnt, Long viewCnt, Integer voteCnt, Float yuanCnt, Date createTime, Date updateTime, Date publishTime) {
        this.id = id;
        this.uuid = uuid;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.categoryId = categoryId;
        this.synopsis = synopsis;
        this.coverImgUrl = coverImgUrl;
        this.status = status;
        this.isCompleted = isCompleted;
        this.isValid = isValid;
        this.chapterCnt = chapterCnt;
        this.wordCnt = wordCnt;
        this.avgRating = avgRating;
        this.reviewCnt = reviewCnt;
        this.viewCnt = viewCnt;
        this.voteCnt = voteCnt;
        this.yuanCnt = yuanCnt;
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
        this.publishTime = publishTime != null ? new Date(publishTime.getTime()) : null;
    }

    public Novel() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName == null ? null : authorName.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis == null ? null : synopsis.trim();
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl == null ? null : coverImgUrl.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Integer getChapterCnt() {
        return chapterCnt;
    }

    public void setChapterCnt(Integer chapterCnt) {
        this.chapterCnt = chapterCnt;
    }

    public Long getWordCnt() {
        return wordCnt;
    }

    public void setWordCnt(Long wordCnt) {
        this.wordCnt = wordCnt;
    }

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getReviewCnt() {
        return reviewCnt;
    }

    public void setReviewCnt(Integer reviewCnt) {
        this.reviewCnt = reviewCnt;
    }

    public Long getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(Long viewCnt) {
        this.viewCnt = viewCnt;
    }

    public Integer getVoteCnt() {
        return voteCnt;
    }

    public void setVoteCnt(Integer voteCnt) {
        this.voteCnt = voteCnt;
    }

    public Float getYuanCnt() {
        return yuanCnt;
    }

    public void setYuanCnt(Float yuanCnt) {
        this.yuanCnt = yuanCnt;
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

    public Date getPublishTime() {
        return publishTime != null ? new Date(publishTime.getTime()) : null;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime != null ? new Date(publishTime.getTime()) : null;
    }
}