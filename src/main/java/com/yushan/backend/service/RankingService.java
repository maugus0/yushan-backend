package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.*;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import com.yushan.backend.exception.ResourceNotFoundException;
import com.yushan.backend.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private NovelMapper novelMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisUtil redisUtil;

    public PageResponseDTO<NovelDetailResponseDTO> rankNovel(
            Integer page, Integer size, String sortType, Integer categoryId, String timeRange) {

        String redisKey = buildNovelRedisKey(sortType, categoryId);
        long offset = (long) page * size;

        Long totalInRedis = redisUtil.zCard(redisKey);
        long totalElements = totalInRedis != null ? Math.min(totalInRedis, 100) : 0;

        if (offset >= totalElements) {
            return PageResponseDTO.of(Collections.emptyList(), totalElements, page, size);
        }

        long end = offset + size - 1;
        Set<String> novelIdsStr = redisUtil.zReverseRange(redisKey, offset, end);

        if (novelIdsStr == null || novelIdsStr.isEmpty()) {
            return PageResponseDTO.of(Collections.emptyList(), totalElements, page, size);
        }

        List<Integer> orderedNovelIds = novelIdsStr.stream().map(Integer::parseInt).collect(Collectors.toList());
        List<Novel> novelsFromDb = novelMapper.selectByIds(orderedNovelIds);

        Map<Integer, Novel> novelMap = novelsFromDb.stream()
                .collect(Collectors.toMap(Novel::getId, Function.identity()));

        List<Novel> sortedNovels = orderedNovelIds.stream()
                .map(novelMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Integer> categoryIds = sortedNovels.stream().map(Novel::getCategoryId).distinct().collect(Collectors.toList());
        Map<Integer, String> categoryMap = categoryService.getCategoryMapByIds(categoryIds);

        List<NovelDetailResponseDTO> novelDTOs = sortedNovels.stream()
                .map(novel -> convertToNovelDetailResponseDTO(novel, categoryMap))
                .collect(Collectors.toList());

        return PageResponseDTO.of(novelDTOs, totalElements, page, size);
    }

    private NovelRankDTO getNovelRank(Integer novelId, String sortType, Integer categoryId) {
        String redisKey = buildNovelRedisKey(sortType, categoryId);

        Long rank = redisUtil.zReverseRank(redisKey, novelId.toString());

        if (rank == null || rank >= 100) {
            return null;
        }

        Double score = redisUtil.zScore(redisKey, novelId.toString());
        return new NovelRankDTO(novelId, rank + 1, score, redisKey);
    }

    public NovelRankDTO getBestNovelRank(Integer novelId) {
        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null) {
            throw new ResourceNotFoundException("novel not found, id: " + novelId);
        }

        NovelRankDTO bestRank = null;

        NovelRankDTO allViewsRank = getNovelRank(novelId, "view", null);
        if (allViewsRank != null) {
            bestRank = new NovelRankDTO(novelId,  allViewsRank.getRank(), allViewsRank.getScore(), "All-Time Views Ranking");
        }

        NovelRankDTO allVotesRank = getNovelRank(novelId, "vote", null);
        if (allVotesRank != null) {
            if (bestRank == null || allVotesRank.getRank() < bestRank.getRank()) {
                bestRank = new NovelRankDTO(novelId, allVotesRank.getRank(), allVotesRank.getScore(), "All-Time Votes Ranking");
            }
        }

        Integer categoryId = novel.getCategoryId();
        if (categoryId != null) {
            String categoryName = categoryService.getCategoryById(categoryId).getName();

            NovelRankDTO categoryViewRank = getNovelRank(novelId, "view", categoryId);
            if (categoryViewRank != null) {
                if (bestRank == null || categoryViewRank.getRank() < bestRank.getRank()) {
                    bestRank = new NovelRankDTO(novelId, categoryViewRank.getRank(), categoryViewRank.getScore(), categoryName + " Views Ranking");
                }
            }

            NovelRankDTO categoryVoteRank = getNovelRank(novelId, "vote", categoryId);
            if (categoryVoteRank != null) {
                if (bestRank == null || categoryVoteRank.getRank() < bestRank.getRank()) {
                    bestRank = new NovelRankDTO(novelId, categoryVoteRank.getRank(), categoryVoteRank.getScore(), categoryName + " Votes Ranking");
                }
            }
        }

        return bestRank;
    }

    public PageResponseDTO<UserProfileResponseDTO> rankUser(Integer page, Integer size, String timeRange) {
        String redisKey = "ranking:user:exp";
        return getPaginatedRanking(page, size, redisKey,
                uuids -> userMapper.selectByUuids(uuids).stream()
                        .map(this::convertToUserProfileResponseDTO)
                        .collect(Collectors.toList()), dto -> UUID.fromString(dto.getUuid()) );
    }

    public PageResponseDTO<AuthorResponseDTO> rankAuthor(Integer page, Integer size, String sortType, String timeRange) {
        String redisKey = "ranking:author:" + sortType;
        return getPaginatedRanking(page, size, redisKey,
                uuids -> novelMapper.selectAuthorsByUuids(uuids), // DB fetcher
                authorDto -> UUID.fromString(authorDto.getUuid()) // UUID extractor
        );
    }

    private String buildNovelRedisKey(String sortType, Integer categoryId) {
        String baseKey = "ranking:novel:" + ("view".equalsIgnoreCase(sortType) ? "view" : "vote");
        return (categoryId == null || categoryId <= 0) ? baseKey + ":all" : baseKey + ":" + categoryId;
    }

    private <T> PageResponseDTO<T> getPaginatedRanking(int page, int size, String redisKey,
                                                       Function<List<UUID>, List<T>> fetcher,
                                                       Function<T, UUID> uuidExtractor) {
        long offset = (long) page * size;

        Long totalInRedis = redisUtil.zCard(redisKey);
        long totalElements = totalInRedis != null ? Math.min(totalInRedis, 100) : 0;

        if (offset >= totalElements) {
            return PageResponseDTO.of(Collections.emptyList(), totalElements, page, size);
        }

        Set<String> uuidsStr = redisUtil.zReverseRange(redisKey, offset, offset + size - 1);
        if (uuidsStr == null || uuidsStr.isEmpty()) {
            return PageResponseDTO.of(Collections.emptyList(), totalElements, page, size);
        }

        List<UUID> orderedUuids = uuidsStr.stream().map(UUID::fromString).collect(Collectors.toList());

        List<T> dtoList = fetcher.apply(orderedUuids);

        Map<UUID, T> dtoMap = dtoList.stream()
                .collect(Collectors.toMap(uuidExtractor, Function.identity()));

        List<T> sortedDtoList = orderedUuids.stream()
                .map(dtoMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return PageResponseDTO.of(sortedDtoList, totalElements, page, size);
    }

    private UserProfileResponseDTO convertToUserProfileResponseDTO(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setUuid(user.getUuid().toString());
        dto.setUsername(user.getUsername());
        dto.setExp(user.getExp());
        dto.setYuan(user.getYuan());
        dto.setLevel(user.getLevel());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }

    private NovelDetailResponseDTO convertToNovelDetailResponseDTO(Novel novel, Map<Integer, String> categoryMap) {
        NovelDetailResponseDTO dto = new NovelDetailResponseDTO();
        dto.setId(novel.getId());
        dto.setUuid(novel.getUuid());
        dto.setTitle(novel.getTitle());
        dto.setAuthorId(novel.getAuthorId());
        dto.setAuthorUsername(novel.getAuthorName());
        dto.setAvgRating(novel.getAvgRating());
        dto.setViewCnt(novel.getViewCnt());
        dto.setVoteCnt(novel.getVoteCnt());
        dto.setCoverImgUrl(novel.getCoverImgUrl());
        dto.setCategoryId(novel.getCategoryId());
        dto.setSynopsis(novel.getSynopsis());
        dto.setIsCompleted(novel.getIsCompleted());
        if (novel.getCategoryId() != null) {
            dto.setCategoryName(categoryMap.get(novel.getCategoryId()));
        }
        return dto;
    }
}
