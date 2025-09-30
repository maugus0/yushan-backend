package com.yushan.backend.service;

import com.yushan.backend.dao.CategoryMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.NovelCreateRequestDTO;
import com.yushan.backend.dto.NovelResponseDTO;
import com.yushan.backend.dto.NovelUpdateRequestDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.Category;
import com.yushan.backend.enums.NovelStatus;
import com.yushan.backend.exception.NovelNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class NovelService {

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public NovelResponseDTO createNovel(UUID userId, String authorName, NovelCreateRequestDTO req) {
        if (req.getCategoryId() == null || categoryMapper.selectByPrimaryKey(req.getCategoryId()) == null) {
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

        System.out.println("DEBUG: Before insert - novel.getId() = " + novel.getId());
        novelMapper.insertSelective(novel);
        System.out.println("DEBUG: After insert - novel.getId() = " + novel.getId());
        return toResponse(novel);
    }

    public NovelResponseDTO updateNovel(Integer id, NovelUpdateRequestDTO req) {
        Novel existing = novelMapper.selectByPrimaryKey(id);
        if (existing == null) {
            throw new NovelNotFoundException("novel not found");
        }

        if (req.getTitle() != null && !req.getTitle().trim().isEmpty()) existing.setTitle(req.getTitle());
        if (req.getSynopsis() != null && !req.getSynopsis().trim().isEmpty()) existing.setSynopsis(req.getSynopsis());
        if (req.getCategoryId() != null && req.getCategoryId() > 0) {
            if (categoryMapper.selectByPrimaryKey(req.getCategoryId()) == null) {
                throw new IllegalArgumentException("category not found");
            }
            existing.setCategoryId(req.getCategoryId());
        }
        if (req.getCoverImgUrl() != null && !req.getCoverImgUrl().trim().isEmpty()) existing.setCoverImgUrl(req.getCoverImgUrl());
        if (req.getStatus() != null && !req.getStatus().trim().isEmpty()) {
            NovelStatus s = NovelStatus.valueOf(req.getStatus());
            existing.setStatus(mapStatus(s));
        }
        if (req.getIsCompleted() != null) existing.setIsCompleted(req.getIsCompleted());
        existing.setUpdateTime(new Date());

        novelMapper.updateByPrimaryKeySelective(existing);
        return toResponse(existing);
    }

    public NovelResponseDTO getNovel(Integer id) {
        Novel n = novelMapper.selectByPrimaryKey(id);
        if (n == null) {
            throw new NovelNotFoundException("novel not found");
        }
        if (Boolean.FALSE.equals(n.getIsValid()) || Integer.valueOf(mapStatus(NovelStatus.ARCHIVED)).equals(n.getStatus())) {
            throw new NovelNotFoundException("novel not found");
        }
        return toResponse(n);
    }

    private int mapStatus(NovelStatus status) {
        switch (status) {
            case DRAFT:
                return 0;
            case PUBLISHED:
                return 1;
            case ARCHIVED:
                return 2;
            default:
                return 0;
        }
    }

    private NovelResponseDTO toResponse(Novel n) {
        NovelResponseDTO dto = new NovelResponseDTO();
        dto.setId(n.getId());
        dto.setUuid(n.getUuid());
        dto.setTitle(n.getTitle());
        dto.setAuthorId(n.getAuthorId());
        dto.setAuthorUsername(n.getAuthorName());
        dto.setCategoryId(n.getCategoryId());
        if (n.getCategoryId() != null) {
            Category c = categoryMapper.selectByPrimaryKey(n.getCategoryId());
            if (c != null) {
                dto.setCategoryName(c.getName());
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
            case 1: return NovelStatus.PUBLISHED.name();
            case 2: return NovelStatus.ARCHIVED.name();
            default: return NovelStatus.DRAFT.name();
        }
    }
}
