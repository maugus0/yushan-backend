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
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class VoteController {

    @Autowired
    private VoteService voteService;

    /**
     * Toggle a vote for a novel
     */
    @PostMapping("/novels/{novelId}/vote")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<VoteResponseDTO> toggleVote(@PathVariable Integer novelId,
                                                   Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        VoteResponseDTO response = voteService.toggleVote(novelId, userId);

        return ApiResponse.success("Voted successfully", response);
    }

    /**
     * Get a user's all vote record
     */
    @GetMapping("/users/votes")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponseDTO<VoteUserResponseDTO>> getUserVotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        PageResponseDTO<VoteUserResponseDTO> response = voteService.getUserVotes(userId, page, size);
        return ApiResponse.success("User votes retrieved", response);
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
