package com.yushan.backend.enums;

public enum ReportStatus {
    IN_REVIEW("Under review"),
    RESOLVED("Resolved"),
    DISMISSED("Dismissed");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ReportStatus fromString(String status) {
        if (status == null) return null;
        for (ReportStatus reportStatus : values()) {
            if (reportStatus.name().equalsIgnoreCase(status)) {
                return reportStatus;
            }
        }
        return null;
    }
}
