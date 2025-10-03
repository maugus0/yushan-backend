package com.yushan.backend.security;

import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Custom Security Expression Root for method-level security
 * 
 * This class provides custom expressions for authorization checks
 * that can be used with @PreAuthorize and @PostAuthorize annotations
 */
public class SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final Authentication authentication;
    private Object filterObject;
    private Object returnObject;
    private Object target;

    public SecurityExpressionRoot(Authentication authentication) {
        this.authentication = copyAuthentication(authentication);
    }

    @Override
    public Authentication getAuthentication() {
        return copyAuthentication(authentication);
    }

    private static Authentication copyAuthentication(Authentication source) {
        if (source == null) {
            return null;
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            source.getPrincipal(),
            null,
            source.getAuthorities()
        );
        token.setDetails(source.getDetails());
        return token;
    }

    @Override
    public boolean hasAuthority(String authority) {
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals(authority));
    }

    @Override
    public boolean hasAnyAuthority(String... authorities) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> java.util.Arrays.asList(authorities).contains(auth.getAuthority()));
    }

    @Override
    public boolean hasRole(String role) {
        return hasAuthority("ROLE_" + role);
    }

    @Override
    public boolean hasAnyRole(String... roles) {
        String[] roleAuthorities = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleAuthorities[i] = "ROLE_" + roles[i];
        }
        return hasAnyAuthority(roleAuthorities);
    }

    @Override
    public boolean permitAll() {
        return true;
    }

    @Override
    public boolean denyAll() {
        return false;
    }

    @Override
    public boolean isAuthenticated() {
        return authentication != null && authentication.isAuthenticated();
    }

    @Override
    public boolean isAnonymous() {
        return authentication == null || !authentication.isAuthenticated();
    }

    @Override
    public boolean isRememberMe() {
        return authentication != null && authentication.isAuthenticated() && 
               !(authentication instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken);
    }

    @Override
    public boolean isFullyAuthenticated() {
        return authentication != null && authentication.isAuthenticated() && 
               !(authentication instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken);
    }

    @Override
    public boolean hasPermission(Object target, Object permission) {
        // Simple implementation - can be enhanced
        return isAuthenticated();
    }

    @Override
    public boolean hasPermission(Object targetId, String targetType, Object permission) {
        // Simple implementation - can be enhanced
        return isAuthenticated();
    }

    /**
     * Check if current user is an author
     * 
     * @return true if user is an author, false otherwise
     */
    public boolean isAuthor() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.isAuthor();
        }
        
        return false;
    }


    /**
     * Check if current user is admin
     * 
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if current user owns the resource
     * 
     * @param resourceOwnerId The ID of the resource owner
     * @return true if current user owns the resource, false otherwise
     */
    public boolean isOwner(String resourceOwnerId) {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getUserId().equals(resourceOwnerId);
        }
        
        return false;
    }

    /**
     * Check if current user is author or admin
     * 
     * @return true if user is author or admin, false otherwise
     */
    public boolean isAuthorOrAdmin() {
        return isAuthor() || isAdmin();
    }


    /**
     * Check if current user can access the resource
     * (either owner, author, or admin)
     * 
     * @param resourceId The ID of the resource
     * @return true if user can access the resource, false otherwise
     */
    public boolean canAccess(String resourceId) {
        return isOwner(resourceId) || isAuthorOrAdmin();
    }

    /**
     * Get current user details
     * 
     * @return CustomUserDetails of current user, null if not authenticated
     */
    public CustomUserDetails getCurrentUser() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        
        return null;
    }

    /**
     * Get current user ID
     * 
     * @return User ID of current user, null if not authenticated
     */
    public String getCurrentUserId() {
        CustomUserDetails user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    // MethodSecurityExpressionOperations implementation
    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    public void setThis(Object target) {
        this.target = target;
    }

    @Override
    public Object getThis() {
        return target;
    }
}
