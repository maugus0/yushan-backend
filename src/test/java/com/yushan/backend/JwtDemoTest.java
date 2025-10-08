package com.yushan.backend;

import com.yushan.backend.entity.User;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple demo test to verify JWT functionality
 * 
 * This test demonstrates basic JWT operations and can be run to verify
 * that the JWT implementation is working correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
public class JwtDemoTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testJwtBasicFunctionality() {
        // Create a test user
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("demo@example.com");
        user.setUsername("demo_user");
        user.setEmailVerified(true);
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setGender(1);
        user.setLastLogin(new Date());
        user.setLastActive(new Date());
        user.setIsAuthor(true);

        // Test token generation
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        assertNotNull(accessToken, "Access token should not be null");
        assertNotNull(refreshToken, "Refresh token should not be null");
        assertNotEquals(accessToken, refreshToken, "Access and refresh tokens should be different");

        // Test token validation
        assertTrue(jwtUtil.validateToken(accessToken), "Access token should be valid");
        assertTrue(jwtUtil.validateToken(refreshToken), "Refresh token should be valid");
        assertTrue(jwtUtil.validateToken(accessToken, user), "Access token should be valid for the user");

        // Test claim extraction
        String extractedEmail = jwtUtil.extractEmail(accessToken);
        String extractedUserId = jwtUtil.extractUserId(accessToken);
        String extractedTokenType = jwtUtil.extractTokenType(accessToken);

        assertEquals(user.getEmail(), extractedEmail, "Extracted email should match user email");
        assertEquals(user.getUuid().toString(), extractedUserId, "Extracted userId should match user UUID");
        assertEquals("access", extractedTokenType, "Access token type should be 'access'");

