package com.yushan.backend.security;

import com.yushan.backend.dao.NovelMapper;
import com.yushan.backend.entity.Novel;
import com.yushan.backend.security.CustomUserDetailsService.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NovelGuard {

    @Autowired
    private NovelMapper novelMapper;

    public boolean canEdit(Integer novelId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Admin can always edit
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            return false;
        }
        String userIdStr = ((CustomUserDetails) principal).getUserId();
        if (userIdStr == null) return false;
        UUID userId = UUID.fromString(userIdStr);

        Novel novel = novelMapper.selectByPrimaryKey(novelId);
        if (novel == null || novel.getAuthorId() == null) {
            return false;
        }
        return userId.equals(novel.getAuthorId());
    }
}


