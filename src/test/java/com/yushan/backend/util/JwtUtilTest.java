package com.yushan.backend.util;

import com.yushan.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtUtil class
 * 
 * This test class verifies JWT token generation, validation, and claim extraction
 */
@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUuid(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setIsAuthor(true);
    }

    @Test
    void testGenerateAccessToken() {
        // Test access token generation
        String accessToken = jwtUtil.generateAccessToken(testUser);
        
        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());
        
        // Verify token structure (should have 3 parts separated by dots)
        String[] parts = accessToken.split("\\.");
        assertEquals(3, parts.length, "JWT token should have 3 parts");
    }

    @Test
    void testGenerateRefreshToken() {
        // Test refresh token generation
        String refreshToken = jwtUtil.generateRefreshToken(testUser);
        
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        
        // Verify token structure
        String[] parts = refreshToken.split("\\.");
        assertEquals(3, parts.length, "JWT token should have 3 parts");
    }

    @Test
    void testAccessAndRefreshTokensAreDifferent() {
        String accessToken = jwtUtil.generateAccessToken(testUser);
        String refreshToken = jwtUtil.generateRefreshToken(testUser);
        
        assertNotEquals(accessToken, refreshToken, "Access and refresh tokens should be different");
    }

    @Test
    void testValidateTokenWithValidToken() {
        String token = jwtUtil.generateAccessToken(testUser);
        
        assertTrue(jwtUtil.validateToken(token), "Valid token should pass validation");
        assertTrue(jwtUtil.validateToken(token, testUser), "Valid token with correct user should pass validation");
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertFalse(jwtUtil.validateToken(invalidToken), "Invalid token should fail validation");
    }

    @Test
    void testValidateTokenWithWrongUser() {
        String token = jwtUtil.generateAccessToken(testUser);
        
        User differentUser = new User();
        differentUser.setUuid(UUID.randomUUID());
        differentUser.setEmail("different@example.com");
        
        assertFalse(jwtUtil.validateToken(token, differentUser), "Token with wrong user should fail validation");
    }

    @Test
    void testExtractEmail() {
        String token = jwtUtil.generateAccessToken(testUser);
        String email = jwtUtil.extractEmail(token);
        
        assertEquals(testUser.getEmail(), email, "Extracted email should match user email");
    }

    @Test
    void testExtractUserId() {
        String token = jwtUtil.generateAccessToken(testUser);
        String userId = jwtUtil.extractUserId(token);
        
        assertEquals(testUser.getUuid().toString(), userId, "Extracted userId should match user UUID");
    }

    @Test
    void testExtractTokenType() {
        String accessToken = jwtUtil.generateAccessToken(testUser);
        String refreshToken = jwtUtil.generateRefreshToken(testUser);
        
        String accessTokenType = jwtUtil.extractTokenType(accessToken);
        String refreshTokenType = jwtUtil.extractTokenType(refreshToken);
        
        assertEquals("access", accessTokenType, "Access token type should be 'access'");
        assertEquals("refresh", refreshTokenType, "Refresh token type should be 'refresh'");
    }

    @Test
    void testTokenExpiration() {
        String token = jwtUtil.generateAccessToken(testUser);
        
        // Token should be valid immediately after generation
        assertTrue(jwtUtil.validateToken(token), "Token should be valid immediately after generation");
        
        // Note: Testing actual expiration would require waiting for token to expire
        // or mocking the time, which is more complex and not necessary for basic functionality
    }

    @Test
    void testTokenWithNullUser() {
        assertThrows(Exception.class, () -> {
            jwtUtil.generateAccessToken(null);
        }, "Generating token with null user should throw exception");
    }

    @Test
    void testExtractClaimsFromInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThrows(Exception.class, () -> {
            jwtUtil.extractEmail(invalidToken);
        }, "Extracting claims from invalid token should throw exception");
    }

    @Test
    void testMultipleTokenGeneration() {
        // Test that multiple tokens can be generated for the same user
        String token1 = jwtUtil.generateAccessToken(testUser);
        String token2 = jwtUtil.generateAccessToken(testUser);
        
        assertNotEquals(token1, token2, "Multiple tokens for same user should be different");
        assertTrue(jwtUtil.validateToken(token1), "First token should be valid");
        assertTrue(jwtUtil.validateToken(token2), "Second token should be valid");
    }
}
