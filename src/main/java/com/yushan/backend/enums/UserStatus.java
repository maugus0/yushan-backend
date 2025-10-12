package com.yushan.backend.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    NORMAL(0),
    SUSPENDED(1),
    BANNED(2);

    private final int code;

    UserStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @JsonValue
    @Override
    public String toString() {
        return name();
    }

    public static UserStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("No UserStatus with code " + code + " found.");
    }
}