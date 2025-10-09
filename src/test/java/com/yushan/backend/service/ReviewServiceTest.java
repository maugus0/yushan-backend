package com.yushan.backend.service;

import com.yushan.backend.dao.ReviewMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Review;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private NovelService novelService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    private UUID userId;
    private Integer novelId;
    private ReviewCreateRequestDTO createRequest;
    private Review review;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        novelId = 1;
        
        createRequest = new ReviewCreateRequestDTO();
        createRequest.setNovelId(novelId);
        createRequest.setContent("Great novel!");
        createRequest.setRating(5);
        createRequest.setIsSpoiler(false);

        review = new Review();
        review.setId(1);
        review.setUuid(UUID.randomUUID());
        review.setUserId(userId);
        review.setNovelId(novelId);
        review.setContent("Great novel!");
        review.setRating(5);
        review.setIsSpoiler(false);
        review.setCreateTime(new Date());
    }

    @Test
    void createReview_Success() {
        // Given
        when(novelService.getNovel(novelId)).thenReturn(mock(com.yushan.backend.dto.NovelDetailResponseDTO.class));
        when(reviewMapper.selectByUserAndNovel(userId, novelId)).thenReturn(null);
        when(reviewMapper.insertSelective(any(Review.class))).thenReturn(1);
        when(userService.getUsernameById(userId)).thenReturn("testuser");

        // When
        ReviewResponseDTO result = reviewService.createReview(userId, createRequest);

        // Then
        assertNotNull(result);
        assertEquals("Great novel!", result.getContent());
        assertEquals(5, result.getRating());
        
        verify(reviewMapper).insertSelective(any(Review.class));
        verify(novelService).updateNovelRatingAndCount(eq(novelId), anyFloat(), anyInt());
    }

    @Test
    void createReview_AlreadyReviewed_ShouldThrow() {
        // Given
        when(novelService.getNovel(novelId)).thenReturn(mock(com.yushan.backend.dto.NovelDetailResponseDTO.class));
        when(reviewMapper.selectByUserAndNovel(userId, novelId)).thenReturn(review);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            reviewService.createReview(userId, createRequest));
        
        verify(reviewMapper, never()).insertSelective(any());
    }

    @Test
    void getReview_Success() {
        // Given
        when(reviewMapper.selectByPrimaryKey(1)).thenReturn(review);
        when(userService.getUsernameById(userId)).thenReturn("testuser");
        when(novelService.getNovel(novelId)).thenReturn(mock(com.yushan.backend.dto.NovelDetailResponseDTO.class));

        // When
        ReviewResponseDTO result = reviewService.getReview(1);

        // Then
        assertNotNull(result);
        assertEquals("Great novel!", result.getContent());
        assertEquals(5, result.getRating());
    }

    @Test
    void getReview_NotFound_ShouldThrow() {
        // Given
        when(reviewMapper.selectByPrimaryKey(1)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> 
            reviewService.getReview(1));
    }
}
