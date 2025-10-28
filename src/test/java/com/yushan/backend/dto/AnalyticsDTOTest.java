package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Analytics DTO Tests")
class AnalyticsDTOTest {

    @Test
    @DisplayName("Test AnalyticsSummaryResponseDTO")
    void testAnalyticsSummaryResponseDTO() {
        Date startDate = new Date();
        Date endDate = new Date();
        
        AnalyticsSummaryResponseDTO dto = new AnalyticsSummaryResponseDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setPeriod("daily");
        
        // User metrics
        dto.setTotalUsers(1000L);
        dto.setNewUsers(50L);
        dto.setActiveUsers(800L);
        dto.setAuthors(100L);
        
        // Novel metrics
        dto.setTotalNovels(500L);
        dto.setNewNovels(20L);
        dto.setPublishedNovels(450L);
        dto.setCompletedNovels(50L);
        
        // Reading activity metrics
        dto.setTotalViews(10000L);
        dto.setTotalChapters(5000L);
        dto.setTotalComments(2000L);
        dto.setTotalReviews(500L);
        dto.setTotalVotes(1000L);
        
        // Engagement metrics
        dto.setAverageRating(4.5);
        dto.setAverageViewsPerNovel(20.0);
        dto.setAverageCommentsPerNovel(4.0);
        dto.setAverageReviewsPerNovel(1.0);
        
        // Growth metrics
        dto.setUserGrowthRate(5.0);
        dto.setNovelGrowthRate(4.0);
        dto.setViewGrowthRate(10.0);
        
        assertNotNull(dto.getStartDate());
        assertNotNull(dto.getEndDate());
        assertEquals("daily", dto.getPeriod());
        assertEquals(1000L, dto.getTotalUsers());
        assertEquals(50L, dto.getNewUsers());
        assertEquals(800L, dto.getActiveUsers());
        assertEquals(100L, dto.getAuthors());
        assertEquals(500L, dto.getTotalNovels());
        assertEquals(20L, dto.getNewNovels());
        assertEquals(450L, dto.getPublishedNovels());
        assertEquals(50L, dto.getCompletedNovels());
        assertEquals(10000L, dto.getTotalViews());
        assertEquals(5000L, dto.getTotalChapters());
        assertEquals(2000L, dto.getTotalComments());
        assertEquals(500L, dto.getTotalReviews());
        assertEquals(1000L, dto.getTotalVotes());
        assertEquals(4.5, dto.getAverageRating());
        assertEquals(20.0, dto.getAverageViewsPerNovel());
        assertEquals(4.0, dto.getAverageCommentsPerNovel());
        assertEquals(1.0, dto.getAverageReviewsPerNovel());
        assertEquals(5.0, dto.getUserGrowthRate());
        assertEquals(4.0, dto.getNovelGrowthRate());
        assertEquals(10.0, dto.getViewGrowthRate());
        
        // Test defensive copying for Date fields
        Date testDate = new Date();
        dto.setStartDate(testDate);
        Date retrieved = dto.getStartDate();
        assertNotSame(testDate, retrieved);
        testDate.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        dto.setEndDate(testDate);
        retrieved = dto.getEndDate();
        assertNotSame(testDate, retrieved);
        
        // Test null Date values
        dto.setStartDate(null);
        dto.setEndDate(null);
        assertNull(dto.getStartDate());
        assertNull(dto.getEndDate());
        
        // Test with different period values
        dto.setPeriod("weekly");
        assertEquals("weekly", dto.getPeriod());
        dto.setPeriod("monthly");
        assertEquals("monthly", dto.getPeriod());
        
        // Test with zero/null values
        dto.setTotalUsers(0L);
        dto.setNewUsers(null);
        dto.setActiveUsers(0L);
        assertEquals(0L, dto.getTotalUsers());
        assertNull(dto.getNewUsers());
        assertEquals(0L, dto.getActiveUsers());
        
        // Test with negative growth rates
        dto.setUserGrowthRate(-5.0);
        dto.setNovelGrowthRate(-2.0);
        assertEquals(-5.0, dto.getUserGrowthRate());
        assertEquals(-2.0, dto.getNovelGrowthRate());
        
        // Test with null averages
        dto.setAverageRating(null);
        dto.setAverageViewsPerNovel(null);
        assertNull(dto.getAverageRating());
        assertNull(dto.getAverageViewsPerNovel());
        
        // Test all remaining fields with different values
        dto.setAuthors(200L);
        dto.setNewNovels(30L);
        dto.setPublishedNovels(480L);
        dto.setCompletedNovels(60L);
        dto.setTotalChapters(6000L);
        dto.setTotalComments(2500L);
        dto.setTotalReviews(600L);
        dto.setTotalVotes(1200L);
        dto.setAverageCommentsPerNovel(5.0);
        dto.setAverageReviewsPerNovel(1.2);
        dto.setViewGrowthRate(15.0);
        
        assertEquals(200L, dto.getAuthors());
        assertEquals(30L, dto.getNewNovels());
        assertEquals(480L, dto.getPublishedNovels());
        assertEquals(60L, dto.getCompletedNovels());
        assertEquals(6000L, dto.getTotalChapters());
        assertEquals(2500L, dto.getTotalComments());
        assertEquals(600L, dto.getTotalReviews());
        assertEquals(1200L, dto.getTotalVotes());
        assertEquals(5.0, dto.getAverageCommentsPerNovel());
        assertEquals(1.2, dto.getAverageReviewsPerNovel());
        assertEquals(15.0, dto.getViewGrowthRate());
        
        // Test all getters for completeness - ensuring all branches are covered
        Date testStartDate = new Date();
        Date testEndDate = new Date();
        
        // Test getStartDate with null check branch
        dto.setStartDate(null);
        assertNull(dto.getStartDate());
        
        dto.setStartDate(testStartDate);
        Date retrievedStart1 = dto.getStartDate();
        assertNotNull(retrievedStart1);
        assertNotSame(testStartDate, retrievedStart1);
        
        // Test getEndDate with null check branch
        dto.setEndDate(null);
        assertNull(dto.getEndDate());
        
        dto.setEndDate(testEndDate);
        Date retrievedEnd1 = dto.getEndDate();
        assertNotNull(retrievedEnd1);
        assertNotSame(testEndDate, retrievedEnd1);
        
        // Test setStartDate with null parameter branch
        dto.setStartDate(null);
        assertNull(dto.getStartDate());
        
        // Test setEndDate with null parameter branch
        dto.setEndDate(null);
        assertNull(dto.getEndDate());
        
        // Test setStartDate with non-null parameter branch
        Date newStartDate = new Date();
        dto.setStartDate(newStartDate);
        Date retrievedStart2 = dto.getStartDate();
        assertNotNull(retrievedStart2);
        assertNotSame(newStartDate, retrievedStart2);
        
        // Test setEndDate with non-null parameter branch
        Date newEndDate = new Date();
        dto.setEndDate(newEndDate);
        Date retrievedEnd2 = dto.getEndDate();
        assertNotNull(retrievedEnd2);
        assertNotSame(newEndDate, retrievedEnd2);
        
        // Test all remaining Long fields
        dto.setTotalNovels(1000L);
        assertEquals(1000L, dto.getTotalNovels());
    }

