package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.service.NovelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//
import com.yushan.backend.security.NovelGuard;
import static org.mockito.Mockito.*;
import com.yushan.backend.exception.ResourceNotFoundException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Mockito static imports sẽ được thêm khi bật test
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Skeleton tests for NovelController REST endpoints.
 * Verifies routing, authz (roles/ownership), validation, and basic response codes.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class NovelControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NovelService novelService;

    private MockMvc mockMvc;

    @MockBean
    private NovelGuard novelGuard;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void createNovel_AsAuthor_Returns201AndLocation() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "A Title");
        body.put("categoryId", 10);
        body.put("synopsis", "short");
        body.put("coverImgBase64", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD");
        body.put("isCompleted", false);

        com.yushan.backend.dto.NovelDetailResponseDTO resp = new com.yushan.backend.dto.NovelDetailResponseDTO();
        resp.setId(123);
        when(novelService.createNovel(any(), anyString(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/novels")
                        .with(user("author@example.com").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void createNovel_AsUser_Returns403() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "A Title");
        body.put("categoryId", 10);

        mockMvc.perform(post("/api/novels")
                        .with(user("user@example.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createNovel_ValidationErrors_Returns400() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", ""); // NotBlank
        body.put("categoryId", null); // NotNull

        mockMvc.perform(post("/api/novels")
                        .with(user("author@example.com").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void updateNovel_AsOwnerOrAdmin_Returns200() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "New Title");

        com.yushan.backend.dto.NovelDetailResponseDTO resp = new com.yushan.backend.dto.NovelDetailResponseDTO();
        resp.setId(123);
        when(novelGuard.canEdit(eq(123), any())).thenReturn(true);
        when(novelService.updateNovel(eq(123), any())).thenReturn(resp);

        mockMvc.perform(put("/api/novels/123")
                        .with(user("author@example.com").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void updateNovel_NotOwner_Returns401() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "New Title");

        when(novelGuard.canEdit(eq(123), any())).thenReturn(false);

        mockMvc.perform(put("/api/novels/123")
                        .with(user("user@example.com").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getNovel_PublicValid_Returns200() throws Exception {
        com.yushan.backend.dto.NovelDetailResponseDTO resp = new com.yushan.backend.dto.NovelDetailResponseDTO();
        resp.setId(123);
        when(novelService.getNovel(eq(123))).thenReturn(resp);

        mockMvc.perform(get("/api/novels/123"))
                .andExpect(status().isOk());
    }

    @Test
    void getNovel_ArchivedOrInvalid_Returns404() throws Exception {
        when(novelService.getNovel(eq(404))).thenThrow(new ResourceNotFoundException("not_found"));

        mockMvc.perform(get("/api/novels/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void listNovels_Public_Returns200() throws Exception {
        mockMvc.perform(get("/api/novels"))
                .andExpect(status().isOk());
    }

    @Test
    void listNovels_WithPagination_ReturnsPaginatedResults() throws Exception {
        // Arrange
        NovelDetailResponseDTO novel1 = createTestNovelDetailResponseDTO(1, "Novel 1", "Author 1");
        NovelDetailResponseDTO novel2 = createTestNovelDetailResponseDTO(2, "Novel 2", "Author 2");
        List<NovelDetailResponseDTO> novels = Arrays.asList(novel1, novel2);
        
        PageResponseDTO<NovelDetailResponseDTO> pageResponse = PageResponseDTO.of(novels, 25L, 0, 10);
        
        when(novelService.listNovelsWithPagination(any(NovelSearchRequestDTO.class))).thenReturn(pageResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/novels?page=0&size=10&sort=createTime&order=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(25))
                .andExpect(jsonPath("$.data.totalPages").value(3))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.first").value(true))
                .andExpect(jsonPath("$.data.last").value(false))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andExpect(jsonPath("$.data.hasPrevious").value(false));
    }

    @Test
    void listNovels_WithFilters_ReturnsFilteredResults() throws Exception {
        // Arrange
        NovelDetailResponseDTO novel = createTestNovelDetailResponseDTO(1, "Test Novel", "Test Author");
        PageResponseDTO<NovelDetailResponseDTO> pageResponse = PageResponseDTO.of(Arrays.asList(novel), 1L, 0, 10);
        
        when(novelService.listNovelsWithPagination(any(NovelSearchRequestDTO.class))).thenReturn(pageResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/novels?category=1&status=published&search=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void listNovels_WithAuthorFilter_ReturnsAuthorNovels() throws Exception {
        // Arrange
        String authorId = "123e4567-e89b-12d3-a456-426614174000";
        NovelDetailResponseDTO novel = createTestNovelDetailResponseDTO(1, "Author's Novel", "Author Name");
        PageResponseDTO<NovelDetailResponseDTO> pageResponse = PageResponseDTO.of(Arrays.asList(novel), 1L, 0, 10);
        
        when(novelService.listNovelsWithPagination(any(NovelSearchRequestDTO.class))).thenReturn(pageResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/novels?author=" + authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    void listNovels_WithSorting_ReturnsSortedResults() throws Exception {
        // Arrange
        NovelDetailResponseDTO novel1 = createTestNovelDetailResponseDTO(1, "A Novel", "Author 1");
        NovelDetailResponseDTO novel2 = createTestNovelDetailResponseDTO(2, "B Novel", "Author 2");
        List<NovelDetailResponseDTO> novels = Arrays.asList(novel1, novel2);
        PageResponseDTO<NovelDetailResponseDTO> pageResponse = PageResponseDTO.of(novels, 2L, 0, 10);
        
        when(novelService.listNovelsWithPagination(any(NovelSearchRequestDTO.class))).thenReturn(pageResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/novels?sort=title&order=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void listNovels_WithEmptyResults_ReturnsEmptyPage() throws Exception {
        // Arrange
        PageResponseDTO<NovelDetailResponseDTO> pageResponse = PageResponseDTO.of(Arrays.asList(), 0L, 0, 10);
        
        when(novelService.listNovelsWithPagination(any(NovelSearchRequestDTO.class))).thenReturn(pageResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/novels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(0));
    }

    @Test
    void listNovels_WithDefaultParameters_UsesDefaults() throws Exception {
        // Arrange
        PageResponseDTO<NovelDetailResponseDTO> pageResponse = PageResponseDTO.of(Arrays.asList(), 0L, 0, 10);
        
        when(novelService.listNovelsWithPagination(any(NovelSearchRequestDTO.class))).thenReturn(pageResponse);
        
        // Act & Assert - No query parameters
        mockMvc.perform(get("/api/novels"))
                .andExpect(status().isOk());
        
        // Verify service was called with default parameters
        verify(novelService).listNovelsWithPagination(argThat(req -> 
            req.getPage() == 0 && 
            req.getSize() == 10 && 
            "createTime".equals(req.getSort()) && 
            "desc".equals(req.getOrder())
        ));
    }

    private NovelDetailResponseDTO createTestNovelDetailResponseDTO(Integer id, String title, String authorName) {
        NovelDetailResponseDTO dto = new NovelDetailResponseDTO();
        dto.setId(id);
        dto.setUuid(java.util.UUID.randomUUID());
        dto.setTitle(title);
        dto.setAuthorUsername(authorName);
        dto.setCategoryId(1);
        dto.setCategoryName("Test Category");
        dto.setSynopsis("Test synopsis");
        dto.setCoverImgUrl("test-cover.jpg");
        dto.setStatus("PUBLISHED");
        dto.setIsCompleted(false);
        dto.setChapterCnt(5);
        dto.setWordCnt(10000L);
        dto.setAvgRating(4.5f);
        dto.setReviewCnt(10);
        dto.setViewCnt(1000L);
        dto.setVoteCnt(50);
        dto.setYuanCnt(0.0f);
        dto.setCreateTime(new java.util.Date());
        dto.setUpdateTime(new java.util.Date());
        dto.setPublishTime(new java.util.Date());
        return dto;
    }

    @Test
    void submitForReview_ShouldReturnSuccess_WhenAuthorSubmitsDraftNovel() throws Exception {
        // Arrange
        Integer novelId = 1;
        NovelDetailResponseDTO responseDTO = createTestNovelDetailResponseDTO(novelId, "Test Novel", "Test Author");
        responseDTO.setStatus("UNDER_REVIEW");
        
        when(novelService.submitForReview(eq(novelId), any())).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(post("/api/novels/{id}/submit-review", novelId)
                .with(user("testuser").roles("AUTHOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.status").value("UNDER_REVIEW"));
    }

    @Test
    void approveNovel_ShouldReturnSuccess_WhenAdminApprovesNovel() throws Exception {
        // Arrange
        Integer novelId = 1;
        NovelDetailResponseDTO responseDTO = createTestNovelDetailResponseDTO(novelId, "Test Novel", "Test Author");
        responseDTO.setStatus("PUBLISHED");
        
        when(novelService.approveNovel(novelId)).thenReturn(responseDTO);
        
        // Act & Assert
        mockMvc.perform(post("/api/novels/{id}/approve", novelId)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));
    }

    @Test
    void getNovelsUnderReview_ShouldReturnPaginatedResults_WhenAdminRequests() throws Exception {
        // Arrange
        NovelDetailResponseDTO novel1 = createTestNovelDetailResponseDTO(1, "Novel 1", "Author 1");
        NovelDetailResponseDTO novel2 = createTestNovelDetailResponseDTO(2, "Novel 2", "Author 2");
        novel1.setStatus("UNDER_REVIEW");
        novel2.setStatus("UNDER_REVIEW");
        
        PageResponseDTO<NovelDetailResponseDTO> pageResponse = PageResponseDTO.of(
            Arrays.asList(novel1, novel2), 2L, 0, 10);
        
        when(novelService.getNovelsUnderReview(0, 10)).thenReturn(pageResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/novels/admin/under-review")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void updateNovel_ShouldRejectStatusChange_WhenAuthorTriesToChangeStatus() throws Exception {
        // Arrange
        Integer novelId = 1;
        NovelUpdateRequestDTO updateRequest = new NovelUpdateRequestDTO();
        updateRequest.setStatus("PUBLISHED");
        
        // Act & Assert
        mockMvc.perform(put("/api/novels/{id}", novelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(user("testuser").roles("AUTHOR")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void hideNovel_AsAuthor_Returns200() throws Exception {
        NovelDetailResponseDTO response = new NovelDetailResponseDTO();
        response.setId(123);
        when(novelService.hideNovel(123)).thenReturn(response);
        when(novelGuard.canEdit(eq(123), any())).thenReturn(true);

        mockMvc.perform(post("/api/novels/123/hide")
                        .with(user("author@example.com").roles("AUTHOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Novel hidden"));
    }

    @Test
    void archiveNovel_AsAuthor_Returns200() throws Exception {
        NovelDetailResponseDTO response = new NovelDetailResponseDTO();
        response.setId(123);
        when(novelService.archiveNovel(123)).thenReturn(response);
        when(novelGuard.canEdit(eq(123), any())).thenReturn(true);

        mockMvc.perform(post("/api/novels/123/archive")
                        .with(user("author@example.com").roles("AUTHOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Novel archived"));
    }
}


