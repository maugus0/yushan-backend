package com.yushan.backend.dto;

import jakarta.validation.constraints.*;
import java.util.Date;

public class ChapterCreateRequestDTO {
    @NotNull(message = "Novel ID is required")
    private Integer novelId;

    @NotNull(message = "Chapter number is required")
    @Min(value = 1, message = "Chapter number must be at least 1")
    private Integer chapterNumber;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Integer wordCnt;

    private Boolean isPremium = false;

    @Min(value = 0, message = "Yuan cost cannot be negative")
    private Float yuanCost = 0.0f;

    private Boolean isValid = true;

    private Date publishTime;

    // Constructors
    public ChapterCreateRequestDTO() {}

    public ChapterCreateRequestDTO(Integer novelId, Integer chapterNumber, String title,
                                   String content, Integer wordCnt, Boolean isPremium,
                                   Float yuanCost, Boolean isValid, Date publishTime) {
        this.novelId = novelId;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.content = content;
        this.wordCnt = wordCnt;
        this.isPremium = isPremium;
        this.yuanCost = yuanCost;
        this.isValid = isValid;
        this.publishTime = publishTime;
    }

    // Getters and Setters
    public Integer getNovelId() { return novelId; }
    public void setNovelId(Integer novelId) { this.novelId = novelId; }

    public Integer getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(Integer chapterNumber) { this.chapterNumber = chapterNumber; }

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

    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
}