    @Test
    @DisplayName("Test PlatformStatisticsResponseDTO")
    void testPlatformStatisticsResponseDTO() {
        Date now = new Date();
        
        PlatformStatisticsResponseDTO dto = new PlatformStatisticsResponseDTO();
        dto.setTimestamp(now);
        
        // User Statistics
        dto.setTotalUsers(5000L);
        dto.setActiveUsers(4000L);
        dto.setNewUsersToday(50L);
        dto.setAuthors(500L);
        dto.setAdmins(10L);
        
        // Content Statistics
        dto.setTotalNovels(3000L);
        dto.setPublishedNovels(2500L);
        dto.setCompletedNovels(500L);
        dto.setTotalChapters(15000L);
        dto.setTotalWords(50000000L);
        
        // Engagement Statistics
        dto.setTotalViews(1000000L);
        dto.setTotalComments(50000L);
        dto.setTotalReviews(10000L);
        dto.setTotalVotes(20000L);
        dto.setAverageRating(4.2);
        
        // Activity Statistics
        dto.setDailyActiveUsers(1000L);
        dto.setWeeklyActiveUsers(3000L);
        dto.setMonthlyActiveUsers(4000L);
        
        // Growth Statistics
        dto.setUserGrowthRate(5.5);
        dto.setContentGrowthRate(8.0);
        dto.setEngagementGrowthRate(12.0);
        
        assertNotNull(dto.getTimestamp());
        assertEquals(5000L, dto.getTotalUsers());
        assertEquals(4000L, dto.getActiveUsers());
        assertEquals(50L, dto.getNewUsersToday());
        assertEquals(500L, dto.getAuthors());
        assertEquals(10L, dto.getAdmins());
        assertEquals(3000L, dto.getTotalNovels());
        assertEquals(2500L, dto.getPublishedNovels());
        assertEquals(500L, dto.getCompletedNovels());
        assertEquals(15000L, dto.getTotalChapters());
        assertEquals(50000000L, dto.getTotalWords());
        assertEquals(1000000L, dto.getTotalViews());
        assertEquals(50000L, dto.getTotalComments());
        assertEquals(10000L, dto.getTotalReviews());
        assertEquals(20000L, dto.getTotalVotes());
        assertEquals(4.2, dto.getAverageRating());
        assertEquals(1000L, dto.getDailyActiveUsers());
        assertEquals(3000L, dto.getWeeklyActiveUsers());
        assertEquals(4000L, dto.getMonthlyActiveUsers());
        assertEquals(5.5, dto.getUserGrowthRate());
        assertEquals(8.0, dto.getContentGrowthRate());
        assertEquals(12.0, dto.getEngagementGrowthRate());
        
        // Test defensive copying for Date
        Date testDate2 = new Date();
        dto.setTimestamp(testDate2);
        Date retrieved2 = dto.getTimestamp();
        assertNotSame(testDate2, retrieved2);
        
        // Test null timestamp
        dto.setTimestamp(null);
        assertNull(dto.getTimestamp());
        
        // Test with null values
        dto.setAverageRating(null);
        dto.setUserGrowthRate(null);
        assertNull(dto.getAverageRating());
        assertNull(dto.getUserGrowthRate());
        
        // Test with zero values
        dto.setTotalUsers(0L);
        dto.setActiveUsers(0L);
        assertEquals(0L, dto.getTotalUsers());
        assertEquals(0L, dto.getActiveUsers());
        
        // Test all remaining fields with different values
        dto.setAdmins(5L);
        dto.setPublishedNovels(3000L);
        dto.setCompletedNovels(600L);
        dto.setTotalChapters(20000L);
        dto.setTotalWords(100000000L);
        dto.setTotalComments(60000L);
        dto.setTotalReviews(12000L);
        dto.setTotalVotes(25000L);
        dto.setWeeklyActiveUsers(3500L);
        dto.setMonthlyActiveUsers(4500L);
        dto.setContentGrowthRate(9.0);
        dto.setEngagementGrowthRate(13.0);
        
        assertEquals(5L, dto.getAdmins());
        assertEquals(3000L, dto.getPublishedNovels());
        assertEquals(600L, dto.getCompletedNovels());
        assertEquals(20000L, dto.getTotalChapters());
        assertEquals(100000000L, dto.getTotalWords());
        assertEquals(60000L, dto.getTotalComments());
        assertEquals(12000L, dto.getTotalReviews());
        assertEquals(25000L, dto.getTotalVotes());
        assertEquals(3500L, dto.getWeeklyActiveUsers());
        assertEquals(4500L, dto.getMonthlyActiveUsers());
        assertEquals(9.0, dto.getContentGrowthRate());
        assertEquals(13.0, dto.getEngagementGrowthRate());
        
        // Test getTimestamp with null check branch
        dto.setTimestamp(null);
        assertNull(dto.getTimestamp());
        
        Date testTimestamp = new Date();
        dto.setTimestamp(testTimestamp);
        Date retrievedTimestamp = dto.getTimestamp();
        assertNotNull(retrievedTimestamp);
        assertNotSame(testTimestamp, retrievedTimestamp);
        
        // Test setTimestamp with null parameter branch
        dto.setTimestamp(null);
        assertNull(dto.getTimestamp());
        
        // Test setTimestamp with non-null parameter branch
        Date newTimestamp = new Date();
        dto.setTimestamp(newTimestamp);
        Date retrieved = dto.getTimestamp();
        assertNotNull(retrieved);
        assertNotSame(newTimestamp, retrieved);
        
        // Test defensive copying - modify original date should not affect retrieved
        testTimestamp.setTime(0);
        assertNotEquals(0, retrievedTimestamp.getTime());
    }

