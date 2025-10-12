package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PlatformStatisticsResponseDTO {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;
    
    // User Statistics
    private Long totalUsers;
    private Long activeUsers;
    private Long newUsersToday;
    private Long authors;
    private Long admins;
    
    // Content Statistics
    private Long totalNovels;
    private Long publishedNovels;
    private Long completedNovels;
    private Long totalChapters;
    private Long totalWords;
    
    // Engagement Statistics
    private Long totalViews;
    private Long totalComments;
    private Long totalReviews;
    private Long totalVotes;
    private Double averageRating;
    
    // Activity Statistics
    private Long dailyActiveUsers;
    private Long weeklyActiveUsers;
    private Long monthlyActiveUsers;
    
    // Growth Statistics
    private Double userGrowthRate;
    private Double contentGrowthRate;
    private Double engagementGrowthRate;
    
    public PlatformStatisticsResponseDTO() {}
    
    public Date getTimestamp() {
        return timestamp != null ? new Date(timestamp.getTime()) : null;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp != null ? new Date(timestamp.getTime()) : null;
    }
}
