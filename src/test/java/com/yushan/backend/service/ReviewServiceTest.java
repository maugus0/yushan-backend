package com.yushan.backend.service;

import com.yushan.backend.dao.ReviewMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Review;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService Tests")
class ReviewServiceTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private NovelService novelService;

    @Mock
    private UserService userService;

    @Mock
    private EXPService expService;

    @InjectMocks
    private ReviewService reviewService;

    private UUID userId;
    private Integer novelId;
    private Integer reviewId;
    private Review review;
    private ReviewCreateRequestDTO createRequest;
    private ReviewUpdateRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        novelId = 1;
        reviewId = 1;

        review = new Review();
        review.setId(reviewId);
        review.setUuid(UUID.randomUUID());
        review.setUserId(userId);
        review.setNovelId(novelId);
        review.setRating(5);
        review.setTitle("Great novel");
        review.setContent("This is a great novel");
        review.setLikeCnt(0);
        review.setIsSpoiler(false);

        createRequest = new ReviewCreateRequestDTO();
        createRequest.setNovelId(novelId);
        createRequest.setRating(5);
        createRequest.setTitle("Great novel");
        createRequest.setContent("This is a great novel");
        createRequest.setIsSpoiler(false);

        updateRequest = new ReviewUpdateRequestDTO();
        updateRequest.setRating(4);
        updateRequest.setTitle("Updated review");
        updateRequest.setContent("Updated content");
        updateRequest.setIsSpoiler(false);
    }

    @Test
    @DisplayName("Test createReview - Success")
    void testCreateReviewSuccess() {
        when(novelService.getNovel(novelId)).thenReturn(mock(NovelDetailResponseDTO.class));
        when(reviewMapper.selectByUserAndNovel(userId, novelId)).thenReturn(null);
        when(reviewMapper.insertSelective(any(Review.class))).thenReturn(1);
        
        ReviewResponseDTO result = reviewService.createReview(userId, createRequest);
        
        assertNotNull(result);
        verify(reviewMapper).insertSelective(any(Review.class));
        verify(expService).addExp(eq(userId), eq(5f));
    }

    @Test
    @DisplayName("Test createReview - Novel not found")
    void testCreateReviewNovelNotFound() {
        when(novelService.getNovel(novelId)).thenThrow(new ResourceNotFoundException("Novel not found"));
        
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.createReview(userId, createRequest);
        });
    }

    @Test
    @DisplayName("Test createReview - Already reviewed")
    void testCreateReviewAlreadyReviewed() {
        when(novelService.getNovel(novelId)).thenReturn(mock(NovelDetailResponseDTO.class));
        when(reviewMapper.selectByUserAndNovel(userId, novelId)).thenReturn(review);
        
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(userId, createRequest);
        });
    }

    @Test
    @DisplayName("Test updateReview - Success")
    void testUpdateReviewSuccess() {
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        when(reviewMapper.updateByPrimaryKeySelective(any(Review.class))).thenReturn(1);
        
        ReviewResponseDTO result = reviewService.updateReview(reviewId, userId, updateRequest);
        
        assertNotNull(result);
        verify(reviewMapper).updateByPrimaryKeySelective(any(Review.class));
    }

    @Test
    @DisplayName("Test updateReview - Review not found")
    void testUpdateReviewNotFound() {
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(null);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.updateReview(reviewId, userId, updateRequest);
        });
    }

    @Test
    @DisplayName("Test updateReview - Not authorized")
    void testUpdateReviewNotAuthorized() {
        UUID differentUserId = UUID.randomUUID();
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.updateReview(reviewId, differentUserId, updateRequest);
        });
    }

    @Test
    @DisplayName("Test deleteReview - Success")
    void testDeleteReviewSuccess() {
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        when(reviewMapper.deleteByPrimaryKey(reviewId)).thenReturn(1);
        
        boolean result = reviewService.deleteReview(reviewId, userId, false);
        
        assertTrue(result);
        verify(reviewMapper).deleteByPrimaryKey(reviewId);
    }

    @Test
    @DisplayName("Test deleteReview - Not authorized")
    void testDeleteReviewNotAuthorized() {
        UUID differentUserId = UUID.randomUUID();
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.deleteReview(reviewId, differentUserId, false);
        });
    }

    @Test
    @DisplayName("Test deleteReview - Admin can delete")
    void testDeleteReviewAdmin() {
        UUID differentUserId = UUID.randomUUID();
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        when(reviewMapper.deleteByPrimaryKey(reviewId)).thenReturn(1);
        
        boolean result = reviewService.deleteReview(reviewId, differentUserId, true);
        
        assertTrue(result);
        verify(reviewMapper).deleteByPrimaryKey(reviewId);
    }

    @Test
    @DisplayName("Test getReview - Success")
    void testGetReviewSuccess() {
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        when(userService.getUsernameById(userId)).thenReturn("testuser");
        when(novelService.getNovel(novelId)).thenReturn(mock(NovelDetailResponseDTO.class));
        
        ReviewResponseDTO result = reviewService.getReview(reviewId);
        
        assertNotNull(result);
        assertEquals(reviewId, result.getId());
        verify(reviewMapper).selectByPrimaryKey(reviewId);
    }

    @Test
    @DisplayName("Test getReview - Not found")
    void testGetReviewNotFound() {
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(null);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.getReview(reviewId);
        });
    }

    @Test
    @DisplayName("Test toggleLike - Like success")
    void testToggleLikeSuccess() {
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        when(reviewMapper.updateLikeCount(reviewId, 1)).thenReturn(1);
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        
        ReviewResponseDTO result = reviewService.toggleLike(reviewId, userId, true);
        
        assertNotNull(result);
        verify(reviewMapper).updateLikeCount(reviewId, 1);
    }

    @Test
    @DisplayName("Test toggleLike - Unlike success")
    void testToggleUnlikeSuccess() {
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        when(reviewMapper.updateLikeCount(reviewId, -1)).thenReturn(1);
        when(reviewMapper.selectByPrimaryKey(reviewId)).thenReturn(review);
        
        ReviewResponseDTO result = reviewService.toggleLike(reviewId, userId, false);
        
        assertNotNull(result);
        verify(reviewMapper).updateLikeCount(reviewId, -1);
    }

    @Test
    @DisplayName("Test hasUserReviewedNovel - True")
    void testHasUserReviewedNovelTrue() {
        when(reviewMapper.selectByUserAndNovel(userId, novelId)).thenReturn(review);
        
        boolean result = reviewService.hasUserReviewedNovel(userId, novelId);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test hasUserReviewedNovel - False")
    void testHasUserReviewedNovelFalse() {
        when(reviewMapper.selectByUserAndNovel(userId, novelId)).thenReturn(null);
        
        boolean result = reviewService.hasUserReviewedNovel(userId, novelId);
        
        assertFalse(result);
    }
}
