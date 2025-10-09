package com.yushan.backend.integration;

import com.yushan.backend.TestcontainersConfiguration;
import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dao.VoteMapper;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import com.yushan.backend.entity.Vote;
import com.yushan.backend.enums.ErrorCode;
import com.yushan.backend.service.NovelService;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Vote management with real PostgreSQL
 * 
 * This test class verifies:
 * - Vote toggle operations with database persistence
 * - Vote permissions and access control
 * - Vote statistics with database aggregation
 * - Novel vote count updates when votes are added/removed
 * - Database transactions and data integrity
 */
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

    @Autowired
    private NovelService novelService;


    private MockMvc mockMvc;

    private User testUser;
    private User authorUser;
    private User anotherUser;
    private Novel testNovel;
    private String userToken;
    private String authorToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test data
        createTestData();
    }

    /**
     * Test vote toggle - vote if not voted, unvote if already voted
     */
    @Test
    void testToggleVote_VoteIfNotVoted_WithDatabasePersistence() throws Exception {
        // Given - User has not voted yet
        Vote existingVote = voteMapper.selectActiveByUserAndNovel(testUser.getUuid(), testNovel.getId());
        assertThat(existingVote).isNull();

        // When - Toggle vote (should vote)
        mockMvc.perform(post("/api/novels/" + testNovel.getId() + "/vote")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.userVoted").value(true))
                .andExpect(jsonPath("$.data.novelId").value(testNovel.getId()))
                .andExpect(jsonPath("$.data.voteCount").value(1));

        // Then - Verify vote was persisted in database
        Vote createdVote = voteMapper.selectActiveByUserAndNovel(testUser.getUuid(), testNovel.getId());
        assertThat(createdVote).isNotNull();
        assertThat(createdVote.getIsActive()).isTrue();
        assertThat(createdVote.getUserId()).isEqualTo(testUser.getUuid());
        assertThat(createdVote.getNovelId()).isEqualTo(testNovel.getId());
    }

    /**
     * Test vote toggle - unvote if already voted
     */
    @Test
    void testToggleVote_UnvoteIfAlreadyVoted_WithDatabasePersistence() throws Exception {
        // Given - User has already voted
        Vote existingVote = createTestVote(testUser.getUuid(), testNovel.getId(), true);
        voteMapper.insertSelective(existingVote);
        novelService.incrementVoteCount(testNovel.getId());

        // When - Toggle vote (should unvote)
        mockMvc.perform(post("/api/novels/" + testNovel.getId() + "/vote")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.userVoted").value(false))
                .andExpect(jsonPath("$.data.novelId").value(testNovel.getId()))
                .andExpect(jsonPath("$.data.voteCount").value(0));

        // Then - Verify vote was deactivated in database
        Vote deactivatedVote = voteMapper.selectActiveByUserAndNovel(testUser.getUuid(), testNovel.getId());
        assertThat(deactivatedVote).isNull();
        
        // But vote record should still exist (soft delete)
        Vote inactiveVote = voteMapper.selectByUserAndNovel(testUser.getUuid(), testNovel.getId());
        assertThat(inactiveVote).isNotNull();
        assertThat(inactiveVote.getIsActive()).isFalse();
    }

    /**
     * Test vote statistics with database aggregation
     */
    @Test
    void testGetVoteStats_WithDatabaseAggregation() throws Exception {
        // Given - Create multiple votes for the novel using service to update vote count
        Vote vote1 = createTestVote(testUser.getUuid(), testNovel.getId(), true);
        voteMapper.insertSelective(vote1);
        novelService.incrementVoteCount(testNovel.getId());

        Vote vote2 = createTestVote(anotherUser.getUuid(), testNovel.getId(), true);
        voteMapper.insertSelective(vote2);
        novelService.incrementVoteCount(testNovel.getId());

        // When
        mockMvc.perform(get("/api/novels/" + testNovel.getId() + "/vote/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.novelId").value(testNovel.getId()))
                .andExpect(jsonPath("$.data.totalVotes").value(2));

        // Then - Verify statistics are calculated from database
        Novel updatedNovel = novelMapper.selectByPrimaryKey(testNovel.getId());
        assertThat(updatedNovel).isNotNull();
    }

    /**
     * Test user vote status with database query
     */
    @Test
    void testGetUserVoteStatus_WithDatabaseQuery() throws Exception {
        // Given - User has voted
        Vote existingVote = createTestVote(testUser.getUuid(), testNovel.getId(), true);
        voteMapper.insertSelective(existingVote);

        // When
        mockMvc.perform(get("/api/novels/" + testNovel.getId() + "/vote/status")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.novelId").value(testNovel.getId()))
                .andExpect(jsonPath("$.data.hasVoted").value(true))
                .andExpect(jsonPath("$.data.votedAt").exists());
    }

    /**
     * Test vote permissions - author cannot vote own novel
     */
    @Test
    void testVotePermissions_AuthorCannotVoteOwnNovel() throws Exception {
        // When - Author tries to vote their own novel
        mockMvc.perform(post("/api/novels/" + testNovel.getId() + "/vote")
                .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isBadRequest());

        // Then - Verify no vote was created
        Vote authorVote = voteMapper.selectActiveByUserAndNovel(authorUser.getUuid(), testNovel.getId());
        assertThat(authorVote).isNull();
    }


    /**
     * Test database transaction rollback on vote error
     */
    @Test
    void testDatabaseTransactionRollback_OnVoteError() throws Exception {
        // Given - Invalid novel ID
        Integer invalidNovelId = 99999;

        // When - Try to vote for non-existent novel
        mockMvc.perform(post("/api/novels/" + invalidNovelId + "/vote")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());

        // Then - Verify no vote was created
        Vote invalidVote = voteMapper.selectActiveByUserAndNovel(testUser.getUuid(), invalidNovelId);
        assertThat(invalidVote).isNull();
    }

    /**
     * Helper method to create test data
     */
    private void createTestData() {
        // Create test user
        testUser = createTestUser("voteuser@example.com", "voteuser");
        userMapper.insert(testUser);

        // Create author user
        authorUser = createTestUser("voterauthor@example.com", "voterauthor");
        authorUser.setIsAuthor(true);
        userMapper.insert(authorUser);

        // Create another user
        anotherUser = createTestUser("anothervoteuser@example.com", "anothervoteuser");
        userMapper.insert(anotherUser);

        // Create test novel
        testNovel = createTestNovel("Vote Test Novel", "A novel for testing votes");
        testNovel.setAuthorId(authorUser.getUuid());
        novelMapper.insert(testNovel);

        // Generate tokens
        userToken = jwtUtil.generateAccessToken(testUser);
        authorToken = jwtUtil.generateAccessToken(authorUser);
    }

    /**
     * Helper method to create test user
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

    /**
     * Helper method to create test novel
     */
    private Novel createTestNovel(String title, String description) {
        Novel novel = new Novel();
        novel.setId(Math.abs(UUID.randomUUID().hashCode()));
        novel.setUuid(UUID.randomUUID());
        novel.setTitle(title);
        novel.setSynopsis(description);
        novel.setCategoryId(1); // Fantasy category
        novel.setStatus(2); // PUBLISHED status
        novel.setVoteCnt(0); // Initialize vote count to 0
        novel.setCreateTime(new Date());
        novel.setUpdateTime(new Date());
        return novel;
    }

    /**
     * Helper method to create test vote
     */
    private Vote createTestVote(UUID userId, Integer novelId, boolean isActive) {
        Vote vote = new Vote();
        vote.setUserId(userId);
        vote.setNovelId(novelId);
        vote.setIsActive(isActive);
        Date now = new Date();
        vote.setCreateTime(now);
        vote.setUpdateTime(now);
        return vote;
    }
}
