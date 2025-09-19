package com.yushan.backend.entity;

import java.util.Date;
import java.util.UUID;

public class Review {
    private Integer id;

    private UUID uuid;

    private UUID userId;

    private Integer novelId;

    private Integer rating;

    private String title;

    private String content;

    private Integer likeCnt;

    private Boolean isSpoiler;

    private Date createTime;

    private Date updateTime;

    public Review(Integer id, UUID uuid, UUID userId, Integer novelId, Integer rating, String title, String content, Integer likeCnt, Boolean isSpoiler, Date createTime, Date updateTime) {
        this.id = id;
        this.uuid = uuid;
        this.userId = userId;
        this.novelId = novelId;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.likeCnt = likeCnt;
        this.isSpoiler = isSpoiler;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Review() {
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getNovelId() {
        return novelId;
    }

    public void setNovelId(Integer novelId) {
        this.novelId = novelId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
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

    public Integer getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt(Integer likeCnt) {
        this.likeCnt = likeCnt;
    }

    public Boolean getIsSpoiler() {
        return isSpoiler;
    }

    public void setIsSpoiler(Boolean isSpoiler) {
        this.isSpoiler = isSpoiler;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}