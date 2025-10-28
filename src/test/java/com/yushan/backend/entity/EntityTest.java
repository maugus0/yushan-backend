package com.yushan.backend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entity Tests")
class EntityTest {

    @Test
    @DisplayName("Test User entity getters and setters")
    void testUserEntity() {
        UUID uuid = UUID.randomUUID();
        Date now = new Date();
        Date birthday = new Date(System.currentTimeMillis() - 86400000L * 365 * 25); // 25 years ago
        
        User user = new User();
        user.setUuid(uuid);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setHashPassword("hashed");
        user.setEmailVerified(true);
        user.setAvatarUrl("http://avatar.url");
        user.setProfileDetail("Profile");
        user.setBirthday(birthday);
        user.setGender(1);
        user.setStatus(1);
        user.setIsAuthor(true);
        user.setIsAdmin(false);
        user.setLevel(5);
        user.setExp(100.0f);
        user.setYuan(50.0f);
        user.setReadTime(1000.0f);
        user.setReadBookNum(10);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setLastLogin(now);
        user.setLastActive(now);
        
        assertEquals(uuid, user.getUuid());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashed", user.getHashPassword());
        assertTrue(user.getEmailVerified());
        assertEquals("http://avatar.url", user.getAvatarUrl());
        assertEquals("Profile", user.getProfileDetail());
        assertNotNull(user.getBirthday());
        assertEquals(1, user.getGender());
        assertEquals(1, user.getStatus());
        assertTrue(user.getIsAuthor());
        assertFalse(user.getIsAdmin());
        assertEquals(5, user.getLevel());
        assertEquals(100.0f, user.getExp());
        assertEquals(50.0f, user.getYuan());
        assertEquals(1000.0f, user.getReadTime());
        assertEquals(10, user.getReadBookNum());
        assertNotNull(user.getCreateTime());
        assertNotNull(user.getUpdateTime());
        assertNotNull(user.getLastLogin());
        assertNotNull(user.getLastActive());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        user.setBirthday(newDate);
        Date retrieved = user.getBirthday();
        assertNotSame(newDate, retrieved);
        
        // Test null values
        user.setEmail(null);
        user.setUsername(null);
        assertNull(user.getEmail());
        assertNull(user.getUsername());
        
        // Test trimming
        user.setEmail("  TEST@EXAMPLE.COM  ");
        assertEquals("TEST@EXAMPLE.COM", user.getEmail());
        
        user.setUsername("  username  ");
        assertEquals("username", user.getUsername());
    }

