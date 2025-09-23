package com.yushan.backend.security;

import com.yushan.backend.entity.User;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SecurityExpressionRoot class
 * 
 * This test class verifies custom security expressions used in @PreAuthorize annotations
 */
@SpringBootTest
@ActiveProfiles("test")
public class SecurityExpressionRootTest {

    private SecurityExpressionRoot securityExpressionRoot;
    private User regularUser;
    private User authorUser;
    private User verifiedAuthorUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Create regular user
        regularUser = new User();
        regularUser.setUuid(UUID.randomUUID());
        regularUser.setEmail("regular@example.com");
        regularUser.setUsername("regular");
        regularUser.setIsAuthor(false);
        regularUser.setAuthorVerified(false);

        // Create author user
        authorUser = new User();
        authorUser.setUuid(UUID.randomUUID());
        authorUser.setEmail("author@example.com");
        authorUser.setUsername("author");
        authorUser.setIsAuthor(true);
        authorUser.setAuthorVerified(false);

        // Create verified author user
        verifiedAuthorUser = new User();
        verifiedAuthorUser.setUuid(UUID.randomUUID());
        verifiedAuthorUser.setEmail("verified@example.com");
        verifiedAuthorUser.setUsername("verified");
        verifiedAuthorUser.setIsAuthor(true);
        verifiedAuthorUser.setAuthorVerified(true);

