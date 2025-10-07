package com.yushan.backend.interceptor;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import com.yushan.backend.security.CustomUserDetailsService;
import com.yushan.backend.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class UserActivityInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    // user activity update interval in minutes
    @Value("${app.user.activity.update.interval:5}")
    private int updateIntervalMinutes;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("UserActivityInterceptor triggered for URL: {}", request.getRequestURL());
        try {
            UUID userId = shouldUpdateAndGetUserId();
            log.info("User ID: {}", userId);
            if (userId != null && shouldUpdateBasedOnInterval(userId)) {
                updateUserLastActiveAsync(userId);
            }
        } catch (Exception e) {
            // catch all exceptions to avoid affecting main business
            log.warn("Failed to update user activity: {}", e.getMessage());
        }

        return true;
    }


    private UUID shouldUpdateAndGetUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetailsService.CustomUserDetails) {
            String id = ((CustomUserDetailsService.CustomUserDetails) principal).getUserId();
            if (id != null) {
                try {
                    return UUID.fromString(id);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid user ID format: {}", id);
                    return null;
                }
            }
        }
        return null;
    }

    protected boolean shouldUpdateBasedOnInterval(UUID userId) {
        String key = "user:activity:" + userId.toString();
        try {
            if (redisUtil.hasKey(key)) {
                // if key exists, it means it has been updated within the interval
                log.info("false");
                return false;
            } else {
                // set key and expire time
                redisUtil.set(key, "1", updateIntervalMinutes, TimeUnit.MINUTES);
                log.info("true");
                return true;
            }
        } catch (Exception e) {
            log.warn("Error checking update interval with Redis for user: {}", userId, e);
            return true;
        }
    }

    @Async
    public void updateUserLastActiveAsync(UUID userId) {
        try {
            User user = new User();
            user.setUuid(userId);
            user.setLastActive(new Date());
            log.info("user");
            int result = userMapper.updateByPrimaryKeySelective(user);
            log.info("update");
            if (result > 0) {
                log.debug("Successfully updated last active time for user: {}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to update user last active time for user ID: {}", userId, e);
        }
    }
}
