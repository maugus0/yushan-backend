package com.yushan.backend.service;

import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.dto.NovelDetailResponseDTO;
import com.yushan.backend.dto.NovelRankingResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryService categoryService;

    private static final int MAX_TOTAL_RECORDS = 100;

    /**
     * @param page
     * @param size
     * @param sortType
     * @param categoryId
     * @param timeRange
     * @return
     */
    public PageResponseDTO<NovelDetailResponseDTO> rankNovel(
            Integer page,
            Integer size,
            String sortType,
            Integer categoryId,
            String timeRange) {

        int offset = page * size;

        if (offset >= MAX_TOTAL_RECORDS) {
            return PageResponseDTO.of(List.of(), 0L, page, size);
        }

        int adjustedSize = Math.min(size, MAX_TOTAL_RECORDS - offset);

        List<Novel> novels = novelMapper.selectNovelsByRanking(categoryId, sortType, offset, adjustedSize);
        if (novels.isEmpty()) {
            return PageResponseDTO.of(List.of(), 0L, page, size);
        }
        List<Integer> categoryIds = novels.stream()
                .map(Novel::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, String> categoryMap = categoryService.getCategoryMapByIds(categoryIds);

        long total = Math.min(novelMapper.countNovelsByRanking(categoryId), MAX_TOTAL_RECORDS);

        List<NovelDetailResponseDTO> novelDTOs = novels.stream()
                .map(novel -> convertToNovelDetailResponseDTO(novel, categoryMap))
                .collect(Collectors.toList());

        return PageResponseDTO.of(novelDTOs, total, page, size);
    }

    /**
     * rank user by exp
     * @param page
     * @param size
     * @param timeRange
     * @return
     */
    public PageResponseDTO<UserProfileResponseDTO> rankUser(
            Integer page,
            Integer size,
            String timeRange) {

        int offset = page * size;

        if (offset >= MAX_TOTAL_RECORDS) {
            return PageResponseDTO.of(List.of(), 0L, page, size);
        }

        int adjustedSize = Math.min(size, MAX_TOTAL_RECORDS - offset);

        List<User> users = userMapper.selectUsersByRanking(offset, adjustedSize);

        long total = Math.min(userMapper.countAllUsers(), MAX_TOTAL_RECORDS);

        List<UserProfileResponseDTO> userDTOs = users.stream()
                .map(this::convertToUserProfileResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.of(userDTOs, total, page, size);
    }

    /**
     *
     * @param page
     * @param size
     * @param sortType
     * @param timeRange
     * @return
     */
    public PageResponseDTO<AuthorResponseDTO> rankAuthor(
            Integer page,
            Integer size,
            String sortType,
            String timeRange) {

        int offset = page * size;

        if (offset >= MAX_TOTAL_RECORDS) {
            return PageResponseDTO.of(List.of(), 0L, page, size);
        }

        int adjustedSize = Math.min(size, MAX_TOTAL_RECORDS - offset);

        List<AuthorResponseDTO> authors = novelMapper.selectAuthorsByRanking(sortType, offset, adjustedSize);

        long total = Math.min(userMapper.countAllAuthors(), MAX_TOTAL_RECORDS);

        return PageResponseDTO.of(authors, total, page, size);
    }

    /**
     * Get ranking information for a specific novel
     * @param novelId The ID of the novel
     * @param sortType The sorting type (view/vote)
     * @param categoryId Optional category filter
     * @return NovelRankingResponseDTO with ranking information
     */
    public NovelRankingResponseDTO getNovelRanking(Integer novelId, String sortType, Integer categoryId) {
        // Get the novel details
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null || Boolean.FALSE.equals(novel.getIsValid())) {
            throw new RuntimeException("Novel not found");
        }

        // Get overall ranking
        Integer overallRanking = novelMapper.getNovelRanking(novelId, sortType, null);
        
        // Get category ranking if category is specified
        Integer categoryRanking = null;
        if (categoryId != null && categoryId > 0) {
            categoryRanking = novelMapper.getNovelCategoryRanking(novelId, sortType, categoryId);
        }

        // Convert to response DTO
        NovelRankingResponseDTO response = new NovelRankingResponseDTO();
        response.setId(novel.getId());
        response.setUuid(novel.getUuid());
        response.setTitle(novel.getTitle());
        response.setAuthorId(novel.getAuthorId());
        response.setAuthorName(novel.getAuthorName());
        response.setCategoryId(novel.getCategoryId());
        response.setSynopsis(novel.getSynopsis());
        response.setCoverImgUrl(novel.getCoverImgUrl());
        response.setIsCompleted(novel.getIsCompleted());
        response.setViewCnt(novel.getViewCnt());
        response.setVoteCnt(novel.getVoteCnt());
        response.setAvgRating(novel.getAvgRating());
        response.setReviewCnt(novel.getReviewCnt());
        response.setChapterCnt(novel.getChapterCnt());
        response.setWordCnt(novel.getWordCnt());
        response.setYuanCnt(novel.getYuanCnt());
        response.setCreateTime(novel.getCreateTime());
        response.setUpdateTime(novel.getUpdateTime());
        response.setPublishTime(novel.getPublishTime());
        
        // Set ranking information
        response.setRanking(overallRanking);
        response.setSortType(sortType);
        response.setCategoryRanking(categoryRanking);
        response.setOverallRanking(overallRanking);

        // Set category name if available
        if (novel.getCategoryId() != null) {
            try {
                var category = categoryService.getCategoryById(novel.getCategoryId());
                response.setCategoryName(category.getName());
            } catch (Exception e) {
                response.setCategoryName(null);
            }
        }

        return response;
    }

    private UserProfileResponseDTO convertToUserProfileResponseDTO(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setUuid(user.getUuid().toString());
        dto.setUsername(user.getUsername());
        dto.setExp(user.getExp());
        dto.setLevel(user.getLevel());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }

    private NovelDetailResponseDTO convertToNovelDetailResponseDTO(Novel novel, Map<Integer, String> categoryMap) {
        NovelDetailResponseDTO dto = new NovelDetailResponseDTO();
        dto.setId(novel.getId());
        dto.setTitle(novel.getTitle());
        dto.setViewCnt(novel.getViewCnt());
        dto.setVoteCnt(novel.getVoteCnt());
        dto.setCoverImgUrl(novel.getCoverImgUrl());
        dto.setCategoryId(novel.getCategoryId());
        dto.setSynopsis(novel.getSynopsis());
        if (novel.getCategoryId() != null) {
            dto.setCategoryName(categoryMap.get(novel.getCategoryId()));
        }
        return dto;
    }
}
