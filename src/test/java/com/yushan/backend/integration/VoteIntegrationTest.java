package com.yushan.backend.integration;

import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.config.DatabaseConfig;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dao.VoteMapper;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import com.yushan.backend.entity.Vote;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@Transactional
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class VoteIntegrationTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private VoteMapper voteMapper;
    @Autowired
    private NovelMapper novelMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private User testUser;
    private User authorUser;
    private Novel testNovel;
    private String userToken;
    private String authorToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        createTestData();
    }

    @Test
    void testVote_WithDatabasePersistence() throws Exception {
        // Given: User starts with 100 yuan and novel has 0 votes
        assertThat(testUser.getYuan()).isEqualTo(100.0f);
        Novel initialNovel = novelMapper.selectByPrimaryKey(testNovel.getId());
        assertThat(initialNovel.getVoteCnt()).isZero();

        // When
        mockMvc.perform(post("/api/novels/{novelId}/vote", testNovel.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.voteCount").value(1))
                .andExpect(jsonPath("$.data.remainedYuan").value(99.0));

        // Then
        // 1. Verify a vote record was created in the database
        List<Vote> votes = voteMapper.selectByUserIdWithPagination(testUser.getUuid(), 0, 10);
        assertThat(votes).hasSize(1);
        assertThat(votes.get(0).getNovelId()).isEqualTo(testNovel.getId());

        // 2. Verify the novel's vote count was incremented
        Novel updatedNovel = novelMapper.selectByPrimaryKey(testNovel.getId());
        assertThat(updatedNovel.getVoteCnt()).isEqualTo(1);

        // 3. Verify the user's yuan balance was decremented
        User updatedUser = userMapper.selectByPrimaryKey(testUser.getUuid());
        assertThat(updatedUser.getYuan()).isEqualTo(99.0f);
    }

    @Test
    void testVote_shouldFail_whenNotEnoughYuan() throws Exception {
        // Given: Update user to have 0 yuan
        testUser.setYuan(0f);
        userMapper.updateByPrimaryKey(testUser);

        // When & Then
        mockMvc.perform(post("/api/novels/{novelId}/vote", testNovel.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not enough yuan"));

        // Verify that no vote was created and vote count is still 0
        Novel novel = novelMapper.selectByPrimaryKey(testNovel.getId());
        assertThat(novel.getVoteCnt()).isZero();
    }

    // Helper methods
    private void createTestData() {
        authorUser = createTestUser("voteauthor@example.com", "voteauthor");
        authorUser.setIsAuthor(true);
        userMapper.insert(authorUser);
        authorToken = jwtUtil.generateAccessToken(authorUser);

        testUser = createTestUser("voteuser@example.com", "voteuser");
        testUser.setYuan(100.0f);
        userMapper.insert(testUser);
        userToken = jwtUtil.generateAccessToken(testUser);

        testNovel = createTestNovel("Vote Test Novel", "A novel for testing votes", authorUser.getUuid());
        novelMapper.insertSelective(testNovel);
    }

    private User createTestUser(String email, String username) {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername(username);
        user.setHashPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(true);
        user.setAvatarUrl("avatar.jpg");
        user.setStatus(1);
        user.setGender(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setLastLogin(new Date());
        user.setLastActive(new Date());
        return user;
    }

    private Novel createTestNovel(String title, String description, UUID authorId) {
        Novel novel = new Novel();
        novel.setUuid(UUID.randomUUID());
        novel.setTitle(title);
        novel.setSynopsis(description);
        novel.setAuthorId(authorId);
        novel.setCategoryId(1);
        novel.setStatus(2); // PUBLISHED
        novel.setVoteCnt(0);
        return novel;
    }
}