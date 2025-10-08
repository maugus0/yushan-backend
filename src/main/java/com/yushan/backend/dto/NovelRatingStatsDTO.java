package com.yushan.backend.dto;

import lombok.Data;

@Data
public class NovelRatingStatsDTO {
    private Integer novelId;
    private String novelTitle;
    private Integer totalReviews;
    private Float averageRating;
    
    // Rating counts
    private Integer rating5Count;
    private Integer rating4Count;
    private Integer rating3Count;
    private Integer rating2Count;
    private Integer rating1Count;
    
    // Rating percentages
    private Float rating5Percentage;
    private Float rating4Percentage;
    private Float rating3Percentage;
    private Float rating2Percentage;
    private Float rating1Percentage;
}
