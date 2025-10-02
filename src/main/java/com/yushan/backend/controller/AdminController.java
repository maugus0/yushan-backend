package com.yushan.backend.controller;

import com.yushan.backend.dto.*;
import com.yushan.backend.service.AdminService;
import com.yushan.backend.exception.ValidationException;
import com.yushan.backend.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
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
}
