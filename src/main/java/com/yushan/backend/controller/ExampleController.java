package com.yushan.backend.controller;

import com.yushan.backend.dto.ExampleResponseDTO;
import com.yushan.backend.dto.ApiResponse;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Example Controller demonstrating role-based authorization
 * 
 * This controller shows how to use @PreAuthorize annotations
 * with custom security expressions for different user roles
 */
@Slf4j
@RestController
@RequestMapping("/api/example")
@CrossOrigin(origins = "*")
public class ExampleController {

    /**
     * Public endpoint - no authentication required
     */
    @GetMapping("/public")
    public ApiResponse<ExampleResponseDTO> publicEndpoint() {
        ExampleResponseDTO response = new ExampleResponseDTO(
            "This is a public endpoint", 
            "No authentication required"
        );
        return ApiResponse.success("Public endpoint accessed successfully", response);
    }

    /**
     * Protected endpoint - requires authentication
     */
    @GetMapping("/protected")
    public ApiResponse<ExampleResponseDTO> protectedEndpoint(Authentication authentication) {
        ExampleResponseDTO response = new ExampleResponseDTO(
            "This is a protected endpoint", 
            "Authentication required"
        );
        
        if (authentication != null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            response.setUser(userDetails.getProfileUsername());
            response.setIsAuthor(userDetails.isAuthor());
        }
        
        return ApiResponse.success("Protected endpoint accessed successfully", response);
    }

    /**
     * Author-only endpoint - requires author role
     */
    @GetMapping("/author-only")
    @PreAuthorize("isAuthor()")
    public ApiResponse<ExampleResponseDTO> authorOnlyEndpoint(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ExampleResponseDTO response = new ExampleResponseDTO(
            "This is an author-only endpoint", 
            "Author role required",
            userDetails.getProfileUsername(),
            userDetails.isAuthor()
        );
        
        return ApiResponse.success("Author-only endpoint accessed successfully", response);
    }


    /**
     * Admin-only endpoint - requires admin role
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ExampleResponseDTO> adminOnlyEndpoint(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ExampleResponseDTO response = new ExampleResponseDTO(
            "This is an admin-only endpoint", 
            "Admin role required",
            userDetails.getProfileUsername(),
            userDetails.isAuthor()
        );
        response.setIsAdmin(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        return ApiResponse.success("Admin-only endpoint accessed successfully", response);
    }

    /**
     * Author or admin endpoint - requires author or admin role
     */
    @GetMapping("/author-or-admin")
    @PreAuthorize("isAuthorOrAdmin()")
    public ApiResponse<ExampleResponseDTO> authorOrAdminEndpoint(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ExampleResponseDTO response = new ExampleResponseDTO(
            "This endpoint allows authors or admins", 
            "Author or admin role required",
            userDetails.getProfileUsername(),
            userDetails.isAuthor()
        );
        response.setIsAdmin(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        return ApiResponse.success("Author or admin endpoint accessed successfully", response);
    }

    /**
     * Resource ownership endpoint - requires ownership or author/admin role
     */
    @GetMapping("/resource")
    @PreAuthorize("canAccess(#resourceId)")
    public ApiResponse<ExampleResponseDTO> resourceOwnershipEndpoint(
            @RequestParam String resourceId,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ExampleResponseDTO response = new ExampleResponseDTO(
            "This endpoint checks resource ownership", 
            "Resource owner, author, or admin required",
            userDetails.getProfileUsername(),
            userDetails.isAuthor()
        );
        response.setResourceId(resourceId);
        response.setUserId(userDetails.getUserId());
        response.setIsOwner(userDetails.getUserId().equals(resourceId));
        response.setIsAdmin(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        return ApiResponse.success("Resource ownership endpoint accessed successfully", response);
    }

    /**
     * Complex authorization endpoint - multiple conditions
     */
    @GetMapping("/complex")
    @PreAuthorize("isAuthenticated() and (isAuthor() or hasRole('ADMIN') or isOwner(#userId))")
    public ApiResponse<ExampleResponseDTO> complexEndpoint(
            @RequestParam String userId,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ExampleResponseDTO response = new ExampleResponseDTO(
            "This endpoint has complex authorization rules", 
            "Authenticated user who is author, admin, or owner",
            userDetails.getProfileUsername(),
            userDetails.isAuthor()
        );
        response.setUserId(userId);
        response.setCurrentUserId(userDetails.getUserId());
        response.setIsOwner(userDetails.getUserId().equals(userId));
        response.setIsAdmin(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        return ApiResponse.success("Complex authorization endpoint accessed successfully", response);
    }
}
