package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /**
     * Promote user to admin by email
     */
    public UserProfileResponseDTO promoteToAdmin(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Find user by email
        User user = userMapper.selectByEmail(email.trim().toLowerCase(java.util.Locale.ROOT));
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        // Check if user is already admin
        if (user.getIsAdmin() != null && user.getIsAdmin()) {
            throw new IllegalArgumentException("User is already an admin");
        }

        // Update user to admin
        user.setIsAdmin(true);
        user.setUpdateTime(new Date());
        userMapper.updateByPrimaryKeySelective(user);

        // Return updated user profile
        return userService.getUserProfile(user.getUuid());
    }
}
