package com.yushan.backend.controller;

import com.yushan.backend.dto.ApiResponse;
import com.yushan.backend.dto.HistoryResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.exception.UnauthorizedException;
import com.yushan.backend.security.CustomUserDetailsService;
import com.yushan.backend.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class HistoryControllerTest {

    @Mock
    private HistoryService historyService;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetailsService.CustomUserDetails userDetails;

    @InjectMocks
    private HistoryController historyController;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUserId = UUID.randomUUID();

        // Setup authentication mock
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());
    }

    @Test
    void addOrUpdateHistory_ShouldReturnSuccess_WhenValidInput() {
        // Given
        Integer novelId = 1;
        Integer chapterId = 5;

        doNothing().when(historyService).addOrUpdateHistory(any(UUID.class), anyInt(), anyInt());

        // When
        ApiResponse<String> result = historyController.addOrUpdateHistory(novelId, chapterId, authentication);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("History created/updated successfully", result.getMessage());
        verify(historyService).addOrUpdateHistory(testUserId, novelId, chapterId);
    }

    @Test
    void getUserHistory_ShouldReturnHistoryPage_WhenValidRequest() {
        // Given
        int page = 0;
        int size = 20;
        PageResponseDTO<HistoryResponseDTO> mockResponse = new PageResponseDTO<>();
        mockResponse.setTotalElements(10L);
        mockResponse.setTotalPages(1);

        when(historyService.getUserHistory(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        // When
        ApiResponse<PageResponseDTO<HistoryResponseDTO>> result = 
                historyController.getUserHistory(page, size, authentication);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("History retrieved successfully", result.getMessage());
        assertNotNull(result.getData());
        verify(historyService).getUserHistory(testUserId, page, size);
    }

    @Test
    void deleteHistory_ShouldReturnSuccess_WhenValidId() {
        // Given
        Integer historyId = 1;

        doNothing().when(historyService).deleteHistory(any(UUID.class), anyInt());

        // When
        ApiResponse<String> result = historyController.deleteHistory(historyId, authentication);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("History record deleted successfully", result.getMessage());
        verify(historyService).deleteHistory(testUserId, historyId);
    }

    @Test
    void clearHistory_ShouldReturnSuccess_WhenCalled() {
        // Given
        doNothing().when(historyService).clearHistory(any(UUID.class));

        // When
        ApiResponse<String> result = historyController.clearHistory(authentication);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("All history records have been cleared", result.getMessage());
        verify(historyService).clearHistory(testUserId);
    }

    @Test
    void getCurrentUserId_ShouldThrowUnauthorizedException_WhenAuthenticationIsNull() {
        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            historyController.getCurrentUserId(null);
        });
    }

    @Test
    void getCurrentUserId_ShouldThrowUnauthorizedException_WhenNotAuthenticated() {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            historyController.getCurrentUserId(authentication);
        });
    }
}