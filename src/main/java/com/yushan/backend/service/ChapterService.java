package com.yushan.backend.service;

import com.yushan.backend.dao.ChapterMapper;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Chapter;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private NovelMapper novelMapper;

    @Transactional
    public ChapterDetailResponseDTO createChapter(UUID userId, ChapterCreateRequestDTO req) {
        // Validate novel exists and user is the author
        Novel novel = novelMapper.selectByPrimaryKey(req.getNovelId());
        if (novel == null || Boolean.FALSE.equals(novel.getIsValid())) {
            throw new ResourceNotFoundException("novel not found");
        }

        if (!novel.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("only the author can create chapters");
        }

        // Validate chapter number doesn't exist
        if (chapterMapper.existsByNovelIdAndChapterNumber(req.getNovelId(), req.getChapterNumber())) {
            throw new IllegalArgumentException("chapter number already exists for this novel");
        }

        // Calculate word count if not provided
        Integer wordCnt = req.getWordCnt();
        if (wordCnt == null && req.getContent() != null && !req.getContent().trim().isEmpty()) {
            wordCnt = req.getContent().trim().length();
        }

        Date now = new Date();

        // Create chapter entity
        Chapter chapter = new Chapter();
        chapter.setUuid(UUID.randomUUID());
        chapter.setNovelId(req.getNovelId());
        chapter.setChapterNumber(req.getChapterNumber());
        chapter.setTitle(req.getTitle());
        chapter.setContent(req.getContent());
        chapter.setWordCnt(wordCnt);
        chapter.setIsPremium(req.getIsPremium() != null ? req.getIsPremium() : false);
        chapter.setYuanCost(req.getYuanCost() != null ? req.getYuanCost() : 0.0f);
        chapter.setViewCnt(0L);
        chapter.setIsValid(req.getIsValid() != null ? req.getIsValid() : true);
        chapter.setCreateTime(now);
        chapter.setUpdateTime(now);
        chapter.setPublishTime(req.getPublishTime() != null ? req.getPublishTime() : now);

        chapterMapper.insertSelective(chapter);

        // Update novel's chapter count and word count
        updateNovelStatistics(req.getNovelId());

        return getChapterByUuid(chapter.getUuid());
    }

    @Transactional
    public void batchCreateChapters(UUID userId, ChapterBatchCreateRequestDTO req) {
        // Validate novel exists and user is the author
        Novel novel = novelMapper.selectByPrimaryKey(req.getNovelId());
        if (novel == null || Boolean.FALSE.equals(novel.getIsValid())) {
            throw new ResourceNotFoundException("novel not found");
        }

        if (!novel.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("only the author can create chapters");
        }

        List<Chapter> chapters = new ArrayList<>();
        Date now = new Date();

        for (ChapterBatchCreateRequestDTO.ChapterData data : req.getChapters()) {
            // Validate chapter number doesn't exist
            if (chapterMapper.existsByNovelIdAndChapterNumber(req.getNovelId(), data.getChapterNumber())) {
                throw new IllegalArgumentException("chapter number " + data.getChapterNumber() + " already exists");
            }

            // Calculate word count if not provided
            Integer wordCnt = data.getWordCnt();
            if (wordCnt == null && data.getContent() != null && !data.getContent().trim().isEmpty()) {
                wordCnt = data.getContent().trim().length();
            }

            Chapter chapter = new Chapter();
            chapter.setUuid(UUID.randomUUID());
            chapter.setNovelId(req.getNovelId());
            chapter.setChapterNumber(data.getChapterNumber());
            chapter.setTitle(data.getTitle());
            chapter.setContent(data.getContent());
            chapter.setWordCnt(wordCnt);
            chapter.setIsPremium(data.getIsPremium() != null ? data.getIsPremium() : false);
            chapter.setYuanCost(data.getYuanCost() != null ? data.getYuanCost() : 0.0f);
            chapter.setViewCnt(0L);
            chapter.setIsValid(data.getIsValid() != null ? data.getIsValid() : true);
            chapter.setCreateTime(now);
            chapter.setUpdateTime(now);
            chapter.setPublishTime(data.getPublishTime() != null ? data.getPublishTime() : now);

            chapters.add(chapter);
        }

        chapterMapper.batchInsert(chapters);

        // Update novel's chapter count and word count
        updateNovelStatistics(req.getNovelId());
    }

    public ChapterDetailResponseDTO getChapterByUuid(UUID uuid) {
        Chapter chapter = chapterMapper.selectByUuid(uuid);
        if (chapter == null) {
            throw new ResourceNotFoundException("chapter not found");
        }

        // Check if chapter is valid
        if (Boolean.FALSE.equals(chapter.getIsValid())) {
            throw new ResourceNotFoundException("chapter not found");
        }

        ChapterDetailResponseDTO response = toDetailResponse(chapter);

        // Get navigation links
        Chapter nextChapter = chapterMapper.selectNextChapter(chapter.getNovelId(), chapter.getChapterNumber());
        Chapter prevChapter = chapterMapper.selectPreviousChapter(chapter.getNovelId(), chapter.getChapterNumber());

        if (nextChapter != null) {
            response.setNextChapterUuid(nextChapter.getUuid());
        }
        if (prevChapter != null) {
            response.setPreviousChapterUuid(prevChapter.getUuid());
        }

        return response;
    }

    public ChapterDetailResponseDTO getChapterByNovelIdAndNumber(Integer novelId, Integer chapterNumber) {
        Chapter chapter = chapterMapper.selectByNovelIdAndChapterNumber(novelId, chapterNumber);
        if (chapter == null || Boolean.FALSE.equals(chapter.getIsValid())) {
            throw new ResourceNotFoundException("chapter not found");
        }
        return getChapterByUuid(chapter.getUuid());
    }

    public ChapterListResponseDTO getChaptersByNovelId(Integer novelId, Integer page, Integer pageSize, Boolean publishedOnly) {
        // Validate novel exists
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null || Boolean.FALSE.equals(novel.getIsValid())) {
            throw new ResourceNotFoundException("novel not found");
        }

        // Validate and set defaults
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }

        int offset = (page - 1) * pageSize;

        List<Chapter> chapters;
        long totalCount;

        if (Boolean.TRUE.equals(publishedOnly)) {
            chapters = chapterMapper.selectPublishedByNovelIdWithPagination(novelId, offset, pageSize);
            totalCount = chapterMapper.countPublishedByNovelId(novelId);
        } else {
            chapters = chapterMapper.selectByNovelIdWithPagination(novelId, offset, pageSize);
            totalCount = chapterMapper.countByNovelId(novelId);
        }

        List<ChapterListResponseDTO.ChapterSummary> summaries = chapters.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        return new ChapterListResponseDTO(summaries, totalCount, page, pageSize, totalPages);
    }

    public ChapterListResponseDTO searchChapters(ChapterSearchRequestDTO req) {
        // Validate and set defaults
        if (req.getPage() == null || req.getPage() < 1) {
            req.setPage(1);
        }
        if (req.getPageSize() == null || req.getPageSize() <= 0) {
            req.setPageSize(20);
        }
        if (req.getPageSize() > 100) {
            req.setPageSize(100);
        }

        // For now, use the existing methods. In production, add dynamic search to mapper
        return getChaptersByNovelId(
                req.getNovelId(),
                req.getPage(),
                req.getPageSize(),
                req.getPublishedOnly()
        );
    }

    public ChapterStatisticsResponseDTO getChapterStatistics(Integer novelId) {
        // Validate novel exists
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null || Boolean.FALSE.equals(novel.getIsValid())) {
            throw new ResourceNotFoundException("novel not found");
        }

        long totalChapters = chapterMapper.countByNovelId(novelId);
        long publishedChapters = chapterMapper.countPublishedByNovelId(novelId);

        List<Chapter> drafts = chapterMapper.selectDraftsByNovelId(novelId);
        List<Chapter> scheduled = chapterMapper.selectScheduledByNovelId(novelId);

        List<Chapter> allChapters = chapterMapper.selectByNovelId(novelId);

        long premiumChapters = allChapters.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsPremium()))
                .count();
        long freeChapters = totalChapters - premiumChapters;

        long totalWordCount = chapterMapper.sumWordCountByNovelId(novelId);
        long totalViewCount = allChapters.stream()
                .mapToLong(Chapter::getViewCnt)
                .sum();

        float totalRevenue = allChapters.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsPremium()))
                .map(c -> c.getYuanCost() * c.getViewCnt())
                .reduce(0f, Float::sum);

        Integer maxChapterNumber = chapterMapper.selectMaxChapterNumberByNovelId(novelId);

        ChapterStatisticsResponseDTO response = new ChapterStatisticsResponseDTO(
                novelId,
                totalChapters,
                publishedChapters,
                (long) drafts.size(),
                (long) scheduled.size(),
                premiumChapters,
                freeChapters,
                totalWordCount,
                totalViewCount,
                totalRevenue,
                maxChapterNumber
        );

        // Get latest chapter
        if (!allChapters.isEmpty()) {
            Chapter latest = allChapters.stream()
                    .max(Comparator.comparing(Chapter::getPublishTime))
                    .orElse(null);
            if (latest != null) {
                response.setLatestChapter(new ChapterStatisticsResponseDTO.ChapterSummary(
                        latest.getChapterNumber(),
                        latest.getTitle(),
                        latest.getViewCnt()
                ));
            }
        }

        // Get most viewed chapter
        if (!allChapters.isEmpty()) {
            Chapter mostViewed = allChapters.stream()
                    .max(Comparator.comparing(Chapter::getViewCnt))
                    .orElse(null);
            if (mostViewed != null) {
                response.setMostViewedChapter(new ChapterStatisticsResponseDTO.ChapterSummary(
                        mostViewed.getChapterNumber(),
                        mostViewed.getTitle(),
                        mostViewed.getViewCnt()
                ));
            }
        }

        return response;
    }

    @Transactional
    public ChapterDetailResponseDTO updateChapter(UUID userId, ChapterUpdateRequestDTO req) {
        Chapter existing = chapterMapper.selectByUuid(req.getUuid());
        if (existing == null || Boolean.FALSE.equals(existing.getIsValid())) {
            throw new ResourceNotFoundException("chapter not found");
        }

        // Validate user is the author
        Novel novel = novelMapper.selectByPrimaryKey(existing.getNovelId());
        if (novel == null || !novel.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("only the author can update chapters");
        }

        boolean hasChanges = false;

        // Update only provided fields
        if (req.getTitle() != null && !req.getTitle().trim().isEmpty()) {
            if (!req.getTitle().equals(existing.getTitle())) {
                existing.setTitle(req.getTitle());
                hasChanges = true;
            }
        }

        if (req.getContent() != null && !req.getContent().trim().isEmpty()) {
            if (!req.getContent().equals(existing.getContent())) {
                existing.setContent(req.getContent());
                hasChanges = true;

                // Recalculate word count if content changed
                if (req.getWordCnt() == null) {
                    existing.setWordCnt(req.getContent().trim().length());
                }
            }
        }

        if (req.getWordCnt() != null && !req.getWordCnt().equals(existing.getWordCnt())) {
            existing.setWordCnt(req.getWordCnt());
            hasChanges = true;
        }

        if (req.getIsPremium() != null && !req.getIsPremium().equals(existing.getIsPremium())) {
            existing.setIsPremium(req.getIsPremium());
            hasChanges = true;
        }

        if (req.getYuanCost() != null && !req.getYuanCost().equals(existing.getYuanCost())) {
            existing.setYuanCost(req.getYuanCost());
            hasChanges = true;
        }

        if (req.getIsValid() != null && !req.getIsValid().equals(existing.getIsValid())) {
            existing.setIsValid(req.getIsValid());
            hasChanges = true;
        }

        if (req.getPublishTime() != null && !req.getPublishTime().equals(existing.getPublishTime())) {
            existing.setPublishTime(req.getPublishTime());
            hasChanges = true;
        }

        if (hasChanges) {
            existing.setUpdateTime(new Date());
            chapterMapper.updateByPrimaryKeySelective(existing);

            // Update novel statistics if word count changed
            if (req.getWordCnt() != null || req.getContent() != null) {
                updateNovelStatistics(existing.getNovelId());
            }
        }

        return getChapterByUuid(req.getUuid());
    }

    @Transactional
    public void publishChapter(UUID userId, ChapterPublishRequestDTO req) {
        Chapter chapter = chapterMapper.selectByUuid(req.getUuid());
        if (chapter == null || Boolean.FALSE.equals(chapter.getIsValid())) {
            throw new ResourceNotFoundException("chapter not found");
        }

        // Validate user is the author
        Novel novel = novelMapper.selectByPrimaryKey(chapter.getNovelId());
        if (novel == null || !novel.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("only the author can publish chapters");
        }

        chapter.setIsValid(req.getIsValid());
        if (req.getPublishTime() != null) {
            chapter.setPublishTime(req.getPublishTime());
        }
        chapter.setUpdateTime(new Date());

        chapterMapper.updateByPrimaryKeySelective(chapter);
    }

    @Transactional
    public void batchPublishChapters(UUID userId, Integer novelId, Boolean isValid) {
        // Validate novel exists and user is the author
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null || Boolean.FALSE.equals(novel.getIsValid())) {
            throw new ResourceNotFoundException("novel not found");
        }

        if (!novel.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("only the author can publish chapters");
        }

        List<Chapter> chapters = chapterMapper.selectByNovelId(novelId);
        List<Integer> ids = chapters.stream()
                .map(Chapter::getId)
                .collect(Collectors.toList());

        if (!ids.isEmpty()) {
            chapterMapper.updatePublishStatusByIds(ids, isValid);
        }
    }

    @Transactional
    public void incrementViewCount(UUID uuid) {
        Chapter chapter = chapterMapper.selectByUuid(uuid);
        if (chapter == null || Boolean.FALSE.equals(chapter.getIsValid())) {
            throw new ResourceNotFoundException("chapter not found");
        }
        chapterMapper.incrementViewCount(chapter.getId());
    }

    @Transactional
    public void deleteChapter(UUID userId, UUID uuid) {
        Chapter chapter = chapterMapper.selectByUuid(uuid);
        if (chapter == null) {
            throw new ResourceNotFoundException("chapter not found");
        }

        // Validate user is the author
        Novel novel = novelMapper.selectByPrimaryKey(chapter.getNovelId());
        if (novel == null || !novel.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("only the author can delete chapters");
        }

        chapterMapper.softDeleteByUuid(uuid);

        // Update novel statistics
        updateNovelStatistics(chapter.getNovelId());
    }

    @Transactional
    public void deleteChaptersByNovelId(UUID userId, Integer novelId) {
        // Validate novel exists and user is the author
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null || Boolean.FALSE.equals(novel.getIsValid())) {
            throw new ResourceNotFoundException("novel not found");
        }

        if (!novel.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("only the author can delete chapters");
        }

        List<Chapter> chapters = chapterMapper.selectByNovelId(novelId);
        for (Chapter chapter : chapters) {
            chapterMapper.softDeleteByPrimaryKey(chapter.getId());
        }

        // Update novel statistics
        updateNovelStatistics(novelId);
    }

    public UUID getNextChapterUuid(UUID currentChapterUuid) {
        Chapter current = chapterMapper.selectByUuid(currentChapterUuid);
        if (current == null) {
            return null;
        }

        Chapter next = chapterMapper.selectNextChapter(current.getNovelId(), current.getChapterNumber());
        return next != null ? next.getUuid() : null;
    }

    public UUID getPreviousChapterUuid(UUID currentChapterUuid) {
        Chapter current = chapterMapper.selectByUuid(currentChapterUuid);
        if (current == null) {
            return null;
        }

        Chapter prev = chapterMapper.selectPreviousChapter(current.getNovelId(), current.getChapterNumber());
        return prev != null ? prev.getUuid() : null;
    }

    public boolean chapterExists(Integer novelId, Integer chapterNumber) {
        return chapterMapper.existsByNovelIdAndChapterNumber(novelId, chapterNumber);
    }

    public Integer getNextAvailableChapterNumber(Integer novelId) {
        Integer maxChapter = chapterMapper.selectMaxChapterNumberByNovelId(novelId);
        return maxChapter != null ? maxChapter + 1 : 1;
    }

    /**
     * Update novel's chapter count and word count statistics
     * Called after chapter creation, update, or deletion
     */
    @Transactional
    public void updateNovelStatistics(Integer novelId) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null) {
            return;
        }

        long chapterCount = chapterMapper.countByNovelId(novelId);
        long wordCount = chapterMapper.sumWordCountByNovelId(novelId);

        novel.setChapterCnt((int) chapterCount);
        novel.setWordCnt(wordCount);
        novel.setUpdateTime(new Date());

        novelMapper.updateByPrimaryKeySelective(novel);
    }

    // Helper methods
    private ChapterDetailResponseDTO toDetailResponse(Chapter chapter) {
        ChapterDetailResponseDTO dto = new ChapterDetailResponseDTO();
        dto.setUuid(chapter.getUuid());
        dto.setNovelId(chapter.getNovelId());
        dto.setChapterNumber(chapter.getChapterNumber());
        dto.setTitle(chapter.getTitle());
        dto.setContent(chapter.getContent());
        dto.setWordCnt(chapter.getWordCnt());
        dto.setIsPremium(chapter.getIsPremium());
        dto.setYuanCost(chapter.getYuanCost());
        dto.setViewCnt(chapter.getViewCnt());
        dto.setIsValid(chapter.getIsValid());
        dto.setCreateTime(chapter.getCreateTime());
        dto.setUpdateTime(chapter.getUpdateTime());
        dto.setPublishTime(chapter.getPublishTime());
        return dto;
    }

    private ChapterListResponseDTO.ChapterSummary toSummary(Chapter chapter) {
        String preview = chapter.getContent() != null && chapter.getContent().length() > 200
                ? chapter.getContent().substring(0, 200)
                : chapter.getContent();

        return new ChapterListResponseDTO.ChapterSummary(
                chapter.getUuid(),
                chapter.getChapterNumber(),
                chapter.getTitle(),
                preview,
                chapter.getWordCnt(),
                chapter.getIsPremium(),
                chapter.getYuanCost(),
                chapter.getViewCnt(),
                chapter.getPublishTime()
        );
    }
}