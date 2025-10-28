package com.yushan.backend.dto;

import com.yushan.backend.enums.Gender;
import com.yushan.backend.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User DTO Tests")
class UserDTOTest {

    @Test
    @DisplayName("Test UserProfileUpdateRequestDTO")
    void testUserProfileUpdateRequestDTO() {
        UserProfileUpdateRequestDTO dto = new UserProfileUpdateRequestDTO();
        dto.setUsername("newuser");
        dto.setEmail("newemail@example.com");
        dto.setAvatarBase64("data:image/jpeg;base64,ABC123");
        dto.setProfileDetail("Updated profile");
        dto.setGender(Gender.FEMALE);
        dto.setVerificationCode("123456");
        
        assertEquals("newuser", dto.getUsername());
        assertEquals("newemail@example.com", dto.getEmail());
        assertEquals("data:image/jpeg;base64,ABC123", dto.getAvatarBase64());
        assertEquals("Updated profile", dto.getProfileDetail());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertEquals("123456", dto.getVerificationCode());
        
        // Test email normalization - valid email with spaces
        dto.setEmail("Test@Example.COM");
        assertEquals("test@example.com", dto.getEmail());
        
        // Test username validation - valid username
        dto.setUsername("valid_user123");
        assertEquals("valid_user123", dto.getUsername());
        
        // Test username validation - too short (should throw exception)
        assertThrows(IllegalArgumentException.class, () -> {
            dto.setUsername("ab");
        });
        
        // Test username validation - invalid characters (should throw exception)
        assertThrows(IllegalArgumentException.class, () -> {
            dto.setUsername("user@name");
        });
        
        // Test email validation - invalid format (should throw exception)
        assertThrows(IllegalArgumentException.class, () -> {
            dto.setEmail("invalid-email");
        });
    }

    @Test
    @DisplayName("Test UserProfileResponseDTO")
    void testUserProfileResponseDTO() {
        Date now = new Date();
        Date birthday = new Date(System.currentTimeMillis() - 86400000L * 365 * 25);
        
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setUuid("test-uuid");
        dto.setEmail("user@example.com");
        dto.setUsername("testuser");
        dto.setAvatarUrl("http://example.com/avatar.jpg");
        dto.setProfileDetail("Profile details");
        dto.setBirthday(birthday);
        dto.setGender(Gender.MALE);
        dto.setStatus(UserStatus.NORMAL);
        dto.setIsAuthor(true);
        dto.setIsAdmin(false);
        dto.setLevel(5);
        dto.setExp(100.0f);
        dto.setYuan(50.0f);
        dto.setReadTime(500.0f);
        dto.setReadBookNum(10);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        dto.setLastActive(now);
        
        assertEquals("test-uuid", dto.getUuid());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals("testuser", dto.getUsername());
        assertEquals("http://example.com/avatar.jpg", dto.getAvatarUrl());
        assertEquals("Profile details", dto.getProfileDetail());
        assertNotNull(dto.getBirthday());
        assertEquals(Gender.MALE, dto.getGender());
        assertEquals(UserStatus.NORMAL, dto.getStatus());
        assertTrue(dto.getIsAuthor());
        assertFalse(dto.getIsAdmin());
        assertEquals(5, dto.getLevel());
        assertEquals(100.0f, dto.getExp());
        assertEquals(50.0f, dto.getYuan());
        assertEquals(500.0f, dto.getReadTime());
        assertEquals(10, dto.getReadBookNum());
        assertNotNull(dto.getCreateTime());
        assertNotNull(dto.getUpdateTime());
        assertNotNull(dto.getLastActive());
        
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
        
        // Test null Date values
        dto.setBirthday(null);
        dto.setCreateTime(null);
        dto.setUpdateTime(null);
        dto.setLastActive(null);
        assertNull(dto.getBirthday());
        assertNull(dto.getCreateTime());
        assertNull(dto.getUpdateTime());
        assertNull(dto.getLastActive());
        
        // Test all status values
        dto.setStatus(UserStatus.NORMAL);
        assertEquals(UserStatus.NORMAL, dto.getStatus());
        dto.setStatus(UserStatus.SUSPENDED);
        assertEquals(UserStatus.SUSPENDED, dto.getStatus());
        dto.setStatus(UserStatus.BANNED);
        assertEquals(UserStatus.BANNED, dto.getStatus());
        
        // Test all gender values
        dto.setGender(Gender.MALE);
        assertEquals(Gender.MALE, dto.getGender());
        dto.setGender(Gender.FEMALE);
        assertEquals(Gender.FEMALE, dto.getGender());
        dto.setGender(Gender.UNKNOWN);
        assertEquals(Gender.UNKNOWN, dto.getGender());
        
        // Test with null/zero values
        dto.setExp(null);
        dto.setYuan(null);
        dto.setReadTime(null);
        dto.setReadBookNum(null);
        assertNull(dto.getExp());
        assertNull(dto.getYuan());
        assertNull(dto.getReadTime());
        assertNull(dto.getReadBookNum());
        
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
        UserProfileResponseDTO dto2 = new UserProfileResponseDTO();
        dto2.setUuid("test-uuid");
        dto2.setEmail("test@example.com");
        dto2.setUsername("testuser");
        
        UserProfileResponseDTO dto3 = new UserProfileResponseDTO();
        dto3.setUuid("test-uuid");
        dto3.setEmail("test@example.com");
        dto3.setUsername("testuser");
        
        assertEquals(dto2, dto3);
        assertEquals(dto2.hashCode(), dto3.hashCode());
        assertNotEquals(dto2, null);
        assertEquals(dto2, dto2);
        assertTrue(dto2.canEqual(dto3));
    }


