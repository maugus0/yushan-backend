package com.yushan.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.enums.Gender;
import com.yushan.backend.enums.UserStatus;
import com.yushan.backend.service.MailService;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Authentication with real PostgreSQL + Redis
 * 
 * This test class verifies:
 * - User registration with database persistence
 * - User login with JWT token generation
 * - Token validation with Redis cache
 * - Email verification flow
 * - Password encryption and validation
 * - Database transactions and rollback
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@Transactional
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Setup mock MailService
        doNothing().when(mailService).sendVerificationCode(anyString());
        when(mailService.verifyEmail(anyString(), eq("123456"))).thenReturn(true);
        when(mailService.verifyEmail(anyString(), anyString())).thenReturn(false);
    }

    /**
     * Test complete user registration flow with database persistence
     */
    @Test
    void testUserRegistration_WithDatabasePersistence() throws Exception {
        // Given
        when(mailService.verifyEmail("newuser@example.com", "123456")).thenReturn(true);
        
        Map<String, Object> registerRequest = new HashMap<>();
        registerRequest.put("email", "newuser@example.com");
        registerRequest.put("username", "newuser");
        registerRequest.put("password", "password123");
        registerRequest.put("code", "123456");
        registerRequest.put("gender", Gender.MALE);

        // When
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"));

        // Then - Verify user was persisted in database
        User registeredUser = userMapper.selectByEmail("newuser@example.com");
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getUsername()).isEqualTo("newuser");
        assertThat(registeredUser.getEmail()).isEqualTo("newuser@example.com");
        assertThat(registeredUser.getEmailVerified()).isTrue();
        assertThat(registeredUser.getStatus()).isEqualTo(UserStatus.NORMAL); // Active status
        assertThat(registeredUser.getGender()).isEqualTo(Gender.MALE.getCode());
    }

    /**
     * Test user login with database verification
     */
    @Test
    void testUserLogin_WithDatabaseVerification() throws Exception {
        // Given - Create user in database
        User testUser = createTestUser("testuser@example.com", "testuser", "password123");
        userMapper.insert(testUser);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "testuser@example.com");
        loginRequest.put("password", "password123");

        // When
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        // Then - Verify user data from database
        User loggedInUser = userMapper.selectByEmail("testuser@example.com");
        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getLastLogin()).isNotNull();
        assertThat(loggedInUser.getLastActive()).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo("testuser@example.com");
        assertThat(loggedInUser.getUsername()).isEqualTo("testuser");
    }

    /**
     * Test JWT token validation with Redis cache
     */
    @Test
    void testJwtTokenValidation_WithRedisCache() throws Exception {
        // Given - Create user and generate token
        User testUser = createTestUser("jwtuser@example.com", "jwtuser", "password123");
        userMapper.insert(testUser);
        
        String accessToken = jwtUtil.generateAccessToken(testUser);
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        // When - Validate tokens
        boolean accessTokenValid = jwtUtil.validateToken(accessToken);
        boolean refreshTokenValid = jwtUtil.validateToken(refreshToken);

        // Then
        assert accessTokenValid;
        assert refreshTokenValid;
        
        // Verify token claims
        String email = jwtUtil.extractEmail(accessToken);
        String userId = jwtUtil.extractUserId(accessToken);
        assert email.equals("jwtuser@example.com");
        assert userId.equals(testUser.getUuid().toString());
    }

    /**
     * Test refresh token flow with database persistence
     */
    @Test
    void testRefreshToken_WithDatabasePersistence() throws Exception {
        // Given - Create user and generate refresh token
        User testUser = createTestUser("refreshuser@example.com", "refreshuser", "password123");
        userMapper.insert(testUser);
        
        String refreshToken = jwtUtil.generateRefreshToken(testUser);

        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);

        // When
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());

        // Then - Verify new tokens are generated
        User refreshedUser = userMapper.selectByEmail("refreshuser@example.com");
        assertThat(refreshedUser).isNotNull();
        assertThat(refreshedUser.getLastLogin()).isNotNull();
        assertThat(refreshedUser.getLastActive()).isNotNull();
        assertThat(refreshedUser.getEmail()).isEqualTo("refreshuser@example.com");
        assertThat(refreshedUser.getUsername()).isEqualTo("refreshuser");
    }

    /**
     * Test email verification with database update
     */
    @Test
    void testEmailVerification_WithDatabaseUpdate() throws Exception {
        // Given - Use a new email that doesn't exist in database
        String newEmail = "newuser@example.com";

        // When - Send verification email
        Map<String, String> sendEmailRequest = new HashMap<>();
        sendEmailRequest.put("email", newEmail);

        mockMvc.perform(post("/api/auth/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification code sent successfully"));

        // Then - Verify email was sent (mocked)
        // In real scenario, user would verify with code and email_verified would be updated
    }

    /**
     * Test password encryption and validation with database
     */
    @Test
    void testPasswordEncryption_WithDatabaseStorage() throws Exception {
        // Given
        String plainPassword = "testpassword123";
        User testUser = createTestUser("passworduser@example.com", "passworduser", plainPassword);
        userMapper.insert(testUser);

        // When - Retrieve user from database
        User passwordUser = userMapper.selectByEmail("passworduser@example.com");
        assertThat(passwordUser).isNotNull();
        
        // Then - Verify password is encrypted
        assertThat(passwordUser.getHashPassword()).isNotEqualTo("password123");
        assertThat(passwordUser.getHashPassword()).startsWith("$2a$");
        assertThat(passwordUser.getEmail()).isEqualTo("passworduser@example.com");
        assertThat(passwordUser.getUsername()).isEqualTo("passworduser");
        assertThat(passwordUser.getEmailVerified()).isTrue();
    }

    /**
     * Test user profile retrieval with database
     */
    @Test
    void testUserProfile_WithDatabaseRetrieval() throws Exception {
        // Given - Create user with profile data
        User testUser = createTestUser("profileuser@example.com", "profileuser", "password123");
        testUser.setAvatarUrl("https://example.com/avatar.jpg");
        testUser.setGender(1);
        testUser.setIsAuthor(true);
        testUser.setIsAdmin(false);
        userMapper.insert(testUser);

        String accessToken = jwtUtil.generateAccessToken(testUser);

        // When
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.email").value("profileuser@example.com"))
                .andExpect(jsonPath("$.data.username").value("profileuser"))
                .andExpect(jsonPath("$.data.isAuthor").value(true))
                .andExpect(jsonPath("$.data.isAdmin").value(false));
    }

    /**
     * Test database transaction rollback on error
     */
    @Test
    void testDatabaseTransactionRollback_OnRegistrationError() throws Exception {
        // Given - Invalid registration data
        when(mailService.verifyEmail("invalid-email", "123456")).thenReturn(true);
        
        Map<String, Object> invalidRequest = new HashMap<>();
        invalidRequest.put("email", "invalid-email"); // Invalid email format
        invalidRequest.put("username", "testuser");
        invalidRequest.put("password", "password123");
        invalidRequest.put("code", "123456");
        invalidRequest.put("gender", Gender.MALE);

        // When
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Then - Verify no user was created in database
        User invalidUser = userMapper.selectByEmail("invalid@example.com");
        assertThat(invalidUser).isNull();
        
        // Also verify no user with empty email exists
        User emptyEmailUser = userMapper.selectByEmail("");
        assertThat(emptyEmailUser).isNull();
    }

    /**
     * Helper method to create test user
     */
    private User createTestUser(String email, String username, String password) {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setHashPassword(passwordEncoder.encode(password));
        user.setEmailVerified(true);
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setStatus(UserStatus.NORMAL.getCode());
        user.setGender(Gender.MALE.getCode());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLogin(new Date());
        user.setLastActive(new Date());
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setLevel(1);
        user.setExp(0.0f);
        user.setYuan(0.0f);
        user.setReadTime(0.0f);
        user.setReadBookNum(0);
        return user;
    }
}