package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.service.ChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ChapterController REST endpoints.
 * All endpoints require authentication - no public endpoints.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class ChapterControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChapterService chapterService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Nested
    @DisplayName("POST /api/chapters - Create Chapter")
    class CreateChapterTests {

        @Test
        @DisplayName("Should create chapter successfully as AUTHOR")
        void createChapter_AsAuthor_Returns201() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);
            requestBody.put("chapterNumber", 1);
            requestBody.put("title", "Chapter 1: The Beginning");
            requestBody.put("content", "This is the content of the first chapter...");
            requestBody.put("isPremium", false);
            requestBody.put("yuanCost", 0.0);

            ChapterDetailResponseDTO mockResponse = createTestChapterDetailResponseDTO();
            when(chapterService.createChapter(isNull(), any(ChapterCreateRequestDTO.class)))
                    .thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(post("/api/chapters")
                            .with(user("author@example.com").roles("AUTHOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapter created successfully"))
                    .andExpect(jsonPath("$.data.chapterNumber").value(1))
                    .andExpect(jsonPath("$.data.title").value("Test Chapter"));

            verify(chapterService).createChapter(isNull(), any(ChapterCreateRequestDTO.class));
        }

        @Test
        @DisplayName("Should create chapter successfully as ADMIN")
        void createChapter_AsAdmin_Returns201() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);
            requestBody.put("chapterNumber", 1);
            requestBody.put("title", "Admin Chapter");
            requestBody.put("content", "Admin content");

            ChapterDetailResponseDTO mockResponse = createTestChapterDetailResponseDTO();
            when(chapterService.createChapter(isNull(), any(ChapterCreateRequestDTO.class)))
                    .thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(post("/api/chapters")
                            .with(user("admin@example.com").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()));

            verify(chapterService).createChapter(isNull(), any(ChapterCreateRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 401 when USER tries to create chapter")
        void createChapter_AsUser_Returns401() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);
            requestBody.put("chapterNumber", 1);
            requestBody.put("title", "Chapter 1");
            requestBody.put("content", "Test content");

            // When & Then
            mockMvc.perform(post("/api/chapters")
                            .with(user("user@example.com").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void createChapter_Unauthenticated_Returns401() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);
            requestBody.put("chapterNumber", 1);
            requestBody.put("title", "Chapter 1");
            requestBody.put("content", "Test content");

            // When & Then
            mockMvc.perform(post("/api/chapters")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 400 for validation errors")
        void createChapter_ValidationErrors_Returns400() throws Exception {
            // Given - Invalid request with missing required fields
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", null); // Required field
            requestBody.put("chapterNumber", null); // Required field
            requestBody.put("title", ""); // NotBlank

            // When & Then
            mockMvc.perform(post("/api/chapters")
                            .with(user("author@example.com").roles("AUTHOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("POST /api/chapters/batch - Batch Create Chapters")
    class BatchCreateChaptersTests {

        @Test
        @DisplayName("Should batch create chapters successfully as AUTHOR")
        void batchCreateChapters_AsAuthor_Returns201() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);
            
            Map<String, Object> chapterData1 = new HashMap<>();
            chapterData1.put("chapterNumber", 1);
            chapterData1.put("title", "Chapter 1");
            chapterData1.put("content", "Content 1");
            
            Map<String, Object> chapterData2 = new HashMap<>();
            chapterData2.put("chapterNumber", 2);
            chapterData2.put("title", "Chapter 2");
            chapterData2.put("content", "Content 2");
            
            requestBody.put("chapters", Arrays.asList(chapterData1, chapterData2));

            doNothing().when(chapterService).batchCreateChapters(isNull(), any(ChapterBatchCreateRequestDTO.class));

            // When & Then
            mockMvc.perform(post("/api/chapters/batch")
                            .with(user("author@example.com").roles("AUTHOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapters created successfully"));

            verify(chapterService).batchCreateChapters(isNull(), any(ChapterBatchCreateRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 401 when USER tries to batch create chapters")
        void batchCreateChapters_AsUser_Returns401() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);
            Map<String, Object> chapterData = new HashMap<>();
            chapterData.put("chapterNumber", 1);
            chapterData.put("title", "Chapter 1");
            chapterData.put("content", "Content 1");
            requestBody.put("chapters", Arrays.asList(chapterData));

            // When & Then
            mockMvc.perform(post("/api/chapters/batch")
                            .with(user("user@example.com").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void batchCreateChapters_Unauthenticated_Returns401() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);

            // When & Then
            mockMvc.perform(post("/api/chapters/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("GET /api/chapters/{uuid} - Get Chapter by UUID")
    class GetChapterByUuidTests {

        @Test
        @DisplayName("Should return chapter successfully for authenticated user")
        void getChapterByUuid_Authenticated_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            ChapterDetailResponseDTO mockResponse = createTestChapterDetailResponseDTO();
            when(chapterService.getChapterByUuid(chapterUuid)).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/chapters/{uuid}", chapterUuid)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapter retrieved successfully"))
                    .andExpect(jsonPath("$.data.title").value("Test Chapter"));

            verify(chapterService).getChapterByUuid(chapterUuid);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void getChapterByUuid_Unauthenticated_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/chapters/{uuid}", chapterUuid))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 404 when chapter not found")
        void getChapterByUuid_NotFound_Returns404() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            when(chapterService.getChapterByUuid(chapterUuid))
                    .thenThrow(new ResourceNotFoundException("Chapter not found"));

            // When & Then
            mockMvc.perform(get("/api/chapters/{uuid}", chapterUuid)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isNotFound());

            verify(chapterService).getChapterByUuid(chapterUuid);
        }
    }

    @Nested
    @DisplayName("GET /api/chapters/novel/{novelId}/number/{chapterNumber} - Get Chapter by Novel and Number")
    class GetChapterByNovelIdAndNumberTests {

        @Test
        @DisplayName("Should return chapter successfully for authenticated user")
        void getChapterByNovelIdAndNumber_Authenticated_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            Integer chapterNumber = 1;
            ChapterDetailResponseDTO mockResponse = createTestChapterDetailResponseDTO();
            when(chapterService.getChapterByNovelIdAndNumber(novelId, chapterNumber)).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/number/{chapterNumber}", novelId, chapterNumber)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapter retrieved successfully"))
                    .andExpect(jsonPath("$.data.title").value("Test Chapter"));

            verify(chapterService).getChapterByNovelIdAndNumber(novelId, chapterNumber);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void getChapterByNovelIdAndNumber_Unauthenticated_Returns401() throws Exception {
            // Given
            Integer novelId = 1;
            Integer chapterNumber = 1;

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/number/{chapterNumber}", novelId, chapterNumber))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 404 when chapter not found")
        void getChapterByNovelIdAndNumber_NotFound_Returns404() throws Exception {
            // Given
            Integer novelId = 1;
            Integer chapterNumber = 999;
            when(chapterService.getChapterByNovelIdAndNumber(novelId, chapterNumber))
                    .thenThrow(new ResourceNotFoundException("Chapter not found"));

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/number/{chapterNumber}", novelId, chapterNumber)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isNotFound());

            verify(chapterService).getChapterByNovelIdAndNumber(novelId, chapterNumber);
        }
    }

    @Nested
    @DisplayName("GET /api/chapters/novel/{novelId} - Get Chapters by Novel ID")
    class GetChaptersByNovelIdTests {

        @Test
        @DisplayName("Should return chapters successfully with default pagination")
        void getChaptersByNovelId_Authenticated_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            ChapterListResponseDTO mockResponse = createTestChapterListResponseDTO();
            when(chapterService.getChaptersByNovelId(novelId, 1, 20, true)).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}", novelId)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapters retrieved successfully"))
                    .andExpect(jsonPath("$.data.chapters").isArray())
                    .andExpect(jsonPath("$.data.totalCount").value(2));

            verify(chapterService).getChaptersByNovelId(novelId, 1, 20, true);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void getChaptersByNovelId_Unauthenticated_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}", novelId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return chapters with custom pagination parameters")
        void getChaptersByNovelId_WithPagination_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            ChapterListResponseDTO mockResponse = createTestChapterListResponseDTO();
            when(chapterService.getChaptersByNovelId(novelId, 2, 10, false)).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}?page=2&pageSize=10&publishedOnly=false", novelId)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()));

            verify(chapterService).getChaptersByNovelId(novelId, 2, 10, false);
        }
    }

    @Nested
    @DisplayName("POST /api/chapters/search - Search Chapters")
    class SearchChaptersTests {

        @Test
        @DisplayName("Should search chapters successfully")
        void searchChapters_Authenticated_Returns200() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);
            requestBody.put("page", 1);
            requestBody.put("pageSize", 20);
            requestBody.put("publishedOnly", true);

            ChapterListResponseDTO mockResponse = createTestChapterListResponseDTO();
            when(chapterService.searchChapters(any(ChapterSearchRequestDTO.class))).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(post("/api/chapters/search")
                            .with(user("user@example.com").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapters retrieved successfully"));

            verify(chapterService).searchChapters(any(ChapterSearchRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void searchChapters_Unauthenticated_Returns401() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("novelId", 1);

            // When & Then
            mockMvc.perform(post("/api/chapters/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("GET /api/chapters/novel/{novelId}/statistics - Get Chapter Statistics")
    class GetChapterStatisticsTests {

        @Test
        @DisplayName("Should return statistics successfully for AUTHOR")
        void getChapterStatistics_AsAuthor_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            ChapterStatisticsResponseDTO mockResponse = createTestChapterStatisticsResponseDTO();
            when(chapterService.getChapterStatistics(novelId)).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/statistics", novelId)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Statistics retrieved successfully"))
                    .andExpect(jsonPath("$.data.totalChapters").value(10))
                    .andExpect(jsonPath("$.data.publishedChapters").value(8));

            verify(chapterService).getChapterStatistics(novelId);
        }

        @Test
        @DisplayName("Should return statistics successfully for ADMIN")
        void getChapterStatistics_AsAdmin_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            ChapterStatisticsResponseDTO mockResponse = createTestChapterStatisticsResponseDTO();
            when(chapterService.getChapterStatistics(novelId)).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/statistics", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalChapters").value(10));

            verify(chapterService).getChapterStatistics(novelId);
        }

        @Test
        @DisplayName("Should return 401 when USER tries to access statistics")
        void getChapterStatistics_AsUser_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/statistics", novelId)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void getChapterStatistics_Unauthenticated_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/statistics", novelId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("PUT /api/chapters - Update Chapter")
    class UpdateChapterTests {

        @Test
        @DisplayName("Should update chapter successfully as AUTHOR")
        void updateChapter_AsAuthor_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("uuid", chapterUuid.toString());
            requestBody.put("title", "Updated Title");
            requestBody.put("content", "Updated content");

            ChapterDetailResponseDTO mockResponse = createTestChapterDetailResponseDTO();
            mockResponse.setTitle("Updated Title");
            when(chapterService.updateChapter(isNull(), any(ChapterUpdateRequestDTO.class)))
                    .thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(put("/api/chapters")
                            .with(user("author@example.com").roles("AUTHOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapter updated successfully"))
                    .andExpect(jsonPath("$.data.title").value("Updated Title"));

            verify(chapterService).updateChapter(isNull(), any(ChapterUpdateRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 401 when USER tries to update chapter")
        void updateChapter_AsUser_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("uuid", chapterUuid.toString());
            requestBody.put("title", "Updated Title");

            // When & Then
            mockMvc.perform(put("/api/chapters")
                            .with(user("user@example.com").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void updateChapter_Unauthenticated_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("uuid", chapterUuid.toString());
            requestBody.put("title", "Updated Title");

            // When & Then
            mockMvc.perform(put("/api/chapters")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 404 when chapter not found")
        void updateChapter_ChapterNotFound_Returns404() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("uuid", chapterUuid.toString());
            requestBody.put("title", "Updated Title");

            when(chapterService.updateChapter(isNull(), any(ChapterUpdateRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Chapter not found"));

            // When & Then
            mockMvc.perform(put("/api/chapters")
                            .with(user("author@example.com").roles("AUTHOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isNotFound());

            verify(chapterService).updateChapter(isNull(), any(ChapterUpdateRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("PATCH /api/chapters/publish - Publish Chapter")
    class PublishChapterTests {

        @Test
        @DisplayName("Should publish chapter successfully as AUTHOR")
        void publishChapter_AsAuthor_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("uuid", chapterUuid.toString());
            requestBody.put("isValid", true);

            doNothing().when(chapterService).publishChapter(isNull(), any(ChapterPublishRequestDTO.class));

            // When & Then
            mockMvc.perform(patch("/api/chapters/publish")
                            .with(user("author@example.com").roles("AUTHOR"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapter publish status updated successfully"));

            verify(chapterService).publishChapter(isNull(), any(ChapterPublishRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 401 when USER tries to publish chapter")
        void publishChapter_AsUser_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("uuid", chapterUuid.toString());
            requestBody.put("isValid", true);

            // When & Then
            mockMvc.perform(patch("/api/chapters/publish")
                            .with(user("user@example.com").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void publishChapter_Unauthenticated_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("uuid", chapterUuid.toString());
            requestBody.put("isValid", true);

            // When & Then
            mockMvc.perform(patch("/api/chapters/publish")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("PATCH /api/chapters/novel/{novelId}/publish - Batch Publish Chapters")
    class BatchPublishChaptersTests {

        @Test
        @DisplayName("Should batch publish chapters successfully as AUTHOR")
        void batchPublishChapters_AsAuthor_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            Boolean isValid = true;

            doNothing().when(chapterService).batchPublishChapters(isNull(), eq(novelId), eq(isValid));

            // When & Then
            mockMvc.perform(patch("/api/chapters/novel/{novelId}/publish?isValid=true", novelId)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapters publish status updated successfully"));

            verify(chapterService).batchPublishChapters(isNull(), eq(novelId), eq(isValid));
        }

        @Test
        @DisplayName("Should return 401 when USER tries to batch publish chapters")
        void batchPublishChapters_AsUser_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(patch("/api/chapters/novel/{novelId}/publish?isValid=true", novelId)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void batchPublishChapters_Unauthenticated_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(patch("/api/chapters/novel/{novelId}/publish?isValid=true", novelId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("POST /api/chapters/{uuid}/view - Increment View Count")
    class IncrementViewCountTests {

        @Test
        @DisplayName("Should increment view count successfully when authenticated")
        void incrementViewCount_Authenticated_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            doNothing().when(chapterService).incrementViewCount(chapterUuid);

            // When & Then
            mockMvc.perform(post("/api/chapters/{uuid}/view", chapterUuid)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("View count incremented"));

            verify(chapterService).incrementViewCount(chapterUuid);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void incrementViewCount_Unauthenticated_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(post("/api/chapters/{uuid}/view", chapterUuid))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("DELETE /api/chapters/{uuid} - Delete Chapter")
    class DeleteChapterTests {

        @Test
        @DisplayName("Should delete chapter successfully as AUTHOR")
        void deleteChapter_AsAuthor_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            doNothing().when(chapterService).deleteChapter(isNull(), eq(chapterUuid));

            // When & Then
            mockMvc.perform(delete("/api/chapters/{uuid}", chapterUuid)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapter deleted successfully"));

            verify(chapterService).deleteChapter(isNull(), eq(chapterUuid));
        }

        @Test
        @DisplayName("Should return 401 when USER tries to delete chapter")
        void deleteChapter_AsUser_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/chapters/{uuid}", chapterUuid)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void deleteChapter_Unauthenticated_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/chapters/{uuid}", chapterUuid))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 404 when chapter not found")
        void deleteChapter_ChapterNotFound_Returns404() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Chapter not found"))
                    .when(chapterService).deleteChapter(isNull(), eq(chapterUuid));

            // When & Then
            mockMvc.perform(delete("/api/chapters/{uuid}", chapterUuid)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isNotFound());

            verify(chapterService).deleteChapter(isNull(), eq(chapterUuid));
        }
    }

    @Nested
    @DisplayName("DELETE /api/chapters/novel/{novelId} - Delete All Chapters by Novel ID")
    class DeleteChaptersByNovelIdTests {

        @Test
        @DisplayName("Should delete all chapters successfully as AUTHOR")
        void deleteChaptersByNovelId_AsAuthor_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            doNothing().when(chapterService).deleteChaptersByNovelId(isNull(), eq(novelId));

            // When & Then
            mockMvc.perform(delete("/api/chapters/novel/{novelId}", novelId)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("All chapters deleted successfully"));

            verify(chapterService).deleteChaptersByNovelId(isNull(), eq(novelId));
        }

        @Test
        @DisplayName("Should return 401 when USER tries to delete all chapters")
        void deleteChaptersByNovelId_AsUser_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(delete("/api/chapters/novel/{novelId}", novelId)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void deleteChaptersByNovelId_Unauthenticated_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(delete("/api/chapters/novel/{novelId}", novelId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("GET /api/chapters/{uuid}/next - Get Next Chapter")
    class GetNextChapterTests {

        @Test
        @DisplayName("Should return next chapter UUID successfully when authenticated")
        void getNextChapter_Authenticated_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            UUID nextUuid = UUID.randomUUID();
            when(chapterService.getNextChapterUuid(chapterUuid)).thenReturn(nextUuid);

            // When & Then
            mockMvc.perform(get("/api/chapters/{uuid}/next", chapterUuid)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Next chapter retrieved"))
                    .andExpect(jsonPath("$.data").value(nextUuid.toString()));

            verify(chapterService).getNextChapterUuid(chapterUuid);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void getNextChapter_Unauthenticated_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/chapters/{uuid}/next", chapterUuid))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("GET /api/chapters/{uuid}/previous - Get Previous Chapter")
    class GetPreviousChapterTests {

        @Test
        @DisplayName("Should return previous chapter UUID successfully when authenticated")
        void getPreviousChapter_Authenticated_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            UUID prevUuid = UUID.randomUUID();
            when(chapterService.getPreviousChapterUuid(chapterUuid)).thenReturn(prevUuid);

            // When & Then
            mockMvc.perform(get("/api/chapters/{uuid}/previous", chapterUuid)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Previous chapter retrieved"))
                    .andExpect(jsonPath("$.data").value(prevUuid.toString()));

            verify(chapterService).getPreviousChapterUuid(chapterUuid);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void getPreviousChapter_Unauthenticated_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/chapters/{uuid}/previous", chapterUuid))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("GET /api/chapters/exists - Check Chapter Exists")
    class ChapterExistsTests {

        @Test
        @DisplayName("Should return true when chapter exists")
        void chapterExists_Found_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            Integer chapterNumber = 1;
            when(chapterService.chapterExists(novelId, chapterNumber)).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/api/chapters/exists?novelId={novelId}&chapterNumber={chapterNumber}", novelId, chapterNumber)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapter existence checked"))
                    .andExpect(jsonPath("$.data").value(true));

            verify(chapterService).chapterExists(novelId, chapterNumber);
        }

        @Test
        @DisplayName("Should return false when chapter does not exist")
        void chapterExists_NotFound_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            Integer chapterNumber = 999;
            when(chapterService.chapterExists(novelId, chapterNumber)).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/api/chapters/exists?novelId={novelId}&chapterNumber={chapterNumber}", novelId, chapterNumber)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(false));

            verify(chapterService).chapterExists(novelId, chapterNumber);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void chapterExists_Unauthenticated_Returns401() throws Exception {
            // Given
            Integer novelId = 1;
            Integer chapterNumber = 1;

            // When & Then
            mockMvc.perform(get("/api/chapters/exists?novelId={novelId}&chapterNumber={chapterNumber}", novelId, chapterNumber))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    @Nested
    @DisplayName("GET /api/chapters/novel/{novelId}/next-number - Get Next Available Chapter Number")
    class GetNextAvailableChapterNumberTests {

        @Test
        @DisplayName("Should return next available chapter number as AUTHOR")
        void getNextAvailableChapterNumber_AsAuthor_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            Integer nextNumber = 6;
            when(chapterService.getNextAvailableChapterNumber(novelId)).thenReturn(nextNumber);

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/next-number", novelId)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Next chapter number retrieved"))
                    .andExpect(jsonPath("$.data").value(6));

            verify(chapterService).getNextAvailableChapterNumber(novelId);
        }

        @Test
        @DisplayName("Should return next available chapter number as ADMIN")
        void getNextAvailableChapterNumber_AsAdmin_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            Integer nextNumber = 6;
            when(chapterService.getNextAvailableChapterNumber(novelId)).thenReturn(nextNumber);

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/next-number", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(6));

            verify(chapterService).getNextAvailableChapterNumber(novelId);
        }

        @Test
        @DisplayName("Should return 401 when USER tries to get next chapter number")
        void getNextAvailableChapterNumber_AsUser_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/next-number", novelId)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void getNextAvailableChapterNumber_Unauthenticated_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(get("/api/chapters/novel/{novelId}/next-number", novelId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }
    }

    // Helper methods for creating test DTOs
    private ChapterDetailResponseDTO createTestChapterDetailResponseDTO() {
        ChapterDetailResponseDTO response = new ChapterDetailResponseDTO();
        response.setUuid(UUID.randomUUID());
        response.setNovelId(1);
        response.setChapterNumber(1);
        response.setTitle("Test Chapter");
        response.setContent("Test content for the chapter");
        response.setWordCnt(100);
        response.setIsPremium(false);
        response.setYuanCost(0.0f);
        response.setViewCnt(0L);
        response.setIsValid(true);
        response.setCreateTime(new Date());
        response.setUpdateTime(new Date());
        response.setPublishTime(new Date());
        return response;
    }

    private ChapterListResponseDTO createTestChapterListResponseDTO() {
        ChapterListResponseDTO.ChapterSummary summary1 = new ChapterListResponseDTO.ChapterSummary();
        summary1.setUuid(UUID.randomUUID());
        summary1.setChapterNumber(1);
        summary1.setTitle("Chapter 1");
        summary1.setWordCnt(100);
        summary1.setIsPremium(false);
        summary1.setYuanCost(0.0f);
        summary1.setViewCnt(0L);
        summary1.setPublishTime(new Date());

        ChapterListResponseDTO.ChapterSummary summary2 = new ChapterListResponseDTO.ChapterSummary();
        summary2.setUuid(UUID.randomUUID());
        summary2.setChapterNumber(2);
        summary2.setTitle("Chapter 2");
        summary2.setWordCnt(150);
        summary2.setIsPremium(false);
        summary2.setYuanCost(0.0f);
        summary2.setViewCnt(5L);
        summary2.setPublishTime(new Date());

        List<ChapterListResponseDTO.ChapterSummary> chapters = Arrays.asList(summary1, summary2);
        return new ChapterListResponseDTO(chapters, 2L, 1, 20, 1);
    }

    private ChapterStatisticsResponseDTO createTestChapterStatisticsResponseDTO() {
        return new ChapterStatisticsResponseDTO(
                1, // novelId
                10L, // totalChapters
                8L, // publishedChapters
                1L, // draftChapters
                1L, // scheduledChapters
                2L, // premiumChapters
                8L, // freeChapters
                5000L, // totalWordCount
                1000L, // totalViewCount
                200.0f, // totalRevenue
                10 // maxChapterNumber
        );
    }

    @Nested
    @DisplayName("DELETE /api/chapters/admin/{uuid} - Admin Delete Chapter")
    class AdminDeleteChapterTests {

        @Test
        @DisplayName("Should delete chapter successfully as ADMIN")
        void adminDeleteChapter_AsAdmin_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            doNothing().when(chapterService).adminDeleteChapter(eq(chapterUuid));

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Chapter deleted successfully by admin"));

            verify(chapterService).adminDeleteChapter(eq(chapterUuid));
        }

        @Test
        @DisplayName("Should return 401 when AUTHOR tries to use admin delete")
        void adminDeleteChapter_AsAuthor_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when USER tries to use admin delete")
        void adminDeleteChapter_AsUser_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void adminDeleteChapter_Unauthenticated_Returns401() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 404 when chapter not found")
        void adminDeleteChapter_ChapterNotFound_Returns404() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Chapter not found"))
                    .when(chapterService).adminDeleteChapter(eq(chapterUuid));

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isNotFound());

            verify(chapterService).adminDeleteChapter(eq(chapterUuid));
        }

        @Test
        @DisplayName("Should not require userId parameter - bypasses ownership check")
        void adminDeleteChapter_NoUserIdRequired_Returns200() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            doNothing().when(chapterService).adminDeleteChapter(eq(chapterUuid));

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Chapter deleted successfully by admin"));

            // Verify service method is called without userId
            verify(chapterService).adminDeleteChapter(eq(chapterUuid));
            verify(chapterService, never()).deleteChapter(any(), any());
        }

        @Test
        @DisplayName("Should handle multiple admin delete requests")
        void adminDeleteChapter_MultipleRequests_Returns200() throws Exception {
            // Given
            UUID chapterUuid1 = UUID.randomUUID();
            UUID chapterUuid2 = UUID.randomUUID();
            UUID chapterUuid3 = UUID.randomUUID();

            doNothing().when(chapterService).adminDeleteChapter(any(UUID.class));

            // When & Then - First deletion
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid1)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            // Second deletion
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid2)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            // Third deletion
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid3)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            verify(chapterService, times(3)).adminDeleteChapter(any(UUID.class));
            verify(chapterService).adminDeleteChapter(eq(chapterUuid1));
            verify(chapterService).adminDeleteChapter(eq(chapterUuid2));
            verify(chapterService).adminDeleteChapter(eq(chapterUuid3));
        }
    }

    @Nested
    @DisplayName("DELETE /api/chapters/admin/novel/{novelId} - Admin Delete All Chapters by Novel ID")
    class AdminDeleteChaptersByNovelIdTests {

        @Test
        @DisplayName("Should delete all chapters successfully as ADMIN")
        void adminDeleteChaptersByNovelId_AsAdmin_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            doNothing().when(chapterService).adminDeleteChaptersByNovelId(eq(novelId));

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("All chapters deleted successfully by admin"));

            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId));
        }

        @Test
        @DisplayName("Should return 401 when AUTHOR tries to use admin delete all")
        void adminDeleteChaptersByNovelId_AsAuthor_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("author@example.com").roles("AUTHOR")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when USER tries to use admin delete all")
        void adminDeleteChaptersByNovelId_AsUser_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("user@example.com").roles("USER")))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 401 when unauthenticated")
        void adminDeleteChaptersByNovelId_Unauthenticated_Returns401() throws Exception {
            // Given
            Integer novelId = 1;

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Should return 404 when novel not found")
        void adminDeleteChaptersByNovelId_NovelNotFound_Returns404() throws Exception {
            // Given
            Integer novelId = 999;
            doThrow(new ResourceNotFoundException("Novel not found"))
                    .when(chapterService).adminDeleteChaptersByNovelId(eq(novelId));

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isNotFound());

            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId));
        }

        @Test
        @DisplayName("Should not require userId parameter - bypasses ownership check")
        void adminDeleteChaptersByNovelId_NoUserIdRequired_Returns200() throws Exception {
            // Given
            Integer novelId = 1;
            doNothing().when(chapterService).adminDeleteChaptersByNovelId(eq(novelId));

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("All chapters deleted successfully by admin"));

            // Verify service method is called without userId
            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId));
            verify(chapterService, never()).deleteChaptersByNovelId(any(), any());
        }

        @Test
        @DisplayName("Should handle deletion of different novels")
        void adminDeleteChaptersByNovelId_DifferentNovels_Returns200() throws Exception {
            // Given
            Integer novelId1 = 1;
            Integer novelId2 = 2;
            Integer novelId3 = 3;

            doNothing().when(chapterService).adminDeleteChaptersByNovelId(any(Integer.class));

            // When & Then - Delete chapters from first novel
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId1)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            // Delete chapters from second novel
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId2)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            // Delete chapters from third novel
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId3)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            verify(chapterService, times(3)).adminDeleteChaptersByNovelId(any(Integer.class));
            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId1));
            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId2));
            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId3));
        }

        @Test
        @DisplayName("Should handle archived novel deletion attempt")
        void adminDeleteChaptersByNovelId_ArchivedNovel_Returns404() throws Exception {
            // Given
            Integer novelId = 5;
            doThrow(new ResourceNotFoundException("Novel not found"))
                    .when(chapterService).adminDeleteChaptersByNovelId(eq(novelId));

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isNotFound());

            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId));
        }

        @Test
        @DisplayName("Should successfully delete when novel has no chapters")
        void adminDeleteChaptersByNovelId_NoChapters_Returns200() throws Exception {
            // Given
            Integer novelId = 10;
            doNothing().when(chapterService).adminDeleteChaptersByNovelId(eq(novelId));

            // When & Then
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("All chapters deleted successfully by admin"));

            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId));
        }

        @Test
        @DisplayName("Should accept various novel ID formats")
        void adminDeleteChaptersByNovelId_VariousIds_Returns200() throws Exception {
            // Given
            doNothing().when(chapterService).adminDeleteChaptersByNovelId(any(Integer.class));

            // When & Then - Test with ID 1
            mockMvc.perform(delete("/api/chapters/admin/novel/1")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            // Test with large ID
            mockMvc.perform(delete("/api/chapters/admin/novel/999999")
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            verify(chapterService).adminDeleteChaptersByNovelId(eq(1));
            verify(chapterService).adminDeleteChaptersByNovelId(eq(999999));
        }
    }

    @Nested
    @DisplayName("Admin Endpoints - Security and Authorization Tests")
    class AdminEndpointsSecurityTests {

        @Test
        @DisplayName("Admin endpoints should be separate from author endpoints")
        void adminEndpoints_SeparateFromAuthorEndpoints_DifferentPaths() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Integer novelId = 1;

            // When & Then - Verify admin paths are different from author paths
            // Admin single delete
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            // Admin bulk delete
            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            // Verify the methods are called correctly
            verify(chapterService).adminDeleteChapter(eq(chapterUuid));
            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId));
        }

        @Test
        @DisplayName("Should enforce ADMIN role strictly")
        void adminEndpoints_StrictRoleEnforcement_OnlyAdmin() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();

            // When & Then - Test all non-admin roles
            String[] nonAdminRoles = {"USER", "AUTHOR", "MODERATOR", "GUEST"};

            for (String role : nonAdminRoles) {
                mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                                .with(user("user@example.com").roles(role)))
                        .andExpect(status().isUnauthorized());
            }

            // Verify service is never called for non-admin roles
            verifyNoInteractions(chapterService);
        }

        @Test
        @DisplayName("Admin can delete chapters from any author")
        void adminEndpoints_CanDeleteAnyAuthorsChapters_Success() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            doNothing().when(chapterService).adminDeleteChapter(eq(chapterUuid));

            // When & Then - Admin deletes without author check
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Chapter deleted successfully by admin"));

            // Verify no userId was passed (bypasses ownership check)
            verify(chapterService).adminDeleteChapter(eq(chapterUuid));
        }

        @Test
        @DisplayName("Both admin endpoints should work independently")
        void adminEndpoints_BothWorkIndependently_Success() throws Exception {
            // Given
            UUID chapterUuid = UUID.randomUUID();
            Integer novelId = 1;

            doNothing().when(chapterService).adminDeleteChapter(any(UUID.class));
            doNothing().when(chapterService).adminDeleteChaptersByNovelId(any(Integer.class));

            // When & Then - Use both endpoints
            mockMvc.perform(delete("/api/chapters/admin/{uuid}", chapterUuid)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/api/chapters/admin/novel/{novelId}", novelId)
                            .with(user("admin@example.com").roles("ADMIN")))
                    .andExpect(status().isOk());

            verify(chapterService).adminDeleteChapter(eq(chapterUuid));
            verify(chapterService).adminDeleteChaptersByNovelId(eq(novelId));
        }
    }
}
