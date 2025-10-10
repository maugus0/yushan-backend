package com.yushan.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class ChapterSearchRequestDTO {
    private Integer novelId;

    private Integer chapterNumber;

    private String titleKeyword;

    private Boolean isPremium;

    private Boolean isValid;

    private Boolean publishedOnly;

    @Min(value = 1, message = "Page must be at least 1")
    private Integer page = 1;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private Integer pageSize = 20;

    private String sortBy = "chapterNumber";

    private String sortOrder = "asc";

    // Constructors
    public ChapterSearchRequestDTO() {}

    public ChapterSearchRequestDTO(Integer novelId, Integer chapterNumber, String titleKeyword,
                                   Boolean isPremium, Boolean isValid, Boolean publishedOnly,
                                   Integer page, Integer pageSize, String sortBy, String sortOrder) {
        this.novelId = novelId;
        this.chapterNumber = chapterNumber;
        this.titleKeyword = titleKeyword;
        this.isPremium = isPremium;
        this.isValid = isValid;
        this.publishedOnly = publishedOnly;
        this.page = page;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    // Getters and Setters
    public Integer getNovelId() { return novelId; }
    public void setNovelId(Integer novelId) { this.novelId = novelId; }

    public Integer getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(Integer chapterNumber) { this.chapterNumber = chapterNumber; }

    public String getTitleKeyword() { return titleKeyword; }
    public void setTitleKeyword(String titleKeyword) {
        this.titleKeyword = titleKeyword != null ? titleKeyword.trim() : null;
    }

    public Boolean getIsPremium() { return isPremium; }
    public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }

    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }

    public Boolean getPublishedOnly() { return publishedOnly; }
    public void setPublishedOnly(Boolean publishedOnly) { this.publishedOnly = publishedOnly; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    // Helper method to calculate offset for pagination
    public int getOffset() {
        return (page - 1) * pageSize;
    }
}