package com.yushan.backend.dto;

import com.yushan.backend.entity.User;
import lombok.Data;

@Data
public class UserRegisterationResponseDTO {
    private User user;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}