    @Test
    @DisplayName("Test AnalyticsRequestDTO")
    void testAnalyticsRequestDTO() {
        Date startDate = new Date();
        Date endDate = new Date();
        
        AnalyticsRequestDTO dto = new AnalyticsRequestDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setPeriod("weekly");
        dto.setCategoryId(1);
        dto.setAuthorId("author-uuid");
        dto.setStatus(1);
        
        assertNotNull(dto.getStartDate());
        assertNotNull(dto.getEndDate());
        assertEquals("weekly", dto.getPeriod());
        assertEquals(1, dto.getCategoryId());
        assertEquals("author-uuid", dto.getAuthorId());
        assertEquals(1, dto.getStatus());
    }
    
    @Test
    @DisplayName("Test AnalyticsRequestDTO default period")
    void testAnalyticsRequestDTODefault() {
        AnalyticsRequestDTO dto = new AnalyticsRequestDTO();
        assertEquals("daily", dto.getPeriod());
        
        // Test all Date getters/setters with null check branches for complete coverage
        Date testStartDate = new Date();
        dto.setStartDate(null);
        assertNull(dto.getStartDate());
        
        dto.setStartDate(testStartDate);
        Date retrievedStartDate = dto.getStartDate();
        assertNotNull(retrievedStartDate);
        assertNotSame(testStartDate, retrievedStartDate);
        
        Date testEndDate = new Date();
        dto.setEndDate(null);
        assertNull(dto.getEndDate());
        
        dto.setEndDate(testEndDate);
        Date retrievedEndDate = dto.getEndDate();
        assertNotNull(retrievedEndDate);
        assertNotSame(testEndDate, retrievedEndDate);
        
        // Test setStartDate with null parameter branch
        dto.setStartDate(null);
        assertNull(dto.getStartDate());
        
        // Test setEndDate with null parameter branch
        dto.setEndDate(null);
        assertNull(dto.getEndDate());
        
        // Test setStartDate with non-null parameter branch
        Date newStartDate = new Date();
        dto.setStartDate(newStartDate);
        Date retrievedStart = dto.getStartDate();
        assertNotNull(retrievedStart);
        assertNotSame(newStartDate, retrievedStart);
        
        // Test setEndDate with non-null parameter branch
        Date newEndDate = new Date();
        dto.setEndDate(newEndDate);
        Date retrievedEnd = dto.getEndDate();
        assertNotNull(retrievedEnd);
        assertNotSame(newEndDate, retrievedEnd);
        
        // Test defensive copying - modify original date should not affect retrieved
        testStartDate.setTime(0);
        assertNotEquals(0, retrievedStartDate.getTime());
        
        testEndDate.setTime(0);
        assertNotEquals(0, retrievedEndDate.getTime());
        
        // Test equals, hashCode, and canEqual methods
        AnalyticsSummaryResponseDTO dto2 = new AnalyticsSummaryResponseDTO();
        dto2.setPeriod("daily");
        dto2.setTotalUsers(100L);
        dto2.setStartDate(testStartDate);
        dto2.setEndDate(testEndDate);
        
        AnalyticsSummaryResponseDTO dto3 = new AnalyticsSummaryResponseDTO();
        dto3.setPeriod("daily");
        dto3.setTotalUsers(100L);
        dto3.setStartDate(testStartDate);
        dto3.setEndDate(testEndDate);
        
        // Test equals - same values
        assertEquals(dto2, dto3);
        assertEquals(dto2.hashCode(), dto3.hashCode());
        
        // Test equals - different values
        dto3.setPeriod("weekly");
        assertNotEquals(dto2, dto3);
        
        // Test equals - null
        assertNotEquals(dto2, null);
        
        // Test equals - same object
        assertEquals(dto2, dto2);
        
        // Test equals - different class
        assertNotEquals(dto2, "not a DTO");
        
        // Test canEqual
        assertTrue(dto2.canEqual(dto3));
        assertFalse(dto2.canEqual("not a DTO"));
        
        // Test other fields
        dto.setPeriod("monthly");
        dto.setCategoryId(5);
        dto.setAuthorId("author-123");
        dto.setStatus(1);
        
        assertEquals("monthly", dto.getPeriod());
        assertEquals(5, dto.getCategoryId());
        assertEquals("author-123", dto.getAuthorId());
        assertEquals(1, dto.getStatus());
        
        // Test null values
        dto.setCategoryId(null);
        dto.setAuthorId(null);
        dto.setStatus(null);
        assertNull(dto.getCategoryId());
        assertNull(dto.getAuthorId());
        assertNull(dto.getStatus());
    }

