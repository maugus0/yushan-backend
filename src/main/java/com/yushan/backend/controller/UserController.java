package com.yushan.backend.controller;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.dto.UserProfileUpdateResponseDTO;
import com.yushan.backend.dto.EmailVerificationRequestDTO;
import com.yushan.backend.dto.ApiResponse;
import com.yushan.backend.entity.User;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.UserService;
import com.yushan.backend.util.JwtUtil;
import com.yushan.backend.exception.UnauthorizedException;
import com.yushan.backend.exception.ForbiddenException;
import com.yushan.backend.exception.ValidationException;
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
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Return current authenticated user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserProfileResponseDTO> getCurrentUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
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
            throw new ValidationException("User ID not found");
        }

        UserProfileResponseDTO dto = userService.getUserProfile(userId);
        if (dto == null) {
            throw new ValidationException("User not found");
        }
        return ApiResponse.success("User profile retrieved successfully", dto);
    }

    /**
     * Update current user's editable profile fields
     */
    @PutMapping("/{id}/profile")
    @PreAuthorize("isOwner(#id.toString())")
    public ApiResponse<UserProfileUpdateResponseDTO> updateProfile(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UserProfileUpdateRequestDTO body,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        // Ownership check: only the owner can update their profile (admin bypass can be added later)
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            String currentId = ((CustomUserDetails) principal).getUserId();
            if (currentId == null || !id.toString().equals(currentId)) {
                throw new ForbiddenException("Access denied");
            }
        } else {
            throw new UnauthorizedException("Authentication required");
        }

        try {
            UserProfileUpdateResponseDTO updateResponse = userService.updateUserProfileSelective(id, body);
            if (updateResponse == null) {
                throw new ValidationException("User not found");
            }
            
            // If email was changed, generate new tokens
            if (updateResponse.isEmailChanged()) {
                // Get updated user from database
                User updatedUser = userMapper.selectByPrimaryKey(id);
                if (updatedUser != null) {
                    // Generate new tokens with updated email
                    String newAccessToken = jwtUtil.generateAccessToken(updatedUser);
                    String newRefreshToken = jwtUtil.generateRefreshToken(updatedUser);
                    
                    // Set new tokens in response
                    updateResponse.setAccessToken(newAccessToken);
                    updateResponse.setRefreshToken(newRefreshToken);
                    updateResponse.setTokenType("Bearer");
                    updateResponse.setExpiresIn(jwtUtil.getAccessTokenExpiration());
                }
            }
            
            return ApiResponse.success("Profile updated successfully", updateResponse);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    /**
     * Send verification email for email change
     */
    @PostMapping("/send-email-change-verification")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> sendEmailChangeVerification(
            @RequestBody EmailVerificationRequestDTO emailRequest,
            Authentication authentication) {
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UnauthorizedException("Authentication required");
            }

            String newEmail = emailRequest.getEmail();
            if (newEmail == null || newEmail.trim().isEmpty()) {
                throw new ValidationException("Email is required");
            }

            // Basic email format validation
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new ValidationException("Invalid email format");
            }

            userService.sendEmailChangeVerification(newEmail.trim().toLowerCase(java.util.Locale.ROOT));

            return ApiResponse.success("Verification code sent successfully");
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new ValidationException("Failed to send verification email: " + e.getMessage());
        }
    }
}