package com.yushan.backend.controller;

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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class HistoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HistoryService historyService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HistoryController historyController;

    @Mock
    private CustomUserDetailsService.CustomUserDetails userDetails;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(historyController).build();

        testUserId = UUID.randomUUID();

        // Setup authentication mock
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void addOrUpdateHistory_ShouldReturnSuccess_WhenValidInput() throws Exception {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());
        Integer novelId = 1;
        Integer chapterId = 5;

        doNothing().when(historyService).addOrUpdateHistory(any(UUID.class), anyInt(), anyInt());

        // When & Then
        mockMvc.perform(post("/api/history/novels/{novelId}/chapters/{chapterId}", novelId, chapterId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("History created/updated successfully"));

        verify(historyService).addOrUpdateHistory(testUserId, novelId, chapterId);
    }

    @Test
    void getUserHistory_ShouldReturnHistoryPage_WhenValidRequest() throws Exception {
        // Given
        int page = 0;
        int size = 20;
        PageResponseDTO<HistoryResponseDTO> mockResponse = new PageResponseDTO<>();
        mockResponse.setTotalElements(10L);
        mockResponse.setTotalPages(1);

        when(historyService.getUserHistory(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/history")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("History retrieved successfully"))
                .andExpect(jsonPath("$.data").exists());

        verify(historyService).getUserHistory(testUserId, page, size);
    }

    @Test
    void deleteHistory_ShouldReturnSuccess_WhenValidId() throws Exception {
        // Given
        Integer historyId = 1;

        doNothing().when(historyService).deleteHistory(any(UUID.class), anyInt());

        // When & Then
        mockMvc.perform(delete("/api/history/{id}", historyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("History record deleted successfully"));

        verify(historyService).deleteHistory(testUserId, historyId);
    }

    @Test
    void clearHistory_ShouldReturnSuccess_WhenCalled() throws Exception {
        // Given
        doNothing().when(historyService).clearHistory(any(UUID.class));

        // When & Then
        mockMvc.perform(delete("/api/history/clear")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("All history records have been cleared"));

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