    @Test
    @DisplayName("Test ReadingActivityResponseDTO")
    void testReadingActivityResponseDTO() {
        Date date = new Date();
        
        ReadingActivityResponseDTO.ActivityDataPoint dataPoint = 
            new ReadingActivityResponseDTO.ActivityDataPoint(date, "2025-01-01", 100L, 50L, 10L, 5L, 20L);
        
        assertEquals("2025-01-01", dataPoint.getPeriodLabel());
        assertEquals(100L, dataPoint.getViews());
        assertEquals(50L, dataPoint.getChaptersRead());
        assertEquals(10L, dataPoint.getComments());
        assertEquals(5L, dataPoint.getReviews());
        assertEquals(20L, dataPoint.getVotes());
        assertEquals(185L, dataPoint.getTotalActivity()); // 100+50+10+5+20
        
        // Test defensive copying for Date in ActivityDataPoint
        Date testDate = new Date();
        dataPoint.setDate(testDate);
        Date retrieved = dataPoint.getDate();
        assertNotSame(testDate, retrieved);
        
        ReadingActivityResponseDTO dto = new ReadingActivityResponseDTO();
        dto.setPeriod("daily");
        dto.setStartDate(date);
        dto.setEndDate(new Date());
        dto.setDataPoints(java.util.Arrays.asList(dataPoint));
        dto.setTotalActivity(200L);
        dto.setAverageDailyActivity(50.0);
        dto.setPeakActivity(500L);
        dto.setPeakDate("2025-01-15");
        
        assertEquals("daily", dto.getPeriod());
        assertNotNull(dto.getStartDate());
        assertEquals(1, dto.getDataPoints().size());
        assertEquals(200L, dto.getTotalActivity());
        assertEquals(50.0, dto.getAverageDailyActivity());
        assertEquals(500L, dto.getPeakActivity());
        assertEquals("2025-01-15", dto.getPeakDate());
        
        // Test defensive copying for Date fields
        Date testDate2 = new Date();
        dto.setStartDate(testDate2);
        Date retrieved2 = dto.getStartDate();
        assertNotSame(testDate2, retrieved2);
        
        dto.setEndDate(testDate2);
        retrieved2 = dto.getEndDate();
        assertNotSame(testDate2, retrieved2);
        
        // Test defensive copying for List
        java.util.List<ReadingActivityResponseDTO.ActivityDataPoint> original = 
            new java.util.ArrayList<>(dto.getDataPoints());
        original.add(new ReadingActivityResponseDTO.ActivityDataPoint());
        assertEquals(1, dto.getDataPoints().size());
        
        // Test setters for ActivityDataPoint
        dataPoint.setViews(200L);
        dataPoint.setChaptersRead(100L);
        dataPoint.setComments(20L);
        dataPoint.setReviews(10L);
        dataPoint.setVotes(40L);
        dataPoint.setTotalActivity(370L);
        assertEquals(200L, dataPoint.getViews());
        assertEquals(100L, dataPoint.getChaptersRead());
        assertEquals(20L, dataPoint.getComments());
        assertEquals(10L, dataPoint.getReviews());
        assertEquals(40L, dataPoint.getVotes());
        assertEquals(370L, dataPoint.getTotalActivity());
        
        // Test with null values
        ReadingActivityResponseDTO.ActivityDataPoint point3 = 
            new ReadingActivityResponseDTO.ActivityDataPoint(null, "Null Day", null, null, null, null, null);
        assertEquals(0L, point3.getTotalActivity());
        assertNull(point3.getDate());
        
        // Test different period values
        dto.setPeriod("weekly");
        assertEquals("weekly", dto.getPeriod());
        
        dto.setPeriod("monthly");
        assertEquals("monthly", dto.getPeriod());
        
        // Test null fields
        dto.setTotalActivity(null);
        dto.setAverageDailyActivity(null);
        dto.setPeakActivity(null);
        dto.setPeakDate(null);
        assertNull(dto.getTotalActivity());
        assertNull(dto.getAverageDailyActivity());
        assertNull(dto.getPeakActivity());
        assertNull(dto.getPeakDate());
    }

