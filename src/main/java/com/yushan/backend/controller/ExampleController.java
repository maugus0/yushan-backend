package com.yushan.backend.controller;

import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public endpoint");
        response.put("access", "No authentication required");
        return ResponseEntity.ok(response);
    }

    /**
     * Protected endpoint - requires authentication
     */
    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a protected endpoint");
        response.put("access", "Authentication required");
        
        if (authentication != null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            response.put("user", userDetails.getUser().getUsername());
            response.put("isAuthor", userDetails.isAuthor());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Author-only endpoint - requires author role
     */
    @GetMapping("/author-only")
    @PreAuthorize("isAuthor()")
    public ResponseEntity<Map<String, Object>> authorOnlyEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is an author-only endpoint");
        response.put("access", "Author role required");
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        response.put("user", userDetails.getUser().getUsername());
        response.put("isAuthor", userDetails.isAuthor());
        response.put("isVerifiedAuthor", userDetails.isVerifiedAuthor());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verified author-only endpoint - requires verified author role
     */
    @GetMapping("/verified-author-only")
    @PreAuthorize("isVerifiedAuthor()")
    public ResponseEntity<Map<String, Object>> verifiedAuthorOnlyEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a verified author-only endpoint");
        response.put("access", "Verified author role required");
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        response.put("user", userDetails.getUser().getUsername());
        response.put("isAuthor", userDetails.isAuthor());
        response.put("isVerifiedAuthor", userDetails.isVerifiedAuthor());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Admin-only endpoint - requires admin role
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminOnlyEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is an admin-only endpoint");
        response.put("access", "Admin role required");
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        response.put("user", userDetails.getUser().getUsername());
        response.put("isAdmin", userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Author or admin endpoint - requires author or admin role
     */
    @GetMapping("/author-or-admin")
    @PreAuthorize("isAuthorOrAdmin()")
    public ResponseEntity<Map<String, Object>> authorOrAdminEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint allows authors or admins");
        response.put("access", "Author or admin role required");
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        response.put("user", userDetails.getUser().getUsername());
        response.put("isAuthor", userDetails.isAuthor());
        response.put("isAdmin", userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Resource ownership endpoint - requires ownership or author/admin role
     */
    @GetMapping("/resource")
    @PreAuthorize("canAccess(#resourceId)")
    public ResponseEntity<Map<String, Object>> resourceOwnershipEndpoint(
            @RequestParam String resourceId,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint checks resource ownership");
        response.put("resourceId", resourceId);
        response.put("access", "Resource owner, author, or admin required");
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        response.put("user", userDetails.getUser().getUsername());
        response.put("userId", userDetails.getUserId());
        response.put("isOwner", userDetails.getUserId().equals(resourceId));
        response.put("isAuthor", userDetails.isAuthor());
        response.put("isAdmin", userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Complex authorization endpoint - multiple conditions
     */
    @GetMapping("/complex")
    @PreAuthorize("isAuthenticated() and (isAuthor() or hasRole('ADMIN') or isOwner(#userId))")
    public ResponseEntity<Map<String, Object>> complexEndpoint(
            @RequestParam String userId,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This endpoint has complex authorization rules");
        response.put("userId", userId);
        response.put("access", "Authenticated user who is author, admin, or owner");
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        response.put("user", userDetails.getUser().getUsername());
        response.put("currentUserId", userDetails.getUserId());
        response.put("isOwner", userDetails.getUserId().equals(userId));
        response.put("isAuthor", userDetails.isAuthor());
        response.put("isAdmin", userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        return ResponseEntity.ok(response);
    }
}
