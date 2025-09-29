package com.yushan.backend.controller;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.dto.EmailChangeVerificationRequestDTO;
import com.yushan.backend.dto.EmailChangeVerificationResponseDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import com.yushan.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.yushan.backend.dto.UserProfileUpdateRequestDTO;

import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<UserProfileResponseDTO> getCurrentUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
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
            return ResponseEntity.status(401).build();
        }

        UserProfileResponseDTO dto = userService.getUserProfile(userId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * Update current user's editable profile fields
     */
    @PutMapping("/{id}/profile")
    @PreAuthorize("isOwner(#id.toString())")
    public ResponseEntity<?> updateProfile(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UserProfileUpdateRequestDTO body,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        // Ownership check: only the owner can update their profile (admin bypass can be added later)
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            String currentId = ((CustomUserDetails) principal).getUserId();
            if (currentId == null || !id.toString().equals(currentId)) {
                return ResponseEntity.status(403).build();
            }
        } else {
            return ResponseEntity.status(401).build();
        }

        try {
            UserProfileResponseDTO updated = userService.updateUserProfileSelective(id, body);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Send verification email for email change
     */
    @PostMapping("/send-email-change-verification")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmailChangeVerificationResponseDTO> sendEmailChangeVerification(
            @RequestBody EmailChangeVerificationRequestDTO emailRequest,
            Authentication authentication) {
        EmailChangeVerificationResponseDTO response = new EmailChangeVerificationResponseDTO();
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.setSuccess(false);
                response.setMessage("Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String newEmail = emailRequest.getEmail();
            if (newEmail == null || newEmail.trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Basic email format validation
            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                response.setSuccess(false);
                response.setMessage("Invalid email format");
                return ResponseEntity.badRequest().body(response);
            }

            userService.sendEmailChangeVerification(newEmail.trim().toLowerCase(java.util.Locale.ROOT));

            response.setSuccess(true);
            response.setMessage("Verification code sent successfully");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to send verification email: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
