package com.yushan.backend.dao;

import com.yushan.backend.dto.AnalyticsTrendResponseDTO;
import com.yushan.backend.dto.AnalyticsSummaryResponseDTO;
import com.yushan.backend.dto.ReadingActivityResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface AnalyticsMapper {
    
    // User analytics
    List<AnalyticsTrendResponseDTO.TrendDataPoint> getUserTrends(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("period") String period);
    
    Long getTotalUsers(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getNewUsers(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getActiveUsers(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getAuthors(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Novel analytics
    List<AnalyticsTrendResponseDTO.TrendDataPoint> getNovelTrends(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("period") String period,
            @Param("categoryId") Integer categoryId,
            @Param("authorId") String authorId,
            @Param("status") Integer status);
    
    Long getTotalNovels(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getNewNovels(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getPublishedNovels(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getCompletedNovels(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Reading activity analytics
    List<ReadingActivityResponseDTO.ActivityDataPoint> getReadingActivityTrends(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("period") String period);
    
    Long getTotalViews(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getTotalChapters(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getTotalComments(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getTotalReviews(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Long getTotalVotes(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Summary analytics
    AnalyticsSummaryResponseDTO getAnalyticsSummary(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
    
    // Growth rate calculations
    Double getUserGrowthRate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Double getNovelGrowthRate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Double getViewGrowthRate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    // Average calculations
    Double getAverageRating(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Double getAverageViewsPerNovel(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Double getAverageCommentsPerNovel(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    Double getAverageReviewsPerNovel(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
