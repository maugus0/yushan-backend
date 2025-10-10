package com.yushan.backend.util;

import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Chapter;
import com.yushan.backend.entity.Comment;
import com.yushan.backend.entity.User;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for creating test data for Comment-related tests
 */
public class CommentTestUtils {

    public static final UUID TEST_USER_ID = UUID.fromString("12345678-1234-1234-1234-123456789abc");
    public static final UUID TEST_AUTHOR_ID = UUID.fromString("87654321-4321-4321-4321-cba987654321");
    public static final Integer TEST_CHAPTER_ID = 1;
    public static final Integer TEST_COMMENT_ID = 1;
    public static final Integer TEST_NOVEL_ID = 1;

    /**
     * Create a test Comment entity
     */
    public static Comment createTestComment() {
        return createTestComment(TEST_COMMENT_ID, TEST_USER_ID, TEST_CHAPTER_ID);
    }

    /**
     * Create a test Comment entity with specified IDs
     */
    public static Comment createTestComment(Integer commentId, UUID userId, Integer chapterId) {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setUserId(userId);
        comment.setChapterId(chapterId);
        comment.setContent("This is a test comment");
        comment.setLikeCnt(5);
        comment.setIsSpoiler(false);
        Date now = new Date();
        comment.setCreateTime(now);
        comment.setUpdateTime(now);
        return comment;
    }

    /**
     * Create a test spoiler Comment entity
     */
    public static Comment createTestSpoilerComment() {
        Comment comment = createTestComment();
        comment.setContent("This is a spoiler comment");
        comment.setIsSpoiler(true);
        return comment;
    }

    /**
     * Create a test Chapter entity
     */
    public static Chapter createTestChapter() {
        return createTestChapter(TEST_CHAPTER_ID, TEST_NOVEL_ID);
    }

    /**
     * Create a test Chapter entity with specified IDs
     */
    public static Chapter createTestChapter(Integer chapterId, Integer novelId) {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        chapter.setNovelId(novelId);
        chapter.setTitle("Test Chapter Title");
        chapter.setContent("This is test chapter content");
        chapter.setChapterNumber(1);
        chapter.setIsValid(true);
        Date now = new Date();
        chapter.setCreateTime(now);
        chapter.setUpdateTime(now);
        return chapter;
    }

    /**
     * Create a test User entity
     */
    public static User createTestUser() {
        return createTestUser(TEST_USER_ID, "testuser", false, false);
    }

    /**
     * Create a test User entity with specified role
     */
    public static User createTestUser(UUID userId, String username, boolean isAuthor, boolean isAdmin) {
        User user = new User();
        user.setUuid(userId);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setHashPassword("hashedpassword");
        user.setIsAuthor(isAuthor);
        user.setIsAdmin(isAdmin);
        user.setStatus(1); // Active
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        return user;
    }

    /**
     * Create a test admin User entity
     */
    public static User createTestAdminUser() {
        return createTestUser(UUID.randomUUID(), "admin", false, true);
    }

    /**
     * Create a test author User entity
     */
    public static User createTestAuthorUser() {
        return createTestUser(UUID.randomUUID(), "author", true, false);
    }

    /**
     * Create CustomUserDetails for testing
     */
    public static CustomUserDetails createTestUserDetails() {
        return new CustomUserDetails(createTestUser());
    }

    /**
     * Create CustomUserDetails for admin testing
     */
    public static CustomUserDetails createTestAdminUserDetails() {
        return new CustomUserDetails(createTestAdminUser());
    }

    /**
     * Create CustomUserDetails for author testing
     */
    public static CustomUserDetails createTestAuthorUserDetails() {
        return new CustomUserDetails(createTestAuthorUser());
    }

