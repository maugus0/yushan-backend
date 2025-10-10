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
}
