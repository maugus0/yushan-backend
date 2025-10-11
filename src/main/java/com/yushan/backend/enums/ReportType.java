package com.yushan.backend.enums;

public enum ReportType {
    PORNOGRAPHIC("Pornographic Content"),
    HATE_BULLYING("Hate or Bullying"),
    PERSONAL_INFO("Release of personal info"),
    INAPPROPRIATE("Other inappropriate material"),
    SPAM("Spam");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ReportType fromString(String type) {
        if (type == null) return null;
        for (ReportType reportType : values()) {
            if (reportType.name().equalsIgnoreCase(type)) {
                return reportType;
            }
        }
        return null;
    }
}
