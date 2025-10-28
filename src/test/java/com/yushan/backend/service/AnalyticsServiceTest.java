package com.yushan.backend.service;

import com.yushan.backend.dao.AnalyticsMapper;
import com.yushan.backend.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AnalyticsService
 */
@DisplayName("AnalyticsService Tests")
class AnalyticsServiceTest {

    @Mock
    private AnalyticsMapper analyticsMapper;

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        analyticsService = new AnalyticsService();
        
        try {
            Field field = AnalyticsService.class.getDeclaredField("analyticsMapper");
            field.setAccessible(true);
            field.set(analyticsService, analyticsMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock", e);
        }
    }

    @Test
    @DisplayName("Get user trends with default date range")
    void getUserTrends_WithDefaultDateRange_ReturnsTrends() {
        // Given
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod("daily");
        
        AnalyticsTrendResponseDTO.TrendDataPoint dataPoint = new AnalyticsTrendResponseDTO.TrendDataPoint();
        dataPoint.setPeriodLabel("2024-01-01");
        dataPoint.setCount(100L);
        dataPoint.setGrowthRate(0.0);
        
        when(analyticsMapper.getUserTrends(any(Date.class), any(Date.class), eq("daily")))
            .thenReturn(Arrays.asList(dataPoint));

        // When
        AnalyticsTrendResponseDTO result = analyticsService.getUserTrends(request);

        // Then
        assertNotNull(result);
        assertEquals("daily", result.getPeriod());
        assertEquals(100L, result.getTotalCount());
        assertNotNull(result.getStartDate());
        assertNotNull(result.getEndDate());
    }

    @Test
    @DisplayName("Get user trends with custom date range")
    void getUserTrends_WithCustomDateRange_ReturnsTrends() {
        // Given
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod("daily");
        request.setStartDate(new Date(System.currentTimeMillis() - 86400000));
        request.setEndDate(new Date());
        
        when(analyticsMapper.getUserTrends(any(Date.class), any(Date.class), eq("daily")))
            .thenReturn(Arrays.asList());

        // When
        AnalyticsTrendResponseDTO result = analyticsService.getUserTrends(request);

        // Then
        assertNotNull(result);
        assertEquals("daily", result.getPeriod());
    }

    @Test
    @DisplayName("Get novel trends with filtering")
    void getNovelTrends_WithFiltering_ReturnsTrends() {
        // Given
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod("weekly");
        request.setCategoryId(1);
        request.setAuthorId(UUID.randomUUID().toString());
        request.setStatus(1);
        
        when(analyticsMapper.getNovelTrends(any(Date.class), any(Date.class), eq("weekly"), eq(1), anyString(), eq(1)))
            .thenReturn(Arrays.asList());

        // When
        AnalyticsTrendResponseDTO result = analyticsService.getNovelTrends(request);

        // Then
        assertNotNull(result);
        assertEquals("weekly", result.getPeriod());
        verify(analyticsMapper).getNovelTrends(any(Date.class), any(Date.class), eq("weekly"), eq(1), anyString(), eq(1));
    }

    @Test
    @DisplayName("Get reading activity trends")
    void getReadingActivityTrends_ReturnsActivity() {
        // Given
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod("daily");
        
        ReadingActivityResponseDTO.ActivityDataPoint dataPoint = new ReadingActivityResponseDTO.ActivityDataPoint();
        dataPoint.setPeriodLabel("2024-01-01");
        dataPoint.setTotalActivity(1000L);
        
        when(analyticsMapper.getReadingActivityTrends(any(Date.class), any(Date.class), eq("daily"), isNull(), isNull()))
            .thenReturn(Arrays.asList(dataPoint));

        // When
        ReadingActivityResponseDTO result = analyticsService.getReadingActivityTrends(request);

        // Then
        assertNotNull(result);
        assertEquals("daily", result.getPeriod());
        assertEquals(1000L, result.getTotalActivity());
    }