        // Create admin user
        adminUser = new User();
        adminUser.setUuid(UUID.randomUUID());
        adminUser.setEmail("admin@example.com");
        adminUser.setUsername("admin");
        adminUser.setIsAuthor(false);
        adminUser.setAuthorVerified(false);
    }

    private Authentication createAuthentication(User user, String... roles) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Collection<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .collect(java.util.stream.Collectors.toList());
        
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    @Test
    void testIsAuthorWithAuthor() {
        Authentication auth = createAuthentication(authorUser, "ROLE_USER", "ROLE_AUTHOR");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.isAuthor(), "Author user should return true for isAuthor()");
    }

    @Test
    void testIsAuthorWithRegularUser() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.isAuthor(), "Regular user should return false for isAuthor()");
    }

    @Test
    void testIsAuthorWithNullAuthentication() {
        securityExpressionRoot = new SecurityExpressionRoot(null);
        
        assertFalse(securityExpressionRoot.isAuthor(), "Null authentication should return false for isAuthor()");
    }

    @Test
    void testIsVerifiedAuthorWithVerifiedAuthor() {
        Authentication auth = createAuthentication(verifiedAuthorUser, "ROLE_USER", "ROLE_AUTHOR");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.isVerifiedAuthor(), "Verified author should return true for isVerifiedAuthor()");
    }

    @Test
    void testIsVerifiedAuthorWithRegularAuthor() {
        Authentication auth = createAuthentication(authorUser, "ROLE_USER", "ROLE_AUTHOR");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.isVerifiedAuthor(), "Regular author should return false for isVerifiedAuthor()");
    }

    @Test
    void testIsVerifiedAuthorWithRegularUser() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.isVerifiedAuthor(), "Regular user should return false for isVerifiedAuthor()");
    }

    @Test
    void testIsAdminWithAdmin() {
        Authentication auth = createAuthentication(adminUser, "ROLE_USER", "ROLE_ADMIN");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.isAdmin(), "Admin user should return true for isAdmin()");
    }

    @Test
    void testIsAdminWithRegularUser() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.isAdmin(), "Regular user should return false for isAdmin()");
    }

    @Test
    void testIsAuthorOrAdminWithAuthor() {
        Authentication auth = createAuthentication(authorUser, "ROLE_USER", "ROLE_AUTHOR");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.isAuthorOrAdmin(), "Author should return true for isAuthorOrAdmin()");
    }

    @Test
    void testIsAuthorOrAdminWithAdmin() {
        Authentication auth = createAuthentication(adminUser, "ROLE_USER", "ROLE_ADMIN");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.isAuthorOrAdmin(), "Admin should return true for isAuthorOrAdmin()");
    }

    @Test
    void testIsAuthorOrAdminWithRegularUser() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.isAuthorOrAdmin(), "Regular user should return false for isAuthorOrAdmin()");
    }

    @Test
    void testIsOwnerWithCorrectUserId() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        String userId = regularUser.getUuid().toString();
        assertTrue(securityExpressionRoot.isOwner(userId), "User should be owner of their own resource");
    }

    @Test
    void testIsOwnerWithDifferentUserId() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        String differentUserId = UUID.randomUUID().toString();
        assertFalse(securityExpressionRoot.isOwner(differentUserId), "User should not be owner of different resource");
    }

    @Test
    void testIsOwnerWithNullUserId() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.isOwner(null), "Null userId should return false for isOwner()");
    }

    @Test
    void testCanAccessWithOwner() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        String resourceId = regularUser.getUuid().toString();
        assertTrue(securityExpressionRoot.canAccess(resourceId), "User should be able to access their own resource");
    }

    @Test
    void testCanAccessWithAuthor() {
        Authentication auth = createAuthentication(authorUser, "ROLE_USER", "ROLE_AUTHOR");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        String resourceId = regularUser.getUuid().toString(); // Different user's resource
        assertTrue(securityExpressionRoot.canAccess(resourceId), "Author should be able to access any resource");
    }

    @Test
    void testCanAccessWithAdmin() {
        Authentication auth = createAuthentication(adminUser, "ROLE_USER", "ROLE_ADMIN");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        String resourceId = regularUser.getUuid().toString(); // Different user's resource
        assertTrue(securityExpressionRoot.canAccess(resourceId), "Admin should be able to access any resource");
    }

    @Test
    void testCanAccessWithRegularUserAndDifferentResource() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        String resourceId = UUID.randomUUID().toString(); // Different user's resource
        assertFalse(securityExpressionRoot.canAccess(resourceId), "Regular user should not access different user's resource");
    }

    @Test
    void testCanAccessWithNullResourceId() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.canAccess(null), "Null resourceId should return false for canAccess()");
    }

    @Test
    void testIsAuthenticatedWithValidAuthentication() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.isAuthenticated(), "Valid authentication should return true for isAuthenticated()");
    }

    @Test
    void testIsAuthenticatedWithNullAuthentication() {
        securityExpressionRoot = new SecurityExpressionRoot(null);
        
        assertFalse(securityExpressionRoot.isAuthenticated(), "Null authentication should return false for isAuthenticated()");
    }

    @Test
    void testHasRoleWithCorrectRole() {
        Authentication auth = createAuthentication(adminUser, "ROLE_USER", "ROLE_ADMIN");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.hasRole("ADMIN"), "User with ADMIN role should return true for hasRole('ADMIN')");
    }

    @Test
    void testHasRoleWithIncorrectRole() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.hasRole("ADMIN"), "User without ADMIN role should return false for hasRole('ADMIN')");
    }

    @Test
    void testHasAnyRoleWithMatchingRole() {
        Authentication auth = createAuthentication(authorUser, "ROLE_USER", "ROLE_AUTHOR");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.hasAnyRole("AUTHOR", "ADMIN"), "User with AUTHOR role should return true for hasAnyRole('AUTHOR', 'ADMIN')");
    }

    @Test
    void testHasAnyRoleWithNoMatchingRole() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.hasAnyRole("AUTHOR", "ADMIN"), "User without AUTHOR or ADMIN role should return false for hasAnyRole('AUTHOR', 'ADMIN')");
    }

    @Test
    void testHasAuthorityWithCorrectAuthority() {
        Authentication auth = createAuthentication(adminUser, "ROLE_USER", "ROLE_ADMIN", "READ", "WRITE");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertTrue(securityExpressionRoot.hasAuthority("READ"), "User with READ authority should return true for hasAuthority('READ')");
    }

    @Test
    void testHasAuthorityWithIncorrectAuthority() {
        Authentication auth = createAuthentication(regularUser, "ROLE_USER");
        securityExpressionRoot = new SecurityExpressionRoot(auth);
        
        assertFalse(securityExpressionRoot.hasAuthority("DELETE"), "User without DELETE authority should return false for hasAuthority('DELETE')");
    }
}
