package com.yushan.backend.entity;

import java.util.Date;
import java.util.UUID;

public class Chapter {
    private Integer id;

    private UUID uuid;

    private Integer novelId;

    private Integer chapterNumber;

    private String title;

    private String content;

    private Integer wordCnt;

    private Boolean isPremium;

    private Float yuanCost;

    private Long viewCnt;

    private Boolean isValid;

    private Date createTime;

    private Date updateTime;

    private Date publishTime;

    public Chapter(Integer id, UUID uuid, Integer novelId, Integer chapterNumber, String title, String content, Integer wordCnt, Boolean isPremium, Float yuanCost, Long viewCnt, Boolean isValid, Date createTime, Date updateTime, Date publishTime) {
        this.id = id;
        this.uuid = uuid;
        this.novelId = novelId;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.content = content;
        this.wordCnt = wordCnt;
        this.isPremium = isPremium;
        this.yuanCost = yuanCost;
        this.viewCnt = viewCnt;
        this.isValid = isValid;
        this.createTime = createTime != null ? new Date(createTime.getTime()) : null;
        this.updateTime = updateTime != null ? new Date(updateTime.getTime()) : null;
        this.publishTime = publishTime != null ? new Date(publishTime.getTime()) : null;
    }

    public Chapter() {
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

    public Integer getNovelId() {
        return novelId;
    }

    public void setNovelId(Integer novelId) {
        this.novelId = novelId;
    }

    public Integer getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(Integer chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getWordCnt() {
        return wordCnt;
    }

    public void setWordCnt(Integer wordCnt) {
        this.wordCnt = wordCnt;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public Float getYuanCost() {
        return yuanCost;
    }

    public void setYuanCost(Float yuanCost) {
        this.yuanCost = yuanCost;
    }

    public Long getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(Long viewCnt) {
        this.viewCnt = viewCnt;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
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