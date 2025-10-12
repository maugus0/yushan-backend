package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TopContentResponseDTO {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    
    private List<TopNovel> topNovels;
    private List<TopAuthor> topAuthors;
    private List<TopCategory> topCategories;
    
    public static class TopNovel {
        private Integer id;
        private String title;
        private String authorName;
        private String categoryName;
        private Long viewCount;
        private Long voteCount;
        private Double rating;
        private Integer chapterCount;
        private Long wordCount;
        
        public TopNovel() {}
        
        public TopNovel(Integer id, String title, String authorName, String categoryName, 
                       Long viewCount, Long voteCount, Double rating, Integer chapterCount, Long wordCount) {
            this.id = id;
            this.title = title;
            this.authorName = authorName;
            this.categoryName = categoryName;
            this.viewCount = viewCount;
            this.voteCount = voteCount;
            this.rating = rating;
            this.chapterCount = chapterCount;
            this.wordCount = wordCount;
        }
        
        // Getters and Setters for TopNovel
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getAuthorName() { return authorName; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }
        
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public Long getViewCount() { return viewCount; }
        public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
        
        public Long getVoteCount() { return voteCount; }
        public void setVoteCount(Long voteCount) { this.voteCount = voteCount; }
        
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        
        public Integer getChapterCount() { return chapterCount; }
        public void setChapterCount(Integer chapterCount) { this.chapterCount = chapterCount; }
        
        public Long getWordCount() { return wordCount; }
        public void setWordCount(Long wordCount) { this.wordCount = wordCount; }
    }
    
    public static class TopAuthor {
        private String authorId;
        private String authorName;
        private Integer novelCount;
        private Long totalViews;
        private Long totalVotes;
        private Double averageRating;
        private Long totalWords;
        
        public TopAuthor() {}
        
        public TopAuthor(String authorId, String authorName, Integer novelCount, 
                        Long totalViews, Long totalVotes, Double averageRating, Long totalWords) {
            this.authorId = authorId;
            this.authorName = authorName;
            this.novelCount = novelCount;
            this.totalViews = totalViews;
            this.totalVotes = totalVotes;
            this.averageRating = averageRating;
            this.totalWords = totalWords;
        }
        
        // Getters and Setters for TopAuthor
        public String getAuthorId() { return authorId; }
        public void setAuthorId(String authorId) { this.authorId = authorId; }
        
        public String getAuthorName() { return authorName; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }
        
        public Integer getNovelCount() { return novelCount; }
        public void setNovelCount(Integer novelCount) { this.novelCount = novelCount; }
        
        public Long getTotalViews() { return totalViews; }
        public void setTotalViews(Long totalViews) { this.totalViews = totalViews; }
        
        public Long getTotalVotes() { return totalVotes; }
        public void setTotalVotes(Long totalVotes) { this.totalVotes = totalVotes; }
        
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
        
        public Long getTotalWords() { return totalWords; }
        public void setTotalWords(Long totalWords) { this.totalWords = totalWords; }
    }
    
    public static class TopCategory {
        private Integer categoryId;
        private String categoryName;
        private Integer novelCount;
        private Long totalViews;
        private Long totalVotes;
        private Double averageRating;
        
        public TopCategory() {}
        
        public TopCategory(Integer categoryId, String categoryName, Integer novelCount,
                          Long totalViews, Long totalVotes, Double averageRating) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.novelCount = novelCount;
            this.totalViews = totalViews;
            this.totalVotes = totalVotes;
            this.averageRating = averageRating;
        }
        
        // Getters and Setters for TopCategory
        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
        
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public Integer getNovelCount() { return novelCount; }
        public void setNovelCount(Integer novelCount) { this.novelCount = novelCount; }
        
        public Long getTotalViews() { return totalViews; }
        public void setTotalViews(Long totalViews) { this.totalViews = totalViews; }
        
        public Long getTotalVotes() { return totalVotes; }
        public void setTotalVotes(Long totalVotes) { this.totalVotes = totalVotes; }
        
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    }
    
    public TopContentResponseDTO() {}
    
    public Date getDate() {
        return date != null ? new Date(date.getTime()) : null;
    }
    
    public void setDate(Date date) {
        this.date = date != null ? new Date(date.getTime()) : null;
    }
    
    public List<TopNovel> getTopNovels() {
        return topNovels != null ? new ArrayList<>(topNovels) : new ArrayList<>();
    }

    public void setTopNovels(List<TopNovel> topNovels) {
        this.topNovels = topNovels != null ? new ArrayList<>(topNovels) : new ArrayList<>();
    }
    
    public List<TopAuthor> getTopAuthors() {
        return topAuthors != null ? new ArrayList<>(topAuthors) : new ArrayList<>();
    }

    public void setTopAuthors(List<TopAuthor> topAuthors) {
        this.topAuthors = topAuthors != null ? new ArrayList<>(topAuthors) : new ArrayList<>();
    }
    
    public List<TopCategory> getTopCategories() {
        return topCategories != null ? new ArrayList<>(topCategories) : new ArrayList<>();
    }

    public void setTopCategories(List<TopCategory> topCategories) {
        this.topCategories = topCategories != null ? new ArrayList<>(topCategories) : new ArrayList<>();
    }
}
