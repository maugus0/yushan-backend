package com.yushan.backend;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.util.JwtUtil;
import com.yushan.backend.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test - Basic integration testing with real Postgres + Redis using Testcontainers
 * 
 * This test class provides:
 * 1. Quick smoke test for system startup
 * 2. Basic database connectivity verification
 * 3. Basic Redis connectivity verification
 * 4. Basic JWT functionality verification
 * 5. Basic controller accessibility verification
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
class IntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // This test verifies that:
        // 1. Spring Boot application starts successfully
        // 2. Postgres container is running and accessible
        // 3. Redis container is running and accessible
        // 4. All beans are created correctly
        
        // Verify essential beans are available
        assertNotNull(context, "WebApplicationContext should be available");
        assertNotNull(userMapper, "UserMapper should be available");
        assertNotNull(jwtUtil, "JwtUtil should be available");
        assertNotNull(passwordEncoder, "PasswordEncoder should be available");
        assertNotNull(redisUtil, "RedisUtil should be available");
    }

    @Test
    void basicDatabaseConnectivity() throws Exception {
        // Quick database connectivity test
        User testUser = new User();
        testUser.setUuid(UUID.randomUUID());
        testUser.setEmail("basic-test@example.com");
        testUser.setUsername("basictest");
        testUser.setHashPassword(passwordEncoder.encode("password123"));
        testUser.setEmailVerified(true);
        testUser.setAvatarUrl("https://example.com/avatar.jpg");
        testUser.setStatus(1); // Active status
        testUser.setGender(1);
        testUser.setIsAuthor(false);
        testUser.setIsAdmin(false);
        testUser.setLevel(1);
        testUser.setExp(0.0f);
        testUser.setYuan(0.0f);
        testUser.setReadTime(0.0f);
        testUser.setReadBookNum(0);
        testUser.setCreateTime(new Date());
        testUser.setUpdateTime(new Date());
        testUser.setLastLogin(new Date());
        testUser.setLastActive(new Date());
        
        // Test database write
        userMapper.insert(testUser);
        
        // Test database read
        User retrievedUser = userMapper.selectByEmail("basic-test@example.com");
        assertNotNull(retrievedUser, "Database connectivity should work");
        assertEquals("basic-test@example.com", retrievedUser.getEmail(), "Database data integrity should be maintained");
    }

    @Test
    void basicRedisConnectivity() throws Exception {
        // Quick Redis connectivity test
        String testKey = "basic-test:redis";
        String testValue = "Redis connectivity test";
        
        // Test Redis operations
        redisUtil.set(testKey, testValue);
        String retrievedValue = redisUtil.get(testKey);
        assertNotNull(retrievedValue, "Redis connectivity should work");
        assertEquals(testValue, retrievedValue, "Redis data integrity should be maintained");
        
        // Cleanup
        redisUtil.delete(testKey);
    }

    @Test
    void basicJwtFunctionality() throws Exception {
        // Quick JWT functionality test
        User testUser = new User();
        testUser.setUuid(UUID.randomUUID());
        testUser.setEmail("jwt-test@example.com");
        testUser.setUsername("jwttest");
        testUser.setHashPassword(passwordEncoder.encode("password123"));
        testUser.setEmailVerified(true);
        testUser.setIsAuthor(false);
        testUser.setIsAdmin(false);
        
        // Test JWT token generation
        String accessToken = jwtUtil.generateAccessToken(testUser);
        assertNotNull(accessToken, "JWT token generation should work");
        assertFalse(accessToken.isEmpty(), "JWT token should not be empty");
        
        // Test JWT token validation
        boolean isValid = jwtUtil.validateToken(accessToken);
        assertTrue(isValid, "JWT token validation should work");
        
        // Test token claims extraction
        String email = jwtUtil.extractEmail(accessToken);
        assertNotNull(email, "JWT token claims extraction should work");
        assertEquals("jwt-test@example.com", email, "JWT token claims should be correct");
    }

    @Test
    void basicControllerAccessibility() throws Exception {
        // Quick controller accessibility test
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        // Test public endpoint
        mockMvc.perform(get("/api/example/public"))
                .andExpect(status().isOk());
    }
}
