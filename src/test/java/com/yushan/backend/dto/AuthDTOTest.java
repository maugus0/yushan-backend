package com.yushan.backend.dto;

import com.yushan.backend.enums.Gender;
import com.yushan.backend.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Auth DTO Tests")
class AuthDTOTest {

    @Test
    @DisplayName("Test UserLoginRequestDTO")
    void testUserLoginRequestDTO() {
        UserLoginRequestDTO dto = new UserLoginRequestDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("password123");
        
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
        
        // Test email normalization
        dto.setEmail("  USER@EXAMPLE.COM  ");
        assertEquals("user@example.com", dto.getEmail());
        
        // Test null email
        dto.setEmail(null);
        assertNull(dto.getEmail());
    }

    @Test
    @DisplayName("Test UserLoginRequestDTO email trimming and lowercase")
    void testUserLoginRequestDTOEmailNormalization() {
        UserLoginRequestDTO dto = new UserLoginRequestDTO();
        dto.setEmail("  User@Example.COM  ");
        
        assertEquals("user@example.com", dto.getEmail());
    }

    @Test
    @DisplayName("Test UserRegistrationRequestDTO")
    void testUserRegistrationRequestDTO() {
        Date birthday = new Date(System.currentTimeMillis() - 86400000L * 365 * 25);
        
        UserRegistrationRequestDTO dto = new UserRegistrationRequestDTO();
        dto.setUsername("testuser");
        dto.setEmail("user@example.com");
        dto.setPassword("password123");
        dto.setCode("123456");
        dto.setBirthday(birthday);
        dto.setGender(Gender.MALE);
        
        assertEquals("testuser", dto.getUsername());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("password123", dto.getPassword());
        assertEquals("123456", dto.getCode());
        assertNotNull(dto.getBirthday());
        assertEquals(Gender.MALE, dto.getGender());
        
        // Test email normalization
        dto.setEmail("  USER@EXAMPLE.COM  ");
        assertEquals("user@example.com", dto.getEmail());
        
        // Test defensive copying for Date
        Date testDate = new Date();
        dto.setBirthday(testDate);
        Date retrieved = dto.getBirthday();
        assertNotSame(testDate, retrieved);
        testDate.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        // Test default gender
        UserRegistrationRequestDTO dto2 = new UserRegistrationRequestDTO();
        assertEquals(Gender.UNKNOWN, dto2.getGender());
    }

    @Test
    @DisplayName("Test RefreshRequestDTO")
    void testRefreshRequestDTO() {
        RefreshRequestDTO dto = new RefreshRequestDTO();
        dto.setRefreshToken("refresh-token-123");
        
        assertEquals("refresh-token-123", dto.getRefreshToken());
        
        // Test with different tokens
        dto.setRefreshToken("another-refresh-token");
        assertEquals("another-refresh-token", dto.getRefreshToken());
        
        // Test with null
        dto.setRefreshToken(null);
        assertNull(dto.getRefreshToken());
    }
    
    @Test
    @DisplayName("Test EmailVerificationRequestDTO")
    void testEmailVerificationRequestDTO() {
        EmailVerificationRequestDTO dto = new EmailVerificationRequestDTO();
        dto.setEmail("user@example.com");
        
        assertEquals("user@example.com", dto.getEmail());
        
        // Test email normalization (trim and lowercase)
        dto.setEmail("  USER@EXAMPLE.COM  ");
        assertEquals("user@example.com", dto.getEmail());
        
        dto.setEmail("  Test@Example.COM  ");
        assertEquals("test@example.com", dto.getEmail());
        
        // Test null email
        dto.setEmail(null);
        assertNull(dto.getEmail());
        
        // Test getEmail with null check branch
        EmailVerificationRequestDTO dto2 = new EmailVerificationRequestDTO();
        assertNull(dto2.getEmail());
        
        dto2.setEmail("test@example.com");
        assertEquals("test@example.com", dto2.getEmail());
        
        // Test equals, hashCode, and canEqual methods
        EmailVerificationRequestDTO dto3 = new EmailVerificationRequestDTO();
        dto3.setEmail("test@example.com");
        
        EmailVerificationRequestDTO dto4 = new EmailVerificationRequestDTO();
        dto4.setEmail("test@example.com");
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, null);
        assertEquals(dto3, dto3);
        assertTrue(dto3.canEqual(dto4));
        
