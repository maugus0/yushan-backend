package com.yushan.backend.enums;

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

    public static Gender fromCode(Integer code) {
        if (code == null) return UNKNOWN;
        for (Gender gender : values()) {
            if (gender.code == code) {
                return gender;
            }
        }
        return UNKNOWN;
    }

    public static boolean isDefaultAvatar(String avatarUrl) {
        if (avatarUrl == null) return false;
        for (Gender gender : values()) {
            if (gender.avatarUrl.equals(avatarUrl)) {
                return true;
            }
        }
        return false;
    }

    public int getCode() {
        return code;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
