package com.yushan.backend.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChapterListResponseDTO {
    private List<ChapterSummary> chapters;
    private Long totalCount;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;

    // Nested class for chapter summary
    public static class ChapterSummary {
        private UUID uuid;
        private Integer chapterNumber;
        private String title;
        private String contentPreview;
        private Integer wordCnt;
        private Boolean isPremium;
        private Float yuanCost;
        private Long viewCnt;
        private Date publishTime;

        // Constructors
        public ChapterSummary() {}

        public ChapterSummary(UUID uuid, Integer chapterNumber, String title,
                              String contentPreview, Integer wordCnt, Boolean isPremium,
                              Float yuanCost, Long viewCnt, Date publishTime) {
            this.uuid = uuid;
            this.chapterNumber = chapterNumber;
            this.title = title;
            this.contentPreview = contentPreview;
            this.wordCnt = wordCnt;
            this.isPremium = isPremium;
            this.yuanCost = yuanCost;
            this.viewCnt = viewCnt;
            this.publishTime = publishTime;
        }

        // Getters and Setters
        public UUID getUuid() { return uuid; }
        public void setUuid(UUID uuid) { this.uuid = uuid; }

        public Integer getChapterNumber() { return chapterNumber; }
        public void setChapterNumber(Integer chapterNumber) { this.chapterNumber = chapterNumber; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContentPreview() { return contentPreview; }
        public void setContentPreview(String contentPreview) { this.contentPreview = contentPreview; }

        public Integer getWordCnt() { return wordCnt; }
        public void setWordCnt(Integer wordCnt) { this.wordCnt = wordCnt; }

        public Boolean getIsPremium() { return isPremium; }
        public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }

        public Float getYuanCost() { return yuanCost; }
        public void setYuanCost(Float yuanCost) { this.yuanCost = yuanCost; }

        public Long getViewCnt() { return viewCnt; }
        public void setViewCnt(Long viewCnt) { this.viewCnt = viewCnt; }

        public Date getPublishTime() { return publishTime; }
        public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }
    }

    // Constructors
    public ChapterListResponseDTO() {}

    public ChapterListResponseDTO(List<ChapterSummary> chapters, Long totalCount,
                                  Integer currentPage, Integer pageSize, Integer totalPages) {
        this.chapters = chapters;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    // Getters and Setters
    public List<ChapterSummary> getChapters() { return chapters; }
    public void setChapters(List<ChapterSummary> chapters) { this.chapters = chapters; }

    public Long getTotalCount() { return totalCount; }
    public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }

    public Integer getCurrentPage() { return currentPage; }
    public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
}
