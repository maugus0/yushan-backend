package com.yushan.backend.dto;

import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.UUID;

public class ChapterUpdateRequestDTO {
    @NotNull(message = "Chapter UUID is required")
    private UUID uuid;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private String content;

    private Integer wordCnt;

    private Boolean isPremium;

    @Min(value = 0, message = "Yuan cost cannot be negative")
    private Float yuanCost;

    private Boolean isValid;

    private Date publishTime;

    // Constructors
    public ChapterUpdateRequestDTO() {}

    public ChapterUpdateRequestDTO(UUID uuid, String title, String content, Integer wordCnt,
                                   Boolean isPremium, Float yuanCost, Boolean isValid,
                                   Date publishTime) {
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.wordCnt = wordCnt;
        this.isPremium = isPremium;
        this.yuanCost = yuanCost;
        this.isValid = isValid;
        this.publishTime = publishTime != null ? (Date) publishTime.clone() : null;
    }

    // Getters and Setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title.trim() : null; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getWordCnt() { return wordCnt; }
    public void setWordCnt(Integer wordCnt) { this.wordCnt = wordCnt; }

    public Boolean getIsPremium() { return isPremium; }
    public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }

    public Float getYuanCost() { return yuanCost; }
    public void setYuanCost(Float yuanCost) { this.yuanCost = yuanCost; }

    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }

    public Date getPublishTime() { 
        return publishTime != null ? (Date) publishTime.clone() : null; 
    }
    public void setPublishTime(Date publishTime) { 
        this.publishTime = publishTime != null ? (Date) publishTime.clone() : null; 
    }
}
