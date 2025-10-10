package com.yushan.backend.controller;

import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.NovelDetailResponseDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.service.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class RankingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RankingService rankingService;

    @InjectMocks
    private RankingController rankingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rankingController).build();
    }

    @Test
    void getNovelRanking_ShouldReturnSuccessResponse() throws Exception {
        // Given
        PageResponseDTO<NovelDetailResponseDTO> mockResponse = new PageResponseDTO<>();
        when(rankingService.rankNovel(anyInt(), anyInt(), anyString(), any(), anyString()))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/ranking/novel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Novels retrieved successfully"));
    }

    @Test
    void getUserRanking_ShouldReturnSuccessResponse() throws Exception {
        // Given
        PageResponseDTO<UserProfileResponseDTO> mockResponse = new PageResponseDTO<>();
        when(rankingService.rankUser(anyInt(), anyInt(), anyString()))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/ranking/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Novels retrieved successfully"));
    }

    @Test
    void getAuthorRanking_ShouldReturnSuccessResponse() throws Exception {
        // Given
        PageResponseDTO<AuthorResponseDTO> mockResponse = new PageResponseDTO<>();
        when(rankingService.rankAuthor(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/ranking/author")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Novels retrieved successfully"));
    }

    @Test
    void getNovelRanking_WithParameters_ShouldReturnSuccessResponse() throws Exception {
        // Given
        PageResponseDTO<NovelDetailResponseDTO> mockResponse = new PageResponseDTO<>();
        when(rankingService.rankNovel(anyInt(), anyInt(), anyString(), any(), anyString()))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/ranking/novel")
                        .param("page", "1")
                        .param("size", "20")
                        .param("sortType", "vote")
                        .param("category", "1")
                        .param("timeRange", "weekly")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Novels retrieved successfully"));
    }
}
