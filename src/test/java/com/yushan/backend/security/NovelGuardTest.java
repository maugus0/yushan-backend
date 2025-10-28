package com.yushan.backend.security;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NovelGuard Tests")
class NovelGuardTest {

    @Mock
    private NovelMapper novelMapper;

    @InjectMocks
    private NovelGuard novelGuard;

    private Authentication createMockAuthentication(String userId) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, null);
    }

    @Test
    @DisplayName("Test canEdit - user is the author")
    void testCanEditUserIsAuthor() {
        UUID userId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setAuthorId(userId);
        novel.setStatus(2); // PUBLISHED
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        boolean result = novelGuard.canEdit(1, createMockAuthentication(userId.toString()));
        
        assertTrue(result);
        verify(novelMapper).selectByPrimaryKey(1);
    }

    @Test
    @DisplayName("Test canEdit - user is not the author")
    void testCanEditUserIsNotAuthor() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setAuthorId(otherUserId);
        novel.setStatus(2);
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        boolean result = novelGuard.canEdit(1, createMockAuthentication(userId.toString()));
        
        assertFalse(result);
        verify(novelMapper).selectByPrimaryKey(1);
    }

    @Test
    @DisplayName("Test canEdit - novel not found")
    void testCanEditNovelNotFound() {
        UUID userId = UUID.randomUUID();
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(null);
        
        boolean result = novelGuard.canEdit(1, createMockAuthentication(userId.toString()));
        
        assertFalse(result);
        verify(novelMapper).selectByPrimaryKey(1);
    }

    @Test
    @DisplayName("Test canEdit - null novel author")
    void testCanEditNullAuthor() {
        UUID userId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setAuthorId(null);
        novel.setStatus(2);
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        boolean result = novelGuard.canEdit(1, createMockAuthentication(userId.toString()));
        
        assertFalse(result);
    }

    @Test
    @DisplayName("Test canEdit - null authentication")
    void testCanEditNullAuthentication() {
        boolean result = novelGuard.canEdit(1, null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Test canEdit - admin can edit")
    void testCanEditAdmin() {
        Novel novel = new Novel();
        novel.setAuthorId(UUID.randomUUID());
        novel.setStatus(2);
        
        lenient().when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        CustomUserDetails adminDetails = mock(CustomUserDetails.class);
        GrantedAuthority adminRole = () -> "ROLE_ADMIN";
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
            adminDetails, null, Arrays.asList(adminRole)
        );
        
        boolean result = novelGuard.canEdit(1, adminAuth);
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test canEdit - DRAFT status")
    void testCanEditDraftStatus() {
        UUID userId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setAuthorId(userId);
        novel.setStatus(0); // DRAFT
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        boolean result = novelGuard.canEdit(1, createMockAuthentication(userId.toString()));
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test canEdit - HIDDEN status")
    void testCanEditHiddenStatus() {
        UUID userId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setAuthorId(userId);
        novel.setStatus(3); // HIDDEN
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        boolean result = novelGuard.canEdit(1, createMockAuthentication(userId.toString()));
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test canEdit - ARCHIVED status")
    void testCanEditArchivedStatus() {
        UUID userId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setAuthorId(userId);
        novel.setStatus(4); // ARCHIVED
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        boolean result = novelGuard.canEdit(1, createMockAuthentication(userId.toString()));
        
        assertFalse(result);
    }

    @Test
    @DisplayName("Test canHideOrUnhide - user is the author")
    void testCanHideOrUnhideUserIsAuthor() {
        UUID userId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setAuthorId(userId);
        novel.setStatus(2); // PUBLISHED
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        boolean result = novelGuard.canHideOrUnhide(1, createMockAuthentication(userId.toString()));
        
        assertTrue(result);
        verify(novelMapper).selectByPrimaryKey(1);
    }

    @Test
    @DisplayName("Test canHideOrUnhide - HIDDEN status")
    void testCanHideOrUnhideHiddenStatus() {
        UUID userId = UUID.randomUUID();
        
        Novel novel = new Novel();
        novel.setAuthorId(userId);
        novel.setStatus(3); // HIDDEN
        
        when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        boolean result = novelGuard.canHideOrUnhide(1, createMockAuthentication(userId.toString()));
        
        assertTrue(result);
    }

    @Test
    @DisplayName("Test canHideOrUnhide - null authentication")
    void testCanHideOrUnhideNullAuthentication() {
        boolean result = novelGuard.canHideOrUnhide(1, null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Test canHideOrUnhide - admin can hide/unhide")
    void testCanHideOrUnhideAdmin() {
        Novel novel = new Novel();
        novel.setAuthorId(UUID.randomUUID());
        novel.setStatus(2);
        
        lenient().when(novelMapper.selectByPrimaryKey(1)).thenReturn(novel);
        
        CustomUserDetails adminDetails = mock(CustomUserDetails.class);
        GrantedAuthority adminRole = () -> "ROLE_ADMIN";
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
            adminDetails, null, Arrays.asList(adminRole)
        );
        
        boolean result = novelGuard.canHideOrUnhide(1, adminAuth);
        
        assertTrue(result);
    }
}
