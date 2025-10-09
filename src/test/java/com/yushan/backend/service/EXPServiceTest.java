package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EXPServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EXPService expService;

    private UUID testUuid;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUuid = UUID.randomUUID();
        testUser = new User();
        testUser.setUuid(testUuid);
        testUser.setExp(0.0f);
        testUser.setLevel(1);
    }

    @Test
    void addExp_ShouldUpdateUserExpAndLevel() {
        // Given
        when(userMapper.selectByPrimaryKey(testUuid)).thenReturn(testUser);

        // When
        expService.addExp(testUuid, 150.0f);

        // Then
        assertEquals(150.0f, testUser.getExp());
        assertEquals(2, testUser.getLevel());
        verify(userMapper).selectByPrimaryKey(testUuid);
        verify(userMapper).updateByPrimaryKey(testUser);
    }

    @Test
    void checkLevel_WithNullExp_ShouldReturnLevel1() {
        // When & Then
        assertEquals(1, expService.checkLevel(null));
    }

    @Test
    void checkLevel_BelowFirstThreshold_ShouldReturnLevel1() {
        // When & Then
        assertEquals(1, expService.checkLevel(50.0f));
    }

    @Test
    void checkLevel_AtFirstThreshold_ShouldReturnLevel2() {
        // When & Then
        assertEquals(2, expService.checkLevel(100.0f));
    }

    @Test
    void checkLevel_BetweenFirstAndSecondThreshold_ShouldReturnLevel2() {
        // When & Then
        assertEquals(2, expService.checkLevel(300.0f));
    }

    @Test
    void checkLevel_AtLastThreshold_ShouldReturnMaxLevel() {
        // When & Then
        assertEquals(5, expService.checkLevel(5000.0f));
    }

    @Test
    void checkLevel_AboveLastThreshold_ShouldReturnMaxLevel() {
        // When & Then
        assertEquals(5, expService.checkLevel(10000.0f));
    }
}
