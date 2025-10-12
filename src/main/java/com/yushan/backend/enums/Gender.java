package com.yushan.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    UNKNOWN(0, "user.png"),
    MALE(1, "user_male.png"),
    FEMALE(2, "user_female.png");

    private final int code;
    private final String avatarUrl;

    Gender(int code, String avatarUrl) {
        this.code = code;
        this.avatarUrl = avatarUrl;
    }

    public int getCode() {
        return code;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public static Gender fromCode(Integer code) {
        if (code == null) return UNKNOWN;
        for (Gender gender : values()) {
            if (gender.code == code) {
                return gender;
            }
        }
        return UNKNOWN;
    }

    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) {
            return UNKNOWN;
        }
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN; // 如果传入无效字符串，则默认为UNKNOWN
        }
    }

    @JsonValue
    @Override
    public String toString() {
        return name();
    }

    public static boolean isDefaultAvatar(String avatarUrl) {
        if (avatarUrl == null) return true;
        for (Gender gender : values()) {
            if (gender.avatarUrl.equals(avatarUrl)) {
                return true;
            }
        }
        return false;
    }
}