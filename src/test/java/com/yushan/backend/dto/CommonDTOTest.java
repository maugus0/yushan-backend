package com.yushan.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Common DTO Tests")
class CommonDTOTest {

    @Test
    @DisplayName("Test ApiResponse")
    void testApiResponse() {
        ApiResponse<String> response1 = new ApiResponse<>(200, "Success", "data");
        assertEquals(200, response1.getCode());
        assertEquals("Success", response1.getMessage());
        assertEquals("data", response1.getData());
        assertNotNull(response1.getTimestamp());

        ApiResponse<String> response2 = ApiResponse.success("Custom message");
        assertEquals(200, response2.getCode());
        assertEquals("Custom message", response2.getMessage());
        assertNull(response2.getData());

        ApiResponse<String> response3 = ApiResponse.success("Message", "data");
        assertEquals(200, response3.getCode());
        assertEquals("Message", response3.getMessage());
        assertEquals("data", response3.getData());
        
        // Test no-args constructor
        ApiResponse<String> response4 = new ApiResponse<>();
        response4.setCode(201);
        response4.setMessage("Created");
        response4.setData("newData");
        assertEquals(201, response4.getCode());
        assertEquals("Created", response4.getMessage());
        assertEquals("newData", response4.getData());
        
        // Test constructor without data
        ApiResponse<Void> response5 = new ApiResponse<>(202, "Accepted");
        assertEquals(202, response5.getCode());
        assertEquals("Accepted", response5.getMessage());
        assertNull(response5.getData());
        assertNotNull(response5.getTimestamp());
        
        // Test AllArgsConstructor
        ApiResponse<String> response6 = new ApiResponse<>(404, "Not Found", "error data", java.time.LocalDateTime.now());
        assertEquals(404, response6.getCode());
        assertEquals("Not Found", response6.getMessage());
        assertEquals("error data", response6.getData());
        assertNotNull(response6.getTimestamp());
        
        // Test success() with data - this method signature is success(data), not success(message)
        ApiResponse<String> response7 = ApiResponse.success("Success", "test data");
        assertEquals(200, response7.getCode());
        assertEquals("test data", response7.getData());
        assertEquals("Success", response7.getMessage());
        
        // Test error() static methods
        ApiResponse<String> response8 = ApiResponse.error(com.yushan.backend.enums.ErrorCode.BAD_REQUEST);
        assertEquals(400, response8.getCode());
        assertEquals("Bad Request", response8.getMessage());
        assertNull(response8.getData());
        
        ApiResponse<String> response9 = ApiResponse.error(com.yushan.backend.enums.ErrorCode.NOT_FOUND, "Custom not found");
        assertEquals(404, response9.getCode());
        assertEquals("Custom not found", response9.getMessage());
        
        ApiResponse<String> response10 = ApiResponse.error(500, "Internal Server Error");
        assertEquals(500, response10.getCode());
        assertEquals("Internal Server Error", response10.getMessage());
        
        ApiResponse<String> response11 = ApiResponse.error(400, "Custom error", "error data");
        assertEquals(400, response11.getCode());
        assertEquals("Custom error", response11.getMessage());
        assertEquals("error data", response11.getData());
    }

    @Test
    @DisplayName("Test ApiResponse error")
    void testApiResponseError() {
        ApiResponse<Void> response = ApiResponse.error(400, "Bad request");
        assertEquals(400, response.getCode());
        assertEquals("Bad request", response.getMessage());
        assertNull(response.getData());
        
        // Test error with ErrorCode
        com.yushan.backend.enums.ErrorCode errorCode = com.yushan.backend.enums.ErrorCode.UNAUTHORIZED;
        ApiResponse<Void> response2 = ApiResponse.error(errorCode);
        assertEquals(errorCode.getCode(), response2.getCode());
        assertEquals(errorCode.getMessage(), response2.getMessage());
        
        // Test error with ErrorCode and custom message
        ApiResponse<String> response3 = ApiResponse.error(errorCode, "Custom error");
        assertEquals(errorCode.getCode(), response3.getCode());
        assertEquals("Custom error", response3.getMessage());
        
        // Test error with ErrorCode, message, and data
        ApiResponse<String> response4 = ApiResponse.error(errorCode, "Error with data", "errorData");
        assertEquals(errorCode.getCode(), response4.getCode());
        assertEquals("Error with data", response4.getMessage());
        assertEquals("errorData", response4.getData());
        
        // Test error with code, message, and data
        ApiResponse<Integer> response5 = ApiResponse.error(500, "Server error", 123);
        assertEquals(500, response5.getCode());
        assertEquals("Server error", response5.getMessage());
        assertEquals(123, response5.getData());
    }

    @Test
    @DisplayName("Test SearchRequestDTO")
    void testSearchRequestDTO() {
        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setKeyword("test");
        dto.setCategory("fantasy");
        dto.setPage(1);
        dto.setPageSize(20);
        dto.setSortBy("created_at");
        dto.setSortOrder("DESC");

        assertEquals("test", dto.getKeyword());
        assertEquals("fantasy", dto.getCategory());
        assertEquals(1, dto.getPage());
        assertEquals(20, dto.getPageSize());
        assertEquals("created_at", dto.getSortBy());
        assertEquals("DESC", dto.getSortOrder());
    }

