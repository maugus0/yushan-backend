package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private String testEmail;
    private UserProfileResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        
        testUser = new User();
        testUser.setUuid(UUID.randomUUID());
        testUser.setEmail(testEmail);
        testUser.setUsername("testuser");
        testUser.setEmailVerified(true);
        testUser.setAvatarUrl("https://example.com/avatar.jpg");
        testUser.setGender(1);
        testUser.setLastLogin(new Date());
        testUser.setLastActive(new Date());
        testUser.setIsAuthor(false);
        testUser.setIsAdmin(false);

        expectedResponse = new UserProfileResponseDTO();
        expectedResponse.setEmail(testEmail);
        expectedResponse.setUsername("testuser");
        expectedResponse.setIsAdmin(true);
    }

    @Test
    void promoteToAdmin_Success() {
        // Given
        when(userMapper.selectByEmail(testEmail)).thenReturn(testUser);
        when(userService.getUserProfile(testUser.getUuid())).thenReturn(expectedResponse);

        // When
        UserProfileResponseDTO result = adminService.promoteToAdmin(testEmail);

        // Then
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        assertTrue(result.getIsAdmin());
        assertTrue(testUser.getIsAdmin());
        verify(userMapper).updateByPrimaryKeySelective(testUser);
    }

    @Test
    void promoteToAdmin_UserNotFound() {
        // Given
        when(userMapper.selectByEmail(testEmail)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> adminService.promoteToAdmin(testEmail));
        
        assertEquals("User not found with email: " + testEmail, exception.getMessage());
        verify(userMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void promoteToAdmin_UserAlreadyAdmin() {
        // Given
        testUser.setIsAdmin(true);
        when(userMapper.selectByEmail(testEmail)).thenReturn(testUser);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> adminService.promoteToAdmin(testEmail));
        
        assertEquals("User is already an admin", exception.getMessage());
        verify(userMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void promoteToAdmin_EmptyEmail() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> adminService.promoteToAdmin(""));
        
        assertEquals("Email is required", exception.getMessage());
        verify(userMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void promoteToAdmin_NullEmail() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> adminService.promoteToAdmin(null));
        
        assertEquals("Email is required", exception.getMessage());
        verify(userMapper, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void promoteToAdmin_WhitespaceEmail() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> adminService.promoteToAdmin("   "));
        
        assertEquals("Email is required", exception.getMessage());
        verify(userMapper, never()).updateByPrimaryKeySelective(any());
    }
}
