package com.yushan.backend.controller;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.PageResponseDTO;
import com.yushan.backend.dto.VoteResponseDTO;
import com.yushan.backend.dto.VoteUserResponseDTO;
import com.yushan.backend.security.CustomUserDetailsService;
import com.yushan.backend.service.VoteService;
import com.yushan.backend.util.JwtUtil;
import com.yushan.backend.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    // Mocks required because @WebMvcTest loads parts of the security configuration
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private RedisUtil redisUtil;
    @Mock
    private Authentication authentication;

    private final UUID testUserId = UUID.randomUUID();
    private final Integer testNovelId = 123;

    void setupAuthentication() {
        CustomUserDetailsService.CustomUserDetails userDetails = mock(CustomUserDetailsService.CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(testUserId.toString());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("Endpoint Access Control")
    class AccessControl {

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous user cannot vote")
        void anonymousUser_cannotToggleVote() throws Exception {
            mockMvc.perform(post("/api/novels/{novelId}/vote", testNovelId).with(csrf()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("Anonymous user cannot get their votes")
        void anonymousUser_cannotGetUserVotes() throws Exception {
            mockMvc.perform(get("/api/users/votes"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Authenticated User Endpoint Tests")
    @WithMockUser // Ensures a user is authenticated for these tests
    class AuthenticatedUserTests {

        @Test
        @DisplayName("POST /novels/{novelId}/vote should call service and return correct response with remainedYuan")
        void toggleVote_shouldCallServiceAndReturnResponse() throws Exception {
            setupAuthentication();
            // Given: The VoteResponseDTO now includes remainedYuan
            VoteResponseDTO mockResponse = new VoteResponseDTO(testNovelId, 10, 98.5f);
            when(voteService.toggleVote(testNovelId, testUserId)).thenReturn(mockResponse);

            // When & Then
            mockMvc.perform(post("/api/novels/{novelId}/vote", testNovelId).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Voted successfully"))
                    .andExpect(jsonPath("$.data.novelId").value(testNovelId))
                    .andExpect(jsonPath("$.data.voteCount").value(10))
                    .andExpect(jsonPath("$.data.remainedYuan").value(98.5));

            // Verify
            verify(voteService).toggleVote(testNovelId, testUserId);
        }

        @Test
        @DisplayName("GET /users/votes should call service and return a page of votes")
        void getUserVotes_shouldCallServiceAndReturnPage() throws Exception {
            setupAuthentication();
            // Given
            int page = 1;
            int size = 5;
            PageResponseDTO<VoteUserResponseDTO> mockPage = new PageResponseDTO<>(Collections.emptyList(), 0L, page, size);
            when(voteService.getUserVotes(testUserId, page, size)).thenReturn(mockPage);

            // When & Then
            mockMvc.perform(get("/api/users/votes?page={page}&size={size}", page, size))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User votes retrieved"))
                    .andExpect(jsonPath("$.data.currentPage").value(page))
                    .andExpect(jsonPath("$.data.size").value(size));

            // Verify
            verify(voteService).getUserVotes(testUserId, page, size);
        }
    }
}