package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Get user trends analytics
     * Admin only endpoint for analyzing user registration trends
     */
    @GetMapping("/users/trends")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AnalyticsTrendResponseDTO> getUserTrends(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "period", defaultValue = "daily") String period,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "authorId", required = false) String authorId,
            @RequestParam(value = "status", required = false) Integer status) {
        
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod(period);
        request.setCategoryId(categoryId);
        request.setAuthorId(authorId);
        request.setStatus(status);
        
        // Parse dates if provided
        if (startDate != null && !startDate.isEmpty()) {
            try {
                request.setStartDate(java.sql.Date.valueOf(startDate));
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid startDate format. Use YYYY-MM-DD");
            }
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            try {
                request.setEndDate(java.sql.Date.valueOf(endDate));
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid endDate format. Use YYYY-MM-DD");
            }
        }

        AnalyticsTrendResponseDTO response = analyticsService.getUserTrends(request);
        return ApiResponse.success("User trends retrieved successfully", response);
    }

    /**
     * Get novel trends analytics
     * Admin only endpoint for analyzing novel creation trends
     */
    @GetMapping("/novels/trends")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AnalyticsTrendResponseDTO> getNovelTrends(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "period", defaultValue = "daily") String period,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "authorId", required = false) String authorId,
            @RequestParam(value = "status", required = false) Integer status) {
        
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod(period);
        request.setCategoryId(categoryId);
        request.setAuthorId(authorId);
        request.setStatus(status);
        
        // Parse dates if provided
        if (startDate != null && !startDate.isEmpty()) {
            try {
                request.setStartDate(java.sql.Date.valueOf(startDate));
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid startDate format. Use YYYY-MM-DD");
            }
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            try {
                request.setEndDate(java.sql.Date.valueOf(endDate));
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid endDate format. Use YYYY-MM-DD");
            }
        }

        AnalyticsTrendResponseDTO response = analyticsService.getNovelTrends(request);
        return ApiResponse.success("Novel trends retrieved successfully", response);
    }

    /**
     * Get reading activity analytics
     * Admin only endpoint for analyzing reading activity trends
     */
    @GetMapping("/reading/activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReadingActivityResponseDTO> getReadingActivityTrends(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "period", defaultValue = "daily") String period) {
        
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod(period);
        
        // Parse dates if provided
        if (startDate != null && !startDate.isEmpty()) {
            try {
                request.setStartDate(java.sql.Date.valueOf(startDate));
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid startDate format. Use YYYY-MM-DD");
            }
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            try {
                request.setEndDate(java.sql.Date.valueOf(endDate));
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid endDate format. Use YYYY-MM-DD");
            }
        }

        ReadingActivityResponseDTO response = analyticsService.getReadingActivityTrends(request);
        return ApiResponse.success("Reading activity trends retrieved successfully", response);
    }

    /**
     * Get analytics summary
     * Admin only endpoint for getting comprehensive analytics summary
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AnalyticsSummaryResponseDTO> getAnalyticsSummary(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "period", defaultValue = "daily") String period) {
        
        AnalyticsRequestDTO request = new AnalyticsRequestDTO();
        request.setPeriod(period);
        
        // Parse dates if provided
        if (startDate != null && !startDate.isEmpty()) {
            try {
                request.setStartDate(java.sql.Date.valueOf(startDate));
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid startDate format. Use YYYY-MM-DD");
            }
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            try {
                request.setEndDate(java.sql.Date.valueOf(endDate));
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid endDate format. Use YYYY-MM-DD");
            }
        }

        AnalyticsSummaryResponseDTO response = analyticsService.getAnalyticsSummary(request);
        return ApiResponse.success("Analytics summary retrieved successfully", response);
    }

    /**
     * Get platform-wide statistics overview
     * Admin only endpoint for comprehensive platform statistics
     */
    @GetMapping("/platform/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PlatformStatisticsResponseDTO> getPlatformStatistics() {
        PlatformStatisticsResponseDTO response = analyticsService.getPlatformStatistics();
        return ApiResponse.success("Platform statistics retrieved successfully", response);
    }

    /**
     * Get daily active users statistics
     * Admin only endpoint for DAU analysis
     */
    @GetMapping("/platform/dau")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DailyActiveUsersResponseDTO> getDailyActiveUsers(
            @RequestParam(value = "date", required = false) String date) {
        
        Date targetDate = new Date();
        if (date != null && !date.isEmpty()) {
            try {
                targetDate = java.sql.Date.valueOf(date);
            } catch (Exception e) {
                return ApiResponse.error(400, "Invalid date format. Use YYYY-MM-DD");
            }
        }

        DailyActiveUsersResponseDTO response = analyticsService.getDailyActiveUsers(targetDate);
        return ApiResponse.success("Daily active users retrieved successfully", response);
    }

    /**
     * Get top content statistics
     * Admin only endpoint for top content analysis
     */
    @GetMapping("/platform/top-content")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TopContentResponseDTO> getTopContent(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        
        if (limit <= 0 || limit > 100) {
            return ApiResponse.error(400, "Limit must be between 1 and 100");
        }

        TopContentResponseDTO response = analyticsService.getTopContent(limit);
        return ApiResponse.success("Top content statistics retrieved successfully", response);
    }
}
