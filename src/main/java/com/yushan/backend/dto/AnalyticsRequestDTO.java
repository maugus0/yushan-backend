package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;

@Data
public class AnalyticsRequestDTO {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;
    
    @Pattern(regexp = "^(daily|weekly|monthly)$", message = "Period must be daily, weekly, or monthly")
    private String period = "daily";
    
    private Integer categoryId;
    
    private String authorId;
    
    private Integer status;
    
    // Getters and setters with defensive copying for dates
    public Date getStartDate() {
        return startDate != null ? new Date(startDate.getTime()) : null;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate != null ? new Date(startDate.getTime()) : null;
    }
    
    public Date getEndDate() {
        return endDate != null ? new Date(endDate.getTime()) : null;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate != null ? new Date(endDate.getTime()) : null;
    }
}
