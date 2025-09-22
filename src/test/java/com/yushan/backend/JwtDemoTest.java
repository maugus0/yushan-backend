package com.yushan.backend;

import com.yushan.backend.entity.User;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
        user.setIsAuthor(true);
        user.setAuthorVerified(false);

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
        String extractedUsername = jwtUtil.extractUsername(accessToken);
        String extractedUserId = jwtUtil.extractUserId(accessToken);
        Boolean extractedIsAuthor = jwtUtil.extractIsAuthor(accessToken);
        String extractedTokenType = jwtUtil.extractTokenType(accessToken);

        assertEquals(user.getEmail(), extractedEmail, "Extracted email should match user email");
        assertEquals(user.getEmail(), extractedUsername, "Extracted username should match user email");
        assertEquals(user.getUuid().toString(), extractedUserId, "Extracted userId should match user UUID");
        assertEquals(user.getIsAuthor(), extractedIsAuthor, "Extracted isAuthor should match user isAuthor");
        assertEquals("access", extractedTokenType, "Access token type should be 'access'");

        // Test refresh token type
        String refreshTokenType = jwtUtil.extractTokenType(refreshToken);
        assertEquals("refresh", refreshTokenType, "Refresh token type should be 'refresh'");

        System.out.println("✅ JWT Basic Functionality Test Passed!");
        System.out.println("   - Token generation: ✓");
        System.out.println("   - Token validation: ✓");
        System.out.println("   - Claim extraction: ✓");
        System.out.println("   - Token types: ✓");
    }

    @Test
    void testJwtTokenStructure() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("structure@example.com");
        user.setUsername("structure_user");
        user.setIsAuthor(false);
        user.setAuthorVerified(false);

        String token = jwtUtil.generateAccessToken(user);

        // JWT tokens should have 3 parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token should have exactly 3 parts (header.payload.signature)");

        // Each part should not be empty
        for (int i = 0; i < parts.length; i++) {
            assertFalse(parts[i].isEmpty(), "JWT token part " + (i + 1) + " should not be empty");
        }

        System.out.println("✅ JWT Token Structure Test Passed!");
        System.out.println("   - Token parts: " + parts.length);
        System.out.println("   - Header: " + parts[0].substring(0, Math.min(20, parts[0].length())) + "...");
        System.out.println("   - Payload: " + parts[1].substring(0, Math.min(20, parts[1].length())) + "...");
        System.out.println("   - Signature: " + parts[2].substring(0, Math.min(20, parts[2].length())) + "...");
    }

    @Test
    void testJwtWithDifferentUsers() {
        // Create two different users
        User user1 = new User();
        user1.setUuid(UUID.randomUUID());
        user1.setEmail("user1@example.com");
        user1.setUsername("user1");
        user1.setIsAuthor(true);
        user1.setAuthorVerified(true);

        User user2 = new User();
        user2.setUuid(UUID.randomUUID());
        user2.setEmail("user2@example.com");
        user2.setUsername("user2");
        user2.setIsAuthor(false);
        user2.setAuthorVerified(false);

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
        assertEquals(true, jwtUtil.extractIsAuthor(token1), "Token1 should indicate user1 is author");
        assertEquals(false, jwtUtil.extractIsAuthor(token2), "Token2 should indicate user2 is not author");

        System.out.println("✅ JWT Different Users Test Passed!");
        System.out.println("   - User1 (Author): " + jwtUtil.extractEmail(token1) + " - isAuthor: " + jwtUtil.extractIsAuthor(token1));
        System.out.println("   - User2 (Regular): " + jwtUtil.extractEmail(token2) + " - isAuthor: " + jwtUtil.extractIsAuthor(token2));
    }
}
