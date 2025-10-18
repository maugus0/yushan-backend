package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReviewController reviewController;

    private UUID userId;
    private ReviewCreateRequestDTO createRequest;
    private ReviewResponseDTO reviewResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        createRequest = new ReviewCreateRequestDTO();
        createRequest.setNovelId(1);
        createRequest.setContent("Great novel!");
        createRequest.setRating(5);
        createRequest.setIsSpoiler(false);

        reviewResponse = new ReviewResponseDTO();
        reviewResponse.setId(1);
        reviewResponse.setContent("Great novel!");
        reviewResponse.setRating(5);
        reviewResponse.setUsername("testuser");
        reviewResponse.setNovelTitle("Test Novel");

        // Mock authentication - only when needed
    }

    @Test
    void createReview_Success() {
        // Given
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(userId.toString());
        when(reviewService.createReview(userId, createRequest)).thenReturn(reviewResponse);

        // When
        ApiResponse<ReviewResponseDTO> response = 
            reviewController.createReview(createRequest, authentication);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("Review created successfully", response.getMessage());
        assertEquals(reviewResponse, response.getData());
        
        verify(reviewService).createReview(userId, createRequest);
    }

    @Test
    void getReview_Success() {
        // Given
        when(reviewService.getReview(1)).thenReturn(reviewResponse);

        // When
        ApiResponse<ReviewResponseDTO> response = 
            reviewController.getReview(1);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("Review retrieved successfully", response.getMessage());
        assertEquals(reviewResponse, response.getData());
        
        verify(reviewService).getReview(1);
    }

    @Test
    void getMyReviews_Success() {
        // Given
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(userId.toString());
        when(reviewService.getUserReviews(userId))
            .thenReturn(Arrays.asList(reviewResponse));

        // When
        ApiResponse<List<ReviewResponseDTO>> response = 
            reviewController.getMyReviews(authentication);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("Your reviews retrieved successfully", response.getMessage());
        assertEquals(Arrays.asList(reviewResponse), response.getData());
        
        verify(reviewService).getUserReviews(userId);
    }

    // ========================================
    // LIKE FUNCTIONALITY TESTS
    // ========================================

    @Test
    void likeReview_Success() {
        // Given
        Integer reviewId = 1;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(userId.toString());
        
        ReviewResponseDTO likedResponse = new ReviewResponseDTO();
        likedResponse.setId(reviewId);
        likedResponse.setContent("Great novel!");
        likedResponse.setRating(5);
        likedResponse.setUsername("testuser");
        likedResponse.setNovelTitle("Test Novel");
        likedResponse.setLikeCnt(1);
        
        when(reviewService.toggleLike(reviewId, userId, true)).thenReturn(likedResponse);

        // When
        ApiResponse<ReviewResponseDTO> response = 
            reviewController.likeReview(reviewId, authentication);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("Review liked successfully", response.getMessage());
        assertEquals(likedResponse, response.getData());
        assertEquals(1, response.getData().getLikeCnt());
        
        verify(reviewService).toggleLike(reviewId, userId, true);
    }

    @Test
    void unlikeReview_Success() {
        // Given
        Integer reviewId = 1;
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(userId.toString());
        
        ReviewResponseDTO unlikedResponse = new ReviewResponseDTO();
        unlikedResponse.setId(reviewId);
        unlikedResponse.setContent("Great novel!");
        unlikedResponse.setRating(5);
        unlikedResponse.setUsername("testuser");
        unlikedResponse.setNovelTitle("Test Novel");
        unlikedResponse.setLikeCnt(0);
        
        when(reviewService.toggleLike(reviewId, userId, false)).thenReturn(unlikedResponse);

        // When
        ApiResponse<ReviewResponseDTO> response = 
            reviewController.unlikeReview(reviewId, authentication);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("Review unliked successfully", response.getMessage());
        assertEquals(unlikedResponse, response.getData());
        assertEquals(0, response.getData().getLikeCnt());
        
        verify(reviewService).toggleLike(reviewId, userId, false);
    }
}
