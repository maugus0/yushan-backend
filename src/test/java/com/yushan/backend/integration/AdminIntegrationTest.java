package com.yushan.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.config.DatabaseConfig;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.AdminUpdateUserDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.UserStatus;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("integration-test")
@Import({TestcontainersConfiguration.class, DatabaseConfig.class})
@Transactional
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class AdminIntegrationTest {

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

    private User adminUser;
    private User normalUser;
    private User authorUser;
    private String adminToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        createTestData();
    }

    @Test
    void testListUsers_withIsAuthorFilter_shouldReturnCorrectUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users?isAuthor=true")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2))) // adminUser and authorUser
                .andExpect(jsonPath("$.data.content[?(@.username == 'admin_user')]").exists())
                .andExpect(jsonPath("$.data.content[?(@.username == 'author_user')]").exists());
    }

    @Test
    void testGetUserDetail_shouldReturnCorrectDetails() throws Exception {
        mockMvc.perform(get("/api/admin/users/{uuid}/status", normalUser.getUuid())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(normalUser.getEmail()));
    }

    @Test
    void testUpdateUser_shouldChangeStatusAndRoleInDatabase() throws Exception {
        // Given
        AdminUpdateUserDTO requestBody = new AdminUpdateUserDTO();
        requestBody.setStatus(UserStatus.BANNED);

        // When
        mockMvc.perform(put("/api/admin/users/{uuid}/status", normalUser.getUuid())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());

        // Then: Verify changes in the database
        User updatedUser = userMapper.selectByPrimaryKey(normalUser.getUuid());
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.BANNED.ordinal());
        assertThat(updatedUser.getIsAuthor()).isTrue();
        assertThat(updatedUser.getIsAdmin()).isFalse();
    }

    // Helper Methods
    private void createTestData() {
        adminUser = createTestUser("admin@test.com", "admin_user", true, true);
        authorUser = createTestUser("author@test.com", "author_user", true, false);
        normalUser = createTestUser("user@test.com", "normal_user", false, false);

        userMapper.insert(adminUser);
        userMapper.insert(authorUser);
        userMapper.insert(normalUser);

        adminToken = jwtUtil.generateAccessToken(adminUser);
    }

    private User createTestUser(String email, String username, boolean isAuthor, boolean isAdmin) {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setHashPassword(passwordEncoder.encode("password123"));
        user.setIsAuthor(isAuthor);
        user.setIsAdmin(isAdmin);
        user.setStatus(UserStatus.NORMAL.ordinal());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLogin(new Date());
        user.setLastActive(new Date());
        user.setEmailVerified(true);
        user.setAvatarUrl("avatar.jpg");
        user.setGender(0);
        return user;
    }
}