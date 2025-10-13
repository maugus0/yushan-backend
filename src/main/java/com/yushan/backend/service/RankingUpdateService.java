package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import com.yushan.backend.util.RedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RankingUpdateService {

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    private static final String RANK_NOVEL_VIEW_ALL = "ranking:novel:view:all";
    private static final String RANK_NOVEL_VOTE_ALL = "ranking:novel:vote:all";
    private static final String RANK_NOVEL_VIEW_CATE_PREFIX = "ranking:novel:view:";
    private static final String RANK_NOVEL_VOTE_CATE_PREFIX = "ranking:novel:vote:";
    private static final String RANK_USER_EXP = "ranking:user:exp";
    private static final String RANK_AUTHOR_VOTE = "ranking:author:vote";
    private static final String RANK_AUTHOR_VIEW = "ranking:author:view";
    private static final String RANK_AUTHOR_NOVEL_NUM = "ranking:author:novelNum";

    @PostConstruct
    public void runUpdateOnStartup() {
        log.info("update ranking while starting");
        try {
            updateAllRankings();
        } catch (Exception e) {
            log.warn("Failed to initialize rankings due to Redis connection issue: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(readOnly = true)
    public void updateAllRankings() {
        log.info("Start updating daily ranking");
        updateNovelRankings();
        updateUserRankings();
        updateAuthorRankings();
        log.info("Finished updating daily ranking");
    }
    public void updateNovelRankings() {
        log.info("Updating novel ranking");
        List<Novel> allNovels = novelMapper.selectAllNovelsForRanking();
        Map<Integer, List<Novel>> novelsByCategory = allNovels.stream()
                .filter(novel -> novel.getCategoryId() != null)
                .collect(Collectors.groupingBy(Novel::getCategoryId));

        Set<String> oldKeys = redisUtil.keys("ranking:novel:*");
        if (oldKeys != null && !oldKeys.isEmpty()) {
            redisUtil.delete(oldKeys);
        }

        for (Novel novel : allNovels) {
            redisUtil.zAdd(RANK_NOVEL_VIEW_ALL, novel.getId().toString(), novel.getViewCnt());
            redisUtil.zAdd(RANK_NOVEL_VOTE_ALL, novel.getId().toString(), novel.getVoteCnt());
        }

        for (Map.Entry<Integer, List<Novel>> entry : novelsByCategory.entrySet()) {
            String viewKey = RANK_NOVEL_VIEW_CATE_PREFIX + entry.getKey();
            String voteKey = RANK_NOVEL_VOTE_CATE_PREFIX + entry.getKey();
            for (Novel novel : entry.getValue()) {
                redisUtil.zAdd(viewKey, novel.getId().toString(), novel.getViewCnt());
                redisUtil.zAdd(voteKey, novel.getId().toString(), novel.getVoteCnt());
            }
        }
        log.info("Updated for {} novels and {} categories", allNovels.size(), novelsByCategory.size());
    }

    public void updateUserRankings() {
        log.info("Updating user ranking");
        List<User> allUsers = userMapper.selectAllUsersForRanking();

        redisUtil.delete(RANK_USER_EXP);

        for (User user : allUsers) {
            redisUtil.zAdd(RANK_USER_EXP, user.getUuid().toString(), user.getExp());
        }
        log.info("Updated {} users", allUsers.size());
    }

    public void updateAuthorRankings() {
        log.info("Updating author ranking");
        List<AuthorResponseDTO> allAuthors = novelMapper.selectAuthorsByRanking("vote", 0, Integer.MAX_VALUE);

        redisUtil.delete(List.of(RANK_AUTHOR_VOTE, RANK_AUTHOR_VIEW, RANK_AUTHOR_NOVEL_NUM));

        for (AuthorResponseDTO author : allAuthors) {
            redisUtil.zAdd(RANK_AUTHOR_VOTE, author.getUuid(), author.getTotalVoteCnt());
            redisUtil.zAdd(RANK_AUTHOR_VIEW, author.getUuid(), author.getTotalViewCnt());
            redisUtil.zAdd(RANK_AUTHOR_NOVEL_NUM, author.getUuid(), author.getNovelNum());
        }
        log.info("Updated {} authors", allAuthors.size());
    }
}