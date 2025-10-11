package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AnalyticsSummaryResponseDTO {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;
    
    private String period;
    
    // User metrics
    private Long totalUsers;
    private Long newUsers;
    private Long activeUsers;
    private Long authors;
    
    // Novel metrics
    private Long totalNovels;
    private Long newNovels;
    private Long publishedNovels;
    private Long completedNovels;
    
    // Reading activity metrics
    private Long totalViews;
    private Long totalChapters;
    private Long totalComments;
    private Long totalReviews;
    private Long totalVotes;
    
    // Engagement metrics
    private Double averageRating;
    private Double averageViewsPerNovel;
    private Double averageCommentsPerNovel;
    private Double averageReviewsPerNovel;
    
    // Growth metrics
    private Double userGrowthRate;
    private Double novelGrowthRate;
    private Double viewGrowthRate;
    
    public AnalyticsSummaryResponseDTO() {}
    
    public Date getStartDate() {
        return startDate != null ? new Date(startDate.getTime()) : null;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
    }
    
    public Date getEndDate() {
        return endDate != null ? new Date(endDate.getTime()) : null;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate != null ? new Date(endDate.getTime()) : null;
    }
}