    @Test
    @DisplayName("Test Category entity getters and setters")
    void testCategoryEntity() {
        Date now = new Date();
        
        Category category = new Category();
        category.setId(1);
        category.setName("Fantasy");
        category.setDescription("Fantasy novels");
        category.setSlug("fantasy");
        category.setIsActive(true);
        category.setCreateTime(now);
        category.setUpdateTime(now);
        
        assertEquals(1, category.getId());
        assertEquals("Fantasy", category.getName());
        assertEquals("Fantasy novels", category.getDescription());
        assertEquals("fantasy", category.getSlug());
        assertTrue(category.getIsActive());
        assertNotNull(category.getCreateTime());
        assertNotNull(category.getUpdateTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        category.setCreateTime(newDate);
        Date retrieved = category.getCreateTime();
        assertNotSame(newDate, retrieved);
        
        // Test null values and trimming
        category.setName(null);
        category.setDescription(null);
        category.setSlug(null);
        assertNull(category.getName());
        assertNull(category.getDescription());
        assertNull(category.getSlug());
        
        category.setName("  Test  ");
        assertEquals("Test", category.getName());
    }

    @Test
    @DisplayName("Test Novel entity getters and setters")
    void testNovelEntity() {
        UUID uuid = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Date now = new Date();
        
        Novel novel = new Novel();
        novel.setId(1);
        novel.setUuid(uuid);
        novel.setTitle("Test Novel");
        novel.setAuthorId(authorId);
        novel.setAuthorName("Author");
        novel.setCategoryId(1);
        novel.setSynopsis("Synopsis");
        novel.setCoverImgUrl("http://cover.url");
        novel.setStatus(1);
        novel.setIsCompleted(false);
        novel.setChapterCnt(10);
        novel.setWordCnt(10000L);
        novel.setAvgRating(4.5f);
        novel.setReviewCnt(5);
        novel.setViewCnt(1000L);
        novel.setVoteCnt(50);
        novel.setYuanCnt(100.0f);
        novel.setCreateTime(now);
        novel.setUpdateTime(now);
        novel.setPublishTime(now);
        
        assertEquals(1, novel.getId());
        assertEquals(uuid, novel.getUuid());
        assertEquals("Test Novel", novel.getTitle());
        assertEquals(authorId, novel.getAuthorId());
        assertEquals("Author", novel.getAuthorName());
        assertEquals(1, novel.getCategoryId());
        assertEquals("Synopsis", novel.getSynopsis());
        assertEquals("http://cover.url", novel.getCoverImgUrl());
        assertEquals(1, novel.getStatus());
        assertFalse(novel.getIsCompleted());
        assertEquals(10, novel.getChapterCnt());
        assertEquals(10000L, novel.getWordCnt());
        assertEquals(4.5f, novel.getAvgRating());
        assertEquals(5, novel.getReviewCnt());
        assertEquals(1000L, novel.getViewCnt());
        assertEquals(50, novel.getVoteCnt());
        assertEquals(100.0f, novel.getYuanCnt());
        assertNotNull(novel.getCreateTime());
        assertNotNull(novel.getUpdateTime());
        assertNotNull(novel.getPublishTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        novel.setCreateTime(newDate);
        Date retrieved = novel.getCreateTime();
        assertNotSame(newDate, retrieved);
    }

    @Test
    @DisplayName("Test Chapter entity getters and setters")
    void testChapterEntity() {
        UUID uuid = UUID.randomUUID();
        Date now = new Date();
        
        Chapter chapter = new Chapter();
        chapter.setId(1);
        chapter.setUuid(uuid);
        chapter.setNovelId(1);
        chapter.setChapterNumber(1);
        chapter.setTitle("Chapter 1");
        chapter.setContent("Content");
        chapter.setWordCnt(1000);
        chapter.setIsPremium(false);
        chapter.setYuanCost(0.0f);
        chapter.setViewCnt(100L);
        chapter.setIsValid(true);
        chapter.setCreateTime(now);
        chapter.setUpdateTime(now);
        chapter.setPublishTime(now);
        
        assertEquals(1, chapter.getId());
        assertEquals(uuid, chapter.getUuid());
        assertEquals(1, chapter.getNovelId());
        assertEquals(1, chapter.getChapterNumber());
        assertEquals("Chapter 1", chapter.getTitle());
        assertEquals("Content", chapter.getContent());
        assertEquals(1000, chapter.getWordCnt());
        assertFalse(chapter.getIsPremium());
        assertEquals(0.0f, chapter.getYuanCost());
        assertEquals(100L, chapter.getViewCnt());
        assertTrue(chapter.getIsValid());
        assertNotNull(chapter.getCreateTime());
        assertNotNull(chapter.getUpdateTime());
        assertNotNull(chapter.getPublishTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        chapter.setCreateTime(newDate);
        Date retrieved = chapter.getCreateTime();
        assertNotSame(newDate, retrieved);
    }

    @Test
    @DisplayName("Test Comment entity getters and setters")
    void testCommentEntity() {
        UUID userId = UUID.randomUUID();
        Date now = new Date();
        
        Comment comment = new Comment();
        comment.setId(1);
        comment.setUserId(userId);
        comment.setChapterId(1);
        comment.setContent("Comment content");
        comment.setLikeCnt(5);
        comment.setIsSpoiler(false);
        comment.setCreateTime(now);
        comment.setUpdateTime(now);
        
        assertEquals(1, comment.getId());
        assertEquals(userId, comment.getUserId());
        assertEquals(1, comment.getChapterId());
        assertEquals("Comment content", comment.getContent());
        assertEquals(5, comment.getLikeCnt());
        assertFalse(comment.getIsSpoiler());
        assertNotNull(comment.getCreateTime());
        assertNotNull(comment.getUpdateTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        comment.setCreateTime(newDate);
        Date retrieved = comment.getCreateTime();
        assertNotSame(newDate, retrieved);
    }

    @Test
    @DisplayName("Test Review entity getters and setters")
    void testReviewEntity() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Date now = new Date();
        
        Review review = new Review();
        review.setId(1);
        review.setUuid(uuid);
        review.setUserId(userId);
        review.setNovelId(1);
        review.setRating(5);
        review.setTitle("Great novel");
        review.setContent("Review content");
        review.setLikeCnt(10);
        review.setIsSpoiler(false);
        review.setCreateTime(now);
        review.setUpdateTime(now);
        
        assertEquals(1, review.getId());
        assertEquals(uuid, review.getUuid());
        assertEquals(userId, review.getUserId());
        assertEquals(1, review.getNovelId());
        assertEquals(5, review.getRating());
        assertEquals("Great novel", review.getTitle());
        assertEquals("Review content", review.getContent());
        assertEquals(10, review.getLikeCnt());
        assertFalse(review.getIsSpoiler());
        assertNotNull(review.getCreateTime());
        assertNotNull(review.getUpdateTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        review.setCreateTime(newDate);
        Date retrieved = review.getCreateTime();
        assertNotSame(newDate, retrieved);
        
        // Test trimming
        review.setTitle("  Title  ");
        assertEquals("Title", review.getTitle());
    }

    @Test
    @DisplayName("Test Vote entity getters and setters")
    void testVoteEntity() {
        Date now = new Date();
        
        Vote vote = new Vote();
        vote.setId(1);
        vote.setUserId(UUID.randomUUID());
        vote.setNovelId(1);
        vote.setCreateTime(now);
        vote.setUpdateTime(now);
        
        assertEquals(1, vote.getId());
        assertNotNull(vote.getUserId());
        assertEquals(1, vote.getNovelId());
        assertNotNull(vote.getCreateTime());
        assertNotNull(vote.getUpdateTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        vote.setCreateTime(newDate);
        Date retrieved = vote.getCreateTime();
        assertNotSame(newDate, retrieved);
        
        // Test null values
        vote.setCreateTime(null);
        assertNull(vote.getCreateTime());
    }

    @Test
    @DisplayName("Test Library entity getters and setters")
    void testLibraryEntity() {
        Date now = new Date();
        
        UUID uuid = UUID.randomUUID();
        Library library = new Library();
        library.setId(1);
        library.setUuid(uuid);
        library.setUserId(UUID.randomUUID());
        library.setCreateTime(now);
        library.setUpdateTime(now);
        
        assertEquals(1, library.getId());
        assertEquals(uuid, library.getUuid());
        assertNotNull(library.getUserId());
        assertNotNull(library.getCreateTime());
        assertNotNull(library.getUpdateTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        library.setCreateTime(newDate);
        Date retrieved = library.getCreateTime();
        assertNotSame(newDate, retrieved);
    }

    @Test
    @DisplayName("Test History entity getters and setters")
    void testHistoryEntity() {
        Date now = new Date();
        
        History history = new History();
        history.setId(1);
        history.setUserId(UUID.randomUUID());
        history.setNovelId(1);
        history.setChapterId(1);
        history.setCreateTime(now);
        history.setUpdateTime(now);
        
        assertEquals(1, history.getId());
        assertNotNull(history.getUserId());
        assertEquals(1, history.getNovelId());
        assertEquals(1, history.getChapterId());
        assertNotNull(history.getCreateTime());
        assertNotNull(history.getUpdateTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        history.setCreateTime(newDate);
        Date retrieved = history.getCreateTime();
        assertNotSame(newDate, retrieved);
    }

    @Test
    @DisplayName("Test Report entity getters and setters")
    void testReportEntity() {
        UUID uuid = UUID.randomUUID();
        Date now = new Date();
        
        Report report = new Report();
        report.setId(1);
        report.setUuid(uuid);
        report.setReporterId(UUID.randomUUID());
        report.setReportType("NOVEL");
        report.setContentType("NOVEL");
        report.setContentId(1);
        report.setReason("Spam");
        report.setStatus("PENDING");
        report.setResolvedBy(UUID.randomUUID());
        report.setAdminNotes("Notes");
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        
        assertEquals(1, report.getId());
        assertEquals(uuid, report.getUuid());
        assertNotNull(report.getReporterId());
        assertEquals("NOVEL", report.getReportType());
        assertEquals("NOVEL", report.getContentType());
        assertEquals(1, report.getContentId());
        assertEquals("Spam", report.getReason());
        assertEquals("PENDING", report.getStatus());
        assertNotNull(report.getResolvedBy());
        assertEquals("Notes", report.getAdminNotes());
        assertNotNull(report.getCreatedAt());
        assertNotNull(report.getUpdatedAt());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        report.setCreatedAt(newDate);
        Date retrieved = report.getCreatedAt();
        assertNotSame(newDate, retrieved);
    }

    @Test
    @DisplayName("Test NovelLibrary entity getters and setters")
    void testNovelLibraryEntity() {
        Date now = new Date();
        
        NovelLibrary novelLibrary = new NovelLibrary();
        novelLibrary.setId(1);
        novelLibrary.setLibraryId(1);
        novelLibrary.setNovelId(1);
        novelLibrary.setProgress(50);
        novelLibrary.setCreateTime(now);
        novelLibrary.setUpdateTime(now);
        
        assertEquals(1, novelLibrary.getId());
        assertEquals(1, novelLibrary.getLibraryId());
        assertEquals(1, novelLibrary.getNovelId());
        assertEquals(50, novelLibrary.getProgress());
        assertNotNull(novelLibrary.getCreateTime());
        assertNotNull(novelLibrary.getUpdateTime());
        
        // Test defensive copying for Date fields
        Date newDate = new Date();
        novelLibrary.setCreateTime(newDate);
        Date retrieved = novelLibrary.getCreateTime();
        assertNotSame(newDate, retrieved);
    }
}