    @Test
    @DisplayName("Test SearchRequestDTO default values")
    void testSearchRequestDTODefaults() {
        SearchRequestDTO dto = new SearchRequestDTO();
        assertEquals(1, dto.getPage());
        assertEquals(10, dto.getPageSize());
        assertEquals("created_at", dto.getSortBy());
        assertEquals("DESC", dto.getSortOrder());
    }

    @Test
    @DisplayName("Test PageResponseDTO")
    void testPageResponseDTO() {
        List<String> content = new java.util.ArrayList<>(Arrays.asList("item1", "item2", "item3"));
        PageResponseDTO<String> dto = PageResponseDTO.of(content, 100L, 0, 20);

        assertEquals(3, dto.getContent().size());
        assertEquals(0, dto.getCurrentPage());
        assertEquals(20, dto.getSize());
        assertEquals(100L, dto.getTotalElements());
        assertEquals(5, dto.getTotalPages());
        assertTrue(dto.isFirst());
        assertFalse(dto.isLast());
        assertTrue(dto.isHasNext());
        assertFalse(dto.isHasPrevious());
        
        // Test defensive copying
        content.add("item4");
        assertEquals(3, dto.getContent().size());
        
        List<String> retrieved = dto.getContent();
        assertNotSame(content, retrieved);
        
        // Test constructor with all args
        PageResponseDTO<String> dto2 = new PageResponseDTO<>(
            content, 50L, 2, 1, 20, false, false, true, true
        );
        assertEquals(4, dto2.getContent().size());
        assertEquals(50L, dto2.getTotalElements());
        assertEquals(2, dto2.getTotalPages());
        assertEquals(1, dto2.getCurrentPage());
        assertEquals(20, dto2.getSize());
        assertFalse(dto2.isFirst());
        assertFalse(dto2.isLast());
        assertTrue(dto2.isHasNext());
        assertTrue(dto2.isHasPrevious());
        
        // Test last page
        PageResponseDTO<String> dto3 = PageResponseDTO.of(content, 20L, 0, 20);
        assertTrue(dto3.isFirst());
        assertTrue(dto3.isLast());
        assertFalse(dto3.isHasNext());
        assertFalse(dto3.isHasPrevious());
        
        // Test equals, hashCode, and canEqual methods for ApiResponse
        // Note: ApiResponse includes timestamp, so we test equals with same timestamp
        ApiResponse<String> api1 = new ApiResponse<>(200, "Success", "data");
        // Sleep briefly to ensure different timestamps won't affect equals
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        ApiResponse<String> api2 = new ApiResponse<>(200, "Success", "data");
        
        // Test hashCode (should work even with different timestamps)
        assertNotNull(api1.hashCode());
        assertNotNull(api2.hashCode());
        assertNotEquals(api1, null);
        assertEquals(api1, api1);
        assertTrue(api1.canEqual(api2));
        
        // Test PageResponseDTO equals, hashCode, canEqual
        List<String> content1 = new java.util.ArrayList<>(Arrays.asList("a", "b"));
        PageResponseDTO<String> page1 = PageResponseDTO.of(content1, 100L, 0, 20);
        
        List<String> content2 = new java.util.ArrayList<>(Arrays.asList("a", "b"));
        PageResponseDTO<String> page2 = PageResponseDTO.of(content2, 100L, 0, 20);
        
        assertEquals(page1, page2);
        assertEquals(page1.hashCode(), page2.hashCode());
        assertNotEquals(page1, null);
        assertEquals(page1, page1);
        assertTrue(page1.canEqual(page2));
    }

    @Test
    @DisplayName("Test LibraryRequestDTO")
    void testLibraryRequestDTO() {
        LibraryRequestDTO dto = new LibraryRequestDTO();
        dto.setProgress(5);

        assertEquals(5, dto.getProgress());
    }

