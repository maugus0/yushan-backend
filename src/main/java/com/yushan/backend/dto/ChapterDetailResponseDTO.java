package com.yushan.backend.dto;

import java.util.Date;
import java.util.UUID;

public class ChapterDetailResponseDTO {
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

    // Navigation links
    private UUID previousChapterUuid;
    private UUID nextChapterUuid;

    // Constructors
    public ChapterDetailResponseDTO() {}

    public ChapterDetailResponseDTO(UUID uuid, Integer novelId, Integer chapterNumber,
                                    String title, String content, Integer wordCnt,
                                    Boolean isPremium, Float yuanCost, Long viewCnt,
                                    Boolean isValid, Date createTime, Date updateTime,
                                    Date publishTime) {
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
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.publishTime = publishTime;
    }

    // Getters and Setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public Integer getNovelId() { return novelId; }
    public void setNovelId(Integer novelId) { this.novelId = novelId; }

    public Integer getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(Integer chapterNumber) { this.chapterNumber = chapterNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getWordCnt() { return wordCnt; }
    public void setWordCnt(Integer wordCnt) { this.wordCnt = wordCnt; }

    public Boolean getIsPremium() { return isPremium; }
    public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }

    public Float getYuanCost() { return yuanCost; }
    public void setYuanCost(Float yuanCost) { this.yuanCost = yuanCost; }

    public Long getViewCnt() { return viewCnt; }
    public void setViewCnt(Long viewCnt) { this.viewCnt = viewCnt; }

    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }

    public UUID getPreviousChapterUuid() { return previousChapterUuid; }
    public void setPreviousChapterUuid(UUID previousChapterUuid) { this.previousChapterUuid = previousChapterUuid; }

    public UUID getNextChapterUuid() { return nextChapterUuid; }
    public void setNextChapterUuid(UUID nextChapterUuid) { this.nextChapterUuid = nextChapterUuid; }
}