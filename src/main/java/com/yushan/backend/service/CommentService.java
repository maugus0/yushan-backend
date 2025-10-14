package com.yushan.backend.service;

import com.yushan.backend.dao.ChapterMapper;
import com.yushan.backend.dao.CommentMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Chapter;
import com.yushan.backend.entity.Comment;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private EXPService expService;

    private static final Float COMMENT_EXP = 5f;

    /**
     * Create a new comment
     * Users can only have one comment per chapter
     */
    @Transactional
    public CommentResponseDTO createComment(UUID userId, CommentCreateRequestDTO request) {
        // Check if chapter exists
        Chapter chapter = chapterMapper.selectByPrimaryKey(request.getChapterId());
        if (chapter == null || Boolean.FALSE.equals(chapter.getIsValid())) {
            throw new ResourceNotFoundException("Chapter not found");
        }

        // Check if user already commented on this chapter
        boolean alreadyCommented = commentMapper.existsByUserAndChapter(userId, request.getChapterId());
        if (alreadyCommented) {
            throw new IllegalArgumentException("You have already commented on this chapter");
        }

        // Create new comment
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setChapterId(request.getChapterId());
        comment.setContent(request.getContent().trim());
        comment.setLikeCnt(0);
        comment.setIsSpoiler(request.getIsSpoiler() != null ? request.getIsSpoiler() : false);
        Date now = new Date();
        comment.setCreateTime(now);
        comment.setUpdateTime(now);

        commentMapper.insertSelective(comment);

        expService.addExp(userId, COMMENT_EXP);

        return toResponseDTO(comment, userId);
    }

    /**
     * Update an existing comment
     * Only the author of the comment can update it
     */
    @Transactional
    public CommentResponseDTO updateComment(Integer commentId, UUID userId, CommentUpdateRequestDTO request) {
        Comment existingComment = commentMapper.selectByPrimaryKey(commentId);
        if (existingComment == null) {
            throw new ResourceNotFoundException("Comment not found");
        }

        // Check if user is the author of the comment
        if (!existingComment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own comments");
        }

        // Update fields if provided
        boolean hasChanges = false;

        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            String newContent = request.getContent().trim();
            if (!newContent.equals(existingComment.getContent())) {
                existingComment.setContent(newContent);
                hasChanges = true;
            }
        }

        if (request.getIsSpoiler() != null && !request.getIsSpoiler().equals(existingComment.getIsSpoiler())) {
            existingComment.setIsSpoiler(request.getIsSpoiler());
            hasChanges = true;
        }

        if (hasChanges) {
            existingComment.setUpdateTime(new Date());
            commentMapper.updateByPrimaryKeySelective(existingComment);
        }

        return toResponseDTO(existingComment, userId);
    }

    /**
     * Delete a comment
     * Only the author of the comment or admin can delete it
     */
    @Transactional
    public boolean deleteComment(Integer commentId, UUID userId, boolean isAdmin) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment not found");
        }

        // Check if user is the author or admin
        if (!comment.getUserId().equals(userId) && !isAdmin) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        int result = commentMapper.deleteByPrimaryKey(commentId);
        return result > 0;
    }

    /**
     * Get comment by ID
     */
    public CommentResponseDTO getComment(Integer commentId, UUID currentUserId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment not found");
        }
        return toResponseDTO(comment, currentUserId);
    }

    /**
     * Get comments for a specific chapter with pagination
     */
    public CommentListResponseDTO getCommentsByChapter(Integer chapterId, UUID currentUserId,
                                                       int page, int size, String sort, String order) {
        // Validate chapter exists
        Chapter chapter = chapterMapper.selectByPrimaryKey(chapterId);
        if (chapter == null || Boolean.FALSE.equals(chapter.getIsValid())) {
            throw new ResourceNotFoundException("Chapter not found");
        }

        // Validate and set defaults
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
        if (sort == null || sort.trim().isEmpty()) sort = "createTime";
        if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
            order = "desc";
        }

        CommentSearchRequestDTO request = CommentSearchRequestDTO.builder()
                .chapterId(chapterId)
                .sort(sort)
                .order(order)
                .page(page)
                .size(size)
                .build();

        List<Comment> comments = commentMapper.selectCommentsWithPagination(request);
        long totalCount = commentMapper.countComments(request);

        List<CommentResponseDTO> commentDTOs = comments.stream()
                .map(c -> toResponseDTO(c, currentUserId))
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalCount / size);

        return CommentListResponseDTO.builder()
                .comments(commentDTOs)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    /**
     * Get comments for a specific novel with pagination (across all chapters)
     */
    public CommentListResponseDTO getCommentsByNovel(Integer novelId, UUID currentUserId,
                                                     CommentSearchRequestDTO request) {
        // Validate and set defaults
        if (request.getPage() < 0) {
            request.setPage(0);
        }
        if (request.getSize() <= 0) {
            request.setSize(20);
        }
        if (request.getSize() > 100) {
            request.setSize(100);
        }
        if (request.getSort() == null || request.getSort().trim().isEmpty()) {
            request.setSort("createTime");
        }
        if (request.getOrder() == null || (!request.getOrder().equalsIgnoreCase("asc") && !request.getOrder().equalsIgnoreCase("desc"))) {
            request.setOrder("desc");
        }

        List<Comment> comments = commentMapper.selectCommentsByNovelWithPagination(
                novelId,
                request.getIsSpoiler(),
                request.getSearch(),
                request.getSort(),
                request.getOrder(),
                request.getPage(),
                request.getSize()
        );

        long totalCount = commentMapper.countCommentsByNovel(
                novelId,
                request.getIsSpoiler(),
                request.getSearch()
        );

        List<CommentResponseDTO> commentDTOs = comments.stream()
                .map(c -> toResponseDTO(c, currentUserId))
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalCount / request.getSize());

        return CommentListResponseDTO.builder()
                .comments(commentDTOs)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(request.getPage())
                .pageSize(request.getSize())
                .build();
    }

    /**
     * Get all comments with pagination and filtering
     */
    public CommentListResponseDTO getAllComments(CommentSearchRequestDTO request, UUID currentUserId) {
        // Validate and set defaults
        if (request.getSize() > 100) {
            request.setSize(100);
        }
        if (request.getSort() == null || request.getSort().trim().isEmpty()) {
            request.setSort("createTime");
        }
        if (request.getOrder() == null || (!request.getOrder().equalsIgnoreCase("asc") && !request.getOrder().equalsIgnoreCase("desc"))) {
            request.setOrder("desc");
        }

        List<Comment> comments = commentMapper.selectCommentsWithPagination(request);
        long totalCount = commentMapper.countComments(request);

        List<CommentResponseDTO> commentDTOs = comments.stream()
                .map(c -> toResponseDTO(c, currentUserId))
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalCount / request.getSize());

        return CommentListResponseDTO.builder()
                .comments(commentDTOs)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .currentPage(request.getPage())
                .pageSize(request.getSize())
                .build();
    }

    /**
     * Get user's comments
     */
    public List<CommentResponseDTO> getUserComments(UUID userId) {
        List<Comment> comments = commentMapper.selectByUserId(userId);
        return comments.stream()
                .map(c -> toResponseDTO(c, userId))
                .collect(Collectors.toList());
    }

    /**
     * Toggle like on a comment (increment like count)
     * In a real application, you would need a separate table to track user likes
     */
    @Transactional
    public CommentResponseDTO toggleLike(Integer commentId, UUID currentUserId, boolean isLiking) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment not found");
        }

        // Increment or decrement like count
        int increment = isLiking ? 1 : -1;
        int result = commentMapper.updateLikeCount(commentId, increment);

        if (result > 0) {
            // Fetch updated comment
            comment = commentMapper.selectByPrimaryKey(commentId);
        }

        return toResponseDTO(comment, currentUserId);
    }

    /**
     * Check if user has commented on a chapter
     */
    public boolean hasUserCommentedOnChapter(UUID userId, Integer chapterId) {
        return commentMapper.existsByUserAndChapter(userId, chapterId);
    }

    /**
     * Get comment statistics for a chapter
     */
    public CommentStatisticsDTO getChapterCommentStats(Integer chapterId) {
        // Validate chapter exists
        Chapter chapter = chapterMapper.selectByPrimaryKey(chapterId);
        if (chapter == null || Boolean.FALSE.equals(chapter.getIsValid())) {
            throw new ResourceNotFoundException("Chapter not found");
        }

        List<Comment> comments = commentMapper.selectByChapterId(chapterId);

        CommentStatisticsDTO stats = CommentStatisticsDTO.builder()
                .chapterId(chapterId)
                .chapterTitle(chapter.getTitle())
                .totalComments((long) comments.size())
                .build();

        if (!comments.isEmpty()) {
            long spoilerComments = comments.stream()
                    .filter(Comment::getIsSpoiler)
                    .count();

            stats.setSpoilerComments(spoilerComments);
            stats.setNonSpoilerComments(comments.size() - spoilerComments);

            // Calculate average likes
            double avgLikes = comments.stream()
                    .mapToInt(Comment::getLikeCnt)
                    .average()
                    .orElse(0.0);
            stats.setAvgLikesPerComment((int) Math.round(avgLikes));

            // Find most liked comment
            Comment mostLiked = comments.stream()
                    .max((c1, c2) -> Integer.compare(c1.getLikeCnt(), c2.getLikeCnt()))
                    .orElse(null);

            if (mostLiked != null) {
                stats.setMostLikedCommentId(mostLiked.getId());
            }
        } else {
            stats.setSpoilerComments(0L);
            stats.setNonSpoilerComments(0L);
            stats.setAvgLikesPerComment(0);
        }

        return stats;
    }

    /**
     * Batch delete comments (Admin only)
     */
    @Transactional
    public int batchDeleteComments(CommentBatchDeleteRequestDTO request, boolean isAdmin) {
        if (!isAdmin) {
            throw new IllegalArgumentException("Only administrators can perform batch delete");
        }

        if (request.getCommentIds() == null || request.getCommentIds().isEmpty()) {
            throw new IllegalArgumentException("Comment IDs list cannot be empty");
        }

        int deletedCount = 0;
        for (Integer commentId : request.getCommentIds()) {
            Comment comment = commentMapper.selectByPrimaryKey(commentId);
            if (comment != null) {
                int result = commentMapper.deleteByPrimaryKey(commentId);
                if (result > 0) {
                    deletedCount++;
                }
            }
        }

        return deletedCount;
    }

    /**
     * Convert Comment entity to CommentResponseDTO
     */
    private CommentResponseDTO toResponseDTO(Comment comment, UUID currentUserId) {
        CommentResponseDTO dto = CommentResponseDTO.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .chapterId(comment.getChapterId())
                .content(comment.getContent())
                .likeCnt(comment.getLikeCnt())
                .isSpoiler(comment.getIsSpoiler())
                .createTime(comment.getCreateTime())
                .updateTime(comment.getUpdateTime())
                .isOwnComment(currentUserId != null && currentUserId.equals(comment.getUserId()))
                .build();

        // Get username from UserService
        try {
            String username = userService.getUsernameById(comment.getUserId());
            dto.setUsername(username);
        } catch (Exception e) {
            dto.setUsername(null);
        }

        // Get chapter title from ChapterMapper
        Chapter chapter = chapterMapper.selectByPrimaryKey(comment.getChapterId());
        if (chapter != null) {
            dto.setChapterTitle(chapter.getTitle());
        } else {
            dto.setChapterTitle("Chapter not found");
        }

        return dto;
    }
    /**
     * Get moderation statistics for admin dashboard
     */
    public CommentModerationStatsDTO getModerationStatistics() {
        CommentModerationStatsDTO stats = new CommentModerationStatsDTO();

        // Get all comments for analysis
        CommentSearchRequestDTO allCommentsRequest = CommentSearchRequestDTO.builder()
                .page(0)
                .size(Integer.MAX_VALUE)
                .build();

        long totalComments = commentMapper.countComments(allCommentsRequest);
        stats.setTotalComments(totalComments);

        // Count spoiler vs non-spoiler
        CommentSearchRequestDTO spoilerRequest = CommentSearchRequestDTO.builder()
                .isSpoiler(true)
                .page(0)
                .size(1)
                .build();
        long spoilerCount = commentMapper.countComments(spoilerRequest);
        stats.setSpoilerComments(spoilerCount);
        stats.setNonSpoilerComments(totalComments - spoilerCount);

        // Time-based statistics
        stats.setCommentsToday(commentMapper.countCommentsInLastDays(1));
        stats.setCommentsThisWeek(commentMapper.countCommentsInLastDays(7));
        stats.setCommentsThisMonth(commentMapper.countCommentsInLastDays(30));

        // Get most active user
        Comment mostActiveUserComment = commentMapper.selectMostActiveUser();
        if (mostActiveUserComment != null) {
            String username = userService.getUsernameById(mostActiveUserComment.getUserId());
            stats.setMostActiveUsername(username != null ? username : "Unknown");
            stats.setMostActiveUserCommentCount(
                    commentMapper.countCommentsByUser(mostActiveUserComment.getUserId())
            );
        }

        // Get most commented chapter
        Comment mostCommentedChapterComment = commentMapper.selectMostCommentedChapter();
        if (mostCommentedChapterComment != null) {
            stats.setMostCommentedChapterId(mostCommentedChapterComment.getChapterId());
            Chapter chapter = chapterMapper.selectByPrimaryKey(mostCommentedChapterComment.getChapterId());
            if (chapter != null) {
                stats.setMostCommentedChapterTitle(chapter.getTitle());
            }
            stats.setMostCommentedChapterCount(
                    commentMapper.countByChapterId(mostCommentedChapterComment.getChapterId())
            );
        }

        return stats;
    }

    /**
     * Delete all comments by a specific user (admin moderation)
     */
    @Transactional
    public int deleteAllUserComments(UUID userId) {
        List<Comment> userComments = commentMapper.selectByUserId(userId);
        int deletedCount = 0;

        for (Comment comment : userComments) {
            int result = commentMapper.deleteByPrimaryKey(comment.getId());
            if (result > 0) {
                deletedCount++;
            }
        }

        return deletedCount;
    }

    /**
     * Delete all comments for a specific chapter (admin cleanup)
     */
    @Transactional
    public int deleteAllChapterComments(Integer chapterId) {
        List<Comment> chapterComments = commentMapper.selectByChapterId(chapterId);
        int deletedCount = 0;

        for (Comment comment : chapterComments) {
            int result = commentMapper.deleteByPrimaryKey(comment.getId());
            if (result > 0) {
                deletedCount++;
            }
        }

        return deletedCount;
    }

    /**
     * Bulk update spoiler status for multiple comments (admin moderation)
     */
    @Transactional
    public int bulkUpdateSpoilerStatus(CommentBulkSpoilerUpdateRequestDTO request) {
        if (request.getCommentIds() == null || request.getCommentIds().isEmpty()) {
            throw new IllegalArgumentException("Comment IDs list cannot be empty");
        }

        int updatedCount = 0;

        for (Integer commentId : request.getCommentIds()) {
            Comment comment = commentMapper.selectByPrimaryKey(commentId);
            if (comment != null) {
                comment.setIsSpoiler(request.getIsSpoiler());
                comment.setUpdateTime(new Date());
                int result = commentMapper.updateByPrimaryKeySelective(comment);
                if (result > 0) {
                    updatedCount++;
                }
            }
        }

        return updatedCount;
    }
}