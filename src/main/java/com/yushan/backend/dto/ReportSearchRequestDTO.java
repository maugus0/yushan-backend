package com.yushan.backend.dto;

import lombok.Data;

@Data
public class ReportSearchRequestDTO {
    private String status;
    private String reportType;
    private String search;
    private String sort = "createdAt";
    private String order = "desc";
    private int page = 0;
    private int size = 10;
}