        // Test UserLoginRequestDTO equals, hashCode, canEqual
        UserLoginRequestDTO dto5 = new UserLoginRequestDTO();
        dto5.setEmail("user@example.com");
        dto5.setPassword("password");
        
        UserLoginRequestDTO dto6 = new UserLoginRequestDTO();
        dto6.setEmail("user@example.com");
        dto6.setPassword("password");
        
        assertEquals(dto5, dto6);
        assertEquals(dto5.hashCode(), dto6.hashCode());
        assertNotEquals(dto5, null);
        assertEquals(dto5, dto5);
        assertTrue(dto5.canEqual(dto6));
        
        // Test UserRegistrationRequestDTO equals, hashCode, canEqual
        Date birthday = new Date(System.currentTimeMillis() - 86400000L * 365 * 25);
        UserRegistrationRequestDTO dto7 = new UserRegistrationRequestDTO();
        dto7.setEmail("user@example.com");
        dto7.setUsername("user");
        dto7.setPassword("password");
        dto7.setBirthday(birthday);
        dto7.setGender(Gender.MALE);
        dto7.setCode("123456");
        
        UserRegistrationRequestDTO dto8 = new UserRegistrationRequestDTO();
        dto8.setEmail("user@example.com");
        dto8.setUsername("user");
        dto8.setPassword("password");
        dto8.setBirthday(birthday);
        dto8.setGender(Gender.MALE);
        dto8.setCode("123456");
        
        assertEquals(dto7, dto8);
        assertEquals(dto7.hashCode(), dto8.hashCode());
        assertNotEquals(dto7, null);
        assertEquals(dto7, dto7);
        assertTrue(dto7.canEqual(dto8));
        
        // Test RefreshRequestDTO equals, hashCode, canEqual
        RefreshRequestDTO dto9 = new RefreshRequestDTO();
        dto9.setRefreshToken("token");
        
        RefreshRequestDTO dto10 = new RefreshRequestDTO();
        dto10.setRefreshToken("token");
        
