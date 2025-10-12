package com.yushan.backend.dto;

import com.yushan.backend.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpdateUserDTO {
    @NotNull(message = "Status cannot be null")
    private UserStatus status;
}