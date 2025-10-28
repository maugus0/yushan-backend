package com.yushan.backend.security;

import com.yushan.backend.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Test
    @DisplayName("Test CustomUserDetails with regular user")
    void testCustomUserDetailsRegularUser() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setUsername("testuser");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setStatus(0); // NORMAL
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            new CustomUserDetailsService.CustomUserDetails(user);
        
        assertEquals(user.getUuid().toString(), userDetails.getUserId());
        assertEquals("testuser", userDetails.getProfileUsername());
        assertEquals("hashed", userDetails.getPassword());
        assertEquals("user@example.com", userDetails.getUsername());
        
        // Test authorities
        var authorities = userDetails.getAuthorities();
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertFalse(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHOR")));
        assertFalse(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        
        // Test account status
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Test CustomUserDetails with author")
    void testCustomUserDetailsAuthor() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("author@example.com");
        user.setUsername("author");
        user.setHashPassword("hashed");
        user.setIsAuthor(true);
        user.setIsAdmin(false);
        user.setStatus(0); // NORMAL
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            new CustomUserDetailsService.CustomUserDetails(user);
        
        var authorities = userDetails.getAuthorities();
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHOR")));
        assertFalse(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Test CustomUserDetails with admin")
    void testCustomUserDetailsAdmin() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("admin@example.com");
        user.setUsername("admin");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(true);
        user.setStatus(0); // NORMAL
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            new CustomUserDetailsService.CustomUserDetails(user);
        
        var authorities = userDetails.getAuthorities();
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertFalse(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_AUTHOR")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Test CustomUserDetails with suspended user")
    void testCustomUserDetailsSuspended() {
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("suspended@example.com");
        user.setUsername("suspended");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setStatus(2); // SUSPENDED
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            new CustomUserDetailsService.CustomUserDetails(user);
        
        assertFalse(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Test CustomUserDetails with null UUID")
    void testCustomUserDetailsNullUuid() {
        User user = new User();
        user.setUuid(null);
        user.setEmail("test@example.com");
        user.setUsername("test");
        user.setHashPassword("hashed");
        user.setIsAuthor(false);
        user.setIsAdmin(false);
        user.setStatus(0); // NORMAL
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            new CustomUserDetailsService.CustomUserDetails(user);
        
        assertNull(userDetails.getUserId());
    }
}