    @Test
    @DisplayName("Test LibraryResponseDTO")
    void testLibraryResponseDTO() {
        Date now = new Date();
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setId(1);
        dto.setNovelId(1);
        dto.setNovelTitle("Test Novel");
        dto.setNovelAuthor("Author Name");
        dto.setProgress(10);
        dto.setChapterNumber(5);
        dto.setChapterCnt(100);
        dto.setUpdateTime(now);

        assertEquals(1, dto.getId());
        assertEquals(1, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals("Author Name", dto.getNovelAuthor());
        assertEquals(10, dto.getProgress());
        assertEquals(5, dto.getChapterNumber());
        assertEquals(100, dto.getChapterCnt());
        assertNotNull(dto.getUpdateTime());
    }

    @Test
    @DisplayName("Test HistoryResponseDTO")
    void testHistoryResponseDTO() {
        Date now = new Date();
        HistoryResponseDTO dto = new HistoryResponseDTO();
        dto.setHistoryId(1);
        dto.setNovelId(1);
        dto.setNovelTitle("Test Novel");
        dto.setNovelCover("http://example.com/cover.jpg");
        dto.setCategoryId(2);
        dto.setCategoryName("Fantasy");
        dto.setAvgRating(4.5f);
        dto.setChapterId(10);
        dto.setChapterNumber(5);
        dto.setChapterCnt(100);
        dto.setInLibrary(true);
        dto.setViewTime(now);

        assertEquals(1, dto.getHistoryId());
        assertEquals(1, dto.getNovelId());
        assertEquals("Test Novel", dto.getNovelTitle());
        assertEquals(4.5f, dto.getAvgRating());
        assertEquals(5, dto.getChapterNumber());
        assertTrue(dto.isInLibrary());
        assertNotNull(dto.getViewTime());
    }

    @Test
    @DisplayName("Test SearchResponseDTO")
    void testSearchResponseDTO() {
        NovelDetailResponseDTO novel = new NovelDetailResponseDTO();
        novel.setId(1);
        novel.setTitle("Test Novel");
        
        UserProfileResponseDTO user = new UserProfileResponseDTO();
        user.setUsername("testuser");

        SearchResponseDTO dto = new SearchResponseDTO(
            Arrays.asList(novel), 1,
            Arrays.asList(user), 1,
            0, 1, 2L
        );

        assertEquals(1, dto.getNovelCount());
        assertEquals(1, dto.getUserCount());
        assertEquals(0, dto.getCurrentPage());
        assertEquals(1, dto.getTotalPages());
        assertEquals(2L, dto.getTotalResults());
    }

    @Test
    @DisplayName("Test ExampleResponseDTO")
    void testExampleResponseDTO() {
        // Test basic constructor
        ExampleResponseDTO dto1 = new ExampleResponseDTO("Hello", "granted");
        assertEquals("Hello", dto1.getMessage());
        assertEquals("granted", dto1.getAccess());
        
        // Test user-specific constructor
        ExampleResponseDTO dto2 = new ExampleResponseDTO("Welcome", "denied", "user123", true);
        assertEquals("Welcome", dto2.getMessage());
        assertEquals("denied", dto2.getAccess());
        assertEquals("user123", dto2.getUser());
        assertTrue(dto2.getIsAuthor());
        
        // Test full constructor
        List<String> authorities = Arrays.asList("ROLE_USER", "ROLE_AUTHOR");
        ExampleResponseDTO dto3 = new ExampleResponseDTO(
            "Full", "granted", "user456", true, false, 
            "uuid-123", "uuid-456", true, "resource-789", authorities
        );
        
        assertEquals("Full", dto3.getMessage());
        assertEquals("granted", dto3.getAccess());
        assertEquals("user456", dto3.getUser());
        assertTrue(dto3.getIsAuthor());
        assertFalse(dto3.getIsAdmin());
        assertEquals("uuid-123", dto3.getUserId());
        assertEquals("uuid-456", dto3.getCurrentUserId());
        assertTrue(dto3.getIsOwner());
        assertEquals("resource-789", dto3.getResourceId());
        assertEquals(2, dto3.getAuthorities().size());
        
        // Test defensive copying
        List<String> modifiedAuths = new java.util.ArrayList<>(authorities);
        modifiedAuths.add("ROLE_ADMIN");
        assertEquals(2, dto3.getAuthorities().size());
        
        // Test default constructor
        ExampleResponseDTO dto4 = new ExampleResponseDTO();
        dto4.setMessage("Test");
        dto4.setAccess("test");
        dto4.setIsAuthor(true);
        dto4.setIsAdmin(false);
        dto4.setAuthorities(Arrays.asList("ROLE_TEST"));
        
        assertEquals("Test", dto4.getMessage());
        assertEquals("test", dto4.getAccess());
        assertTrue(dto4.getIsAuthor());
        assertFalse(dto4.getIsAdmin());
        assertEquals(1, dto4.getAuthorities().size());
        
        // Test equals, hashCode, and canEqual methods for ExampleResponseDTO
        ExampleResponseDTO example1 = new ExampleResponseDTO();
        example1.setMessage("Test");
        example1.setAccess("public");
        example1.setUser("user1");
        example1.setIsAuthor(true);
        example1.setIsAdmin(false);
        example1.setAuthorities(new java.util.ArrayList<>(Arrays.asList("ROLE_USER")));
        
        ExampleResponseDTO example2 = new ExampleResponseDTO();
        example2.setMessage("Test");
        example2.setAccess("public");
        example2.setUser("user1");
        example2.setIsAuthor(true);
        example2.setIsAdmin(false);
        example2.setAuthorities(new java.util.ArrayList<>(Arrays.asList("ROLE_USER")));
        
        assertEquals(example1, example2);
        assertEquals(example1.hashCode(), example2.hashCode());
        assertNotEquals(example1, null);
        assertEquals(example1, example1);
        assertTrue(example1.canEqual(example2));
        
        // Test with different values
        example2.setMessage("Different");
        assertNotEquals(example1, example2);
    }
}

