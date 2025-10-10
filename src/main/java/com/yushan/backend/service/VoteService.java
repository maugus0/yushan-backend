package com.yushan.backend.service;

import com.yushan.backend.dao.VoteMapper;
import com.yushan.backend.dto.VoteResponseDTO;
import com.yushan.backend.dto.VoteStatsResponseDTO;
import com.yushan.backend.dto.VoteStatusResponseDTO;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.Vote;
import com.yushan.backend.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class VoteService {

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private NovelService novelService;

    @Autowired
    private EXPService expService;

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

        // Check current vote status
        Vote activeVote = voteMapper.selectActiveByUserAndNovel(userId, novelId);
        
        if (activeVote != null) {
            // User has active vote → unvote
            return unvote(novelId, userId);
        } else {
            // User has no active vote → vote
            return vote(novelId, userId);
        }
    }

    /**
     * Vote for a novel (internal method)
     */
    private VoteResponseDTO vote(Integer novelId, UUID userId) {
        // Check if user has inactive vote (reactivate it)
        Vote inactiveVote = voteMapper.selectByUserAndNovel(userId, novelId);
        if (inactiveVote != null && !inactiveVote.getIsActive()) {
            // Reactivate existing vote
            voteMapper.reactivateVote(userId, novelId);
        } else {
            // Create new vote
            Vote vote = new Vote();
            vote.setUserId(userId);
            vote.setNovelId(novelId);
            vote.setIsActive(true);
            Date now = new Date();
            vote.setCreateTime(now);
            vote.setUpdateTime(now);

            voteMapper.insertSelective(vote);
        }

        // Update novel vote count
        novelService.incrementVoteCount(novelId);

        // Get updated vote count
        Integer updatedVoteCount = novelService.getNovelVoteCount(novelId);

        // add exp
        expService.addExp(userId, EXP_PER_VOTE);

        return new VoteResponseDTO(
            novelId,
            updatedVoteCount,
            true
        );
    }

    /**
     * Unvote for a novel (internal method)
     */
    private VoteResponseDTO unvote(Integer novelId, UUID userId) {
        // Deactivate vote (soft delete)
        voteMapper.deactivateVote(userId, novelId);

        // Update novel vote count
        novelService.decrementVoteCount(novelId);

        // Get updated vote count
        Integer updatedVoteCount = novelService.getNovelVoteCount(novelId);

        return new VoteResponseDTO(
            novelId,
            updatedVoteCount,
            false
        );
    }

    /**
     * Get vote statistics for a novel
     */
    public VoteStatsResponseDTO getVoteStats(Integer novelId) {
        Integer voteCount = novelService.getNovelVoteCount(novelId);
        return new VoteStatsResponseDTO(novelId, voteCount);
    }

    /**
     * Get user's vote status for a novel
     */
    public VoteStatusResponseDTO getUserVoteStatus(Integer novelId, UUID userId) {
        // Validate novel exists using NovelService
        novelService.getNovelVoteCount(novelId);

        Vote existingVote = voteMapper.selectActiveByUserAndNovel(userId, novelId);
        
        if (existingVote == null) {
            return new VoteStatusResponseDTO(novelId, false, null);
        } else {
            return new VoteStatusResponseDTO(
                novelId,
                true,
                existingVote.getCreateTime()
            );
        }
    }
}
