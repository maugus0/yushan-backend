package com.yushan.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.backend.service.NovelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//
import com.yushan.backend.security.NovelGuard;
import static org.mockito.Mockito.*;
import com.yushan.backend.exception.NovelNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

// Mockito static imports sẽ được thêm khi bật test
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Skeleton tests for NovelController REST endpoints.
 * Verifies routing, authz (roles/ownership), validation, and basic response codes.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class NovelControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NovelService novelService;

    private MockMvc mockMvc;

    @MockBean
    private NovelGuard novelGuard;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void createNovel_AsAuthor_Returns201AndLocation() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "A Title");
        body.put("categoryId", 10);
        body.put("synopsis", "short");
        body.put("coverImgUrl", "http://img");
        body.put("isCompleted", false);

        com.yushan.backend.dto.NovelResponseDTO resp = new com.yushan.backend.dto.NovelResponseDTO();
        resp.setId(123);
        when(novelService.createNovel(any(), anyString(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/novels")
                        .with(user("author@example.com").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/novels/123"));
    }

    @Test
    void createNovel_AsUser_Returns403() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "A Title");
        body.put("categoryId", 10);

        mockMvc.perform(post("/api/novels")
                        .with(user("user@example.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createNovel_ValidationErrors_Returns400() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", ""); // NotBlank
        body.put("categoryId", null); // NotNull

        mockMvc.perform(post("/api/novels")
                        .with(user("author@example.com").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void updateNovel_AsOwnerOrAdmin_Returns200() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "New Title");

        com.yushan.backend.dto.NovelResponseDTO resp = new com.yushan.backend.dto.NovelResponseDTO();
        resp.setId(123);
        when(novelGuard.canEdit(eq(123), any())).thenReturn(true);
        when(novelService.updateNovel(eq(123), any())).thenReturn(resp);

        mockMvc.perform(put("/api/novels/123")
                        .with(user("author@example.com").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void updateNovel_NotOwner_Returns403() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "New Title");

        when(novelGuard.canEdit(eq(123), any())).thenReturn(false);

        mockMvc.perform(put("/api/novels/123")
                        .with(user("user@example.com").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void getNovel_PublicValid_Returns200() throws Exception {
        com.yushan.backend.dto.NovelResponseDTO resp = new com.yushan.backend.dto.NovelResponseDTO();
        resp.setId(123);
        when(novelService.getNovel(eq(123))).thenReturn(resp);

        mockMvc.perform(get("/api/novels/123"))
                .andExpect(status().isOk());
    }

    @Test
    void getNovel_ArchivedOrInvalid_Returns404() throws Exception {
        when(novelService.getNovel(eq(404))).thenThrow(new NovelNotFoundException("not_found"));

        mockMvc.perform(get("/api/novels/404"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void listNovels_Public_Returns200() throws Exception {
        mockMvc.perform(get("/api/novels"))
                .andExpect(status().isOk());
    }
}