    @Test
    @DisplayName("Get analytics summary with growth rates")
    void getAnalyticsSummary_CalculatesGrowthRates() {
        // Given
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod("daily");
        
        when(analyticsMapper.getTotalUsers(any(Date.class), any(Date.class))).thenReturn(1000L);
        when(analyticsMapper.getNewUsers(any(Date.class), any(Date.class))).thenReturn(50L);
        when(analyticsMapper.getActiveUsers(any(Date.class), any(Date.class))).thenReturn(800L);
        when(analyticsMapper.getAuthors(any(Date.class), any(Date.class))).thenReturn(100L);
        when(analyticsMapper.getTotalNovels(any(Date.class), any(Date.class))).thenReturn(500L);
        when(analyticsMapper.getNewNovels(any(Date.class), any(Date.class))).thenReturn(20L);
        when(analyticsMapper.getPublishedNovels(any(Date.class), any(Date.class))).thenReturn(400L);
        when(analyticsMapper.getCompletedNovels(any(Date.class), any(Date.class))).thenReturn(50L);
        when(analyticsMapper.getTotalViews(any(Date.class), any(Date.class))).thenReturn(10000L);
        when(analyticsMapper.getTotalChapters(any(Date.class), any(Date.class))).thenReturn(5000L);
        when(analyticsMapper.getTotalComments(any(Date.class), any(Date.class))).thenReturn(2000L);
        when(analyticsMapper.getTotalReviews(any(Date.class), any(Date.class))).thenReturn(500L);
        when(analyticsMapper.getTotalVotes(any(Date.class), any(Date.class))).thenReturn(1000L);
        when(analyticsMapper.getAverageRating(any(Date.class), any(Date.class))).thenReturn(4.5);
        when(analyticsMapper.getAverageViewsPerNovel(any(Date.class), any(Date.class))).thenReturn(20.0);
        when(analyticsMapper.getAverageCommentsPerNovel(any(Date.class), any(Date.class))).thenReturn(4.0);
        when(analyticsMapper.getAverageReviewsPerNovel(any(Date.class), any(Date.class))).thenReturn(1.0);
        
        // Mock growth rate calculations
        when(analyticsMapper.getNewUsers(any(Date.class), any(Date.class))).thenReturn(40L, 50L);
        when(analyticsMapper.getNewNovels(any(Date.class), any(Date.class))).thenReturn(15L, 20L);
        when(analyticsMapper.getTotalViews(any(Date.class), any(Date.class))).thenReturn(8000L, 10000L);

        // When
        AnalyticsSummaryResponseDTO result = analyticsService.getAnalyticsSummary(request);

        // Then
        assertNotNull(result);
        assertEquals(1000L, result.getTotalUsers());
        assertEquals(40L, result.getNewUsers());
        assertNotNull(result.getUserGrowthRate());
    }

