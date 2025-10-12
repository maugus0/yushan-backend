package com.yushan.backend.service;

import com.yushan.backend.dao.AnalyticsMapper;
import com.yushan.backend.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsMapper analyticsMapper;

    /**
     * Get user trends with filtering and date range
     */
    public AnalyticsTrendResponseDTO getUserTrends(AnalyticsRequestDTO request) {
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        String period = request.getPeriod();

        // Set default date range if not provided
        if (startDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            if (endDate == null) {
                endDate = cal.getTime();
            }
            if (startDate == null) {
                cal.add(Calendar.DAY_OF_MONTH, -30); // Default to last 30 days
                startDate = cal.getTime();
            }
        }

        List<AnalyticsTrendResponseDTO.TrendDataPoint> dataPoints = 
            analyticsMapper.getUserTrends(startDate, endDate, period);

        // Calculate growth rates
        calculateGrowthRates(dataPoints);

        AnalyticsTrendResponseDTO response = new AnalyticsTrendResponseDTO();
        response.setPeriod(period);
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setDataPoints(dataPoints);
        response.setTotalCount(dataPoints.stream().mapToLong(dp -> dp.getCount()).sum());
        response.setAverageGrowth(calculateAverageGrowth(dataPoints));
        
        // Find peak value
        if (!dataPoints.isEmpty()) {
            AnalyticsTrendResponseDTO.TrendDataPoint peak = dataPoints.stream()
                .max((dp1, dp2) -> Long.compare(dp1.getCount(), dp2.getCount()))
                .orElse(null);
            if (peak != null) {
                response.setPeakValue(peak.getCount());
                response.setPeakDate(peak.getPeriodLabel());
            }
        }

        return response;
    }

    /**
     * Get novel trends with filtering and date range
     */
    public AnalyticsTrendResponseDTO getNovelTrends(AnalyticsRequestDTO request) {
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        String period = request.getPeriod();

        // Set default date range if not provided
        if (startDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            if (endDate == null) {
                endDate = cal.getTime();
            }
            if (startDate == null) {
                cal.add(Calendar.DAY_OF_MONTH, -30); // Default to last 30 days
                startDate = cal.getTime();
            }
        }

        List<AnalyticsTrendResponseDTO.TrendDataPoint> dataPoints = 
            analyticsMapper.getNovelTrends(startDate, endDate, period, 
                request.getCategoryId(), request.getAuthorId(), request.getStatus());

        // Calculate growth rates
        calculateGrowthRates(dataPoints);

        AnalyticsTrendResponseDTO response = new AnalyticsTrendResponseDTO();
        response.setPeriod(period);
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setDataPoints(dataPoints);
        response.setTotalCount(dataPoints.stream().mapToLong(dp -> dp.getCount()).sum());
        response.setAverageGrowth(calculateAverageGrowth(dataPoints));
        
        // Find peak value
        if (!dataPoints.isEmpty()) {
            AnalyticsTrendResponseDTO.TrendDataPoint peak = dataPoints.stream()
                .max((dp1, dp2) -> Long.compare(dp1.getCount(), dp2.getCount()))
                .orElse(null);
            if (peak != null) {
                response.setPeakValue(peak.getCount());
                response.setPeakDate(peak.getPeriodLabel());
            }
        }

        return response;
    }

    /**
     * Get reading activity trends
     */
    public ReadingActivityResponseDTO getReadingActivityTrends(AnalyticsRequestDTO request) {
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        String period = request.getPeriod();

        // Set default date range if not provided
        if (startDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            if (endDate == null) {
                endDate = cal.getTime();
            }
            if (startDate == null) {
                cal.add(Calendar.DAY_OF_MONTH, -30); // Default to last 30 days
                startDate = cal.getTime();
            }
        }

        UUID authorId = request.getAuthorId() != null ? UUID.fromString(request.getAuthorId()) : null;
        List<ReadingActivityResponseDTO.ActivityDataPoint> dataPoints = 
            analyticsMapper.getReadingActivityTrends(startDate, endDate, period, request.getCategoryId(), authorId);

        ReadingActivityResponseDTO response = new ReadingActivityResponseDTO();
        response.setPeriod(period);
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setDataPoints(dataPoints);
        
        // Calculate totals and averages
        Long totalActivity = dataPoints.stream()
            .mapToLong(dp -> dp.getTotalActivity() != null ? dp.getTotalActivity() : 0L)
            .sum();
        response.setTotalActivity(totalActivity);
        
        if (!dataPoints.isEmpty()) {
            response.setAverageDailyActivity(totalActivity.doubleValue() / dataPoints.size());
            
            // Find peak activity
            ReadingActivityResponseDTO.ActivityDataPoint peak = dataPoints.stream()
                .max((dp1, dp2) -> Long.compare(
                    dp1.getTotalActivity() != null ? dp1.getTotalActivity() : 0L,
                    dp2.getTotalActivity() != null ? dp2.getTotalActivity() : 0L))
                .orElse(null);
            if (peak != null) {
                response.setPeakActivity(peak.getTotalActivity() != null ? peak.getTotalActivity() : 0L);
                response.setPeakDate(peak.getPeriodLabel());
            }
        }

        return response;
    }

    /**
     * Get analytics summary
     */
    public AnalyticsSummaryResponseDTO getAnalyticsSummary(AnalyticsRequestDTO request) {
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();

        // Set default date range if not provided
        if (startDate == null || endDate == null) {
            Calendar cal = Calendar.getInstance();
            if (endDate == null) {
                endDate = cal.getTime();
            }
            if (startDate == null) {
                cal.add(Calendar.DAY_OF_MONTH, -30); // Default to last 30 days
                startDate = cal.getTime();
            }
        }

        AnalyticsSummaryResponseDTO response = new AnalyticsSummaryResponseDTO();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setPeriod(request.getPeriod());

        // Get user metrics
        response.setTotalUsers(analyticsMapper.getTotalUsers(startDate, endDate));
        response.setNewUsers(analyticsMapper.getNewUsers(startDate, endDate));
        response.setActiveUsers(analyticsMapper.getActiveUsers(startDate, endDate));
        response.setAuthors(analyticsMapper.getAuthors(startDate, endDate));

        // Get novel metrics
        response.setTotalNovels(analyticsMapper.getTotalNovels(startDate, endDate));
        response.setNewNovels(analyticsMapper.getNewNovels(startDate, endDate));
        response.setPublishedNovels(analyticsMapper.getPublishedNovels(startDate, endDate));
        response.setCompletedNovels(analyticsMapper.getCompletedNovels(startDate, endDate));

        // Get activity metrics
        response.setTotalViews(analyticsMapper.getTotalViews(startDate, endDate));
        response.setTotalChapters(analyticsMapper.getTotalChapters(startDate, endDate));
        response.setTotalComments(analyticsMapper.getTotalComments(startDate, endDate));
        response.setTotalReviews(analyticsMapper.getTotalReviews(startDate, endDate));
        response.setTotalVotes(analyticsMapper.getTotalVotes(startDate, endDate));

        // Get engagement metrics
        response.setAverageRating(analyticsMapper.getAverageRating(startDate, endDate));
        response.setAverageViewsPerNovel(analyticsMapper.getAverageViewsPerNovel(startDate, endDate));
        response.setAverageCommentsPerNovel(analyticsMapper.getAverageCommentsPerNovel(startDate, endDate));
        response.setAverageReviewsPerNovel(analyticsMapper.getAverageReviewsPerNovel(startDate, endDate));

        // Calculate growth rates
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, -30); // Previous 30 days
        Date previousStartDate = cal.getTime();
        Date previousEndDate = new Date(startDate.getTime() - 1);
        
        // User growth rate
        Long previousUsers = analyticsMapper.getNewUsers(previousStartDate, previousEndDate);
        Long currentUsers = analyticsMapper.getNewUsers(startDate, endDate);
        response.setUserGrowthRate(calculateGrowthRate(previousUsers, currentUsers));
        
        // Novel growth rate
        Long previousNovels = analyticsMapper.getNewNovels(previousStartDate, previousEndDate);
        Long currentNovels = analyticsMapper.getNewNovels(startDate, endDate);
        response.setNovelGrowthRate(calculateGrowthRate(previousNovels, currentNovels));
        
        // View growth rate
        Long previousViews = analyticsMapper.getTotalViews(previousStartDate, previousEndDate);
        Long currentViews = analyticsMapper.getTotalViews(startDate, endDate);
        response.setViewGrowthRate(calculateGrowthRate(previousViews, currentViews));

        return response;
    }

    /**
     * Calculate growth rates for trend data points
     */
    private void calculateGrowthRates(List<AnalyticsTrendResponseDTO.TrendDataPoint> dataPoints) {
        for (int i = 0; i < dataPoints.size(); i++) {
            AnalyticsTrendResponseDTO.TrendDataPoint current = dataPoints.get(i);
            if (i > 0) {
                AnalyticsTrendResponseDTO.TrendDataPoint previous = dataPoints.get(i - 1);
                if (previous.getCount() > 0) {
                    double growthRate = ((current.getCount() - previous.getCount()) / (double) previous.getCount()) * 100;
                    current.setGrowthRate(growthRate);
                } else {
                    current.setGrowthRate(0.0);
                }
            } else {
                current.setGrowthRate(0.0);
            }
        }
    }

    /**
     * Calculate average growth rate
     */
    private Double calculateAverageGrowth(List<AnalyticsTrendResponseDTO.TrendDataPoint> dataPoints) {
        if (dataPoints.isEmpty()) {
            return 0.0;
        }

        double totalGrowth = dataPoints.stream()
            .filter(dp -> dp.getGrowthRate() != null)
            .mapToDouble(dp -> dp.getGrowthRate())
            .sum();

        long validPoints = dataPoints.stream()
            .filter(dp -> dp.getGrowthRate() != null)
            .count();

        return validPoints > 0 ? totalGrowth / validPoints : 0.0;
    }

    /**
     * Calculate growth rate between two periods
     */
    private Double calculateGrowthRate(Long previousValue, Long currentValue) {
        if (previousValue == null || previousValue == 0) {
            return currentValue != null && currentValue > 0 ? 100.0 : 0.0;
        }
        
        if (currentValue == null) {
            return -100.0;
        }
        
        return ((currentValue - previousValue) / (double) previousValue) * 100.0;
    }
}