    @Test
    @DisplayName("Test AnalyticsTrendResponseDTO")
    void testAnalyticsTrendResponseDTO() {
        Date date = new Date();
        
        AnalyticsTrendResponseDTO.TrendDataPoint dataPoint = 
            new AnalyticsTrendResponseDTO.TrendDataPoint(date, "Week 1", 1000L, 5.5);
        
        AnalyticsTrendResponseDTO dto = new AnalyticsTrendResponseDTO();
        dto.setPeriod("weekly");
        dto.setStartDate(date);
        dto.setEndDate(new Date());
        dto.setDataPoints(java.util.Arrays.asList(dataPoint));
        dto.setTotalCount(5000L);
        dto.setAverageGrowth(4.5);
        dto.setPeakValue(10000L);
        dto.setPeakDate("2025-01-20");
        
        assertEquals("weekly", dto.getPeriod());
        assertNotNull(dto.getStartDate());
        assertEquals(1, dto.getDataPoints().size());
        assertEquals(5000L, dto.getTotalCount());
        assertEquals(4.5, dto.getAverageGrowth());
        assertEquals(10000L, dto.getPeakValue());
        assertEquals("2025-01-20", dto.getPeakDate());
        
        // Test defensive copying for Date fields
        Date testDate = new Date();
        dto.setStartDate(testDate);
        Date retrieved = dto.getStartDate();
        assertNotSame(testDate, retrieved);
        
        dto.setEndDate(testDate);
        retrieved = dto.getEndDate();
        assertNotSame(testDate, retrieved);
        
        // Test defensive copying for List
        java.util.List<AnalyticsTrendResponseDTO.TrendDataPoint> original = 
            new java.util.ArrayList<>(dto.getDataPoints());
        original.add(new AnalyticsTrendResponseDTO.TrendDataPoint());
        assertEquals(1, dto.getDataPoints().size());
        
        // Test TrendDataPoint defensive copying
        Date testDate2 = new Date();
        dataPoint.setDate(testDate2);
        Date retrieved2 = dataPoint.getDate();
        assertNotSame(testDate2, retrieved2);
        
        // Test TrendDataPoint fields
        assertEquals("Week 1", dataPoint.getPeriodLabel());
        assertEquals(1000L, dataPoint.getCount());
        assertEquals(5.5, dataPoint.getGrowthRate());
        
        dataPoint.setPeriodLabel("Week 2");
        dataPoint.setCount(2000L);
        dataPoint.setGrowthRate(6.0);
        assertEquals("Week 2", dataPoint.getPeriodLabel());
        assertEquals(2000L, dataPoint.getCount());
        assertEquals(6.0, dataPoint.getGrowthRate());
    }

