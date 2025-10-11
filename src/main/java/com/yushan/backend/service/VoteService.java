package com.yushan.backend.service;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dao.VoteMapper;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.VoteResponseDTO;
import com.yushan.backend.dto.VoteUserResponseDTO;
import com.yushan.backend.entity.*;
import com.yushan.backend.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VoteService {

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private NovelService novelService;

    @Autowired
    private EXPService expService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NovelMapper novelMapper;

    private static final Float EXP_PER_VOTE = 3f;

    /**
     * Toggle vote for a novel (vote if not voted, unvote if already voted)
     */
    @Transactional
    public VoteResponseDTO toggleVote(Integer novelId, UUID userId) {
        // Validate novel exists and get novel data
        Novel novel = novelService.getNovelEntity(novelId);

        // Check if user is the author (cannot vote own novel)
        if (novel.getAuthorId().equals(userId)) {
            throw new ValidationException("Cannot vote your own novel");
        }

        User user = userMapper.selectByPrimaryKey(userId);

        //Check if user have enough yuan
        if (user.getYuan() < 1) {
            throw new ValidationException("Not enough yuan");
        }

        // Create new vote
        Vote vote = new Vote();
        vote.setUserId(userId);
        vote.setNovelId(novelId);
        Date now = new Date();
        vote.setCreateTime(now);
        vote.setUpdateTime(now);

        voteMapper.insertSelective(vote);

        // update yuan
        user.setYuan(user.getYuan() - 1);
        userMapper.updateByPrimaryKeySelective(user);

        // Update novel vote count
        novelService.incrementVoteCount(novelId);

        // Get updated vote count
        Integer updatedVoteCount = novelService.getNovelVoteCount(novelId);

        // add exp
        expService.addExp(userId, EXP_PER_VOTE);

        return new VoteResponseDTO(novelId, updatedVoteCount, user.getYuan());
    }

    public PageResponseDTO<VoteUserResponseDTO> getUserVotes(UUID userId, int page, int size) {
        int offset = page * size;
        long totalElements = voteMapper.countByUserId(userId);

        if (totalElements == 0) {
            return new PageResponseDTO<>(Collections.emptyList(), 0L, page, size);
        }

        List<Vote> votes = voteMapper.selectByUserIdWithPagination(userId, offset, size);
        if (votes.isEmpty()) {
            return new PageResponseDTO<>(Collections.emptyList(), totalElements, page, size);
        }

        List<Integer> novelIds = votes.stream()
                .map(Vote::getNovelId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, Novel> novelMap = novelMapper.selectByIds(novelIds).stream()
                .collect(Collectors.toMap(Novel::getId, novel -> novel));

        List<VoteUserResponseDTO> dtos = votes.stream()
                .map(vote -> {
                    Novel novel = novelMap.get(vote.getNovelId());
                    return convertToDTO(vote, novel);
                })
                .collect(Collectors.toList());

        return new PageResponseDTO<>(dtos, totalElements, page, size);
    }

    private VoteUserResponseDTO convertToDTO(Vote vote, Novel novel) {
        VoteUserResponseDTO dto = new VoteUserResponseDTO();
        dto.setId(vote.getId());
        dto.setNovelId(vote.getNovelId());
        dto.setNovelTitle(novel.getTitle());
        dto.setVotedTime(vote.getCreateTime());

        return dto;
    }
}
