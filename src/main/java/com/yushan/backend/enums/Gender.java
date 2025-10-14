package com.yushan.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public enum Gender {
    UNKNOWN(0, getAvatarUrl("avatar.unknown", "user.png")),
    MALE(1, getAvatarUrl("avatar.male", "user_male.png")),
    FEMALE(2, getAvatarUrl("avatar.female", "user_female.png"));

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
            return Gender.valueOf(value.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
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

    private static String getAvatarUrl(String key, String defaultValue) {
        try (InputStream input = Gender.class.getClassLoader()
                .getResourceAsStream("avatar-base64.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                return props.getProperty(key, defaultValue);
            }
        } catch (IOException e) {
            // Log error if needed
        }
        return defaultValue;
    }
}