    @Test
    @DisplayName("Get platform statistics")
    void getPlatformStatistics_ReturnsPlatformStats() {
        // Given
        when(analyticsMapper.getTotalUsersAll()).thenReturn(5000L);
        when(analyticsMapper.getActiveUsersAll()).thenReturn(3000L);
        when(analyticsMapper.getNewUsersToday()).thenReturn(50L);
        when(analyticsMapper.getAuthorsAll()).thenReturn(200L);
        when(analyticsMapper.getAdminsAll()).thenReturn(5L);
        when(analyticsMapper.getTotalNovelsAll()).thenReturn(2000L);
        when(analyticsMapper.getPublishedNovelsAll()).thenReturn(1500L);
        when(analyticsMapper.getCompletedNovelsAll()).thenReturn(200L);
        when(analyticsMapper.getTotalChaptersAll()).thenReturn(10000L);
        when(analyticsMapper.getTotalWordsAll()).thenReturn(50000000L);
        when(analyticsMapper.getTotalViewsAll()).thenReturn(500000L);
        when(analyticsMapper.getTotalCommentsAll()).thenReturn(10000L);
        when(analyticsMapper.getTotalReviewsAll()).thenReturn(2000L);
        when(analyticsMapper.getTotalVotesAll()).thenReturn(5000L);
        when(analyticsMapper.getAverageRatingAll()).thenReturn(4.5);
        when(analyticsMapper.getDailyActiveUsers(any(Date.class))).thenReturn(1000L);
        when(analyticsMapper.getWeeklyActiveUsers(any(Date.class), any(Date.class))).thenReturn(5000L);
        when(analyticsMapper.getMonthlyActiveUsers(any(Date.class), any(Date.class))).thenReturn(10000L);
        when(analyticsMapper.getPlatformUserGrowthRate()).thenReturn(10.5);
        when(analyticsMapper.getPlatformContentGrowthRate()).thenReturn(15.2);
        when(analyticsMapper.getPlatformEngagementGrowthRate()).thenReturn(8.3);

        // When
        PlatformStatisticsResponseDTO result = analyticsService.getPlatformStatistics();

        // Then
        assertNotNull(result);
        assertEquals(5000L, result.getTotalUsers());
        assertEquals(3000L, result.getActiveUsers());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("Get daily active users with hourly breakdown")
    void getDailyActiveUsers_WithHourlyBreakdown() {
        // Given
        Date targetDate = new Date();
        
        DailyActiveUsersResponseDTO.ActivityDataPoint hourlyPoint = 
            new DailyActiveUsersResponseDTO.ActivityDataPoint(10, 100L, 10L, 50L);
        
        when(analyticsMapper.getDailyActiveUsers(eq(targetDate))).thenReturn(500L);
        when(analyticsMapper.getWeeklyActiveUsers(any(Date.class), eq(targetDate))).thenReturn(2000L);
        when(analyticsMapper.getMonthlyActiveUsers(any(Date.class), eq(targetDate))).thenReturn(5000L);
        when(analyticsMapper.getHourlyActiveUsers(eq(targetDate))).thenReturn(Arrays.asList(hourlyPoint));

        // When
        DailyActiveUsersResponseDTO result = analyticsService.getDailyActiveUsers(targetDate);

        // Then
        assertNotNull(result);
        assertEquals(500L, result.getDau());
        assertEquals(2000L, result.getWau());
        assertEquals(5000L, result.getMau());
        assertNotNull(result.getHourlyBreakdown());
    }

    @Test
    @DisplayName("Get top content statistics")
    void getTopContent_WithLimit_ReturnsTopContent() {
        // Given
        Integer limit = 10;
        
        TopContentResponseDTO.TopNovel topNovel = new TopContentResponseDTO.TopNovel();
        topNovel.setId(1);
        topNovel.setTitle("Top Novel");
        
        TopContentResponseDTO.TopAuthor topAuthor = new TopContentResponseDTO.TopAuthor();
        topAuthor.setAuthorId("author-1");
        topAuthor.setAuthorName("topauthor");
        
        TopContentResponseDTO.TopCategory topCategory = new TopContentResponseDTO.TopCategory();
        topCategory.setCategoryId(1);
        topCategory.setCategoryName("Fantasy");
        
        when(analyticsMapper.getTopNovels(limit)).thenReturn(Arrays.asList(topNovel));
        when(analyticsMapper.getTopAuthors(limit)).thenReturn(Arrays.asList(topAuthor));
        when(analyticsMapper.getTopCategories(limit)).thenReturn(Arrays.asList(topCategory));

        // When
        TopContentResponseDTO result = analyticsService.getTopContent(limit);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTopNovels());
        assertNotNull(result.getTopAuthors());
        assertNotNull(result.getTopCategories());
        assertEquals(1, result.getTopNovels().size());
    }

    @Test
    @DisplayName("Get top content with default limit")
    void getTopContent_WithDefaultLimit_ReturnsTopContent() {
        // Given
        when(analyticsMapper.getTopNovels(10)).thenReturn(new ArrayList<>());
        when(analyticsMapper.getTopAuthors(10)).thenReturn(new ArrayList<>());
        when(analyticsMapper.getTopCategories(10)).thenReturn(new ArrayList<>());

        // When
        TopContentResponseDTO result = analyticsService.getTopContent(null);

        // Then
        assertNotNull(result);
        verify(analyticsMapper).getTopNovels(10);
        verify(analyticsMapper).getTopAuthors(10);
        verify(analyticsMapper).getTopCategories(10);
    }
}

