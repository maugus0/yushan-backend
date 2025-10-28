package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AnalyticsController
 * Admin only endpoints for analytics
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class AnalyticsControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnalyticsService analyticsService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Nested
    @DisplayName("GET /api/admin/analytics/users/trends - Get User Trends")
    class GetUserTrendsTests {

        @Test
        @DisplayName("Should return user trends successfully as ADMIN")
        void getUserTrends_AsAdmin_Returns200() throws Exception {
            // Given
            AnalyticsTrendResponseDTO mockResponse = createMockUserTrendsResponse();
            when(analyticsService.getUserTrends(any(AnalyticsRequestDTO.class))).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/admin/analytics/users/trends")
                            .param("period", "daily")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("User trends retrieved successfully"));

            verify(analyticsService).getUserTrends(any(AnalyticsRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 403 when USER tries to access user trends")
        void getUserTrends_AsUser_Returns403() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/users/trends")
                            .param("period", "daily")
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(analyticsService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void getUserTrends_Unauthenticated_Returns401() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/users/trends"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(analyticsService);
        }

        @Test
        @DisplayName("Should handle invalid date format")
        void getUserTrends_InvalidDate_Returns400() throws Exception {
            // When & Then - invalid date returns 400 with error message
            mockMvc.perform(get("/api/admin/analytics/users/trends")
                            .param("startDate", "invalid-date")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400));

            verifyNoInteractions(analyticsService);
        }
    }

    @Nested
    @DisplayName("GET /api/admin/analytics/novels/trends - Get Novel Trends")
    class GetNovelTrendsTests {

        @Test
        @DisplayName("Should return novel trends successfully as ADMIN")
        void getNovelTrends_AsAdmin_Returns200() throws Exception {
            // Given
            AnalyticsTrendResponseDTO mockResponse = createMockNovelTrendsResponse();
            when(analyticsService.getNovelTrends(any(AnalyticsRequestDTO.class))).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/admin/analytics/novels/trends")
                            .param("period", "weekly")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Novel trends retrieved successfully"));

            verify(analyticsService).getNovelTrends(any(AnalyticsRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 403 when USER tries to access novel trends")
        void getNovelTrends_AsUser_Returns403() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/novels/trends")
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(analyticsService);
        }
    }

    @Nested
    @DisplayName("GET /api/admin/analytics/reading/activity - Get Reading Activity Trends")
    class GetReadingActivityTrendsTests {

        @Test
        @DisplayName("Should return reading activity trends successfully as ADMIN")
        void getReadingActivityTrends_AsAdmin_Returns200() throws Exception {
            // Given
            ReadingActivityResponseDTO mockResponse = createMockReadingActivityResponse();
            when(analyticsService.getReadingActivityTrends(any(AnalyticsRequestDTO.class))).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/admin/analytics/reading/activity")
                            .param("period", "daily")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Reading activity trends retrieved successfully"));

            verify(analyticsService).getReadingActivityTrends(any(AnalyticsRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 403 when USER tries to access reading activity trends")
        void getReadingActivityTrends_AsUser_Returns403() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/reading/activity")
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(analyticsService);
        }
    }

    @Nested
    @DisplayName("GET /api/admin/analytics/summary - Get Analytics Summary")
    class GetAnalyticsSummaryTests {

        @Test
        @DisplayName("Should return analytics summary successfully as ADMIN")
        void getAnalyticsSummary_AsAdmin_Returns200() throws Exception {
            // Given
            AnalyticsSummaryResponseDTO mockResponse = createMockAnalyticsSummaryResponse();
            when(analyticsService.getAnalyticsSummary(any(AnalyticsRequestDTO.class))).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/admin/analytics/summary")
                            .param("period", "daily")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Analytics summary retrieved successfully"));

            verify(analyticsService).getAnalyticsSummary(any(AnalyticsRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 403 when USER tries to access analytics summary")
        void getAnalyticsSummary_AsUser_Returns403() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/summary")
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(analyticsService);
        }
    }

    @Nested
    @DisplayName("GET /api/admin/analytics/platform/overview - Get Platform Statistics")
    class GetPlatformStatisticsTests {

        @Test
        @DisplayName("Should return platform statistics successfully as ADMIN")
        void getPlatformStatistics_AsAdmin_Returns200() throws Exception {
            // Given
            PlatformStatisticsResponseDTO mockResponse = createMockPlatformStatisticsResponse();
            when(analyticsService.getPlatformStatistics()).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/admin/analytics/platform/overview")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Platform statistics retrieved successfully"));

            verify(analyticsService).getPlatformStatistics();
        }

        @Test
        @DisplayName("Should return 403 when USER tries to access platform statistics")
        void getPlatformStatistics_AsUser_Returns403() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/platform/overview")
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(analyticsService);
        }
    }

    @Nested
    @DisplayName("GET /api/admin/analytics/platform/dau - Get Daily Active Users")
    class GetDailyActiveUsersTests {

        @Test
        @DisplayName("Should return daily active users successfully as ADMIN")
        void getDailyActiveUsers_AsAdmin_Returns200() throws Exception {
            // Given
            DailyActiveUsersResponseDTO mockResponse = createMockDailyActiveUsersResponse();
            when(analyticsService.getDailyActiveUsers(any(java.util.Date.class))).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/admin/analytics/platform/dau")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Daily active users retrieved successfully"));

            verify(analyticsService).getDailyActiveUsers(any(java.util.Date.class));
        }

        @Test
        @DisplayName("Should return 403 when USER tries to access daily active users")
        void getDailyActiveUsers_AsUser_Returns403() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/platform/dau")
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(analyticsService);
        }
    }

    @Nested
    @DisplayName("GET /api/admin/analytics/platform/top-content - Get Top Content")
    class GetTopContentTests {

        @Test
        @DisplayName("Should return top content successfully as ADMIN")
        void getTopContent_AsAdmin_Returns200() throws Exception {
            // Given
            TopContentResponseDTO mockResponse = createMockTopContentResponse();
            when(analyticsService.getTopContent(any(Integer.class))).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/admin/analytics/platform/top-content")
                            .param("limit", "10")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Top content statistics retrieved successfully"));

            verify(analyticsService).getTopContent(eq(10));
        }

        @Test
        @DisplayName("Should return 400 when limit is invalid")
        void getTopContent_InvalidLimit_Returns400() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/platform/top-content")
                            .param("limit", "0")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400));

            verifyNoInteractions(analyticsService);
        }

        @Test
        @DisplayName("Should return 400 when limit exceeds 100")
        void getTopContent_ExceedLimit_Returns400() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/platform/top-content")
                            .param("limit", "101")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400));

            verifyNoInteractions(analyticsService);
        }

        @Test
        @DisplayName("Should return 403 when USER tries to access top content")
        void getTopContent_AsUser_Returns403() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/admin/analytics/platform/top-content")
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(analyticsService);
        }
    }

    // Helper methods
    private AnalyticsTrendResponseDTO createMockUserTrendsResponse() {
        AnalyticsTrendResponseDTO response = new AnalyticsTrendResponseDTO();
        response.setPeriod("daily");
        response.setTotalCount(100L);
        response.setAverageGrowth(5.0);
        return response;
    }

    private AnalyticsTrendResponseDTO createMockNovelTrendsResponse() {
        AnalyticsTrendResponseDTO response = new AnalyticsTrendResponseDTO();
        response.setPeriod("weekly");
        response.setTotalCount(50L);
        response.setAverageGrowth(3.0);
        return response;
    }

    private ReadingActivityResponseDTO createMockReadingActivityResponse() {
        ReadingActivityResponseDTO response = new ReadingActivityResponseDTO();
        response.setPeriod("daily");
        response.setTotalActivity(1000L);
        return response;
    }

    private AnalyticsSummaryResponseDTO createMockAnalyticsSummaryResponse() {
        AnalyticsSummaryResponseDTO response = new AnalyticsSummaryResponseDTO();
        response.setTotalUsers(1000L);
        response.setNewUsers(50L);
        response.setActiveUsers(800L);
        response.setAuthors(100L);
        response.setTotalNovels(500L);
        response.setNewNovels(20L);
        response.setPublishedNovels(400L);
        response.setCompletedNovels(50L);
        response.setTotalViews(10000L);
        response.setTotalChapters(5000L);
        response.setTotalComments(2000L);
        response.setTotalReviews(500L);
        response.setTotalVotes(1000L);
        response.setAverageRating(4.5);
        response.setAverageViewsPerNovel(20.0);
        response.setAverageCommentsPerNovel(4.0);
        response.setAverageReviewsPerNovel(1.0);
        response.setUserGrowthRate(10.0);
        response.setNovelGrowthRate(15.0);
        response.setViewGrowthRate(20.0);
        response.setPeriod("daily");
        return response;
    }

    private PlatformStatisticsResponseDTO createMockPlatformStatisticsResponse() {
        PlatformStatisticsResponseDTO response = new PlatformStatisticsResponseDTO();
        response.setTotalUsers(5000L);
        response.setActiveUsers(3000L);
        response.setNewUsersToday(50L);
        response.setAuthors(200L);
        response.setAdmins(5L);
        response.setTotalNovels(2000L);
        response.setPublishedNovels(1500L);
        response.setCompletedNovels(200L);
        response.setTotalChapters(10000L);
        response.setTotalWords(50000000L);
        response.setTotalViews(500000L);
        response.setTotalComments(10000L);
        response.setTotalReviews(2000L);
        response.setTotalVotes(5000L);
        response.setAverageRating(4.5);
        response.setDailyActiveUsers(1000L);
        response.setWeeklyActiveUsers(5000L);
        response.setMonthlyActiveUsers(10000L);
        response.setUserGrowthRate(10.5);
        response.setContentGrowthRate(15.2);
        response.setEngagementGrowthRate(8.3);
        response.setTimestamp(new java.util.Date());
        return response;
    }

    private DailyActiveUsersResponseDTO createMockDailyActiveUsersResponse() {
        DailyActiveUsersResponseDTO response = new DailyActiveUsersResponseDTO();
        response.setDate(new java.util.Date());
        response.setDau(500L);
        response.setWau(2000L);
        response.setMau(5000L);
        return response;
    }

    private TopContentResponseDTO createMockTopContentResponse() {
        TopContentResponseDTO response = new TopContentResponseDTO();
        response.setDate(new java.util.Date());
        TopContentResponseDTO.TopNovel topNovel = new TopContentResponseDTO.TopNovel();
        topNovel.setId(1);
        topNovel.setTitle("Top Novel");
        response.setTopNovels(Arrays.asList(topNovel));
        response.setTopAuthors(Arrays.asList());
        response.setTopCategories(Arrays.asList());
        return response;
    }
}