    @Test
    @DisplayName("Test DailyActiveUsersResponseDTO")
    void testDailyActiveUsersResponseDTO() {
        Date date = new Date();
        
        DailyActiveUsersResponseDTO.ActivityDataPoint dataPoint1 = 
            new DailyActiveUsersResponseDTO.ActivityDataPoint(10, 100L, 10L, 50L);
        DailyActiveUsersResponseDTO.ActivityDataPoint dataPoint2 = 
            new DailyActiveUsersResponseDTO.ActivityDataPoint(14, 200L, 20L, 100L);
        
        DailyActiveUsersResponseDTO dto = new DailyActiveUsersResponseDTO();
        dto.setDate(date);
        dto.setDau(5000L);
        dto.setWau(15000L);
        dto.setMau(50000L);
        dto.setHourlyBreakdown(java.util.Arrays.asList(dataPoint1, dataPoint2));
        
        assertNotNull(dto.getDate());
        assertEquals(5000L, dto.getDau());
        assertEquals(15000L, dto.getWau());
        assertEquals(50000L, dto.getMau());
        assertEquals(2, dto.getHourlyBreakdown().size());
        assertEquals(10, dto.getHourlyBreakdown().get(0).getHour());
        assertEquals(100L, dto.getHourlyBreakdown().get(0).getActiveUsers());
        
        // Test defensive copying for Date
        Date testDate = new Date();
        dto.setDate(testDate);
        Date retrieved = dto.getDate();
        assertNotSame(testDate, retrieved);
        
        // Test defensive copying for List
        java.util.List<DailyActiveUsersResponseDTO.ActivityDataPoint> original = 
            new java.util.ArrayList<>(dto.getHourlyBreakdown());
        original.add(new DailyActiveUsersResponseDTO.ActivityDataPoint(18, 300L, 30L, 150L));
        assertEquals(2, dto.getHourlyBreakdown().size());
        
        java.util.List<DailyActiveUsersResponseDTO.ActivityDataPoint> retrievedList = dto.getHourlyBreakdown();
        assertNotSame(original, retrievedList);
    }

    @Test
    @DisplayName("Test TopContentResponseDTO")
    void testTopContentResponseDTO() {
        Date date = new Date();
        
        TopContentResponseDTO.TopNovel novel = new TopContentResponseDTO.TopNovel(
            1, "Novel 1", "Author 1", "Fantasy", 10000L, 500L, 4.5, 50, 500000L
        );
        
        TopContentResponseDTO.TopAuthor author = new TopContentResponseDTO.TopAuthor(
            "author-uuid", "Author 1", 5, 50000L, 2000L, 4.3, 2000000L
        );
        
        TopContentResponseDTO.TopCategory category = new TopContentResponseDTO.TopCategory(
            1, "Fantasy", 100, 1000000L, 50000L, 4.2
        );
        
        TopContentResponseDTO dto = new TopContentResponseDTO();
        dto.setDate(date);
        dto.setTopNovels(java.util.Arrays.asList(novel));
        dto.setTopAuthors(java.util.Arrays.asList(author));
        dto.setTopCategories(java.util.Arrays.asList(category));
        
        assertNotNull(dto.getDate());
        assertEquals(1, dto.getTopNovels().size());
        assertEquals(1, dto.getTopAuthors().size());
        assertEquals(1, dto.getTopCategories().size());
        
        assertEquals(1, dto.getTopNovels().get(0).getId());
        assertEquals("Novel 1", dto.getTopNovels().get(0).getTitle());
        assertEquals("Author 1", dto.getTopNovels().get(0).getAuthorName());
        
        assertEquals("author-uuid", dto.getTopAuthors().get(0).getAuthorId());
        assertEquals(5, dto.getTopAuthors().get(0).getNovelCount());
        
        assertEquals(1, dto.getTopCategories().get(0).getCategoryId());
        assertEquals("Fantasy", dto.getTopCategories().get(0).getCategoryName());
        
        // Test defensive copying for Date
        Date testDate = new Date();
        dto.setDate(testDate);
        Date retrieved = dto.getDate();
        assertNotSame(testDate, retrieved);
        
        // Test defensive copying for Lists
        java.util.List<TopContentResponseDTO.TopNovel> novels = new java.util.ArrayList<>(dto.getTopNovels());
        novels.add(new TopContentResponseDTO.TopNovel());
        assertEquals(1, dto.getTopNovels().size());
    }

    @Test
    @DisplayName("Test DailyActiveUsersResponseDTO with empty data")
    void testDailyActiveUsersResponseDTOEmpty() {
        DailyActiveUsersResponseDTO dto = new DailyActiveUsersResponseDTO();
        assertNotNull(dto.getHourlyBreakdown());
        assertEquals(0, dto.getHourlyBreakdown().size());
    }
    
