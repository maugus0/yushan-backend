package com.yushan.backend.dto;

public class ChapterStatisticsResponseDTO {
    private Integer novelId;
    private Long totalChapters;
    private Long publishedChapters;
    private Long draftChapters;
    private Long scheduledChapters;
    private Long premiumChapters;
    private Long freeChapters;
    private Long totalWordCount;
    private Long totalViewCount;
    private Float totalRevenue;
    private Integer maxChapterNumber;
    private ChapterSummary latestChapter;
    private ChapterSummary mostViewedChapter;

    // Nested class for chapter summary in statistics
    public static class ChapterSummary {
        private Integer chapterNumber;
        private String title;
        private Long viewCnt;

        // Constructors
        public ChapterSummary() {}

        public ChapterSummary(Integer chapterNumber, String title, Long viewCnt) {
            this.chapterNumber = chapterNumber;
            this.title = title;
            this.viewCnt = viewCnt;
        }

        // Copy constructor
        public ChapterSummary(ChapterSummary other) {
            if (other != null) {
                this.chapterNumber = other.chapterNumber;
                this.title = other.title;
                this.viewCnt = other.viewCnt;
            }
        }

        // Getters and Setters
        public Integer getChapterNumber() { return chapterNumber; }
        public void setChapterNumber(Integer chapterNumber) { this.chapterNumber = chapterNumber; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Long getViewCnt() { return viewCnt; }
        public void setViewCnt(Long viewCnt) { this.viewCnt = viewCnt; }
    }

    // Constructors
    public ChapterStatisticsResponseDTO() {}

    public ChapterStatisticsResponseDTO(Integer novelId, Long totalChapters, Long publishedChapters,
                                        Long draftChapters, Long scheduledChapters, Long premiumChapters,
                                        Long freeChapters, Long totalWordCount, Long totalViewCount,
                                        Float totalRevenue, Integer maxChapterNumber) {
        this.novelId = novelId;
        this.totalChapters = totalChapters;
        this.publishedChapters = publishedChapters;
        this.draftChapters = draftChapters;
        this.scheduledChapters = scheduledChapters;
        this.premiumChapters = premiumChapters;
        this.freeChapters = freeChapters;
        this.totalWordCount = totalWordCount;
        this.totalViewCount = totalViewCount;
        this.totalRevenue = totalRevenue;
        this.maxChapterNumber = maxChapterNumber;
    }

    // Getters and Setters
    public Integer getNovelId() { return novelId; }
    public void setNovelId(Integer novelId) { this.novelId = novelId; }

    public Long getTotalChapters() { return totalChapters; }
    public void setTotalChapters(Long totalChapters) { this.totalChapters = totalChapters; }

    public Long getPublishedChapters() { return publishedChapters; }
    public void setPublishedChapters(Long publishedChapters) { this.publishedChapters = publishedChapters; }

    public Long getDraftChapters() { return draftChapters; }
    public void setDraftChapters(Long draftChapters) { this.draftChapters = draftChapters; }

    public Long getScheduledChapters() { return scheduledChapters; }
    public void setScheduledChapters(Long scheduledChapters) { this.scheduledChapters = scheduledChapters; }

    public Long getPremiumChapters() { return premiumChapters; }
    public void setPremiumChapters(Long premiumChapters) { this.premiumChapters = premiumChapters; }

    public Long getFreeChapters() { return freeChapters; }
    public void setFreeChapters(Long freeChapters) { this.freeChapters = freeChapters; }

    public Long getTotalWordCount() { return totalWordCount; }
    public void setTotalWordCount(Long totalWordCount) { this.totalWordCount = totalWordCount; }

    public Long getTotalViewCount() { return totalViewCount; }
    public void setTotalViewCount(Long totalViewCount) { this.totalViewCount = totalViewCount; }

    public Float getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Float totalRevenue) { this.totalRevenue = totalRevenue; }

    public Integer getMaxChapterNumber() { return maxChapterNumber; }
    public void setMaxChapterNumber(Integer maxChapterNumber) { this.maxChapterNumber = maxChapterNumber; }

    public ChapterSummary getLatestChapter() { 
        return latestChapter != null ? new ChapterSummary(latestChapter) : null; 
    }
    public void setLatestChapter(ChapterSummary latestChapter) { 
        this.latestChapter = latestChapter != null ? new ChapterSummary(latestChapter) : null; 
    }

    public ChapterSummary getMostViewedChapter() { 
        return mostViewedChapter != null ? new ChapterSummary(mostViewedChapter) : null; 
    }
    public void setMostViewedChapter(ChapterSummary mostViewedChapter) { 
        this.mostViewedChapter = mostViewedChapter != null ? new ChapterSummary(mostViewedChapter) : null; 
    }
}