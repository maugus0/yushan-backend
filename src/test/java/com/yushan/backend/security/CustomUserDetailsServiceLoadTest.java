package com.yushan.backend.security;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Load Tests")
class CustomUserDetailsServiceLoadTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Test loadUserByUsername - user found")
    void testLoadUserByUsernameUserFound() {
        String email = "test@example.com";
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername("testuser");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setStatus(1);
        
        when(userMapper.selectByEmail(email)).thenReturn(user);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("hashed", userDetails.getPassword());
        verify(userMapper).selectByEmail(email);
    }

    @Test
    @DisplayName("Test loadUserByUsername - user not found")
    void testLoadUserByUsernameUserNotFound() {
        String email = "notfound@example.com";
        
        when(userMapper.selectByEmail(email)).thenReturn(null);
        
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
        
        verify(userMapper).selectByEmail(email);
    }

    @Test
    @DisplayName("Test loadUserByEmail - user found")
    void testLoadUserByEmailUserFound() {
        String email = "test@example.com";
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername("testuser");
        user.setHashPassword("hashed");
        user.setIsAuthor(true);
        user.setIsAdmin(false);
        user.setStatus(1);
        
        when(userMapper.selectByEmail(email)).thenReturn(user);
        
        UserDetails userDetails = userDetailsService.loadUserByEmail(email);
        
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_AUTHOR")));
        verify(userMapper).selectByEmail(email);
    }

    @Test
    @DisplayName("Test loadUserByEmail - user not found")
    void testLoadUserByEmailUserNotFound() {
        String email = "notfound@example.com";
        
        when(userMapper.selectByEmail(email)).thenReturn(null);
        
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByEmail(email);
        });
        
        verify(userMapper).selectByEmail(email);
    }

    @Test
    @DisplayName("Test loadUserByUsername - admin user")
    void testLoadUserByUsernameAdminUser() {
        String email = "admin@example.com";
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername("admin");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(true);
        user.setStatus(1);
        
        when(userMapper.selectByEmail(email)).thenReturn(user);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        
        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}

