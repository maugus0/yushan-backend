package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.dto.UserProfileResponseDTO;
import com.yushan.backend.dto.UserProfileUpdateRequestDTO;
import com.yushan.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * Load a user's profile by UUID and map to response DTO
     */
    public UserProfileResponseDTO getUserProfile(UUID userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return null;
        }
        return mapToProfileResponse(user);
    }

    /**
     * Update user profile selectively with allowed fields only
     */
    public UserProfileResponseDTO updateUserProfileSelective(UUID userId, UserProfileUpdateRequestDTO req) {
        User existing = userMapper.selectByPrimaryKey(userId);
        if (existing == null) {
            return null;
        }

        User toUpdate = new User();
        toUpdate.setUuid(userId);

        // Optional fields: update only if provided (non-null)
        if (req.getUsername() != null) {
            toUpdate.setUsername(req.getUsername().trim());
        }
        if (req.getAvatarUrl() != null) {
            toUpdate.setAvatarUrl(req.getAvatarUrl().trim());
        }
        if (req.getProfileDetail() != null) {
            toUpdate.setProfileDetail(req.getProfileDetail().trim());
        }
        if (req.getBirthday() != null) {
            toUpdate.setBirthday(req.getBirthday());
        }
        if (req.getGender() != null) {
            try {
                toUpdate.setGender(Integer.parseInt(req.getGender()));
            } catch (NumberFormatException ignored) {
                // validation layer should prevent this; ignore to keep method safe
            }
        }

        // update timestamp
        toUpdate.setUpdateTime(new Date());

        userMapper.updateByPrimaryKeySelective(toUpdate);

        // reload to get latest values
        User updated = userMapper.selectByPrimaryKey(userId);
        return mapToProfileResponse(updated);
    }

    private UserProfileResponseDTO mapToProfileResponse(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setUuid(user.getUuid() != null ? user.getUuid().toString() : null);
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setProfileDetail(user.getProfileDetail());
        dto.setBirthday(user.getBirthday());
        dto.setGender(user.getGender());
        dto.setIsAuthor(user.getIsAuthor());
        dto.setAuthorVerified(user.getAuthorVerified());
        dto.setLevel(user.getLevel());
        dto.setExp(user.getExp());
        dto.setReadTime(user.getReadTime());
        dto.setReadBookNum(user.getReadBookNum());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        dto.setLastActive(user.getLastActive());
        return dto;
    }
}

