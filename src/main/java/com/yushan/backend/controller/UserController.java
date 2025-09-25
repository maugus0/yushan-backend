package com.yushan.backend.controller;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
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
    public ResponseEntity<UserProfileResponseDTO> updateProfile(
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

        UserProfileResponseDTO updated = userService.updateUserProfileSelective(id, body);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}
