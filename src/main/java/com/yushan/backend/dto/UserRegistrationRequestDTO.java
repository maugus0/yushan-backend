package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserRegistrationRequestDTO {
    @NotBlank(message = "email cannot be empty")
    @Email(message = "email format is incorrect")
    @Size(max = 254, message = "email length pasts limitation")
    private String email;

    @NotBlank(message = "username cannot be empty")
    @Size(min = 3, max = 20, message = "username must be in 3-20 char")
    @Pattern(regexp = "^[a-zA-Z0-9_\\.\\-]+$", message = "username can only contain letters, numbers, underscores, dots, and hyphens")
    private String username;

    @NotBlank(message = "password cannot be empty")
    @Size(min = 6, message = "password must have 6 char at least")
    private String password;

    @Past(message = "birthday must be a past date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;

    public Date getBirthday() {
        return birthday != null ? new Date(birthday.getTime()) : null;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday != null ? new Date(birthday.getTime()) : null;
    }

    @Min(value = 0, message = "gender must be 0 or 1 or 2")
    @Max(value = 2, message = "gender must be 0 or 1 or 2")
    @NotNull(message = "gender cannot be empty")
    private Integer gender = 0;

    @NotBlank(message = "code cannot be empty")
    @Size(min = 6, max = 6, message = "code must be 6 char")
    private String code;

    // Getters and Setters
    public String getEmail() {
        return email != null ? email.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }
}
