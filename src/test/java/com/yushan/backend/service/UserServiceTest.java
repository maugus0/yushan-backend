package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.dto.UserProfileUpdateRequestDTO;
import com.yushan.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userMapper = Mockito.mock(UserMapper.class);
        userService = new UserService();

        // Inject mock mapper via reflection (simple without Spring context)
        try {
            java.lang.reflect.Field f = UserService.class.getDeclaredField("userMapper");
            f.setAccessible(true);
            f.set(userService, userMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUserProfile_returnsNull_whenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userMapper.selectByPrimaryKey(id)).thenReturn(null);

        assertNull(userService.getUserProfile(id));
        verify(userMapper).selectByPrimaryKey(id);
    }

    @Test
    void updateUserProfileSelective_updatesOnlyProvidedFields_andReturnsDto() {
        UUID id = UUID.randomUUID();

        User existing = new User();
        existing.setUuid(id);
        existing.setEmail("old@example.com");
        existing.setUsername("oldname");
        existing.setAvatarUrl("old.png");
        existing.setProfileDetail("old profile");
        existing.setGender(1);
        existing.setReadTime(10.5f);
        existing.setReadBookNum(5);

        when(userMapper.selectByPrimaryKey(id)).thenReturn(existing);

        User after = new User();
        after.setUuid(id);
        after.setEmail("old@example.com");
        after.setUsername("newname");
        after.setAvatarUrl("new.png");
        after.setProfileDetail("new profile");
        after.setGender(2);
        after.setReadTime(10.5f);
        after.setReadBookNum(5);
        after.setUpdateTime(new Date());

        when(userMapper.selectByPrimaryKey(id)).thenReturn(existing, after);

        UserProfileUpdateRequestDTO req = new UserProfileUpdateRequestDTO();
        req.setUsername("newname");
        req.setAvatarUrl("new.png");
        req.setProfileDetail("new profile");
        req.setGender("2");

        UserProfileResponseDTO dto = userService.updateUserProfileSelective(id, req);

        // verify mapper called with selective update
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateByPrimaryKeySelective(captor.capture());
        User updatedArg = captor.getValue();
        assertEquals(id, updatedArg.getUuid());
        assertEquals("newname", updatedArg.getUsername());
        assertEquals("new.png", updatedArg.getAvatarUrl());
        assertEquals("new profile", updatedArg.getProfileDetail());
        assertEquals(2, updatedArg.getGender());
        assertNotNull(updatedArg.getUpdateTime());

        // validate returned DTO fields
        assertNotNull(dto);
        assertEquals(id.toString(), dto.getUuid());
        assertEquals("newname", dto.getUsername());
        assertEquals("new.png", dto.getAvatarUrl());
        assertEquals("new profile", dto.getProfileDetail());
        assertEquals(2, dto.getGender());
        assertEquals(10.5f, dto.getReadTime());
        assertEquals(5, dto.getReadBookNum());
    }
}


