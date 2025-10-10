package com.yushan.backend.service;

import com.yushan.backend.dao.CategoryMapper;
import com.yushan.backend.dao.ChapterMapper;
import com.yushan.backend.dao.HistoryMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.HistoryResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.entity.Category;
import com.yushan.backend.entity.Chapter;
import com.yushan.backend.entity.History;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private LibraryService libraryService;

    /**
     * Add or update a viewing history record
     */
    @Transactional
    public void addOrUpdateHistory(UUID userId, Integer novelId, Integer chapterId) {
        // check if exists
        if (novelMapper.selectByPrimaryKey(novelId) == null) {
            throw new ResourceNotFoundException("Novel not found with id: " + novelId);
        }
        Chapter chapter = chapterMapper.selectByPrimaryKey(chapterId);
        if (chapter == null) {
            throw new ResourceNotFoundException("Chapter not found with id: " + chapterId);
        }
        if (chapter.getNovelId() != novelId) {
            throw new ValidationException("Chapter don't belong with novel id: " + novelId);
        }

        History existingHistory = historyMapper.selectByUserAndNovel(userId, novelId);

        if (existingHistory != null) {
            // if the record exists, update chapter & time
            existingHistory.setChapterId(chapterId);
            existingHistory.setUpdateTime(new Date());
            historyMapper.updateByPrimaryKeySelective(existingHistory);
        } else {
            // if the record don't exist, create
            History newHistory = new History();
            newHistory.setUuid(UUID.randomUUID());
            newHistory.setUserId(userId);
            newHistory.setNovelId(novelId);
            newHistory.setChapterId(chapterId);
            Date now = new Date();
            newHistory.setCreateTime(now);
            newHistory.setUpdateTime(now);
            historyMapper.insertSelective(newHistory);
        }
    }

    /**
     * Get the user's viewing history with pagination
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<HistoryResponseDTO> getUserHistory(UUID userId, int page, int size) {
        int offset = page * size;
        long totalElements = historyMapper.countByUserId(userId);
        List<History> histories = historyMapper.selectByUserIdWithPagination(userId, offset, size);

        if (histories.isEmpty()) {
            return new PageResponseDTO<>(Collections.emptyList(), totalElements, page, size);
        }

        List<Integer> novelIds = histories.stream().map(History::getNovelId).distinct().collect(Collectors.toList());
        List<Integer> chapterIds = histories.stream().map(History::getChapterId).distinct().collect(Collectors.toList());

        Map<Integer, Novel> novelMap = novelMapper.selectByIds(novelIds).stream().collect(Collectors.toMap(Novel::getId, n -> n));
        Map<Integer, Chapter> chapterMap = chapterMapper.selectByIds(chapterIds).stream().collect(Collectors.toMap(Chapter::getId, c -> c));

        List<Integer> categoryIds = novelMap.values().stream().map(Novel::getCategoryId).distinct().collect(Collectors.toList());
        Map<Integer, Category> categoryMap = categoryMapper.selectByIds(categoryIds).stream().collect(Collectors.toMap(Category::getId, c -> c));

        Map<Integer, Boolean> libraryStatusMap = libraryService.checkNovelsInLibrary(userId, novelIds);

        List<HistoryResponseDTO> dtos = histories.stream()
                .map(history -> convertToRichDTO(history, novelMap, chapterMap, categoryMap, libraryStatusMap))
                .collect(Collectors.toList());

        return new PageResponseDTO<>(dtos, totalElements, page, size);
    }

    /**
     * Delete a single history record by its ID
     */
    public void deleteHistory(UUID userId, Integer historyId) {
        History history = historyMapper.selectByPrimaryKey(historyId);
        if (history == null || !history.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("History record not found or you don't have permission to delete it.");
        }
        historyMapper.deleteByPrimaryKey(historyId);
    }

    /**
     * Clear all the user's history
     */
    public void clearHistory(UUID userId) {
        historyMapper.deleteByUserId(userId);
    }

    private HistoryResponseDTO convertToRichDTO(History history, Map<Integer, Novel> novelMap, Map<Integer, Chapter> chapterMap, Map<Integer, Category> categoryMap, Map<Integer, Boolean> libraryStatusMap) {
        HistoryResponseDTO dto = new HistoryResponseDTO();
        dto.setHistoryId(history.getId());
        dto.setChapterId(history.getChapterId());
        dto.setNovelId(history.getNovelId());
        dto.setViewTime(history.getUpdateTime());

        Novel novel = novelMap.get(history.getNovelId());
        if (novel != null) {
            dto.setNovelTitle(novel.getTitle());
            dto.setNovelCover(novel.getCoverImgUrl());
            dto.setSynopsis(novel.getSynopsis());
            dto.setAvgRating(novel.getAvgRating());
            dto.setChapterCnt(novel.getChapterCnt());
            dto.setCategoryId(novel.getCategoryId());

            Category category = categoryMap.get(novel.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }

        Chapter chapter = chapterMap.get(history.getChapterId());
        if (chapter != null) {
            dto.setChapterNumber(chapter.getChapterNumber());
        }

        dto.setInLibrary(libraryStatusMap.getOrDefault(history.getNovelId(), false));

        return dto;
    }
}