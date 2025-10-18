package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Create a new review (authenticated users only)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewCreateRequestDTO request,
                                                       Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        ReviewResponseDTO dto = reviewService.createReview(userId, request);
        return ApiResponse.success("Review created successfully", dto);
    }

    /**
     * Update an existing review (only the author)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<ReviewResponseDTO> updateReview(@PathVariable Integer id,
                                                      @Valid @RequestBody ReviewUpdateRequestDTO request,
                                                      Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        ReviewResponseDTO dto = reviewService.updateReview(id, userId, request);
        return ApiResponse.success("Review updated successfully", dto);
    }

    /**
     * Delete a review (only the author or admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<String> deleteReview(@PathVariable Integer id,
                                            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        boolean deleted = reviewService.deleteReview(id, userId, isAdmin);
        if (deleted) {
            return ApiResponse.success("Review deleted successfully");
        }
        return ApiResponse.error(400, "Failed to delete review");
    }

    /**
     * Get review by ID (public)
     */
    @GetMapping("/{id}")
    public ApiResponse<ReviewResponseDTO> getReview(@PathVariable Integer id) {
        ReviewResponseDTO dto = reviewService.getReview(id);
        return ApiResponse.success("Review retrieved successfully", dto);
    }

    /**
     * Get reviews for a specific novel (public)
     */
    @GetMapping("/novel/{novelId}")
    public ApiResponse<PageResponseDTO<ReviewResponseDTO>> getReviewsByNovel(
            @PathVariable Integer novelId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order) {
        
        PageResponseDTO<ReviewResponseDTO> response = reviewService.getReviewsByNovel(novelId, page, size, sort, order);
        return ApiResponse.success("Reviews retrieved successfully", response);
    }

    /**
     * Get all reviews with filtering and pagination (public)
     */
    @GetMapping
    public ApiResponse<PageResponseDTO<ReviewResponseDTO>> getAllReviews(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "novelId", required = false) Integer novelId,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "isSpoiler", required = false) Boolean isSpoiler,
            @RequestParam(value = "search", required = false) String search) {
        
        ReviewSearchRequestDTO request = new ReviewSearchRequestDTO(page, size, sort, order, 
                                                                   novelId, rating, isSpoiler, search);
        PageResponseDTO<ReviewResponseDTO> response = reviewService.getAllReviews(request);
        return ApiResponse.success("Reviews retrieved successfully", response);
    }

    /**
     * Like a review (authenticated users only)
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<ReviewResponseDTO> likeReview(@PathVariable Integer id,
                                                    Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        ReviewResponseDTO dto = reviewService.toggleLike(id, userId, true);
        return ApiResponse.success("Review liked successfully", dto);
    }

    /**
     * Unlike a review (authenticated users only)
     */
    @PostMapping("/{id}/unlike")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<ReviewResponseDTO> unlikeReview(@PathVariable Integer id,
                                                      Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        ReviewResponseDTO dto = reviewService.toggleLike(id, userId, false);
        return ApiResponse.success("Review unliked successfully", dto);
    }

    /**
     * Get current user's reviews (authenticated users only)
     */
    @GetMapping("/my-reviews")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<List<ReviewResponseDTO>> getMyReviews(Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        List<ReviewResponseDTO> reviews = reviewService.getUserReviews(userId);
        return ApiResponse.success("Your reviews retrieved successfully", reviews);
    }

    /**
     * Get user's review for a specific novel (authenticated users only)
     */
    @GetMapping("/my-reviews/novel/{novelId}")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<ReviewResponseDTO> getMyReviewForNovel(@PathVariable Integer novelId,
                                                             Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        ReviewResponseDTO review = reviewService.getUserReviewForNovel(userId, novelId);
        return ApiResponse.success("Your review for this novel retrieved successfully", review);
    }

    /**
     * Check if user has reviewed a novel (authenticated users only)
     */
    @GetMapping("/check/{novelId}")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<Boolean> hasUserReviewedNovel(@PathVariable Integer novelId,
                                                     Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        boolean hasReviewed = reviewService.hasUserReviewedNovel(userId, novelId);
        return ApiResponse.success("Review status checked", hasReviewed);
    }

    /**
     * Get all reviews (admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponseDTO<ReviewResponseDTO>> getAllReviewsAdmin(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "novelId", required = false) Integer novelId,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "isSpoiler", required = false) Boolean isSpoiler,
            @RequestParam(value = "search", required = false) String search) {
        
        ReviewSearchRequestDTO request = new ReviewSearchRequestDTO(page, size, sort, order, 
                                                                   novelId, rating, isSpoiler, search);
        PageResponseDTO<ReviewResponseDTO> response = reviewService.getAllReviews(request);
        return ApiResponse.success("All reviews retrieved successfully", response);
    }

    /**
     * Delete any review (admin only)
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteReviewAdmin(@PathVariable Integer id) {
        boolean deleted = reviewService.deleteReview(id, null, true);
        if (deleted) {
            return ApiResponse.success("Review deleted successfully by admin");
        }
        return ApiResponse.error(400, "Failed to delete review");
    }

    /**
     * Get novel rating statistics (admin only)
     */
    @GetMapping("/novel/{novelId}/rating-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<NovelRatingStatsDTO> getNovelRatingStats(@PathVariable Integer novelId) {
        NovelRatingStatsDTO stats = reviewService.getNovelRatingStats(novelId);
        return ApiResponse.success("Novel rating statistics retrieved", stats);
    }


    /**
     * Helper method to extract user ID from authentication
     */
    private UUID getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication required");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails cud = (CustomUserDetails) principal;
            if (cud.getUserId() != null) {
                return UUID.fromString(cud.getUserId());
            }
        }
        
        throw new IllegalArgumentException("User ID not found in authentication");
    }
}
