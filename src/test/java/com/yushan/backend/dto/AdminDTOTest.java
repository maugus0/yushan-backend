package com.yushan.backend.dto;

import com.yushan.backend.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Admin DTO Tests")
class AdminDTOTest {

    @Test
    @DisplayName("Test AdminPromoteRequestDTO - No args constructor")
    void testAdminPromoteRequestDTONoArgsConstructor() {
        // Test no-args constructor
        AdminPromoteRequestDTO dto1 = new AdminPromoteRequestDTO();
        assertNotNull(dto1);
        assertNull(dto1.getEmail());
        
        // Set email
        dto1.setEmail("test@example.com");
        assertEquals("test@example.com", dto1.getEmail());
    }
    
    @Test
    @DisplayName("Test AdminPromoteRequestDTO - All args constructor")
    void testAdminPromoteRequestDTOAllArgsConstructor() {
        // Test all-args constructor with email
        AdminPromoteRequestDTO dto1 = new AdminPromoteRequestDTO("admin@example.com");
        assertEquals("admin@example.com", dto1.getEmail());
        
        // Test with different email
        AdminPromoteRequestDTO dto2 = new AdminPromoteRequestDTO("user@test.com");
        assertEquals("user@test.com", dto2.getEmail());
        
        // Test with null email
        AdminPromoteRequestDTO dto3 = new AdminPromoteRequestDTO(null);
        assertNull(dto3.getEmail());
    }
    
    @Test
    @DisplayName("Test AdminPromoteRequestDTO - Getter and Setter")
    void testAdminPromoteRequestDTOGetterSetter() {
        AdminPromoteRequestDTO dto = new AdminPromoteRequestDTO();
        
        // Test setter and getter with various email formats
        dto.setEmail("admin@example.com");
        assertEquals("admin@example.com", dto.getEmail());
        
        dto.setEmail("another@example.com");
        assertEquals("another@example.com", dto.getEmail());
        
        dto.setEmail("test.user@example.co.uk");
        assertEquals("test.user@example.co.uk", dto.getEmail());
        
        // Test with null
        dto.setEmail(null);
        assertNull(dto.getEmail());
        
        // Test with empty string
        dto.setEmail("");
        assertEquals("", dto.getEmail());
    }
    
    @Test
    @DisplayName("Test AdminPromoteRequestDTO - toString")
    void testAdminPromoteRequestDTOToString() {
        AdminPromoteRequestDTO dto = new AdminPromoteRequestDTO("admin@example.com");
        String toString = dto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("AdminPromoteRequestDTO"));
        assertTrue(toString.contains("email"));
    }

    @Test
    @DisplayName("Test AdminUpdateUserDTO")
    void testAdminUpdateUserDTO() {
        AdminUpdateUserDTO dto = new AdminUpdateUserDTO();
        dto.setStatus(UserStatus.NORMAL);
        
        assertEquals(UserStatus.NORMAL, dto.getStatus());
        
        // Test all statuses
        dto.setStatus(UserStatus.SUSPENDED);
        assertEquals(UserStatus.SUSPENDED, dto.getStatus());
        
        dto.setStatus(UserStatus.BANNED);
        assertEquals(UserStatus.BANNED, dto.getStatus());
    }

    @Test
    @DisplayName("Test AdminUpdateUserDTO with SUSPENDED")
    void testAdminUpdateUserDTOWithSuspended() {
        AdminUpdateUserDTO dto = new AdminUpdateUserDTO();
        dto.setStatus(UserStatus.SUSPENDED);
        
        assertEquals(UserStatus.SUSPENDED, dto.getStatus());
    }

    @Test
    @DisplayName("Test AdminUserFilterDTO")
    void testAdminUserFilterDTO() {
        AdminUserFilterDTO dto = new AdminUserFilterDTO();
        
        assertEquals(0, dto.getPage());
        assertEquals(20, dto.getSize());
        assertNull(dto.getStatus());
        assertNull(dto.getIsAdmin());
        assertNull(dto.getIsAuthor());
        assertEquals("createTime", dto.getSortBy());
        assertEquals("desc", dto.getSortOrder());
    }

    @Test
    @DisplayName("Test AdminUserFilterDTO with filters")
    void testAdminUserFilterDTOWithFilters() {
        AdminUserFilterDTO dto = new AdminUserFilterDTO();
        dto.setPage(1);
        dto.setSize(10);
        dto.setStatus(UserStatus.NORMAL);
        dto.setIsAdmin(true);
        dto.setIsAuthor(false);
        dto.setSortBy("username");
        dto.setSortOrder("asc");
        
        assertEquals(1, dto.getPage());
        assertEquals(10, dto.getSize());
        assertEquals(UserStatus.NORMAL, dto.getStatus());
        assertTrue(dto.getIsAdmin());
        assertFalse(dto.getIsAuthor());
        assertEquals("username", dto.getSortBy());
        assertEquals("asc", dto.getSortOrder());
    }

    @Test
    @DisplayName("Test AdminUserFilterDTO with all args constructor")
    void testAdminUserFilterDTOAllArgsConstructor() {
        AdminUserFilterDTO dto = new AdminUserFilterDTO(
            2, 15, UserStatus.NORMAL, true, false, "email", "asc"
        );
        
        assertEquals(2, dto.getPage());
        assertEquals(15, dto.getSize());
        assertEquals(UserStatus.NORMAL, dto.getStatus());
        assertTrue(dto.getIsAdmin());
        assertFalse(dto.getIsAuthor());
        assertEquals("email", dto.getSortBy());
        assertEquals("asc", dto.getSortOrder());
        
        // Test equals, hashCode, and canEqual methods
        AdminPromoteRequestDTO dto3 = new AdminPromoteRequestDTO();
        dto3.setEmail("admin@example.com");
        
        AdminPromoteRequestDTO dto4 = new AdminPromoteRequestDTO();
        dto4.setEmail("admin@example.com");
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
        assertNotEquals(dto3, null);
        assertEquals(dto3, dto3);
        assertTrue(dto3.canEqual(dto4));
        
        // Test AdminUpdateUserDTO equals, hashCode, canEqual
        AdminUpdateUserDTO dto5 = new AdminUpdateUserDTO();
        dto5.setStatus(UserStatus.NORMAL);
        
        AdminUpdateUserDTO dto6 = new AdminUpdateUserDTO();
        dto6.setStatus(UserStatus.NORMAL);
        
        assertEquals(dto5, dto6);
        assertEquals(dto5.hashCode(), dto6.hashCode());
        assertNotEquals(dto5, null);
        assertEquals(dto5, dto5);
        assertTrue(dto5.canEqual(dto6));
        
        // Test AdminUserFilterDTO equals, hashCode, canEqual
        AdminUserFilterDTO dto7 = new AdminUserFilterDTO();
        dto7.setStatus(UserStatus.NORMAL);
        dto7.setPage(0);
        dto7.setSize(10);
        
        AdminUserFilterDTO dto8 = new AdminUserFilterDTO();
        dto8.setStatus(UserStatus.NORMAL);
        dto8.setPage(0);
        dto8.setSize(10);
        
        assertEquals(dto7, dto8);
        assertEquals(dto7.hashCode(), dto8.hashCode());
        assertNotEquals(dto7, null);
        assertEquals(dto7, dto7);
        assertTrue(dto7.canEqual(dto8));
    }
}

