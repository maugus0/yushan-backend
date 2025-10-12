package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.AdminUserFilterDTO;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.User;
import com.yushan.backend.enums.UserStatus;
import com.yushan.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private UUID testUserUuid;

    @BeforeEach
    void setUp() {
        testUserUuid = UUID.randomUUID();
        testUser = new User();
        testUser.setUuid(testUserUuid);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
    }

    @Nested
    @DisplayName("listUsers Tests")
    class ListUsers {
        @Test
        @DisplayName("Should return a paginated list of users")
        void shouldReturnPaginatedUsers() {
            // Given
            AdminUserFilterDTO filter = new AdminUserFilterDTO();
            filter.setPage(0);
            filter.setSize(10);
            int offset = 0;
            List<User> users = Collections.singletonList(testUser);

            when(userMapper.countUsersForAdmin(filter)).thenReturn(1L);
            when(userMapper.selectUsersForAdmin(filter, offset)).thenReturn(users);

            // When
            PageResponseDTO<UserProfileResponseDTO> result = adminService.listUsers(filter);

            // Then
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            assertEquals(testUser.getUsername(), result.getContent().get(0).getUsername());
            verify(userMapper).countUsersForAdmin(filter);
            verify(userMapper).selectUsersForAdmin(filter, offset);
        }
    }

    @Nested
    @DisplayName("getUserDetail Tests")
    class GetUserDetail {
        @Test
        @DisplayName("Should return user details when user exists")
        void shouldReturnUserDetails() {
            // Given
            UserProfileResponseDTO userProfile = new UserProfileResponseDTO();
            userProfile.setUuid(testUserUuid.toString());
            when(userService.getUserProfile(testUserUuid)).thenReturn(userProfile);

            // When
            UserProfileResponseDTO result = adminService.getUserDetail(testUserUuid);

            // Then
            assertNotNull(result);
            assertEquals(testUserUuid.toString(), result.getUuid());
            verify(userService).getUserProfile(testUserUuid);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user does not exist")
        void shouldThrowWhenUserNotFound() {
            // Given
            when(userService.getUserProfile(testUserUuid)).thenReturn(null);

            // When & Then
            assertThrows(ResourceNotFoundException.class, () -> adminService.getUserDetail(testUserUuid));
        }
    }

    @Nested
    @DisplayName("updateUserStatus Tests")
    class UpdateUserStatus {
        @Test
        @DisplayName("Should update user status successfully")
        void shouldUpdateStatus() {
            // Given
            when(userMapper.selectByPrimaryKey(testUserUuid)).thenReturn(testUser);

            // When
            adminService.updateUserStatus(testUserUuid, UserStatus.BANNED);

            // Then
            verify(userMapper).updateByPrimaryKeySelective(argThat(user ->
                    user.getUuid().equals(testUserUuid) &&
                            user.getStatus().equals(UserStatus.BANNED.ordinal())
            ));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user to update is not found")
        void shouldThrowWhenUpdatingNonExistentUser() {
            // Given
            when(userMapper.selectByPrimaryKey(testUserUuid)).thenReturn(null);

            // When & Then
            assertThrows(ResourceNotFoundException.class, () ->
                    adminService.updateUserStatus(testUserUuid, UserStatus.BANNED));
            verify(userMapper, never()).updateByPrimaryKeySelective(any());
        }
    }
}