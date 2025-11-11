package com.yushan.backend.enums;

public enum ReportContentType {
    NOVEL,
    COMMENT;

    public static ReportContentType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (ReportContentType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}


