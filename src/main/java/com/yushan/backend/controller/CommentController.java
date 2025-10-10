package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * Create a new comment (authenticated users only)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponseDTO> createComment(@Valid @RequestBody CommentCreateRequestDTO request,
                                                         Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        CommentResponseDTO dto = commentService.createComment(userId, request);
        return ApiResponse.success("Comment created successfully", dto);
    }

    /**
     * Update an existing comment (only the author)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<CommentResponseDTO> updateComment(@PathVariable Integer id,
                                                         @Valid @RequestBody CommentUpdateRequestDTO request,
                                                         Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        CommentResponseDTO dto = commentService.updateComment(id, userId, request);
        return ApiResponse.success("Comment updated successfully", dto);
    }

    /**
     * Delete a comment (only the author or admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<String> deleteComment(@PathVariable Integer id,
                                             Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean deleted = commentService.deleteComment(id, userId, isAdmin);
        if (deleted) {
            return ApiResponse.success("Comment deleted successfully");
        }
        return ApiResponse.error(400, "Failed to delete comment");
    }

    /**
     * Get comment by ID (public)
     */
    @GetMapping("/{id}")
    public ApiResponse<CommentResponseDTO> getComment(@PathVariable Integer id,
                                                      Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        CommentResponseDTO dto = commentService.getComment(id, userId);
        return ApiResponse.success("Comment retrieved successfully", dto);
    }

    /**
     * Get comments for a specific chapter (public)
     */
    @GetMapping("/chapter/{chapterId}")
    public ApiResponse<CommentListResponseDTO> getCommentsByChapter(
            @PathVariable Integer chapterId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            Authentication authentication) {

        UUID userId = authentication != null ? getUserIdFromAuthenticationOrNull(authentication) : null;
        CommentListResponseDTO response = commentService.getCommentsByChapter(chapterId, userId, page, size, sort, order);
        return ApiResponse.success("Comments retrieved successfully", response);
    }

    /**
     * Get comments for a specific novel (public, across all chapters)
     */
    @GetMapping("/novel/{novelId}")
    public ApiResponse<CommentListResponseDTO> getCommentsByNovel(
            @PathVariable Integer novelId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "isSpoiler", required = false) Boolean isSpoiler,
            @RequestParam(value = "search", required = false) String search,
            Authentication authentication) {

        UUID userId = authentication != null ? getUserIdFromAuthenticationOrNull(authentication) : null;

        CommentSearchRequestDTO request = CommentSearchRequestDTO.builder()
                .novelId(novelId)
                .isSpoiler(isSpoiler)
                .search(search)
                .sort(sort)
                .order(order)
                .page(page)
                .size(size)
                .build();

        CommentListResponseDTO response = commentService.getCommentsByNovel(novelId, userId, request);
        return ApiResponse.success("Comments retrieved successfully", response);
    }

    /**
     * Like a comment (authenticated users only)
     * For now, this simply increments the like count
     * In a real application, you would track user likes in a separate table
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<CommentResponseDTO> likeComment(@PathVariable Integer id,
                                                       Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        CommentResponseDTO dto = commentService.toggleLike(id, userId, true);
        return ApiResponse.success("Comment liked successfully", dto);
    }

    /**
     * Unlike a comment (authenticated users only)
     */
    @PostMapping("/{id}/unlike")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<CommentResponseDTO> unlikeComment(@PathVariable Integer id,
                                                         Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        CommentResponseDTO dto = commentService.toggleLike(id, userId, false);
        return ApiResponse.success("Comment unliked successfully", dto);
    }

    /**
     * Get current user's comments (authenticated users only)
     */
    @GetMapping("/my-comments")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<List<CommentResponseDTO>> getMyComments(Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        List<CommentResponseDTO> comments = commentService.getUserComments(userId);
        return ApiResponse.success("Your comments retrieved successfully", comments);
    }

    /**
     * Check if user has commented on a chapter (authenticated users only)
     */
    @GetMapping("/check/chapter/{chapterId}")
    @PreAuthorize("hasAnyRole('USER','AUTHOR','ADMIN')")
    public ApiResponse<Boolean> hasUserCommentedOnChapter(@PathVariable Integer chapterId,
                                                          Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        boolean hasCommented = commentService.hasUserCommentedOnChapter(userId, chapterId);
        return ApiResponse.success("Comment status checked", hasCommented);
    }

    /**
     * Get comment statistics for a chapter (public)
     */
    @GetMapping("/chapter/{chapterId}/statistics")
    public ApiResponse<CommentStatisticsDTO> getChapterCommentStats(@PathVariable Integer chapterId) {
        CommentStatisticsDTO stats = commentService.getChapterCommentStats(chapterId);
        return ApiResponse.success("Comment statistics retrieved", stats);
    }

    // ========================================
    // ADMIN ENDPOINTS
    // ========================================

    /**
     * Get all comments with filtering and pagination (admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CommentListResponseDTO> getAllCommentsAdmin(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "chapterId", required = false) Integer chapterId,
            @RequestParam(value = "novelId", required = false) Integer novelId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "isSpoiler", required = false) Boolean isSpoiler,
            @RequestParam(value = "search", required = false) String search,
            Authentication authentication) {

        UUID currentUserId = getUserIdFromAuthentication(authentication);

        CommentSearchRequestDTO request = CommentSearchRequestDTO.builder()
                .chapterId(chapterId)
                .novelId(novelId)
                .userId(userId != null ? UUID.fromString(userId) : null)
                .isSpoiler(isSpoiler)
                .search(search)
                .sort(sort)
                .order(order)
                .page(page)
                .size(size)
                .build();

        CommentListResponseDTO response = commentService.getAllComments(request, currentUserId);
        return ApiResponse.success("All comments retrieved successfully", response);
    }

    /**
     * Delete any comment (admin only)
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteCommentAdmin(@PathVariable Integer id) {
        boolean deleted = commentService.deleteComment(id, null, true);
        if (deleted) {
            return ApiResponse.success("Comment deleted successfully by admin");
        }
        return ApiResponse.error(400, "Failed to delete comment");
    }

    /**
     * Batch delete comments (admin only)
     */
    @PostMapping("/admin/batch-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> batchDeleteComments(@Valid @RequestBody CommentBatchDeleteRequestDTO request) {
        int deletedCount = commentService.batchDeleteComments(request, true);
        return ApiResponse.success("Successfully deleted " + deletedCount + " comment(s)");
    }

    /**
     * Get all comments (admin search/filter)
     */
    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CommentListResponseDTO> searchCommentsAdmin(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "chapterId", required = false) Integer chapterId,
            @RequestParam(value = "novelId", required = false) Integer novelId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "isSpoiler", required = false) Boolean isSpoiler,
            @RequestParam(value = "search", required = false) String search,
            Authentication authentication) {

        UUID currentUserId = getUserIdFromAuthentication(authentication);

        CommentSearchRequestDTO request = CommentSearchRequestDTO.builder()
                .chapterId(chapterId)
                .novelId(novelId)
                .userId(userId != null ? UUID.fromString(userId) : null)
                .isSpoiler(isSpoiler)
                .search(search)
                .sort(sort)
                .order(order)
                .page(page)
                .size(size)
                .build();

        CommentListResponseDTO response = commentService.getAllComments(request, currentUserId);
        return ApiResponse.success("Comments search completed", response);
    }

    // ========================================
    // HELPER METHODS
    // ========================================

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

    /**
     * Helper method to extract user ID from authentication (returns null if not authenticated)
     */
    private UUID getUserIdFromAuthenticationOrNull(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails cud = (CustomUserDetails) principal;
            if (cud.getUserId() != null) {
                return UUID.fromString(cud.getUserId());
            }
        }

        return null;
    }
}