        assertEquals(dto9, dto10);
        assertEquals(dto9.hashCode(), dto10.hashCode());
        assertNotEquals(dto9, null);
        assertEquals(dto9, dto9);
        assertTrue(dto9.canEqual(dto10));
    }


    @Test
    @DisplayName("Test EmailVerificationRequestDTO email normalization")
    void testEmailVerificationRequestDTOEmailNormalization() {
        EmailVerificationRequestDTO dto = new EmailVerificationRequestDTO();
        dto.setEmail("  User@Example.COM  ");
        
        assertEquals("user@example.com", dto.getEmail());
    }

    @Test
    @DisplayName("Test UserRegistrationResponseDTO")
    void testUserRegistrationResponseDTO() {
        Date now = new Date();
        Date birthday = new Date(System.currentTimeMillis() - 86400000L * 365 * 20);
        
        UserRegistrationResponseDTO dto = new UserRegistrationResponseDTO();
        dto.setUuid("test-uuid");
        dto.setEmail("user@example.com");
        dto.setUsername("testuser");
        dto.setAvatarUrl("http://example.com/avatar.jpg");
        dto.setProfileDetail("Profile details");
        dto.setBirthday(birthday);
        dto.setGender(Gender.MALE);
        dto.setStatus(UserStatus.NORMAL);
        dto.setIsAuthor(false);
        dto.setIsAdmin(false);
        dto.setLevel(1);
        dto.setExp(0.0f);
        dto.setYuan(0.0f);
        dto.setReadTime(0.0f);
        dto.setReadBookNum(0);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        dto.setLastActive(now);
        dto.setAccessToken("token");
        dto.setRefreshToken("refresh");
        dto.setTokenType("Bearer");
        dto.setExpiresIn(3600L);
        // isFirstLoginToday is a boolean primitive, set via reflection or check default value
        
        assertEquals("test-uuid", dto.getUuid());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("testuser", dto.getUsername());
        assertEquals("http://example.com/avatar.jpg", dto.getAvatarUrl());
        assertEquals("Profile details", dto.getProfileDetail());
        assertNotNull(dto.getBirthday());
        assertEquals(Gender.MALE, dto.getGender());
        assertEquals(UserStatus.NORMAL, dto.getStatus());
        assertFalse(dto.getIsAuthor());
        assertFalse(dto.getIsAdmin());
        assertEquals(1, dto.getLevel());
        assertEquals(0.0f, dto.getExp());
        assertEquals(0.0f, dto.getYuan());
        assertEquals(0.0f, dto.getReadTime());
        assertEquals(0, dto.getReadBookNum());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        assertNotNull(dto.getLastActive());
        assertEquals("token", dto.getAccessToken());
        assertEquals("refresh", dto.getRefreshToken());
        assertEquals("Bearer", dto.getTokenType());
        assertEquals(3600L, dto.getExpiresIn());
        // isFirstLoginToday defaults to false for boolean primitive
        
        // Test defensive copying for Date fields
        Date testDate = new Date();
        dto.setBirthday(testDate);
        Date retrieved = dto.getBirthday();
        assertNotSame(testDate, retrieved);
        testDate.setTime(0);
        assertNotEquals(0, retrieved.getTime());
        
        dto.setCreateTime(testDate);
        retrieved = dto.getCreateTime();
        assertNotSame(testDate, retrieved);
        
        dto.setUpdateTime(testDate);
        retrieved = dto.getUpdateTime();
        assertNotSame(testDate, retrieved);
        
        dto.setLastActive(testDate);
        retrieved = dto.getLastActive();
        assertNotSame(testDate, retrieved);
        
        // Test all other fields
        dto.setUuid("new-uuid");
        dto.setEmail("new@example.com");
        dto.setUsername("newuser");
        dto.setAvatarUrl("http://avatar.url");
        dto.setProfileDetail("New profile");
        dto.setGender(Gender.FEMALE);
        dto.setStatus(UserStatus.SUSPENDED);
        dto.setIsAuthor(true);
        dto.setIsAdmin(false);
        dto.setLevel(5);
        dto.setExp(100.0f);
        dto.setYuan(200.0f);
        dto.setReadTime(300.0f);
        dto.setReadBookNum(10);
        dto.setAccessToken("new-access-token");
        dto.setRefreshToken("new-refresh-token");
        dto.setTokenType("Bearer");
        dto.setExpiresIn(7200L);
        
        assertEquals("new-uuid", dto.getUuid());
        assertEquals("new@example.com", dto.getEmail());
        assertEquals("newuser", dto.getUsername());
        assertEquals("http://avatar.url", dto.getAvatarUrl());
        assertEquals("New profile", dto.getProfileDetail());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertEquals(UserStatus.SUSPENDED, dto.getStatus());
        assertTrue(dto.getIsAuthor());
        assertFalse(dto.getIsAdmin());
        assertEquals(5, dto.getLevel());
        assertEquals(100.0f, dto.getExp());
        assertEquals(200.0f, dto.getYuan());
        assertEquals(300.0f, dto.getReadTime());
        assertEquals(10, dto.getReadBookNum());
        assertEquals("new-access-token", dto.getAccessToken());
        assertEquals("new-refresh-token", dto.getRefreshToken());
        assertEquals("Bearer", dto.getTokenType());
        assertEquals(7200L, dto.getExpiresIn());
        
        // Test null Date values
        dto.setBirthday(null);
        dto.setCreateTime(null);
        dto.setUpdateTime(null);
        dto.setLastActive(null);
        assertNull(dto.getBirthday());
        assertNull(dto.getCreateTime());
        assertNull(dto.getUpdateTime());
        assertNull(dto.getLastActive());
        
        // Test all remaining fields with different values
        dto.setUuid("another-uuid");
        dto.setEmail("another@example.com");
        dto.setUsername("anotheruser");
        dto.setAvatarUrl("http://another.avatar");
        dto.setProfileDetail("Another profile");
        dto.setGender(Gender.UNKNOWN);
        dto.setStatus(UserStatus.BANNED);
        dto.setIsAuthor(false);
        dto.setIsAdmin(true);
        dto.setLevel(10);
        dto.setExp(500.0f);
        dto.setYuan(1000.0f);
        dto.setReadTime(1000.0f);
        dto.setReadBookNum(50);
        dto.setAccessToken("another-access-token");
        dto.setRefreshToken("another-refresh-token");
        dto.setTokenType("JWT");
        dto.setExpiresIn(14400L);
        
        assertEquals("another-uuid", dto.getUuid());
        assertEquals("another@example.com", dto.getEmail());
        assertEquals("anotheruser", dto.getUsername());
        assertEquals("http://another.avatar", dto.getAvatarUrl());
        assertEquals("Another profile", dto.getProfileDetail());
        assertEquals(Gender.UNKNOWN, dto.getGender());
        assertEquals(UserStatus.BANNED, dto.getStatus());
        assertFalse(dto.getIsAuthor());
        assertTrue(dto.getIsAdmin());
        assertEquals(10, dto.getLevel());
        assertEquals(500.0f, dto.getExp());
        assertEquals(1000.0f, dto.getYuan());
        assertEquals(1000.0f, dto.getReadTime());
        assertEquals(50, dto.getReadBookNum());
        assertEquals("another-access-token", dto.getAccessToken());
        assertEquals("another-refresh-token", dto.getRefreshToken());
        assertEquals("JWT", dto.getTokenType());
        assertEquals(14400L, dto.getExpiresIn());
        
        // Test all Date getters/setters with null check branches for complete coverage
        Date testBirthday = new Date();
        dto.setBirthday(null);
        assertNull(dto.getBirthday());
        
        dto.setBirthday(testBirthday);
        Date retrievedBirthday = dto.getBirthday();
        assertNotNull(retrievedBirthday);
        assertNotSame(testBirthday, retrievedBirthday);
        
        Date testCreateTime = new Date();
        dto.setCreateTime(null);
        assertNull(dto.getCreateTime());
        
        dto.setCreateTime(testCreateTime);
        Date retrievedCreateTime = dto.getCreateTime();
        assertNotNull(retrievedCreateTime);
        assertNotSame(testCreateTime, retrievedCreateTime);
        
        Date testUpdateTime = new Date();
        dto.setUpdateTime(null);
        assertNull(dto.getUpdateTime());
        
        dto.setUpdateTime(testUpdateTime);
        Date retrievedUpdateTime = dto.getUpdateTime();
        assertNotNull(retrievedUpdateTime);
        assertNotSame(testUpdateTime, retrievedUpdateTime);
        
        Date testLastActive = new Date();
        dto.setLastActive(null);
        assertNull(dto.getLastActive());
        
        dto.setLastActive(testLastActive);
        Date retrievedLastActive = dto.getLastActive();
        assertNotNull(retrievedLastActive);
        assertNotSame(testLastActive, retrievedLastActive);
        
        // Test defensive copying - modify original date should not affect retrieved
        testBirthday.setTime(0);
        assertNotEquals(0, retrievedBirthday.getTime());
        
        testCreateTime.setTime(0);
        assertNotEquals(0, retrievedCreateTime.getTime());
        
        testUpdateTime.setTime(0);
        assertNotEquals(0, retrievedUpdateTime.getTime());
        
        testLastActive.setTime(0);
        assertNotEquals(0, retrievedLastActive.getTime());
        
        // Test equals, hashCode, and canEqual methods
        UserRegistrationResponseDTO dto2 = new UserRegistrationResponseDTO();
        dto2.setUuid("test-uuid");
        dto2.setEmail("test@example.com");
        dto2.setUsername("testuser");
        
        UserRegistrationResponseDTO dto3 = new UserRegistrationResponseDTO();
        dto3.setUuid("test-uuid");
        dto3.setEmail("test@example.com");
        dto3.setUsername("testuser");
        
        assertEquals(dto2, dto3);
        assertEquals(dto2.hashCode(), dto3.hashCode());
        assertNotEquals(dto2, null);
        assertEquals(dto2, dto2);
        assertTrue(dto2.canEqual(dto3));
    }
}

