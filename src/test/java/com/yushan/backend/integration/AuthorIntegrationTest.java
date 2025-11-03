package com.yushan.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.service.MailService;
import com.yushan.backend.util.JwtUtil;
import com.yushan.backend.util.MailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AuthorController with real PostgreSQL + Redis
 *
 * This test class verifies:
 * - Sending author verification email
 * - Upgrading a regular user to an author
 * - Handling of invalid verification codes
 * - Preventing existing authors from re-applying
 * - Database persistence of the 'is_author' status
 */
@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@Transactional
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
        "jwt.secret=test-secret-key-for-integration-tests-123456",
        "jwt.access-token.expiration=3600000",
        "jwt.refresh-token.expiration=86400000"
})
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class AuthorIntegrationTest {

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

    @MockBean
    private MailUtil mailUtil;

    private MockMvc mockMvc;

    private User regularUser;
    private User authorUser;
    private String regularUserToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Setup mock MailService
        doNothing().when(mailService).sendVerificationCode(anyString());
        when(mailService.verifyEmail(anyString(), eq("123456"))).thenReturn(true);
        when(mailService.verifyEmail(anyString(), eq("654321"))).thenReturn(false);

        // Create a regular user for testing
        regularUser = createTestUser("regular@example.com", "regularUser", "password123", false);
        userMapper.insert(regularUser);
        regularUserToken = jwtUtil.generateAccessToken(regularUser);

        // Create an existing author for testing
        authorUser = createTestUser("author@example.com", "authorUser", "password123", true);
        userMapper.insert(authorUser);
    }

    @Test
    void testSendEmailAuthorVerification_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "regular@example.com");

        mockMvc.perform(post("/api/author/send-email-author-verification")
                        .header("Authorization", "Bearer " + regularUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("Verification code sent successfully"));
    }

    @Test
    void testSendEmailAuthorVerification_Fail_AlreadyAuthor() throws Exception {
        String authorToken = jwtUtil.generateAccessToken(authorUser);
        Map<String, String> request = new HashMap<>();
        request.put("email", "author@example.com");

        mockMvc.perform(post("/api/author/send-email-author-verification")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value("User is already an author"));
    }

    @Test
    void testUpgradeToAuthor_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("verificationCode", "123456");

        mockMvc.perform(post("/api/author/upgrade-to-author")
                        .header("Authorization", "Bearer " + regularUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value("User upgraded to author successfully"))
                .andExpect(jsonPath("$.data.isAuthor").value(true));

        // Verify database state
        User updatedUser = userMapper.selectByEmail("regular@example.com");
        assertThat(updatedUser.getIsAuthor()).isTrue();
    }

    @Test
    void testUpgradeToAuthor_Fail_InvalidCode() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("verificationCode", "654321"); // Invalid code

        mockMvc.perform(post("/api/author/upgrade-to-author")
                        .header("Authorization", "Bearer " + regularUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value("Invalid verification code or code expired"));

        // Verify database state has not changed
        User notUpdatedUser = userMapper.selectByEmail("regular@example.com");
        assertThat(notUpdatedUser.getIsAuthor()).isFalse();
    }

    @Test
    void testUpgradeToAuthor_Fail_Unauthorized() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("verificationCode", "123456");

        mockMvc.perform(post("/api/author/upgrade-to-author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()); // Or 401 depending on security config, 403 is common for authenticated access denial
    }

    /**
     * Helper method to create a test user entity.
     */
    private User createTestUser(String email, String username, String password, boolean isAuthor) {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setHashPassword(passwordEncoder.encode(password));
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setStatus(0); // Active status
        user.setGender(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLogin(new Date());
        user.setLastActive(new Date());
        user.setIsAuthor(isAuthor);
        user.setIsAdmin(false);
        return user;
    }
}
