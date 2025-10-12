package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.enums.UserStatus;
import com.yushan.backend.service.AdminService;
import com.yushan.backend.exception.ValidationException;
import com.yushan.backend.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Validated
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Promote user to admin by email
     */
    @PostMapping("/promote-to-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserProfileResponseDTO> promoteToAdmin(
            @Valid @RequestBody AdminPromoteRequestDTO request,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        try {
            UserProfileResponseDTO userProfile = adminService.promoteToAdmin(request.getEmail());
            return ApiResponse.success("User promoted to admin successfully", userProfile);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        } catch (Exception e) {
            throw new ValidationException("Failed to promote user to admin: " + e.getMessage());
        }
    }

    /**
     * Get all users filtered by status, isAdmin, isAuthor, sortBy, sortOrder
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponseDTO<UserProfileResponseDTO>> getAllUsers (
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Boolean isAdmin,
            @RequestParam(required = false) Boolean isAuthor,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        AdminUserFilterDTO filter = new AdminUserFilterDTO(page, size, status, isAdmin, isAuthor, sortBy, sortOrder);
        PageResponseDTO<UserProfileResponseDTO> userPage = adminService.listUsers(filter);
        return ApiResponse.success("Users retrieved successfully", userPage);
    }

    /**
     * update a user's status
     */
    @PutMapping("/users/{uuid}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateUserStatus(
            @PathVariable UUID uuid,
            @Valid @RequestBody AdminUpdateUserDTO statusUpdateDTO) {
        adminService.updateUserStatus(uuid, statusUpdateDTO.getStatus());
        return ApiResponse.success("User status updated successfully");
    }
}