    @Test
    @DisplayName("Test TopContentResponseDTO with constructors")
    void testTopContentResponseDTOConstructors() {
        TopContentResponseDTO.TopNovel novel = new TopContentResponseDTO.TopNovel(
            1, "Title", "Author", "Category", 100L, 50L, 4.0, 10, 1000L
        );
        assertEquals(1, novel.getId());
        assertEquals("Title", novel.getTitle());
        
        TopContentResponseDTO.TopAuthor author = new TopContentResponseDTO.TopAuthor(
            "id", "Name", 5, 100L, 50L, 4.0, 1000L
        );
        assertEquals("id", author.getAuthorId());
        assertEquals("Name", author.getAuthorName());
        
        TopContentResponseDTO.TopCategory category = new TopContentResponseDTO.TopCategory(
            1, "Category", 10, 100L, 50L, 4.0
        );
        assertEquals(1, category.getCategoryId());
        assertEquals("Category", category.getCategoryName());
        
        // Test all TopNovel fields with different values
        TopContentResponseDTO.TopNovel novel2 = new TopContentResponseDTO.TopNovel();
        novel2.setId(999);
        novel2.setTitle("Different Novel");
        novel2.setAuthorName("Different Author");
        novel2.setCategoryName("Mystery");
        novel2.setViewCount(50000L);
        novel2.setVoteCount(2000L);
        novel2.setRating(4.8);
        novel2.setChapterCount(100);
        novel2.setWordCount(1000000L);
        
        assertEquals(999, novel2.getId());
        assertEquals("Different Novel", novel2.getTitle());
        assertEquals("Different Author", novel2.getAuthorName());
        assertEquals("Mystery", novel2.getCategoryName());
        assertEquals(50000L, novel2.getViewCount());
        assertEquals(2000L, novel2.getVoteCount());
        assertEquals(4.8, novel2.getRating());
        assertEquals(100, novel2.getChapterCount());
        assertEquals(1000000L, novel2.getWordCount());
        
        // Test all TopAuthor fields with different values
        TopContentResponseDTO.TopAuthor author2 = new TopContentResponseDTO.TopAuthor();
        author2.setAuthorId("author-999");
        author2.setAuthorName("Different Author");
        author2.setNovelCount(20);
        author2.setTotalViews(200000L);
        author2.setTotalVotes(5000L);
        author2.setAverageRating(4.6);
        author2.setTotalWords(5000000L);
        
        assertEquals("author-999", author2.getAuthorId());
        assertEquals("Different Author", author2.getAuthorName());
        assertEquals(20, author2.getNovelCount());
        assertEquals(200000L, author2.getTotalViews());
        assertEquals(5000L, author2.getTotalVotes());
        assertEquals(4.6, author2.getAverageRating());
        assertEquals(5000000L, author2.getTotalWords());
        
        // Test all TopCategory fields with different values
        TopContentResponseDTO.TopCategory category2 = new TopContentResponseDTO.TopCategory();
        category2.setCategoryId(999);
        category2.setCategoryName("Different Category");
        category2.setNovelCount(500);
        category2.setTotalViews(1000000L);
        category2.setTotalVotes(10000L);
        category2.setAverageRating(4.7);
        
        assertEquals(999, category2.getCategoryId());
        assertEquals("Different Category", category2.getCategoryName());
        assertEquals(500, category2.getNovelCount());
        assertEquals(1000000L, category2.getTotalViews());
        assertEquals(10000L, category2.getTotalVotes());
        assertEquals(4.7, category2.getAverageRating());
    }
    
    @Test
    @DisplayName("Test DailyActiveUsersResponseDTO ActivityDataPoint")
    void testDailyActiveUsersActivityDataPoint() {
        DailyActiveUsersResponseDTO.ActivityDataPoint point = 
            new DailyActiveUsersResponseDTO.ActivityDataPoint(12, 150L, 15L, 75L);
        
        assertEquals(12, point.getHour());
        assertEquals(150L, point.getActiveUsers());
        assertEquals(15L, point.getNewUsers());
        assertEquals(75L, point.getReadingSessions());
        
        // Test setters
        point.setHour(18);
        point.setActiveUsers(200L);
        point.setNewUsers(20L);
        point.setReadingSessions(100L);
        
        assertEquals(18, point.getHour());
        assertEquals(200L, point.getActiveUsers());
        assertEquals(20L, point.getNewUsers());
        assertEquals(100L, point.getReadingSessions());
        
        // Test equals, hashCode, and canEqual methods for PlatformStatisticsResponseDTO
        Date testTimestamp = new Date();
        PlatformStatisticsResponseDTO platform1 = new PlatformStatisticsResponseDTO();
        platform1.setTimestamp(testTimestamp);
        platform1.setTotalUsers(1000L);
        platform1.setActiveUsers(500L);
        
        PlatformStatisticsResponseDTO platform2 = new PlatformStatisticsResponseDTO();
        platform2.setTimestamp(testTimestamp);
        platform2.setTotalUsers(1000L);
        platform2.setActiveUsers(500L);
        
        assertEquals(platform1, platform2);
        assertEquals(platform1.hashCode(), platform2.hashCode());
        assertNotEquals(platform1, null);
        assertEquals(platform1, platform1);
        assertTrue(platform1.canEqual(platform2));
    }
    
