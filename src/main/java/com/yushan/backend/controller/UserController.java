package com.yushan.backend.controller;

import com.yushan.backend.common.Result;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.dto.EmailVerificationRequestDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.yushan.backend.dto.UserProfileUpdateRequestDTO;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * Return current authenticated user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Result<UserProfileResponseDTO> getCurrentUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.noAuth();
        }

        UUID userId = null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            String id = ((CustomUserDetails) principal).getUserId();
            if (id != null) {
                userId = UUID.fromString(id);
            } else {
                // Fallback: resolve by email if needed
                String email = ((CustomUserDetails) principal).getUsername();
                User byEmail = userMapper.selectByEmail(email);
                if (byEmail != null) {
                    userId = byEmail.getUuid();
                }
            }
        }

        if (userId == null) {
            return Result.error("userId is null");
        }

        UserProfileResponseDTO dto = userService.getUserProfile(userId);
        if (dto == null) {
            return Result.error("User not found");
        }
        return Result.success(dto);
    }

    /**
     * Update current user's editable profile fields
     */
    @PutMapping("/{id}/profile")
    @PreAuthorize("isOwner(#id.toString())")
    public Result<UserProfileResponseDTO> updateProfile(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UserProfileUpdateRequestDTO body,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.noAuth();
        }

        // Ownership check: only the owner can update their profile (admin bypass can be added later)
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            String currentId = ((CustomUserDetails) principal).getUserId();
            if (currentId == null || !id.toString().equals(currentId)) {
                return Result.forbidden();
            }
        } else {
            return Result.noAuth();
        }

        try {
            UserProfileResponseDTO updated = userService.updateUserProfileSelective(id, body);
            if (updated == null) {
                return Result.error("User is null");
            }
            return Result.success(updated);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * Send verification email for email change
     */
    @PostMapping("/send-email-change-verification")
    @PreAuthorize("isAuthenticated()")
    public Result<String> sendEmailChangeVerification(
            @RequestBody EmailVerificationRequestDTO emailRequest,
            Authentication authentication) {
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.noAuth();
            }

            String newEmail = emailRequest.getEmail();
            if (newEmail == null || newEmail.trim().isEmpty()) {
                return Result.error("Email is required");
            }

            // Basic email format validation
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return Result.error("Invalid email format");
            }

            userService.sendEmailChangeVerification(newEmail.trim().toLowerCase(java.util.Locale.ROOT));

            return Result.success("Verification code sent successfully");

        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("Failed to send verification email: " + e.getMessage());
        }
    }
}