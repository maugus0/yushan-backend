package com.yushan.backend.service;

import com.yushan.backend.dao.CategoryMapper;
import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.dto.NovelDetailResponseDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.Category;
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
