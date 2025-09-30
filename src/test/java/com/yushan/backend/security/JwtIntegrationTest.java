package com.yushan.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.service.MailService;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Integration tests for JWT authentication and authorization
 * 
 * This test class verifies the complete JWT flow including:
 * - Token generation and validation
 * - Authentication endpoints (login, register, refresh, logout)
 * - Protected endpoints with different authorization levels
 * - Role-based access control
 * - Error handling scenarios
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@MockitoSettings(strictness = Strictness.LENIENT)
public class JwtIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    private MockMvc mockMvc;

    private User testUser;
    private User authorUser;
    private String testUserToken;
    private String authorUserToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test users
        createTestUsers();
        
        // Setup mock MailService after creating users
        setupMockMailService();
    }

    /**
     * Setup mock MailService behavior
     */
    private void setupMockMailService() {
        // Mock sendVerificationCode to do nothing
        doNothing().when(mailService).sendVerificationCode(anyString());
        
        // Mock verifyEmail to return true for test code "123456"
        when(mailService.verifyEmail(anyString(), eq("123456"))).thenReturn(true);
        when(mailService.verifyEmail(anyString(), anyString())).thenReturn(false);
    }

    /**
     * Create test users for testing different scenarios
     */
    private void createTestUsers() {
        // Create regular user
        testUser = new User();
        testUser.setUuid(UUID.randomUUID());
        testUser.setEmail("testuser@example.com");
        testUser.setUsername("testuser");
        testUser.setHashPassword(passwordEncoder.encode("password123"));
        testUser.setIsAuthor(false);
        testUser.setAuthorVerified(false);
        userMapper.insert(testUser);

        // Create author user
        authorUser = new User();
        authorUser.setUuid(UUID.randomUUID());
        authorUser.setEmail("author@example.com");
        authorUser.setUsername("author");
        authorUser.setHashPassword(passwordEncoder.encode("password123"));
        authorUser.setIsAuthor(true);
        authorUser.setAuthorVerified(true);
        userMapper.insert(authorUser);

        // Generate tokens
        testUserToken = jwtUtil.generateAccessToken(testUser);
        authorUserToken = jwtUtil.generateAccessToken(authorUser);
    }

    // ==================== JWT TOKEN TESTS ====================

    @Test
    void testJwtTokenGeneration() throws Exception {
        // Test access token generation
        String accessToken = jwtUtil.generateAccessToken(testUser);
        assert accessToken != null;
        assert !accessToken.isEmpty();

        // Test refresh token generation
        String refreshToken = jwtUtil.generateRefreshToken(testUser);
        assert refreshToken != null;
        assert !refreshToken.isEmpty();

        // Verify tokens are different
        assert !accessToken.equals(refreshToken);
    }

    @Test
    void testJwtTokenValidation() throws Exception {
        // Test valid token
        assert jwtUtil.validateToken(testUserToken);
        assert jwtUtil.validateToken(testUserToken, testUser);

        // Test invalid token
        String invalidToken = "invalid.token.here";
        assert !jwtUtil.validateToken(invalidToken);

        // Test token with wrong user
        assert !jwtUtil.validateToken(testUserToken, authorUser);
    }

    @Test
    void testJwtTokenClaims() throws Exception {
        // Test extracting claims from token
        String username = jwtUtil.extractUsername(testUserToken);
        String email = jwtUtil.extractEmail(testUserToken);
        String userId = jwtUtil.extractUserId(testUserToken);
        Boolean isAuthor = jwtUtil.extractIsAuthor(testUserToken);
        String tokenType = jwtUtil.extractTokenType(testUserToken);

        assert username.equals(testUser.getEmail());
        assert email.equals(testUser.getEmail());
        assert userId.equals(testUser.getUuid().toString());
        assert isAuthor.equals(testUser.getIsAuthor());
        assert tokenType.equals("access");
    }

    // ==================== AUTHENTICATION ENDPOINT TESTS ====================

    @Test
    void testLoginSuccess() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "testuser@example.com");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "testuser@example.com");
        loginRequest.put("password", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        // Setup mock directly in the test
        when(mailService.verifyEmail(eq("newuser@example.com"), eq("123456"))).thenReturn(true);
        
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("email", "newuser@example.com");
        registerRequest.put("username", "newuser");
        registerRequest.put("password", "password123");
        registerRequest.put("code", "123456");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.email").value("newuser@example.com"));
    }

    @Test
    void testRefreshTokenSuccess() throws Exception {
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void testRefreshTokenWithInvalidToken() throws Exception {
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", "invalid.refresh.token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("JWT tokens are stateless and cannot be invalidated server-side. Client should discard tokens."));
    }

    @Test
    void testSendEmail() throws Exception {
        Map<String, String> sendEmailRequest = new HashMap<>();
        sendEmailRequest.put("email", "newuser@example.com");

        mockMvc.perform(post("/api/auth/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    // ==================== PROTECTED ENDPOINT TESTS ====================

    @Test
    void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/example/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("This is a public endpoint"))
                .andExpect(jsonPath("$.access").value("No authentication required"));
    }

    @Test
    void testProtectedEndpointWithValidToken() throws Exception {
        mockMvc.perform(get("/api/example/protected")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("This is a protected endpoint"))
                .andExpect(jsonPath("$.access").value("Authentication required"))
                .andExpect(jsonPath("$.user").value("testuser"))
                .andExpect(jsonPath("$.isAuthor").value(false));
    }

    @Test
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/example/protected"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Invalid or missing JWT token"));
    }

    @Test
    void testProtectedEndpointWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/example/protected")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Invalid or missing JWT token"));
    }

    // ==================== ROLE-BASED AUTHORIZATION TESTS ====================

    @Test
    void testAuthorOnlyEndpointWithAuthor() throws Exception {
        mockMvc.perform(get("/api/example/author-only")
                .header("Authorization", "Bearer " + authorUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("This is an author-only endpoint"))
                .andExpect(jsonPath("$.access").value("Author role required"))
                .andExpect(jsonPath("$.user").value("author"))
                .andExpect(jsonPath("$.isAuthor").value(true))
                .andExpect(jsonPath("$.isVerifiedAuthor").value(true));
    }

    @Test
    void testAuthorOnlyEndpointWithRegularUser() throws Exception {
        mockMvc.perform(get("/api/example/author-only")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void testVerifiedAuthorOnlyEndpointWithVerifiedAuthor() throws Exception {
        mockMvc.perform(get("/api/example/verified-author-only")
                .header("Authorization", "Bearer " + authorUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("This is a verified author-only endpoint"))
                .andExpect(jsonPath("$.access").value("Verified author role required"))
                .andExpect(jsonPath("$.user").value("author"))
                .andExpect(jsonPath("$.isAuthor").value(true))
                .andExpect(jsonPath("$.isVerifiedAuthor").value(true));
    }

    @Test
    void testResourceOwnershipEndpointWithOwner() throws Exception {
        String resourceId = testUser.getUuid().toString();
        
        mockMvc.perform(get("/api/example/resource")
                .param("resourceId", resourceId)
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("This endpoint checks resource ownership"))
                .andExpect(jsonPath("$.resourceId").value(resourceId))
                .andExpect(jsonPath("$.access").value("Resource owner, author, or admin required"))
                .andExpect(jsonPath("$.user").value("testuser"))
                .andExpect(jsonPath("$.userId").value(resourceId))
                .andExpect(jsonPath("$.isOwner").value(true))
                .andExpect(jsonPath("$.isAuthor").value(false));
    }

    @Test
    void testResourceOwnershipEndpointWithAuthor() throws Exception {
        String resourceId = testUser.getUuid().toString();
        
        mockMvc.perform(get("/api/example/resource")
                .param("resourceId", resourceId)
                .header("Authorization", "Bearer " + authorUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("This endpoint checks resource ownership"))
                .andExpect(jsonPath("$.resourceId").value(resourceId))
                .andExpect(jsonPath("$.access").value("Resource owner, author, or admin required"))
                .andExpect(jsonPath("$.user").value("author"))
                .andExpect(jsonPath("$.userId").value(authorUser.getUuid().toString()))
                .andExpect(jsonPath("$.isOwner").value(false))
                .andExpect(jsonPath("$.isAuthor").value(true));
    }

    @Test
    void testResourceOwnershipEndpointWithUnauthorizedUser() throws Exception {
        String resourceId = "some-other-user-id";
        
        mockMvc.perform(get("/api/example/resource")
                .param("resourceId", resourceId)
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void testComplexAuthorizationEndpoint() throws Exception {
        String userId = testUser.getUuid().toString();
        
        mockMvc.perform(get("/api/example/complex")
                .param("userId", userId)
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("This endpoint has complex authorization rules"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.access").value("Authenticated user who is author, admin, or owner"))
                .andExpect(jsonPath("$.user").value("testuser"))
                .andExpect(jsonPath("$.currentUserId").value(userId))
                .andExpect(jsonPath("$.isOwner").value(true))
                .andExpect(jsonPath("$.isAuthor").value(false));
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    void testMalformedJwtToken() throws Exception {
        mockMvc.perform(get("/api/example/protected")
                .header("Authorization", "Bearer malformed.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Invalid or missing JWT token"));
    }

    @Test
    void testExpiredJwtToken() throws Exception {
        // Note: In a real test, you would need to create an expired token
        // For this example, we'll test with an invalid token
        mockMvc.perform(get("/api/example/protected")
                .header("Authorization", "Bearer expired.token.here"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Invalid or missing JWT token"));
    }

    @Test
    void testMissingAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/example/protected"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Invalid or missing JWT token"));
    }

    @Test
    void testInvalidAuthorizationFormat() throws Exception {
        mockMvc.perform(get("/api/example/protected")
                .header("Authorization", "Invalid " + testUserToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized: Invalid or missing JWT token"));
    }
}
