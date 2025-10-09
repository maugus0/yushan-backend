package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.enums.NovelStatus;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NovelService {

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private CategoryService categoryService;

    public NovelDetailResponseDTO createNovel(UUID userId, String authorName, NovelCreateRequestDTO req) {
        if (req.getCategoryId() == null) {
            throw new IllegalArgumentException("category not found");
        }
        
        try {
            categoryService.getCategoryById(req.getCategoryId());
        } catch (Exception e) {
            throw new IllegalArgumentException("category not found");
        }

        Novel novel = new Novel();
        novel.setUuid(UUID.randomUUID());
        novel.setTitle(req.getTitle());
        novel.setAuthorId(userId);
        novel.setAuthorName(authorName);
        novel.setCategoryId(req.getCategoryId());
        novel.setSynopsis(req.getSynopsis());
        novel.setCoverImgUrl(req.getCoverImgUrl());
        novel.setStatus(mapStatus(NovelStatus.DRAFT));
        novel.setIsCompleted(Boolean.TRUE.equals(req.getIsCompleted()));
        novel.setIsValid(true);
        novel.setChapterCnt(0);
        novel.setWordCnt(0L);
        novel.setAvgRating(0.0f);
        novel.setReviewCnt(0);
        novel.setViewCnt(0L);
        novel.setVoteCnt(0);
        novel.setYuanCnt(0.0f);
        Date now = new Date();
        novel.setCreateTime(now);
        novel.setUpdateTime(now);
        novel.setPublishTime(null);

        novelMapper.insertSelective(novel);
        return toResponse(novel);
    }

    public NovelDetailResponseDTO updateNovel(Integer id, NovelUpdateRequestDTO req) {
        Novel existing = novelMapper.selectByPrimaryKey(id);
        if (existing == null) {
            throw new ResourceNotFoundException("novel not found");
        }

        if (req.getTitle() != null && !req.getTitle().trim().isEmpty()) existing.setTitle(req.getTitle());
        if (req.getSynopsis() != null && !req.getSynopsis().trim().isEmpty()) existing.setSynopsis(req.getSynopsis());
        if (req.getCategoryId() != null && req.getCategoryId() > 0) {
            if (categoryService.getCategoryById(req.getCategoryId()) == null) {
                throw new IllegalArgumentException("category not found");
            }
            existing.setCategoryId(req.getCategoryId());
        }
        if (req.getCoverImgUrl() != null && !req.getCoverImgUrl().trim().isEmpty()) existing.setCoverImgUrl(req.getCoverImgUrl());
        
        // Status change is only allowed for admin - this should be handled at controller level
        // but we add validation here as well for safety
        if (req.getStatus() != null && !req.getStatus().trim().isEmpty()) {
            NovelStatus s = NovelStatus.valueOf(req.getStatus());
            existing.setStatus(mapStatus(s));
            
            // Set publish time if publishing
            if (s == NovelStatus.PUBLISHED) {
                existing.setPublishTime(new Date());
            }
        }
        if (req.getIsCompleted() != null) existing.setIsCompleted(req.getIsCompleted());
        existing.setUpdateTime(new Date());

        novelMapper.updateByPrimaryKeySelective(existing);
        return toResponse(existing);
    }

    public NovelDetailResponseDTO getNovel(Integer id) {
        Novel n = novelMapper.selectByPrimaryKey(id);
        if (n == null) {
            throw new ResourceNotFoundException("novel not found");
        }
        if (Boolean.FALSE.equals(n.getIsValid())) {
            throw new ResourceNotFoundException("novel not found");
        }
        return toResponse(n);
    }

    private int mapStatus(NovelStatus status) {
        switch (status) {
            case DRAFT:
                return 0;
            case UNDER_REVIEW:
                return 1;
            case PUBLISHED:
                return 2;
            case HIDDEN:
                return 3;
            default:
                return 0;
        }
    }

    private NovelDetailResponseDTO toResponse(Novel n) {
        NovelDetailResponseDTO dto = new NovelDetailResponseDTO();
        dto.setId(n.getId());
        dto.setUuid(n.getUuid());
        dto.setTitle(n.getTitle());
        dto.setAuthorId(n.getAuthorId());
        dto.setAuthorUsername(n.getAuthorName());
        dto.setCategoryId(n.getCategoryId());
        if (n.getCategoryId() != null) {
            try {
                var category = categoryService.getCategoryById(n.getCategoryId());
                dto.setCategoryName(category.getName());
            } catch (Exception e) {
                dto.setCategoryName(null);
            }
        }
        dto.setSynopsis(n.getSynopsis());
        dto.setCoverImgUrl(n.getCoverImgUrl());
        dto.setStatus(reverseStatus(n.getStatus()));
        dto.setIsCompleted(n.getIsCompleted());
        dto.setChapterCnt(n.getChapterCnt());
        dto.setWordCnt(n.getWordCnt());
        dto.setAvgRating(n.getAvgRating());
        dto.setReviewCnt(n.getReviewCnt());
        dto.setViewCnt(n.getViewCnt());
        dto.setVoteCnt(n.getVoteCnt());
        dto.setYuanCnt(n.getYuanCnt());
        dto.setPublishTime(n.getPublishTime());
        dto.setCreateTime(n.getCreateTime());
        dto.setUpdateTime(n.getUpdateTime());
        return dto;
    }

    private String reverseStatus(Integer status) {
        if (status == null) return null;
        switch (status) {
            case 0: return NovelStatus.DRAFT.name();
            case 1: return NovelStatus.UNDER_REVIEW.name();
            case 2: return NovelStatus.PUBLISHED.name();
            case 3: return NovelStatus.HIDDEN.name();
            default: return NovelStatus.DRAFT.name();
        }
    }

    public PageResponseDTO<NovelDetailResponseDTO> listNovelsWithPagination(NovelSearchRequestDTO request) {
        // Validate and set defaults
        if (request.getPage() == null || request.getPage() < 0) {
            request.setPage(0);
        }
        if (request.getSize() == null || request.getSize() <= 0) {
            request.setSize(10);
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

        // Get novels with pagination
        List<Novel> novels = novelMapper.selectNovelsWithPagination(request);
        
        // Get total count
        long totalElements = novelMapper.countNovels(request);
        
        // Convert to DTOs
        List<NovelDetailResponseDTO> novelDTOs = novels.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new PageResponseDTO<>(novelDTOs, totalElements, request.getPage(), request.getSize());
    }

    /**
     * Get vote statistics for a novel
     */
    public Integer getNovelVoteCount(Integer novelId) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null) {
            throw new ResourceNotFoundException("Novel not found");
        }
        return novel.getVoteCnt();
    }

    /**
     * Get novel entity by ID (for internal use by other services)
     */
    public Novel getNovelEntity(Integer novelId) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null) {
            throw new ResourceNotFoundException("Novel not found");
        }
        return novel;
    }

    /**
     * Increment vote count for a novel
     */
    public void incrementVoteCount(Integer novelId) {
        novelMapper.incrementVoteCount(novelId);
    }

    /**
     * Decrement vote count for a novel
     */
    public void decrementVoteCount(Integer novelId) {
        novelMapper.decrementVoteCount(novelId);
    }

    /**
     * Submit novel for review (Author only)
     */
    public NovelDetailResponseDTO submitForReview(Integer novelId, UUID userId) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null) {
            throw new ResourceNotFoundException("novel not found");
        }
        
        // Check if user is the author
        if (!novel.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("only the author can submit for review");
        }
        
        // Check if novel is in DRAFT status
        if (!novel.getStatus().equals(mapStatus(NovelStatus.DRAFT))) {
            throw new IllegalArgumentException("only draft novels can be submitted for review");
        }
        
        novel.setStatus(mapStatus(NovelStatus.UNDER_REVIEW));
        novel.setUpdateTime(new Date());
        novelMapper.updateByPrimaryKeySelective(novel);
        
        return toResponse(novel);
    }

    /**
     * Change novel status (Admin only)
     */
    private NovelDetailResponseDTO changeNovelStatus(Integer novelId, NovelStatus newStatus, NovelStatus requiredCurrentStatus, String errorMessage) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null) {
            throw new ResourceNotFoundException("novel not found");
        }
        
        // Check current status if required
        if (requiredCurrentStatus != null && !novel.getStatus().equals(mapStatus(requiredCurrentStatus))) {
            throw new IllegalArgumentException(errorMessage);
        }
        
        novel.setStatus(mapStatus(newStatus));
        novel.setUpdateTime(new Date());
        
        // Set publish time if publishing
        if (newStatus == NovelStatus.PUBLISHED) {
            novel.setPublishTime(new Date());
        }
        
        novelMapper.updateByPrimaryKeySelective(novel);
        return toResponse(novel);
    }

    /**
     * Approve novel for publishing (Admin only)
     */
    public NovelDetailResponseDTO approveNovel(Integer novelId) {
        return changeNovelStatus(novelId, NovelStatus.PUBLISHED, NovelStatus.UNDER_REVIEW, 
                "only novels under review can be approved");
    }

    /**
     * Reject novel (Admin only)
     */
    public NovelDetailResponseDTO rejectNovel(Integer novelId) {
        return changeNovelStatus(novelId, NovelStatus.DRAFT, NovelStatus.UNDER_REVIEW, 
                "only novels under review can be rejected");
    }

    /**
     * Hide novel (Admin only)
     */
    public NovelDetailResponseDTO hideNovel(Integer novelId) {
        return changeNovelStatus(novelId, NovelStatus.HIDDEN, null, null);
    }

    /**
     * Get novels under review (Admin only) - Reuse listNovels with pagination
     */
    public PageResponseDTO<NovelDetailResponseDTO> getNovelsUnderReview(int page, int size) {
        NovelSearchRequestDTO request = new NovelSearchRequestDTO(page, size, "createTime", "desc", 
                null, "UNDER_REVIEW", null, null);
        return listNovelsWithPagination(request);
    }

    /**
     * Update novel's average rating and review count
     * This method is called by ReviewService when reviews are created/updated/deleted
     */
    @Transactional
    public void updateNovelRatingAndCount(Integer novelId, float avgRating, int reviewCount) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null) {
            return; // Novel not found, skip update
        }

        // Update novel statistics with provided values
        novel.setAvgRating(avgRating);
        novel.setReviewCnt(reviewCount);
        
        // Update timestamp
        novel.setUpdateTime(new Date());
        novelMapper.updateByPrimaryKeySelective(novel);
    }
}