    @Test
    @DisplayName("Test AuthorUpgradeRequestDTO")
    void testAuthorUpgradeRequestDTO() {
        AuthorUpgradeRequestDTO dto = new AuthorUpgradeRequestDTO();
        dto.setVerificationCode("123456");
        
        assertEquals("123456", dto.getVerificationCode());
    }

    @Test
    @DisplayName("Test AuthorUpgradeRequestDTO all args constructor")
    void testAuthorUpgradeRequestDTOAllArgs() {
        AuthorUpgradeRequestDTO dto = new AuthorUpgradeRequestDTO("654321");
        
        assertEquals("654321", dto.getVerificationCode());
        
        // Test default constructor
        AuthorUpgradeRequestDTO dto2 = new AuthorUpgradeRequestDTO();
        dto2.setVerificationCode("123456");
        assertEquals("123456", dto2.getVerificationCode());
        
        // Test with different code
        dto.setVerificationCode("999999");
        assertEquals("999999", dto.getVerificationCode());
    }

    @Test
    @DisplayName("Test UserProfileUpdateResponseDTO")
    void testUserProfileUpdateResponseDTO() {
        UserProfileResponseDTO profile = new UserProfileResponseDTO();
        profile.setUuid("test-uuid");
        profile.setUsername("testuser");
        profile.setEmail("test@example.com");
        
        UserProfileUpdateResponseDTO dto = new UserProfileUpdateResponseDTO(profile, false);
        
        assertNotNull(dto.getProfile());
        assertEquals("test-uuid", dto.getProfile().getUuid());
        assertEquals("testuser", dto.getProfile().getUsername());
        assertFalse(dto.isEmailChanged());
        
        // Test defensive copying
        profile.setUsername("modified");
        assertEquals("testuser", dto.getProfile().getUsername());
        
        // Test with tokens
        UserProfileUpdateResponseDTO dto2 = new UserProfileUpdateResponseDTO(
            profile, true, "accessToken", "refreshToken", "Bearer", 3600L
        );
        
        assertEquals("accessToken", dto2.getAccessToken());
        assertEquals("refreshToken", dto2.getRefreshToken());
        assertEquals("Bearer", dto2.getTokenType());
        assertEquals(3600L, dto2.getExpiresIn());
        assertTrue(dto2.isEmailChanged());
        
        // Test equals, hashCode, and canEqual methods
        AuthorUpgradeRequestDTO dto3 = new AuthorUpgradeRequestDTO();
        dto3.setVerificationCode("123456");
        
        AuthorUpgradeRequestDTO dto4 = new AuthorUpgradeRequestDTO();
        dto4.setVerificationCode("123456");
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, null);
        assertEquals(dto3, dto3);
        assertTrue(dto3.canEqual(dto4));
        
        // Test UserProfileUpdateRequestDTO equals, hashCode, canEqual
        UserProfileUpdateRequestDTO dto5 = new UserProfileUpdateRequestDTO();
        dto5.setUsername("user");
        dto5.setEmail("user@example.com");
        
        UserProfileUpdateRequestDTO dto6 = new UserProfileUpdateRequestDTO();
        dto6.setUsername("user");
        dto6.setEmail("user@example.com");
        
        assertEquals(dto5, dto6);
        assertEquals(dto5.hashCode(), dto6.hashCode());
        assertNotEquals(dto5, null);
        assertEquals(dto5, dto5);
        assertTrue(dto5.canEqual(dto6));
        
        // Test UserProfileUpdateResponseDTO equals, hashCode, canEqual
        UserProfileUpdateResponseDTO dto7 = new UserProfileUpdateResponseDTO();
        UserProfileResponseDTO profile1 = new UserProfileResponseDTO();
        profile1.setUuid("uuid");
        dto7.setProfile(profile1);
        
        UserProfileUpdateResponseDTO dto8 = new UserProfileUpdateResponseDTO();
        UserProfileResponseDTO profile2 = new UserProfileResponseDTO();
        profile2.setUuid("uuid");
        dto8.setProfile(profile2);
        
        assertEquals(dto7, dto8);
        assertEquals(dto7.hashCode(), dto8.hashCode());
        assertNotEquals(dto7, null);
        assertEquals(dto7, dto7);
        assertTrue(dto7.canEqual(dto8));
    }
}