        // Test refresh token type
        String refreshTokenType = jwtUtil.extractTokenType(refreshToken);
        assertEquals("refresh", refreshTokenType, "Refresh token type should be 'refresh'");
    }

    @Test
    void testJwtTokenStructure() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("structure@example.com");
        user.setUsername("structure_user");
        user.setEmailVerified(true);
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setGender(1);
        user.setLastLogin(new Date());
        user.setLastActive(new Date());
        user.setIsAuthor(false);

        String token = jwtUtil.generateAccessToken(user);

        // JWT tokens should have 3 parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token should have exactly 3 parts (header.payload.signature)");

        // Each part should not be empty
        for (int i = 0; i < parts.length; i++) {
            assertFalse(parts[i].isEmpty(), "JWT token part " + (i + 1) + " should not be empty");
        }
    }

    @Test
    void testJwtWithDifferentUsers() {
        // Create two different users
        User user1 = new User();
        user1.setUuid(UUID.randomUUID());
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");
        user1.setEmailVerified(true);
        user1.setAvatarUrl("https://example.com/avatar1.jpg");
        user1.setGender(1);
        user1.setLastLogin(new Date());
        user1.setLastActive(new Date());
        user1.setIsAuthor(true);

        User user2 = new User();
        user2.setUuid(UUID.randomUUID());
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        user2.setEmailVerified(true);
        user2.setAvatarUrl("https://example.com/avatar2.jpg");
        user2.setGender(1);
        user2.setLastLogin(new Date());
        user2.setLastActive(new Date());
        user2.setIsAuthor(false);

        // Generate tokens for both users
        String token1 = jwtUtil.generateAccessToken(user1);
        String token2 = jwtUtil.generateAccessToken(user2);

        // Tokens should be different
        assertNotEquals(token1, token2, "Tokens for different users should be different");

        // Each token should be valid for its respective user
        assertTrue(jwtUtil.validateToken(token1, user1), "Token1 should be valid for user1");
        assertTrue(jwtUtil.validateToken(token2, user2), "Token2 should be valid for user2");

        // Each token should NOT be valid for the other user
        assertFalse(jwtUtil.validateToken(token1, user2), "Token1 should not be valid for user2");
        assertFalse(jwtUtil.validateToken(token2, user1), "Token2 should not be valid for user1");

        // Extract and verify claims
        assertEquals("user1@example.com", jwtUtil.extractEmail(token1), "Token1 should contain user1's email");
        assertEquals("user2@example.com", jwtUtil.extractEmail(token2), "Token2 should contain user2's email");
    }

    @Test
    void testIsAdminFieldInJwtTokens() {
        // Create admin user
        User adminUser = new User();
        adminUser.setUuid(UUID.randomUUID());
        adminUser.setEmail("admin@example.com");
        adminUser.setUsername("AdminUser");
        adminUser.setHashPassword("hashedpassword");
        adminUser.setEmailVerified(true);
        adminUser.setAvatarUrl("https://example.com/admin-avatar.jpg");
        adminUser.setGender(1);
        adminUser.setLastLogin(new Date());
        adminUser.setLastActive(new Date());
        adminUser.setStatus(1);
        adminUser.setIsAuthor(true);
        adminUser.setIsAdmin(true);  // Admin user
        adminUser.setLevel(5);
        adminUser.setExp(100.0f);
        adminUser.setReadTime(50.0f);
        adminUser.setReadBookNum(10);

        // Create normal user
        User normalUser = new User();
        normalUser.setUuid(UUID.randomUUID());
        normalUser.setEmail("normal@example.com");
        normalUser.setUsername("NormalUser");
        normalUser.setHashPassword("hashedpassword");
        normalUser.setEmailVerified(true);
        normalUser.setAvatarUrl("https://example.com/normal-avatar.jpg");
        normalUser.setGender(1);
        normalUser.setLastLogin(new Date());
        normalUser.setLastActive(new Date());
        normalUser.setStatus(1);
        normalUser.setIsAuthor(false);
        normalUser.setIsAdmin(false);  // Normal user
        normalUser.setLevel(1);
        normalUser.setExp(0.0f);
        normalUser.setReadTime(0.0f);
        normalUser.setReadBookNum(0);

        // Generate tokens
        String adminAccessToken = jwtUtil.generateAccessToken(adminUser);
        String adminRefreshToken = jwtUtil.generateRefreshToken(adminUser);
        String normalAccessToken = jwtUtil.generateAccessToken(normalUser);
        String normalRefreshToken = jwtUtil.generateRefreshToken(normalUser);

        // Test admin tokens
        assertTrue(jwtUtil.validateToken(adminAccessToken), "Admin access token should be valid");
        assertTrue(jwtUtil.validateToken(adminRefreshToken), "Admin refresh token should be valid");
        assertTrue(jwtUtil.isAccessToken(adminAccessToken), "Admin access token should be access token");
        assertTrue(jwtUtil.isRefreshToken(adminRefreshToken), "Admin refresh token should be refresh token");

        // Test normal user tokens
        assertTrue(jwtUtil.validateToken(normalAccessToken), "Normal access token should be valid");
        assertTrue(jwtUtil.validateToken(normalRefreshToken), "Normal refresh token should be valid");
        assertTrue(jwtUtil.isAccessToken(normalAccessToken), "Normal access token should be access token");
        assertTrue(jwtUtil.isRefreshToken(normalRefreshToken), "Normal refresh token should be refresh token");

        // Test token validation with users
        assertTrue(jwtUtil.validateToken(adminAccessToken, adminUser), "Admin access token should be valid for admin user");
        assertTrue(jwtUtil.validateToken(adminRefreshToken, adminUser), "Admin refresh token should be valid for admin user");
        assertTrue(jwtUtil.validateToken(normalAccessToken, normalUser), "Normal access token should be valid for normal user");
        assertTrue(jwtUtil.validateToken(normalRefreshToken, normalUser), "Normal refresh token should be valid for normal user");

        // Test cross-validation (should fail)
        assertFalse(jwtUtil.validateToken(adminAccessToken, normalUser), "Admin token should not be valid for normal user");
        assertFalse(jwtUtil.validateToken(normalAccessToken, adminUser), "Normal token should not be valid for admin user");

        // Extract and verify email claims
        assertEquals("admin@example.com", jwtUtil.extractEmail(adminAccessToken), "Admin access token should contain admin email");
        assertEquals("admin@example.com", jwtUtil.extractEmail(adminRefreshToken), "Admin refresh token should contain admin email");
        assertEquals("normal@example.com", jwtUtil.extractEmail(normalAccessToken), "Normal access token should contain normal email");
        assertEquals("normal@example.com", jwtUtil.extractEmail(normalRefreshToken), "Normal refresh token should contain normal email");

        // Extract and verify user ID claims
        assertEquals(adminUser.getUuid().toString(), jwtUtil.extractUserId(adminAccessToken), "Admin access token should contain admin user ID");
        assertEquals(adminUser.getUuid().toString(), jwtUtil.extractUserId(adminRefreshToken), "Admin refresh token should contain admin user ID");
        assertEquals(normalUser.getUuid().toString(), jwtUtil.extractUserId(normalAccessToken), "Normal access token should contain normal user ID");
        assertEquals(normalUser.getUuid().toString(), jwtUtil.extractUserId(normalRefreshToken), "Normal refresh token should contain normal user ID");

        System.out.println("✅ Admin user tokens generated successfully");
        System.out.println("✅ Normal user tokens generated successfully");
        System.out.println("✅ All token validations passed");
        System.out.println("✅ isAdmin field integration test completed");
    }
}
