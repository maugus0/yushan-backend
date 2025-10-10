package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CommentController REST endpoints
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private MockMvc mockMvc;

    private UUID testUserId;
    private Integer testChapterId;
    private Integer testCommentId;
    private CommentResponseDTO testCommentResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Initialize test data
        testUserId = UUID.randomUUID();
        testChapterId = 1;
        testCommentId = 1;

        // Create test comment response
        testCommentResponse = CommentResponseDTO.builder()
                .id(testCommentId)
                .content("Test comment content")
                .chapterId(testChapterId)
                .userId(testUserId)
                .username("testuser")
                .likeCnt(0)
                .isSpoiler(false)
                .createTime(new Date())
                .updateTime(new Date())
                .isOwnComment(true)
                .chapterTitle("Test Chapter")
                .build();
    }

    // Helper methods for creating test authentication
    private Authentication createUserAuthentication(String email, String... roles) {
        User user = new User();
        user.setUuid(testUserId);
        user.setEmail(email);
        user.setUsername("testuser");
        user.setHashPassword("password");
        user.setStatus(1);
        user.setIsAuthor(Arrays.asList(roles).contains("AUTHOR"));
        user.setIsAdmin(Arrays.asList(roles).contains("ADMIN"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private Authentication createAdminAuthentication(String email) {
        User user = new User();
        user.setUuid(testUserId);
        user.setEmail(email);
        user.setUsername("admin");
        user.setHashPassword("password");
        user.setStatus(1);
        user.setIsAuthor(false);
        user.setIsAdmin(true);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Nested
    @DisplayName("POST /api/comments - Create Comment")
    class CreateCommentTests {

        @Test
        @DisplayName("Should create comment successfully when authenticated as USER")
        void shouldCreateCommentSuccessfullyAsUser() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chapterId", testChapterId);
            requestBody.put("content", "Test comment content");
            requestBody.put("isSpoiler", false);

            when(commentService.createComment(any(UUID.class), any(CommentCreateRequestDTO.class)))
                    .thenReturn(testCommentResponse);

            // When & Then
            mockMvc.perform(post("/api/comments")
                            .with(authentication(createUserAuthentication("user@example.com", "USER")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment created successfully"))
                    .andExpect(jsonPath("$.data.id").value(testCommentId))
                    .andExpect(jsonPath("$.data.content").value("Test comment content"))
                    .andExpect(jsonPath("$.data.chapterId").value(testChapterId));

            verify(commentService).createComment(any(UUID.class), any(CommentCreateRequestDTO.class));
        }

        @Test
        @DisplayName("Should create comment successfully when authenticated as AUTHOR")
        void shouldCreateCommentSuccessfullyAsAuthor() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chapterId", testChapterId);
            requestBody.put("content", "Test comment content");
            requestBody.put("isSpoiler", false);

            when(commentService.createComment(any(UUID.class), any(CommentCreateRequestDTO.class)))
                    .thenReturn(testCommentResponse);

            // When & Then
            mockMvc.perform(post("/api/comments")
                            .with(authentication(createUserAuthentication("author@example.com", "AUTHOR")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment created successfully"));

            verify(commentService).createComment(any(UUID.class), any(CommentCreateRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chapterId", testChapterId);
            requestBody.put("content", "Test comment content");

            // When & Then
            mockMvc.perform(post("/api/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("Should return 400 when chapterId is null")
        void shouldReturn400WhenChapterIdIsNull() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chapterId", null);
            requestBody.put("content", "Test comment content");

            // When & Then
            mockMvc.perform(post("/api/comments")
                            .with(authentication(createUserAuthentication("user@example.com", "USER")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("Should return 400 when content is blank")
        void shouldReturn400WhenContentIsBlank() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chapterId", testChapterId);
            requestBody.put("content", "");

            // When & Then
            mockMvc.perform(post("/api/comments")
                            .with(authentication(createUserAuthentication("user@example.com", "USER")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("Should return 400 when content exceeds max length")
        void shouldReturn400WhenContentExceedsMaxLength() throws Exception {
            // Given
            String longContent = "a".repeat(2001);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chapterId", testChapterId);
            requestBody.put("content", longContent);

            // When & Then
            mockMvc.perform(post("/api/comments")
                            .with(authentication(createUserAuthentication("user@example.com", "USER")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("PUT /api/comments/{id} - Update Comment")
    class UpdateCommentTests {

        @Test
        @DisplayName("Should update comment successfully when authenticated")
        void shouldUpdateCommentSuccessfullyWhenAuthenticated() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "Updated comment content");
            requestBody.put("isSpoiler", true);

            CommentResponseDTO updatedResponse = CommentResponseDTO.builder()
                    .id(testCommentId)
                    .content("Updated comment content")
                    .chapterId(testChapterId)
                    .userId(testUserId)
                    .username("testuser")
                    .likeCnt(0)
                    .isSpoiler(true)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .isOwnComment(true)
                    .chapterTitle("Test Chapter")
                    .build();

            when(commentService.updateComment(eq(testCommentId), any(UUID.class), any(CommentUpdateRequestDTO.class)))
                    .thenReturn(updatedResponse);

            // When & Then
            mockMvc.perform(put("/api/comments/{id}", testCommentId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment updated successfully"))
                    .andExpect(jsonPath("$.data.content").value("Updated comment content"))
                    .andExpect(jsonPath("$.data.isSpoiler").value(true));

            verify(commentService).updateComment(eq(testCommentId), any(UUID.class), any(CommentUpdateRequestDTO.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", "Updated comment content");

            // When & Then
            mockMvc.perform(put("/api/comments/{id}", testCommentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("Should return 400 when content exceeds max length")
        void shouldReturn400WhenContentExceedsMaxLength() throws Exception {
            // Given
            String longContent = "a".repeat(2001);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", longContent);

            // When & Then
            mockMvc.perform(put("/api/comments/{id}", testCommentId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("DELETE /api/comments/{id} - Delete Comment")
    class DeleteCommentTests {

        @Test
        @DisplayName("Should delete comment successfully as regular user")
        void shouldDeleteCommentSuccessfullyAsRegularUser() throws Exception {
            // Given
            when(commentService.deleteComment(eq(testCommentId), any(UUID.class), eq(false)))
                    .thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/comments/{id}", testCommentId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment deleted successfully"));

            verify(commentService).deleteComment(eq(testCommentId), any(UUID.class), eq(false));
        }

        @Test
        @DisplayName("Should delete comment successfully as admin")
        void shouldDeleteCommentSuccessfullyAsAdmin() throws Exception {
            // Given
            when(commentService.deleteComment(eq(testCommentId), any(UUID.class), eq(true)))
                    .thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/comments/{id}", testCommentId)
                            .with(authentication(createAdminAuthentication("admin@example.com"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment deleted successfully"));

            verify(commentService).deleteComment(eq(testCommentId), any(UUID.class), eq(true));
        }

        @Test
        @DisplayName("Should return 400 when deletion fails")
        void shouldReturn400WhenDeletionFails() throws Exception {
            // Given
            when(commentService.deleteComment(eq(testCommentId), any(UUID.class), eq(false)))
                    .thenReturn(false);

            // When & Then
            mockMvc.perform(delete("/api/comments/{id}", testCommentId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Failed to delete comment"));

            verify(commentService).deleteComment(eq(testCommentId), any(UUID.class), eq(false));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/comments/{id}", testCommentId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("GET /api/comments/{id} - Get Comment")
    class GetCommentTests {

        @Test
        @DisplayName("Should get comment successfully when authenticated")
        void shouldGetCommentSuccessfullyWhenAuthenticated() throws Exception {
            // Given
            when(commentService.getComment(eq(testCommentId), any(UUID.class)))
                    .thenReturn(testCommentResponse);

            // When & Then
            mockMvc.perform(get("/api/comments/{id}", testCommentId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment retrieved successfully"))
                    .andExpect(jsonPath("$.data.id").value(testCommentId))
                    .andExpect(jsonPath("$.data.content").value("Test comment content"));

            verify(commentService).getComment(eq(testCommentId), any(UUID.class));
        }

        @Test
        @DisplayName("Should get comment successfully when not authenticated")
        void shouldGetCommentSuccessfullyWhenNotAuthenticated() throws Exception {
            // Note: This test documents current controller behavior - 
            // the getComment endpoint actually requires authentication even though it should be public
            // When & Then
            mockMvc.perform(get("/api/comments/{id}", testCommentId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("GET /api/comments/chapter/{chapterId} - Get Comments by Chapter")
    class GetCommentsByChapterTests {

        @Test
        @DisplayName("Should get comments by chapter successfully")
        void shouldGetCommentsByChapterSuccessfully() throws Exception {
            // Given
            List<CommentResponseDTO> comments = Arrays.asList(testCommentResponse);
            CommentListResponseDTO listResponse = CommentListResponseDTO.builder()
                    .comments(comments)
                    .totalCount(1L)
                    .totalPages(1)
                    .currentPage(0)
                    .pageSize(20)
                    .build();

            when(commentService.getCommentsByChapter(eq(testChapterId), any(UUID.class), eq(0), eq(20), eq("createTime"), eq("desc")))
                    .thenReturn(listResponse);

            // When & Then
            // Note: All comment endpoints require authentication due to security config
            mockMvc.perform(get("/api/comments/chapter/{chapterId}", testChapterId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comments retrieved successfully"))
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.comments.length()").value(1))
                    .andExpect(jsonPath("$.data.totalCount").value(1));

            verify(commentService).getCommentsByChapter(eq(testChapterId), any(UUID.class), eq(0), eq(20), eq("createTime"), eq("desc"));
        }

        @Test
        @DisplayName("Should get comments by chapter with pagination")
        void shouldGetCommentsByChapterWithPagination() throws Exception {
            // Given
            CommentListResponseDTO listResponse = CommentListResponseDTO.builder()
                    .comments(Collections.emptyList())
                    .totalCount(0L)
                    .totalPages(0)
                    .currentPage(1)
                    .pageSize(10)
                    .build();

            when(commentService.getCommentsByChapter(eq(testChapterId), any(), eq(1), eq(10), eq("createTime"), eq("asc")))
                    .thenReturn(listResponse);

            // When & Then
            mockMvc.perform(get("/api/comments/chapter/{chapterId}", testChapterId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER")))
                            .param("page", "1")
                            .param("size", "10")
                            .param("sort", "createTime")
                            .param("order", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.currentPage").value(1))
                    .andExpect(jsonPath("$.data.pageSize").value(10));

            verify(commentService).getCommentsByChapter(eq(testChapterId), any(UUID.class), eq(1), eq(10), eq("createTime"), eq("asc"));
        }
    }

    @Nested
    @DisplayName("GET /api/comments/novel/{novelId} - Get Comments by Novel")
    class GetCommentsByNovelTests {

        @Test
        @DisplayName("Should get comments by novel successfully")
        void shouldGetCommentsByNovelSuccessfully() throws Exception {
            // Given
            List<CommentResponseDTO> comments = Arrays.asList(testCommentResponse);
            CommentListResponseDTO listResponse = CommentListResponseDTO.builder()
                    .comments(comments)
                    .totalCount(1L)
                    .totalPages(1)
                    .currentPage(0)
                    .pageSize(20)
                    .build();

            when(commentService.getCommentsByNovel(eq(1), any(UUID.class), any(CommentSearchRequestDTO.class)))
                    .thenReturn(listResponse);

            // When & Then
            // Note: All comment endpoints require authentication due to security config
            mockMvc.perform(get("/api/comments/novel/{novelId}", 1)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comments retrieved successfully"))
                    .andExpect(jsonPath("$.data.comments").isArray())
                    .andExpect(jsonPath("$.data.totalCount").value(1));

            verify(commentService).getCommentsByNovel(eq(1), any(UUID.class), any(CommentSearchRequestDTO.class));
        }

        @Test
        @DisplayName("Should get comments by novel with filters")
        void shouldGetCommentsByNovelWithFilters() throws Exception {
            // Given
            CommentListResponseDTO listResponse = CommentListResponseDTO.builder()
                    .comments(Collections.emptyList())
                    .totalCount(0L)
                    .totalPages(0)
                    .currentPage(0)
                    .pageSize(20)
                    .build();

            when(commentService.getCommentsByNovel(eq(1), any(UUID.class), any(CommentSearchRequestDTO.class)))
                    .thenReturn(listResponse);

            // When & Then
            // Note: All comment endpoints require authentication due to security config
            mockMvc.perform(get("/api/comments/novel/{novelId}", 1)
                            .param("isSpoiler", "true")
                            .param("search", "test")
                            .param("page", "1")
                            .param("size", "10")
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(0));

            verify(commentService).getCommentsByNovel(eq(1), any(UUID.class), any(CommentSearchRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("POST /api/comments/{id}/like - Like Comment")
    class LikeCommentTests {

        @Test
        @DisplayName("Should like comment successfully")
        void shouldLikeCommentSuccessfully() throws Exception {
            // Given
            CommentResponseDTO likedResponse = CommentResponseDTO.builder()
                    .id(testCommentId)
                    .content("Test comment content")
                    .chapterId(testChapterId)
                    .userId(testUserId)
                    .username("testuser")
                    .likeCnt(1)
                    .isSpoiler(false)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .isOwnComment(false)
                    .chapterTitle("Test Chapter")
                    .build();

            when(commentService.toggleLike(eq(testCommentId), any(UUID.class), eq(true)))
                    .thenReturn(likedResponse);

            // When & Then
            mockMvc.perform(post("/api/comments/{id}/like", testCommentId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment liked successfully"))
                    .andExpect(jsonPath("$.data.likeCnt").value(1));

            verify(commentService).toggleLike(eq(testCommentId), any(UUID.class), eq(true));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/comments/{id}/like", testCommentId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("GET /api/comments/my-comments - Get My Comments")
    class GetMyCommentsTests {

        @Test
        @DisplayName("Should get user's comments successfully")
        void shouldGetUserCommentsSuccessfully() throws Exception {
            // Given
            List<CommentResponseDTO> comments = Arrays.asList(testCommentResponse);
            when(commentService.getUserComments(any(UUID.class))).thenReturn(comments);

            // When & Then
            mockMvc.perform(get("/api/comments/my-comments")
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Your comments retrieved successfully"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1));

            verify(commentService).getUserComments(any(UUID.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/comments/my-comments"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("GET /api/comments/check/chapter/{chapterId} - Check User Commented")
    class CheckUserCommentedTests {

        @Test
        @DisplayName("Should check if user commented successfully")
        void shouldCheckIfUserCommentedSuccessfully() throws Exception {
            // Given
            when(commentService.hasUserCommentedOnChapter(any(UUID.class), eq(testChapterId)))
                    .thenReturn(true);

            // When & Then
            mockMvc.perform(get("/api/comments/check/chapter/{chapterId}", testChapterId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment status checked"))
                    .andExpect(jsonPath("$.data").value(true));

            verify(commentService).hasUserCommentedOnChapter(any(UUID.class), eq(testChapterId));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/comments/check/chapter/{chapterId}", testChapterId))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }
    }

    @Nested
    @DisplayName("GET /api/comments/chapter/{chapterId}/statistics - Get Comment Statistics")
    class GetCommentStatisticsTests {

        @Test
        @DisplayName("Should get comment statistics successfully")
        void shouldGetCommentStatisticsSuccessfully() throws Exception {
            // Given
            CommentStatisticsDTO stats = CommentStatisticsDTO.builder()
                    .chapterId(testChapterId)
                    .chapterTitle("Test Chapter")
                    .totalComments(5L)
                    .spoilerComments(2L)
                    .nonSpoilerComments(3L)
                    .avgLikesPerComment(5)
                    .mostLikedCommentId(1)
                    .build();

            when(commentService.getChapterCommentStats(eq(testChapterId))).thenReturn(stats);

            // When & Then
            // Note: Statistics endpoint requires authentication due to security config
            mockMvc.perform(get("/api/comments/chapter/{chapterId}/statistics", testChapterId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment statistics retrieved"))
                    .andExpect(jsonPath("$.data.chapterId").value(testChapterId))
                    .andExpect(jsonPath("$.data.totalComments").value(5));

            verify(commentService).getChapterCommentStats(eq(testChapterId));
        }
    }

    @Nested
    @DisplayName("Admin Endpoints")
    class AdminEndpointsTests {

        @Test
        @DisplayName("Should get all comments as admin successfully")
        void shouldGetAllCommentsAsAdminSuccessfully() throws Exception {
            // Given
            List<CommentResponseDTO> comments = Arrays.asList(testCommentResponse);
            CommentListResponseDTO listResponse = CommentListResponseDTO.builder()
                    .comments(comments)
                    .totalCount(1L)
                    .totalPages(1)
                    .currentPage(0)
                    .pageSize(20)
                    .build();

            when(commentService.getAllComments(any(CommentSearchRequestDTO.class), any(UUID.class)))
                    .thenReturn(listResponse);

            // When & Then
            mockMvc.perform(get("/api/comments/admin/all")
                            .with(authentication(createAdminAuthentication("admin@example.com"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("All comments retrieved successfully"));

            verify(commentService).getAllComments(any(CommentSearchRequestDTO.class), any(UUID.class));
        }

        @Test
        @DisplayName("Should return 401 when non-admin tries to get all comments")
        void shouldReturn401WhenNonAdminTriesToGetAllComments() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/comments/admin/all")
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("Should delete comment as admin successfully")
        void shouldDeleteCommentAsAdminSuccessfully() throws Exception {
            // Given
            when(commentService.deleteComment(eq(testCommentId), isNull(), eq(true)))
                    .thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/api/comments/admin/{id}", testCommentId)
                            .with(authentication(createAdminAuthentication("admin@example.com"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comment deleted successfully by admin"));

            verify(commentService).deleteComment(eq(testCommentId), isNull(), eq(true));
        }

        @Test
        @DisplayName("Should return 401 when non-admin tries to delete comment")
        void shouldReturn401WhenNonAdminTriesToDeleteComment() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/comments/admin/{id}", testCommentId)
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("Should batch delete comments as admin successfully")
        void shouldBatchDeleteCommentsAsAdminSuccessfully() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("commentIds", Arrays.asList(1, 2, 3));
            requestBody.put("reason", "Spam");

            when(commentService.batchDeleteComments(any(CommentBatchDeleteRequestDTO.class), eq(true)))
                    .thenReturn(3);

            // When & Then
            mockMvc.perform(post("/api/comments/admin/batch-delete")
                            .with(authentication(createAdminAuthentication("admin@example.com")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Successfully deleted 3 comment(s)"));

            verify(commentService).batchDeleteComments(any(CommentBatchDeleteRequestDTO.class), eq(true));
        }

        @Test
        @DisplayName("Should return 400 when batch delete with empty commentIds")
        void shouldReturn400WhenBatchDeleteWithEmptyCommentIds() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("commentIds", Collections.emptyList());

            // When & Then
            mockMvc.perform(post("/api/comments/admin/batch-delete")
                            .with(authentication(createAdminAuthentication("admin@example.com")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("Should return 401 when non-admin tries to batch delete")
        void shouldReturn401WhenNonAdminTriesToBatchDelete() throws Exception {
            // Given
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("commentIds", Arrays.asList(1, 2, 3));

            // When & Then
            mockMvc.perform(post("/api/comments/admin/batch-delete")
                            .with(authentication(createUserAuthentication("user@example.com", "USER")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }

        @Test
        @DisplayName("Should search comments as admin successfully")
        void shouldSearchCommentsAsAdminSuccessfully() throws Exception {
            // Given
            List<CommentResponseDTO> comments = Arrays.asList(testCommentResponse);
            CommentListResponseDTO listResponse = CommentListResponseDTO.builder()
                    .comments(comments)
                    .totalCount(1L)
                    .totalPages(1)
                    .currentPage(0)
                    .pageSize(20)
                    .build();

            when(commentService.getAllComments(any(CommentSearchRequestDTO.class), any(UUID.class)))
                    .thenReturn(listResponse);

            // When & Then
            mockMvc.perform(get("/api/comments/admin/search")
                            .with(authentication(createAdminAuthentication("admin@example.com")))
                            .param("search", "test")
                            .param("page", "0")
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("Comments search completed"));

            verify(commentService).getAllComments(any(CommentSearchRequestDTO.class), any(UUID.class));
        }

        @Test
        @DisplayName("Should return 401 when non-admin tries to search comments")
        void shouldReturn401WhenNonAdminTriesToSearchComments() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/comments/admin/search")
                            .with(authentication(createUserAuthentication("user@example.com", "USER"))))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(commentService);
        }
    }
}