    /**
     * Create a test CommentCreateRequestDTO
     */
    public static CommentCreateRequestDTO createCommentCreateRequest() {
        CommentCreateRequestDTO dto = new CommentCreateRequestDTO();
        dto.setChapterId(TEST_CHAPTER_ID);
        dto.setContent("This is a test comment");
        dto.setIsSpoiler(false);
        return dto;
    }

    /**
     * Create a test CommentCreateRequestDTO with spoiler
     */
    public static CommentCreateRequestDTO createSpoilerCommentCreateRequest() {
        CommentCreateRequestDTO dto = createCommentCreateRequest();
        dto.setContent("This is a spoiler comment");
        dto.setIsSpoiler(true);
        return dto;
    }

    /**
     * Create a test CommentUpdateRequestDTO
     */
    public static CommentUpdateRequestDTO createCommentUpdateRequest() {
        CommentUpdateRequestDTO dto = new CommentUpdateRequestDTO();
        dto.setContent("This is an updated comment");
        dto.setIsSpoiler(false);
        return dto;
    }

    /**
     * Create a test CommentResponseDTO
     */
    public static CommentResponseDTO createCommentResponseDTO() {
        return createCommentResponseDTO(TEST_COMMENT_ID, TEST_USER_ID, TEST_CHAPTER_ID, true);
    }

    /**
     * Create a test CommentResponseDTO with specified parameters
     */
    public static CommentResponseDTO createCommentResponseDTO(Integer commentId, UUID userId, Integer chapterId, boolean isOwnComment) {
        return CommentResponseDTO.builder()
                .id(commentId)
                .userId(userId)
                .chapterId(chapterId)
                .content("This is a test comment")
                .likeCnt(5)
                .isSpoiler(false)
                .createTime(new Date())
                .updateTime(new Date())
                .isOwnComment(isOwnComment)
                .username("testuser")
                .chapterTitle("Test Chapter Title")
                .build();
    }

    /**
     * Create a test CommentListResponseDTO
     */
    public static CommentListResponseDTO createCommentListResponseDTO(List<CommentResponseDTO> comments) {
        return CommentListResponseDTO.builder()
                .comments(comments)
                .totalCount((long) comments.size())
                .totalPages(1)
                .currentPage(0)
                .pageSize(20)
                .build();
    }

    /**
     * Create a test CommentListResponseDTO with single comment
     */
    public static CommentListResponseDTO createSingleCommentListResponseDTO() {
        List<CommentResponseDTO> comments = Arrays.asList(createCommentResponseDTO());
        return createCommentListResponseDTO(comments);
    }

    /**
     * Create a test CommentSearchRequestDTO
     */
    public static CommentSearchRequestDTO createCommentSearchRequest() {
        return CommentSearchRequestDTO.builder()
                .chapterId(TEST_CHAPTER_ID)
                .page(0)
                .size(20)
                .sort("createTime")
                .order("desc")
                .build();
    }

    /**
     * Create a test CommentSearchRequestDTO with filters
     */
    public static CommentSearchRequestDTO createFilteredCommentSearchRequest() {
        return CommentSearchRequestDTO.builder()
                .novelId(TEST_NOVEL_ID)
                .isSpoiler(true)
                .search("test")
                .page(1)
                .size(10)
                .sort("likeCnt")
                .order("asc")
                .build();
    }

    /**
     * Create a test CommentBatchDeleteRequestDTO
     */
    public static CommentBatchDeleteRequestDTO createBatchDeleteRequest() {
        CommentBatchDeleteRequestDTO dto = new CommentBatchDeleteRequestDTO();
        dto.setCommentIds(Arrays.asList(1, 2, 3));
        dto.setReason("Testing batch delete");
        return dto;
    }

    /**
     * Create a test CommentStatisticsDTO
     */
    public static CommentStatisticsDTO createCommentStatisticsDTO() {
        return CommentStatisticsDTO.builder()
                .chapterId(TEST_CHAPTER_ID)
                .chapterTitle("Test Chapter Title")
                .totalComments(10L)
                .build();
    }
}