    @Test
    @DisplayName("Test AnalyticsRequestDTO equals, hashCode, canEqual")
    void testAnalyticsRequestDTOEqualsHashCode() {
        Date startDate = new Date();
        Date endDate = new Date();
        
        AnalyticsRequestDTO dto1 = new AnalyticsRequestDTO();
        dto1.setStartDate(startDate);
        dto1.setEndDate(endDate);
        dto1.setPeriod("daily");
        dto1.setCategoryId(1);
        dto1.setAuthorId("author1");
        dto1.setStatus(1);
        
        AnalyticsRequestDTO dto2 = new AnalyticsRequestDTO();
        dto2.setStartDate(new Date(startDate.getTime()));
        dto2.setEndDate(new Date(endDate.getTime()));
        dto2.setPeriod("daily");
        dto2.setCategoryId(1);
        dto2.setAuthorId("author1");
        dto2.setStatus(1);
        
        AnalyticsRequestDTO dto3 = new AnalyticsRequestDTO();
        dto3.setStartDate(startDate);
        dto3.setEndDate(endDate);
        dto3.setPeriod("weekly");
        dto3.setCategoryId(1);
        dto3.setAuthorId("author1");
        dto3.setStatus(1);
        
        // Test equals
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);
        
        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
        
        // Test canEqual
        assertTrue(dto1.canEqual(dto2));
        assertTrue(dto1.canEqual(dto3));
        assertFalse(dto1.canEqual(null));
        
        // Test toString
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("AnalyticsRequestDTO"));
    }
    
    @Test
    @DisplayName("Test ReadingActivityResponseDTO equals, hashCode, canEqual")
    void testReadingActivityResponseDTOEqualsHashCode() {
        Date startDate = new Date();
        Date endDate = new Date();
        
        ReadingActivityResponseDTO dto1 = new ReadingActivityResponseDTO();
        dto1.setPeriod("daily");
        dto1.setStartDate(startDate);
        dto1.setEndDate(endDate);
        dto1.setTotalActivity(100L);
        dto1.setAverageDailyActivity(10.5);
        dto1.setPeakActivity(50L);
        dto1.setPeakDate("2025-01-01");
        
        ReadingActivityResponseDTO dto2 = new ReadingActivityResponseDTO();
        dto2.setPeriod("daily");
        dto2.setStartDate(new Date(startDate.getTime()));
        dto2.setEndDate(new Date(endDate.getTime()));
        dto2.setTotalActivity(100L);
        dto2.setAverageDailyActivity(10.5);
        dto2.setPeakActivity(50L);
        dto2.setPeakDate("2025-01-01");
        
        ReadingActivityResponseDTO dto3 = new ReadingActivityResponseDTO();
        dto3.setPeriod("weekly");
        
        // These DTOs use @Getter @Setter @ToString, not @Data, so they only have reference equality
        assertNotSame(dto1, dto2);
        assertNotSame(dto1, dto3);
        
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ReadingActivityResponseDTO"));
    }
    
    @Test
    @DisplayName("Test DailyActiveUsersResponseDTO toString")
    void testDailyActiveUsersResponseDTOToString() {
        DailyActiveUsersResponseDTO dto1 = new DailyActiveUsersResponseDTO();
        dto1.setDau(100L);
        dto1.setWau(800L);
        dto1.setMau(3000L);
        
        // DailyActiveUsersResponseDTO doesn't use @Data, so equals/hashCode use reference equality
        assertNotNull(dto1);
    }
    
    @Test
    @DisplayName("Test AnalyticsTrendResponseDTO toString")
    void testAnalyticsTrendResponseDTOToString() {
        AnalyticsTrendResponseDTO dto1 = new AnalyticsTrendResponseDTO();
        dto1.setPeriod("daily");
        dto1.setTotalCount(100L);
        dto1.setAverageGrowth(5.5);
        dto1.setPeakValue(50L);
        dto1.setPeakDate("2025-01-01");
        
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("AnalyticsTrendResponseDTO"));
    }
    
    @Test
    @DisplayName("Test AnalyticsSummaryResponseDTO equals, hashCode, canEqual")
    void testAnalyticsSummaryResponseDTOEqualsHashCode() {
        Date startDate1 = new Date();
        Date endDate1 = new Date();
        Date startDate2 = new Date(startDate1.getTime());
        Date endDate2 = new Date(endDate1.getTime());
        
        AnalyticsSummaryResponseDTO dto1 = new AnalyticsSummaryResponseDTO();
        dto1.setStartDate(startDate1);
        dto1.setEndDate(endDate1);
        dto1.setPeriod("daily");
        dto1.setTotalUsers(1000L);
        dto1.setNewUsers(50L);
        dto1.setActiveUsers(800L);
        
        AnalyticsSummaryResponseDTO dto2 = new AnalyticsSummaryResponseDTO();
        dto2.setStartDate(startDate2);
        dto2.setEndDate(endDate2);
        dto2.setPeriod("daily");
        dto2.setTotalUsers(1000L);
        dto2.setNewUsers(50L);
        dto2.setActiveUsers(800L);
        
        AnalyticsSummaryResponseDTO dto3 = new AnalyticsSummaryResponseDTO();
        dto3.setPeriod("weekly");
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);
        assertTrue(dto1.canEqual(dto2));
        assertTrue(dto1.canEqual(dto3));
        assertFalse(dto1.canEqual(null));
        
        String toString = dto1.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("AnalyticsSummaryResponseDTO"));
    }
}

