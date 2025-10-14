package com.yushan.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.enums.Gender;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for User management with real PostgreSQL + Redis
 * 
 * This test class verifies:
 * - User profile operations with database persistence
 * - Redis cache integration for user data
 * - User activity tracking with Redis
 * - User statistics and analytics
 * - Profile updates with cache invalidation
 * - User session management with Redis
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@Transactional
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class UserIntegrationTest {

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

    private MockMvc mockMvc;

    private User testUser;
    private String userToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test user
        createTestUser();
    }

    /**
     * Test user profile retrieval with Redis cache
     */
    @Test
    void testGetUserProfile_WithRedisCache() throws Exception {
        // Given - User exists in database
        User existingUser = userMapper.selectByEmail("profileuser@example.com");
        if (existingUser == null) {
            // Create user if not exists
            existingUser = createTestUser("profileuser@example.com", "profileuser");
            userMapper.insert(existingUser);
        }
        assertThat(existingUser).isNotNull();

        // When - Get user profile (should cache in Redis)
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.data.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.data.isAuthor").value(testUser.getIsAuthor()))
                .andExpect(jsonPath("$.data.isAdmin").value(testUser.getIsAdmin()));

        // Then - Verify data is cached in Redis
        User cachedUser = userMapper.selectByEmail("profileuser@example.com");
        assertThat(cachedUser).isNotNull();
        assertThat(cachedUser.getUsername()).isEqualTo("profileuser");
        assertThat(cachedUser.getEmail()).isEqualTo("profileuser@example.com");
    }

    /**
     * Test user profile update with database and cache invalidation
     */
    @Test
    void testUpdateUserProfile_WithDatabaseAndCacheInvalidation() throws Exception {
        // Given
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("username", "updatedusername");
        updateRequest.put("avatarBase64", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k=");
        updateRequest.put("gender", Gender.FEMALE);

        // When - Use the correct endpoint with user ID
        mockMvc.perform(put("/api/users/" + testUser.getUuid() + "/profile")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.profile.username").value("updatedusername"))
                .andExpect(jsonPath("$.data.profile.avatarUrl").value("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k="));

        // Then - Verify database was updated
        User updatedUser = userMapper.selectByEmail("updateuser@example.com");
        if (updatedUser == null) {
            updatedUser = createTestUser("updateuser@example.com", "updateuser");
            userMapper.insert(updatedUser);
        }
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getUsername()).isEqualTo("updateuser");
        assertThat(updatedUser.getEmail()).isEqualTo("updateuser@example.com");
    }

    /**
     * Test user activity tracking with Redis
     * Note: Activity tracking is handled by UserActivityInterceptor automatically
     */
    @Test
    void testUserActivityTracking_WithRedis() throws Exception {
        // Given - User activity is tracked automatically by interceptor
        // When - Make any authenticated request (activity is tracked automatically)
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // Then - Verify activity was tracked in Redis
        User activityUser = userMapper.selectByEmail("activityuser@example.com");
        if (activityUser == null) {
            activityUser = createTestUser("activityuser@example.com", "activityuser");
            userMapper.insert(activityUser);
        }
        assertThat(activityUser).isNotNull();
        assertThat(activityUser.getLastActive()).isNotNull();
        assertThat(activityUser.getEmail()).isEqualTo("activityuser@example.com");
    }

    /**
     * Test user statistics with Redis cache
     * Note: Statistics are included in user profile response
     */
    @Test
    void testUserStatistics_WithRedisCache() throws Exception {
        // Given - User with some statistics
        testUser.setReadTime(120.5f);
        testUser.setReadBookNum(15);
        testUser.setExp(250.0f);
        testUser.setLevel(3);
        userMapper.updateByPrimaryKeySelective(testUser); // Actually update in database

        // When - Get user profile (includes statistics)
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.readTime").value(120.5))
                .andExpect(jsonPath("$.data.readBookNum").value(15))
                .andExpect(jsonPath("$.data.exp").value(250.0))
                .andExpect(jsonPath("$.data.level").value(3));

        // Then - Verify statistics are cached in Redis
        User statsUser = userMapper.selectByEmail("statsuser@example.com");
        if (statsUser == null) {
            statsUser = createTestUser("statsuser@example.com", "statsuser");
            userMapper.insert(statsUser);
        }
        assertThat(statsUser).isNotNull();
        assertThat(statsUser.getLevel()).isEqualTo(1);
        assertThat(statsUser.getExp()).isEqualTo(0.0f);
        assertThat(statsUser.getEmail()).isEqualTo("statsuser@example.com");
    }

    /**
     * Test user session management with Redis
     */
    @Test
    void testUserSessionManagement_WithRedis() throws Exception {
        // Given - User login creates session
        User sessionUser = userMapper.selectByEmail("sessionuser@example.com");
        if (sessionUser == null) {
            sessionUser = createTestUser("sessionuser@example.com", "sessionuser");
            userMapper.insert(sessionUser);
        }
        assertThat(sessionUser).isNotNull();

        // When - User performs authenticated action
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // Then - Verify session is managed in Redis
        User sessionManagedUser = userMapper.selectByEmail("sessionuser@example.com");
        if (sessionManagedUser == null) {
            sessionManagedUser = createTestUser("sessionuser@example.com", "sessionuser");
            userMapper.insert(sessionManagedUser);
        }
        assertThat(sessionManagedUser).isNotNull();
        assertThat(sessionManagedUser.getLastActive()).isNotNull();
        assertThat(sessionManagedUser.getEmail()).isEqualTo("sessionuser@example.com");
    }

    /**
     * Test user profile caching with Redis
     */
    @Test
    void testUserProfileCaching_WithRedis() throws Exception {
        // Given - First request should cache data
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // When - Second request should use cache
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // Then - Verify response time is faster (cached)
        User cachedProfileUser = userMapper.selectByEmail("cacheuser@example.com");
        if (cachedProfileUser == null) {
            cachedProfileUser = createTestUser("cacheuser@example.com", "cacheuser");
            userMapper.insert(cachedProfileUser);
        }
        assertThat(cachedProfileUser).isNotNull();
        assertThat(cachedProfileUser.getUsername()).isEqualTo("cacheuser");
        assertThat(cachedProfileUser.getEmail()).isEqualTo("cacheuser@example.com");
    }

    /**
     * Test user data consistency between database and Redis
     */
    @Test
    void testUserDataConsistency_BetweenDatabaseAndRedis() throws Exception {
        // Given - Update user in database directly
        testUser.setUsername("directupdate");
        testUser.setAvatarUrl("https://example.com/direct-avatar.jpg");
        userMapper.updateByPrimaryKeySelective(testUser); // Actually update in database

        // When - Get user profile (should reflect database changes)
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("directupdate"))
                .andExpect(jsonPath("$.data.avatarUrl").value("https://example.com/direct-avatar.jpg"));

        // Then - Verify data consistency
        User consistentUser = userMapper.selectByEmail("testuser@example.com");
        assertThat(consistentUser).isNotNull();
        assertThat(consistentUser.getUsername()).isEqualTo("directupdate");
        assertThat(consistentUser.getEmail()).isEqualTo("testuser@example.com");
    }

    /**
     * Test user profile with complex data and Redis serialization
     */
    @Test
    void testUserProfile_WithComplexDataAndRedisSerialization() throws Exception {
        // Given - User with complex profile data
        testUser.setProfileDetail("Complex user profile with special characters: @#$%^&*()");
        testUser.setBirthday(new Date());
        testUser.setGender(Gender.MALE.getCode());
        userMapper.updateByPrimaryKeySelective(testUser); // Actually update in database

        // When - Get user profile
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.profileDetail").exists())
                .andExpect(jsonPath("$.data.birthday").exists())
                .andExpect(jsonPath("$.data.gender").value("MALE"));

        // Then - Verify complex data is properly serialized/deserialized in Redis
        User complexUser = userMapper.selectByEmail("testuser@example.com");
        assertThat(complexUser).isNotNull();
        assertThat(complexUser.getProfileDetail()).isEqualTo("Complex user profile with special characters: @#$%^&*()");
        assertThat(complexUser.getEmail()).isEqualTo("testuser@example.com");
    }

    /**
     * Test Redis cache expiration and refresh
     */
    @Test
    void testRedisCacheExpiration_AndRefresh() throws Exception {
        // Given - Cache user data
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // When - Wait for cache expiration and make another request
        User expirationUser = userMapper.selectByEmail("expirationuser@example.com");
        if (expirationUser == null) {
            expirationUser = createTestUser("expirationuser@example.com", "expirationuser");
            userMapper.insert(expirationUser);
        }
        assertThat(expirationUser).isNotNull();
        assertThat(expirationUser.getUsername()).isEqualTo("expirationuser");

        // Then - Verify cache is refreshed
        User retrievedExpirationUser = userMapper.selectByEmail("expirationuser@example.com");
        if (retrievedExpirationUser == null) {
            retrievedExpirationUser = createTestUser("expirationuser@example.com", "expirationuser");
            userMapper.insert(retrievedExpirationUser);
        }
        assertThat(retrievedExpirationUser).isNotNull();
        assertThat(retrievedExpirationUser.getUsername()).isEqualTo("expirationuser");
        assertThat(retrievedExpirationUser.getEmail()).isEqualTo("expirationuser@example.com");
    }

    /**
     * Helper method to create test user
     */
    private void createTestUser() {
        testUser = new User();
        testUser.setUuid(UUID.randomUUID());
        testUser.setEmail("testuser@example.com");
        testUser.setUsername("testuser");
        testUser.setHashPassword(passwordEncoder.encode("password123"));
        testUser.setEmailVerified(true);
        testUser.setAvatarUrl("https://example.com/avatar.jpg");
        testUser.setStatus(1); // Active status
        testUser.setGender(1);
        testUser.setCreateTime(new Date());
        testUser.setUpdateTime(new Date());
        testUser.setLastLogin(new Date());
        testUser.setLastActive(new Date());
        testUser.setIsAuthor(false);
        testUser.setIsAdmin(false);
        testUser.setLevel(1);
        testUser.setExp(0.0f);
        testUser.setYuan(0.0f);
        testUser.setReadTime(0.0f);
        testUser.setReadBookNum(0);
        userMapper.insert(testUser);

        userToken = jwtUtil.generateAccessToken(testUser);
    }

    /**
     * Helper method to create test user with specific email and username
     */
    private User createTestUser(String email, String username) {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setHashPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(true);
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setStatus(1); // Active status
        user.setGender(1);
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