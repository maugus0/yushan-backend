package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.exception.ValidationException;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/novels")
@CrossOrigin(origins = "*")
public class VoteController {

    @Autowired
    private VoteService voteService;

    /**
     * Toggle vote for a novel (vote if not voted, unvote if already voted)
     */
    @PostMapping("/{novelId}/vote")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<VoteResponseDTO> toggleVote(@PathVariable Integer novelId,
                                                   Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        VoteResponseDTO response = voteService.toggleVote(novelId, userId);
        
        String message = response.getUserVoted() ? "Voted successfully" : "Vote removed successfully";
        return ApiResponse.success(message, response);
    }

    /**
     * Get vote statistics for a novel
     */
    @GetMapping("/{novelId}/vote/stats")
    @PreAuthorize("permitAll()")
    public ApiResponse<VoteStatsResponseDTO> getVoteStats(@PathVariable Integer novelId) {
        VoteStatsResponseDTO response = voteService.getVoteStats(novelId);
        return ApiResponse.success("Vote statistics retrieved", response);
    }

    /**
     * Get user's vote status for a novel
     */
    @GetMapping("/{novelId}/vote/status")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<VoteStatusResponseDTO> getUserVoteStatus(@PathVariable Integer novelId,
                                                               Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        VoteStatusResponseDTO response = voteService.getUserVoteStatus(novelId, userId);
        return ApiResponse.success("Vote status retrieved", response);
    }

    /**
     * Extract user ID from authentication
     */
    private UUID getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (userDetails.getUserId() != null) {
                return UUID.fromString(userDetails.getUserId());
            }
        }
        throw new ValidationException("User not authenticated or user ID not found.");
    }
}
