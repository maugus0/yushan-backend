package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class AnalyticsTrendResponseDTO {
    
    private String period;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;
    
    private List<TrendDataPoint> dataPoints;
    
    private Long totalCount;
    
    private Double averageGrowth;
    
    private Long peakValue;
    
    private String peakDate;
    
    // Defensive copying for dataPoints
    public List<TrendDataPoint> getDataPoints() {
        return dataPoints != null ? new ArrayList<>(dataPoints) : new ArrayList<>();
    }
    
    public void setDataPoints(List<TrendDataPoint> dataPoints) {
        this.dataPoints = dataPoints != null ? new ArrayList<>(dataPoints) : new ArrayList<>();
    }
    
    // Defensive copying for dates
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
    
    @Getter
    @Setter
    @ToString
    public static class TrendDataPoint {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date date;
        
        private String periodLabel;
        
        private Long count;
        
        private Double growthRate;
        
        public TrendDataPoint() {}
        
        public TrendDataPoint(Date date, String periodLabel, Long count, Double growthRate) {
            this.date = date != null ? new Date(date.getTime()) : null;
            this.periodLabel = periodLabel;
            this.count = count;
            this.growthRate = growthRate;
        }
        
        public Date getDate() {
            return date != null ? new Date(date.getTime()) : null;
        }
        
        public void setDate(Date date) {
            this.date = date != null ? new Date(date.getTime()) : null;
        }
    }
}
