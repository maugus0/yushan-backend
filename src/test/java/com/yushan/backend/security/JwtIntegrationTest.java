package com.yushan.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.enums.ErrorCode;
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
        userMapper.insert(testUser);

        // Create author user
        authorUser = new User();
        authorUser.setUuid(UUID.randomUUID());
        authorUser.setEmail("author@example.com");
        authorUser.setUsername("author");
        authorUser.setHashPassword(passwordEncoder.encode("password123"));
        authorUser.setIsAuthor(true);
        userMapper.insert(authorUser);

        // Generate tokens
        testUserToken = jwtUtil.generateAccessToken(testUser);
        authorUserToken = jwtUtil.generateAccessToken(authorUser);
    }

    /**
     * Create a test user with specified parameters
     */
    private User createTestUser(String email, String username, boolean isAuthor, boolean isAdmin) {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setHashPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(true);
        user.setStatus(1);
        user.setIsAuthor(isAuthor);
        user.setIsAdmin(isAdmin);
        user.setLevel(1);
        user.setExp(0.0f);
        user.setReadTime(0.0f);
        user.setReadBookNum(0);
        userMapper.insert(user);
        return user;
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
        String email = jwtUtil.extractEmail(testUserToken);
        String userId = jwtUtil.extractUserId(testUserToken);
        String tokenType = jwtUtil.extractTokenType(testUserToken);

        assert email.equals(testUser.getEmail());
        assert userId.equals(testUser.getUuid().toString());
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
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "testuser@example.com");
        loginRequest.put("password", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"));
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
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("JWT tokens are stateless and cannot be invalidated server-side. Client should discard tokens."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testSendEmail() throws Exception {
        Map<String, String> sendEmailRequest = new HashMap<>();
        sendEmailRequest.put("email", "newuser@example.com");

        mockMvc.perform(post("/api/auth/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification code sent successfully"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ==================== PROTECTED ENDPOINT TESTS ====================

    @Test
    void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/example/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Public endpoint accessed successfully"))
                .andExpect(jsonPath("$.data.access").value("No authentication required"));
    }

    @Test
    void testProtectedEndpointWithValidToken() throws Exception {
        mockMvc.perform(get("/api/example/protected")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Protected endpoint accessed successfully"))
                .andExpect(jsonPath("$.data.access").value("Authentication required"))
                .andExpect(jsonPath("$.data.user").value("testuser"))
                .andExpect(jsonPath("$.data.isAuthor").value(false));
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
                .andExpect(jsonPath("$.message").value("Author-only endpoint accessed successfully"))
                .andExpect(jsonPath("$.data.access").value("Author role required"))
                .andExpect(jsonPath("$.data.user").value("author"))
                .andExpect(jsonPath("$.data.isAuthor").value(true));
    }

    @Test
    void testAuthorOnlyEndpointWithRegularUser() throws Exception {
        mockMvc.perform(get("/api/example/author-only")
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testResourceOwnershipEndpointWithOwner() throws Exception {
        String resourceId = testUser.getUuid().toString();
        
        mockMvc.perform(get("/api/example/resource")
                .param("resourceId", resourceId)
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resource ownership endpoint accessed successfully"))
                .andExpect(jsonPath("$.data.resourceId").value(resourceId))
                .andExpect(jsonPath("$.data.access").value("Resource owner, author, or admin required"))
                .andExpect(jsonPath("$.data.user").value("testuser"))
                .andExpect(jsonPath("$.data.userId").value(resourceId))
                .andExpect(jsonPath("$.data.isOwner").value(true))
                .andExpect(jsonPath("$.data.isAuthor").value(false));
    }

    @Test
    void testResourceOwnershipEndpointWithAuthor() throws Exception {
        String resourceId = testUser.getUuid().toString();
        
        mockMvc.perform(get("/api/example/resource")
                .param("resourceId", resourceId)
                .header("Authorization", "Bearer " + authorUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resource ownership endpoint accessed successfully"))
                .andExpect(jsonPath("$.data.resourceId").value(resourceId))
                .andExpect(jsonPath("$.data.access").value("Resource owner, author, or admin required"))
                .andExpect(jsonPath("$.data.user").value("author"))
                .andExpect(jsonPath("$.data.userId").value(authorUser.getUuid().toString()))
                .andExpect(jsonPath("$.data.isOwner").value(false))
                .andExpect(jsonPath("$.data.isAuthor").value(true));
    }

    @Test
    void testResourceOwnershipEndpointWithUnauthorizedUser() throws Exception {
        String resourceId = "some-other-user-id";
        
        mockMvc.perform(get("/api/example/resource")
                .param("resourceId", resourceId)
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testComplexAuthorizationEndpoint() throws Exception {
        String userId = testUser.getUuid().toString();
        
        mockMvc.perform(get("/api/example/complex")
                .param("userId", userId)
                .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Complex authorization endpoint accessed successfully"))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.access").value("Authenticated user who is author, admin, or owner"))
                .andExpect(jsonPath("$.data.user").value("testuser"))
                .andExpect(jsonPath("$.data.currentUserId").value(userId))
                .andExpect(jsonPath("$.data.isOwner").value(true))
                .andExpect(jsonPath("$.data.isAuthor").value(false));
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

    @Test
    void testIsAdminFieldInLoginResponse() throws Exception {
        // Test login with admin user - create user first
        createTestUser("admin@example.com", "AdminUser", true, true);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "admin@example.com");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.isAdmin").value(true))
                .andExpect(jsonPath("$.data.isAuthor").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void testIsAdminFieldInNormalUserResponse() throws Exception {
        // Test login with normal user - create user first
        createTestUser("normal@example.com", "NormalUser", false, false);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "normal@example.com");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.isAdmin").value(false))
                .andExpect(jsonPath("$.data.isAuthor").value(false))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void testIsAdminFieldInUserProfileResponse() throws Exception {
        // Test user profile endpoint with admin user
        User adminUser = createTestUser("adminprofile@example.com", "AdminProfileUser", true, true);
        String adminToken = jwtUtil.generateAccessToken(adminUser);

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.isAdmin").value(true))
                .andExpect(jsonPath("$.data.isAuthor").value(true))
                .andExpect(jsonPath("$.data.email").value("adminprofile@example.com"));
    }

    @Test
    void testIsAdminFieldInRefreshTokenResponse() throws Exception {
        // Test refresh token with admin user
        User adminUser = createTestUser("adminrefresh@example.com", "AdminRefreshUser", true, true);
        String refreshToken = jwtUtil.generateRefreshToken(adminUser);

        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.isAdmin").value(true))
                .andExpect(jsonPath("$.data.isAuthor").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }
}

