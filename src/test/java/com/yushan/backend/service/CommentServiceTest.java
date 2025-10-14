package com.yushan.backend.service;

import com.yushan.backend.dao.ChapterMapper;
import com.yushan.backend.dao.CommentMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Chapter;
import com.yushan.backend.entity.Comment;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ChapterMapper chapterMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private EXPService expService;

    private UUID testUserId;
    private Integer testChapterId;
    private Integer testCommentId;
    private Integer testNovelId;
    private Comment testComment;
    private Chapter testChapter;
    private CommentCreateRequestDTO createRequest;
    private CommentUpdateRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testChapterId = 1;
        testCommentId = 1;
        testNovelId = 1;

        // Create test chapter
        testChapter = new Chapter();
        testChapter.setId(testChapterId);
        testChapter.setTitle("Test Chapter");
        testChapter.setIsValid(true);
        testChapter.setNovelId(testNovelId);

        // Create test comment
        testComment = new Comment();
        testComment.setId(testCommentId);
        testComment.setUserId(testUserId);
        testComment.setChapterId(testChapterId);
        testComment.setContent("Test comment content");
        testComment.setLikeCnt(0);
        testComment.setIsSpoiler(false);
        testComment.setCreateTime(new Date());
        testComment.setUpdateTime(new Date());

        // Create test request DTOs
        createRequest = new CommentCreateRequestDTO();
        createRequest.setChapterId(testChapterId);
        createRequest.setContent("Test comment content");
        createRequest.setIsSpoiler(false);

        updateRequest = new CommentUpdateRequestDTO();
        updateRequest.setContent("Updated comment content");
        updateRequest.setIsSpoiler(true);
    }

    // ========================================
    // CREATE COMMENT TESTS
    // ========================================

    @Test
    void createComment_Success() {
        // Arrange
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);
        when(commentMapper.existsByUserAndChapter(testUserId, testChapterId)).thenReturn(false);
        when(commentMapper.insertSelective(any(Comment.class))).thenReturn(1);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");

        doNothing().when(expService).addExp(any(UUID.class), any(Float.class));
        // Act
        CommentResponseDTO result = commentService.createComment(testUserId, createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertEquals(testChapterId, result.getChapterId());
        assertEquals("Test comment content", result.getContent());
        assertEquals(0, result.getLikeCnt());
        assertFalse(result.getIsSpoiler());
        assertTrue(result.getIsOwnComment());
        assertEquals("testuser", result.getUsername());

        verify(chapterMapper, times(2)).selectByPrimaryKey(testChapterId); // Called in createComment and toResponseDTO
        verify(commentMapper).existsByUserAndChapter(testUserId, testChapterId);
        verify(commentMapper).insertSelective(any(Comment.class));
        verify(expService).addExp(testUserId, 5f);
    }

    @Test
    void createComment_ChapterNotFound_ThrowsException() {
        // Arrange
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(testUserId, createRequest)
        );
        assertEquals("Chapter not found", exception.getMessage());

        verify(chapterMapper).selectByPrimaryKey(testChapterId);
        verify(commentMapper, never()).insertSelective(any());
    }

    @Test
    void createComment_ChapterInvalid_ThrowsException() {
        // Arrange
        testChapter.setIsValid(false);
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(testUserId, createRequest)
        );
        assertEquals("Chapter not found", exception.getMessage());
    }

    @Test
    void createComment_UserAlreadyCommented_ThrowsException() {
        // Arrange
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);
        when(commentMapper.existsByUserAndChapter(testUserId, testChapterId)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(testUserId, createRequest)
        );
        assertEquals("You have already commented on this chapter", exception.getMessage());

        verify(commentMapper, never()).insertSelective(any());
    }

    @Test
    void createComment_WithSpoilerFlag_Success() {
        // Arrange
        createRequest.setIsSpoiler(true);
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);
        when(commentMapper.existsByUserAndChapter(testUserId, testChapterId)).thenReturn(false);
        when(commentMapper.insertSelective(any(Comment.class))).thenReturn(1);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");

        // Act
        CommentResponseDTO result = commentService.createComment(testUserId, createRequest);

        // Assert
        assertTrue(result.getIsSpoiler());
    }

    // ========================================
    // UPDATE COMMENT TESTS
    // ========================================

    @Test
    void updateComment_Success() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(commentMapper.updateByPrimaryKeySelective(any(Comment.class))).thenReturn(1);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        CommentResponseDTO result = commentService.updateComment(testCommentId, testUserId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated comment content", result.getContent());
        assertTrue(result.getIsSpoiler());

        verify(commentMapper).selectByPrimaryKey(testCommentId);
        verify(commentMapper).updateByPrimaryKeySelective(any(Comment.class));
    }

    @Test
    void updateComment_CommentNotFound_ThrowsException() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.updateComment(testCommentId, testUserId, updateRequest)
        );
        assertEquals("Comment not found", exception.getMessage());
    }

    @Test
    void updateComment_NotCommentAuthor_ThrowsException() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.updateComment(testCommentId, differentUserId, updateRequest)
        );
        assertEquals("You can only update your own comments", exception.getMessage());

        verify(commentMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void updateComment_NoChanges_SkipsUpdate() {
        // Arrange
        CommentUpdateRequestDTO noChangeRequest = new CommentUpdateRequestDTO();
        noChangeRequest.setContent("Test comment content"); // Same as original
        noChangeRequest.setIsSpoiler(false); // Same as original

        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        CommentResponseDTO result = commentService.updateComment(testCommentId, testUserId, noChangeRequest);

        // Assert
        assertNotNull(result);
        verify(commentMapper, never()).updateByPrimaryKeySelective(any());
    }

    // ========================================
    // DELETE COMMENT TESTS
    // ========================================

    @Test
    void deleteComment_ByAuthor_Success() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(commentMapper.deleteByPrimaryKey(testCommentId)).thenReturn(1);

        // Act
        boolean result = commentService.deleteComment(testCommentId, testUserId, false);

        // Assert
        assertTrue(result);
        verify(commentMapper).deleteByPrimaryKey(testCommentId);
    }

    @Test
    void deleteComment_ByAdmin_Success() {
        // Arrange
        UUID adminUserId = UUID.randomUUID();
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(commentMapper.deleteByPrimaryKey(testCommentId)).thenReturn(1);

        // Act
        boolean result = commentService.deleteComment(testCommentId, adminUserId, true);

        // Assert
        assertTrue(result);
        verify(commentMapper).deleteByPrimaryKey(testCommentId);
    }

    @Test
    void deleteComment_CommentNotFound_ThrowsException() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.deleteComment(testCommentId, testUserId, false)
        );
        assertEquals("Comment not found", exception.getMessage());
    }

    @Test
    void deleteComment_NotAuthorOrAdmin_ThrowsException() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.deleteComment(testCommentId, differentUserId, false)
        );
        assertEquals("You can only delete your own comments", exception.getMessage());

        verify(commentMapper, never()).deleteByPrimaryKey(anyInt());
    }

    // ========================================
    // GET COMMENT TESTS
    // ========================================

    @Test
    void getComment_Success() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        CommentResponseDTO result = commentService.getComment(testCommentId, testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testCommentId, result.getId());
        assertEquals(testUserId, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getIsOwnComment());
    }

    @Test
    void getComment_NotFound_ThrowsException() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.getComment(testCommentId, testUserId)
        );
        assertEquals("Comment not found", exception.getMessage());
    }

    // ========================================
    // GET COMMENTS BY CHAPTER TESTS
    // ========================================

    @Test
    void getCommentsByChapter_Success() {
        // Arrange
        int page = 0, size = 20;
        String sort = "createTime", order = "desc";
        List<Comment> comments = Arrays.asList(testComment);
        long totalCount = 1L;

        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);
        when(commentMapper.selectCommentsWithPagination(any(CommentSearchRequestDTO.class)))
                .thenReturn(comments);
        when(commentMapper.countComments(any(CommentSearchRequestDTO.class))).thenReturn(totalCount);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");

        // Act
        CommentListResponseDTO result = commentService.getCommentsByChapter(
                testChapterId, testUserId, page, size, sort, order);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals(1L, result.getTotalCount());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getCurrentPage());
        assertEquals(20, result.getPageSize());
    }

    @Test
    void getCommentsByChapter_ChapterNotFound_ThrowsException() {
        // Arrange
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.getCommentsByChapter(testChapterId, testUserId, 0, 20, "createTime", "desc")
        );
        assertEquals("Chapter not found", exception.getMessage());
    }

    @Test
    void getCommentsByChapter_ValidatesParameters() {
        // Arrange
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);
        when(commentMapper.selectCommentsWithPagination(any(CommentSearchRequestDTO.class)))
                .thenReturn(Collections.emptyList());
        when(commentMapper.countComments(any(CommentSearchRequestDTO.class))).thenReturn(0L);

        // Act - Test parameter validation
        CommentListResponseDTO result = commentService.getCommentsByChapter(
                testChapterId, testUserId, -1, 0, null, "invalid");

        // Assert - Parameters should be corrected
        assertEquals(0, result.getCurrentPage()); // page corrected from -1 to 0
        assertEquals(20, result.getPageSize()); // size corrected from 0 to 20
    }

    // ========================================
    // LIKE FUNCTIONALITY TESTS
    // ========================================

    @Test
    void toggleLike_LikeComment_Success() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(commentMapper.updateLikeCount(testCommentId, 1)).thenReturn(1);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        CommentResponseDTO result = commentService.toggleLike(testCommentId, testUserId, true);

        // Assert
        assertNotNull(result);
        verify(commentMapper).updateLikeCount(testCommentId, 1);
    }

    @Test
    void toggleLike_UnlikeComment_Success() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(commentMapper.updateLikeCount(testCommentId, -1)).thenReturn(1);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        CommentResponseDTO result = commentService.toggleLike(testCommentId, testUserId, false);

        // Assert
        assertNotNull(result);
        verify(commentMapper).updateLikeCount(testCommentId, -1);
    }

    @Test
    void toggleLike_CommentNotFound_ThrowsException() {
        // Arrange
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.toggleLike(testCommentId, testUserId, true)
        );
        assertEquals("Comment not found", exception.getMessage());
    }

    // ========================================
    // GET COMMENTS BY NOVEL TESTS
    // ========================================

    @Test
    void getCommentsByNovel_Success() {
        // Arrange
        CommentSearchRequestDTO searchRequest = CommentSearchRequestDTO.builder()
                .novelId(1)
                .page(0)
                .size(20)
                .sort("createTime")
                .order("desc")
                .build();

        List<Comment> comments = Arrays.asList(testComment);
        when(commentMapper.selectCommentsByNovelWithPagination(
                eq(1), any(), any(), eq("createTime"), eq("desc"), eq(0), eq(20)))
                .thenReturn(comments);
        when(commentMapper.countCommentsByNovel(eq(1), any(), any())).thenReturn(1L);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        CommentListResponseDTO result = commentService.getCommentsByNovel(1, testUserId, searchRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals(1L, result.getTotalCount());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void getCommentsByNovel_WithSpoilerFilter() {
        // Arrange
        CommentSearchRequestDTO searchRequest = CommentSearchRequestDTO.builder()
                .novelId(1)
                .isSpoiler(true)
                .page(0)
                .size(20)
                .sort("createTime")
                .order("desc")
                .build();

        when(commentMapper.selectCommentsByNovelWithPagination(
                eq(1), eq(true), any(), eq("createTime"), eq("desc"), eq(0), eq(20)))
                .thenReturn(Collections.emptyList());
        when(commentMapper.countCommentsByNovel(eq(1), eq(true), any())).thenReturn(0L);

        // Act
        CommentListResponseDTO result = commentService.getCommentsByNovel(1, testUserId, searchRequest);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getComments().size());
        assertEquals(0L, result.getTotalCount());
    }

    @Test
    void getCommentsByNovel_WithSearchFilter() {
        // Arrange
        CommentSearchRequestDTO searchRequest = CommentSearchRequestDTO.builder()
                .novelId(1)
                .search("test search")
                .page(0)
                .size(20)
                .sort("createTime")
                .order("desc")
                .build();

        when(commentMapper.selectCommentsByNovelWithPagination(
                eq(1), any(), eq("test search"), eq("createTime"), eq("desc"), eq(0), eq(20)))
                .thenReturn(Collections.emptyList());
        when(commentMapper.countCommentsByNovel(eq(1), any(), eq("test search"))).thenReturn(0L);

        // Act
        CommentListResponseDTO result = commentService.getCommentsByNovel(1, testUserId, searchRequest);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getComments().size());
        verify(commentMapper).selectCommentsByNovelWithPagination(
                eq(1), any(), eq("test search"), eq("createTime"), eq("desc"), eq(0), eq(20));
    }

    // ========================================
    // BATCH DELETE TESTS
    // ========================================

    @Test
    void batchDeleteComments_Admin_Success() {
        // Arrange
        List<Integer> commentIds = Arrays.asList(1, 2, 3);
        CommentBatchDeleteRequestDTO request = new CommentBatchDeleteRequestDTO();
        request.setCommentIds(commentIds);

        when(commentMapper.selectByPrimaryKey(1)).thenReturn(testComment);
        when(commentMapper.selectByPrimaryKey(2)).thenReturn(testComment);
        when(commentMapper.selectByPrimaryKey(3)).thenReturn(testComment);
        when(commentMapper.deleteByPrimaryKey(anyInt())).thenReturn(1);

        // Act
        int result = commentService.batchDeleteComments(request, true);

        // Assert
        assertEquals(3, result);
        verify(commentMapper, times(3)).deleteByPrimaryKey(anyInt());
    }

    @Test
    void batchDeleteComments_NotAdmin_ThrowsException() {
        // Arrange
        CommentBatchDeleteRequestDTO request = new CommentBatchDeleteRequestDTO();
        request.setCommentIds(Arrays.asList(1, 2, 3));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.batchDeleteComments(request, false)
        );
        assertEquals("Only administrators can perform batch delete", exception.getMessage());
    }

    // ========================================
    // UTILITY TESTS
    // ========================================

    @Test
    void hasUserCommentedOnChapter_True() {
        // Arrange
        when(commentMapper.existsByUserAndChapter(testUserId, testChapterId)).thenReturn(true);

        // Act
        boolean result = commentService.hasUserCommentedOnChapter(testUserId, testChapterId);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasUserCommentedOnChapter_False() {
        // Arrange
        when(commentMapper.existsByUserAndChapter(testUserId, testChapterId)).thenReturn(false);

        // Act
        boolean result = commentService.hasUserCommentedOnChapter(testUserId, testChapterId);

        // Assert
        assertFalse(result);
    }

    @Test
    void getChapterCommentStats_Success() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);
        when(commentMapper.selectByChapterId(testChapterId)).thenReturn(comments);

        // Act
        CommentStatisticsDTO result = commentService.getChapterCommentStats(testChapterId);

        // Assert
        assertNotNull(result);
        assertEquals(testChapterId, result.getChapterId());
        assertEquals("Test Chapter", result.getChapterTitle());
        assertEquals(1L, result.getTotalComments());
    }

    @Test
    void getUserComments_Success() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);
        when(commentMapper.selectByUserId(testUserId)).thenReturn(comments);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        List<CommentResponseDTO> result = commentService.getUserComments(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserId, result.get(0).getUserId());
    }
    // ========================================
    // ADDITIONAL TESTS FOR INCREASED COVERAGE
    // ========================================

    @Test
    void getAllComments_Success() {
        // Arrange
        CommentSearchRequestDTO searchRequest = CommentSearchRequestDTO.builder()
                .page(0)
                .size(50)
                .sort("createTime")
                .order("desc")
                .build();

        List<Comment> comments = Arrays.asList(testComment);
        when(commentMapper.selectCommentsWithPagination(any())).thenReturn(comments);
        when(commentMapper.countComments(any())).thenReturn(1L);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        CommentListResponseDTO result = commentService.getAllComments(searchRequest, testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals(1L, result.getTotalCount());
    }

    @Test
    void getAllComments_WithInvalidSizeAndSort() {
        // Arrange
        CommentSearchRequestDTO searchRequest = CommentSearchRequestDTO.builder()
                .page(0)
                .size(200) // Exceeds max of 100
                .sort("")
                .order("invalid")
                .build();

        when(commentMapper.selectCommentsWithPagination(any())).thenReturn(Collections.emptyList());
        when(commentMapper.countComments(any())).thenReturn(0L);

        // Act
        CommentListResponseDTO result = commentService.getAllComments(searchRequest, testUserId);

        // Assert
        assertEquals(100, searchRequest.getSize()); // Should be capped at 100
        assertEquals("createTime", searchRequest.getSort()); // Should default to createTime
        assertEquals("desc", searchRequest.getOrder()); // Should default to desc
    }

    @Test
    void deleteAllUserComments_Success() {
        // Arrange
        List<Comment> userComments = Arrays.asList(testComment, testComment, testComment);
        when(commentMapper.selectByUserId(testUserId)).thenReturn(userComments);
        when(commentMapper.deleteByPrimaryKey(anyInt())).thenReturn(1);

        // Act
        int result = commentService.deleteAllUserComments(testUserId);

        // Assert
        assertEquals(3, result);
        verify(commentMapper, times(3)).deleteByPrimaryKey(anyInt());
    }

    @Test
    void deleteAllChapterComments_Success() {
        // Arrange
        List<Comment> chapterComments = Arrays.asList(testComment, testComment);
        when(commentMapper.selectByChapterId(testChapterId)).thenReturn(chapterComments);
        when(commentMapper.deleteByPrimaryKey(anyInt())).thenReturn(1);

        // Act
        int result = commentService.deleteAllChapterComments(testChapterId);

        // Assert
        assertEquals(2, result);
        verify(commentMapper, times(2)).deleteByPrimaryKey(anyInt());
    }

    @Test
    void bulkUpdateSpoilerStatus_Success() {
        // Arrange
        CommentBulkSpoilerUpdateRequestDTO request = new CommentBulkSpoilerUpdateRequestDTO();
        request.setCommentIds(Arrays.asList(1, 2, 3));
        request.setIsSpoiler(true);

        when(commentMapper.selectByPrimaryKey(anyInt())).thenReturn(testComment);
        when(commentMapper.updateByPrimaryKeySelective(any())).thenReturn(1);

        // Act
        int result = commentService.bulkUpdateSpoilerStatus(request);

        // Assert
        assertEquals(3, result);
        verify(commentMapper, times(3)).updateByPrimaryKeySelective(any());
    }

    @Test
    void bulkUpdateSpoilerStatus_EmptyList_ThrowsException() {
        // Arrange
        CommentBulkSpoilerUpdateRequestDTO request = new CommentBulkSpoilerUpdateRequestDTO();
        request.setCommentIds(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.bulkUpdateSpoilerStatus(request)
        );
        assertEquals("Comment IDs list cannot be empty", exception.getMessage());
    }

    @Test
    void getModerationStatistics_Success() {
        // Arrange
        when(commentMapper.countComments(any())).thenReturn(100L, 30L);
        when(commentMapper.countCommentsInLastDays(1)).thenReturn(10L);
        when(commentMapper.countCommentsInLastDays(7)).thenReturn(50L);
        when(commentMapper.countCommentsInLastDays(30)).thenReturn(80L);

        Comment mostActiveComment = new Comment();
        mostActiveComment.setUserId(testUserId);
        when(commentMapper.selectMostActiveUser()).thenReturn(mostActiveComment);
        when(userService.getUsernameById(testUserId)).thenReturn("ActiveUser");
        when(commentMapper.countCommentsByUser(testUserId)).thenReturn(25L);

        Comment mostCommentedChapter = new Comment();
        mostCommentedChapter.setChapterId(testChapterId);
        when(commentMapper.selectMostCommentedChapter()).thenReturn(mostCommentedChapter);
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);
        when(commentMapper.countByChapterId(testChapterId)).thenReturn(40L);

        // Act
        CommentModerationStatsDTO result = commentService.getModerationStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getTotalComments());
        assertEquals(30L, result.getSpoilerComments());
        assertEquals(70L, result.getNonSpoilerComments());
        assertEquals(10L, result.getCommentsToday());
        assertEquals(50L, result.getCommentsThisWeek());
        assertEquals(80L, result.getCommentsThisMonth());
        assertEquals("ActiveUser", result.getMostActiveUsername());
        assertEquals(25L, result.getMostActiveUserCommentCount());
    }

    @Test
    void getModerationStatistics_NoMostActiveUser() {
        // Arrange
        when(commentMapper.countComments(any())).thenReturn(0L);
        when(commentMapper.countCommentsInLastDays(anyInt())).thenReturn(0L);
        when(commentMapper.selectMostActiveUser()).thenReturn(null);
        when(commentMapper.selectMostCommentedChapter()).thenReturn(null);

        // Act
        CommentModerationStatsDTO result = commentService.getModerationStatistics();

        // Assert
        assertNotNull(result);
        assertNull(result.getMostActiveUsername());
        assertNull(result.getMostCommentedChapterId());
    }

    @Test
    void getChapterCommentStats_EmptyComments() {
        // Arrange
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);
        when(commentMapper.selectByChapterId(testChapterId)).thenReturn(Collections.emptyList());

        // Act
        CommentStatisticsDTO result = commentService.getChapterCommentStats(testChapterId);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalComments());
        assertEquals(0L, result.getSpoilerComments());
        assertEquals(0L, result.getNonSpoilerComments());
        assertEquals(0, result.getAvgLikesPerComment());
        assertNull(result.getMostLikedCommentId());
    }

    @Test
    void batchDeleteComments_EmptyList_ThrowsException() {
        // Arrange
        CommentBatchDeleteRequestDTO request = new CommentBatchDeleteRequestDTO();
        request.setCommentIds(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.batchDeleteComments(request, true)
        );
        assertEquals("Comment IDs list cannot be empty", exception.getMessage());
    }
    @Test
    void toResponseDTO_WithUserServiceException() {
        // Arrange - Test when userService.getUsernameById throws exception
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(userService.getUsernameById(testUserId)).thenThrow(new RuntimeException("User not found"));
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(testChapter);

        // Act
        CommentResponseDTO result = commentService.getComment(testCommentId, testUserId);

        // Assert
        assertNotNull(result);
        assertNull(result.getUsername()); // Username should be null when exception occurs
        assertEquals(testCommentId, result.getId());
    }

    @Test
    void toResponseDTO_WithChapterNotFound() {
        // Arrange - Test when chapter is not found in toResponseDTO
        when(commentMapper.selectByPrimaryKey(testCommentId)).thenReturn(testComment);
        when(userService.getUsernameById(testUserId)).thenReturn("testuser");
        when(chapterMapper.selectByPrimaryKey(testChapterId)).thenReturn(null);

        // Act
        CommentResponseDTO result = commentService.getComment(testCommentId, testUserId);

        // Assert
        assertNotNull(result);
        assertEquals("Chapter not found", result.getChapterTitle());
    }

    @Test
    void batchDeleteComments_PartialFailure() {
        // Arrange - Some comments exist, some don't
        CommentBatchDeleteRequestDTO request = new CommentBatchDeleteRequestDTO();
        request.setCommentIds(Arrays.asList(1, 999, 2));

        when(commentMapper.selectByPrimaryKey(1)).thenReturn(testComment);
        when(commentMapper.selectByPrimaryKey(999)).thenReturn(null); // This one doesn't exist
        when(commentMapper.selectByPrimaryKey(2)).thenReturn(testComment);
        when(commentMapper.deleteByPrimaryKey(1)).thenReturn(1);
        when(commentMapper.deleteByPrimaryKey(2)).thenReturn(1);

        // Act
        int result = commentService.batchDeleteComments(request, true);

        // Assert
        assertEquals(2, result); // Only 2 deleted, 1 not found
        verify(commentMapper, times(2)).deleteByPrimaryKey(anyInt());
    }

    @Test
    void getCommentsByNovel_ValidatesInvalidParameters() {
        // Arrange
        CommentSearchRequestDTO searchRequest = CommentSearchRequestDTO.builder()
                .novelId(1)
                .page(-5) // Invalid page
                .size(0) // Invalid size
                .sort(null) // Null sort
                .order("INVALID") // Invalid order
                .build();

        when(commentMapper.selectCommentsByNovelWithPagination(
                anyInt(), any(), any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(commentMapper.countCommentsByNovel(anyInt(), any(), any())).thenReturn(0L);

        // Act
        CommentListResponseDTO result = commentService.getCommentsByNovel(1, testUserId, searchRequest);

        // Assert
        assertEquals(0, searchRequest.getPage()); // Corrected from -5
        assertEquals(20, searchRequest.getSize()); // Corrected from 0
        assertEquals("createTime", searchRequest.getSort()); // Default
        assertEquals("desc", searchRequest.getOrder()); // Default
    }
}
