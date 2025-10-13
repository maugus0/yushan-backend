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
        
        // Check if user is the author
        if (!userId.equals(novel.getAuthorId())) {
            return false;
        }
        
        // Only allow editing if novel is in DRAFT, PUBLISHED, or HIDDEN status
        int status = novel.getStatus();
        return status == 0 || status == 2 || status == 3; // 0 = DRAFT, 2 = PUBLISHED, 3 = HIDDEN
    }

    public boolean canHideOrUnhide(Integer novelId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Admin can always hide/unhide
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
        
        // Check if user is the author
        if (!userId.equals(novel.getAuthorId())) {
            return false;
        }
        
        // Allow hide/unhide for PUBLISHED or HIDDEN novels
        int status = novel.getStatus();
        return status == 2 || status == 3; // 2 = PUBLISHED, 3 = HIDDEN
    }
}


