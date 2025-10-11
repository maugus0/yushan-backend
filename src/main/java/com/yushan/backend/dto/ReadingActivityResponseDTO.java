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
public class ReadingActivityResponseDTO {
    
    private String period;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;
    
    private List<ActivityDataPoint> dataPoints;
    
    private Long totalActivity;
    
    private Double averageDailyActivity;
    
    private Long peakActivity;
    
    private String peakDate;
    
    // Defensive copying for dataPoints
    public List<ActivityDataPoint> getDataPoints() {
        return dataPoints != null ? new ArrayList<>(dataPoints) : new ArrayList<>();
    }
    
    public void setDataPoints(List<ActivityDataPoint> dataPoints) {
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
    public static class ActivityDataPoint {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private Date date;
        
        private String periodLabel;
        
        private Long views;
        private Long chaptersRead;
        private Long comments;
        private Long reviews;
        private Long votes;
        private Long totalActivity;
        
        public ActivityDataPoint() {}
        
        public ActivityDataPoint(Date date, String periodLabel, Long views, Long chaptersRead, 
                               Long comments, Long reviews, Long votes) {
            this.date = date != null ? new Date(date.getTime()) : null;
            this.periodLabel = periodLabel;
            this.views = views;
            this.chaptersRead = chaptersRead;
            this.comments = comments;
            this.reviews = reviews;
            this.votes = votes;
            this.totalActivity = (views != null ? views : 0L) + 
                                (chaptersRead != null ? chaptersRead : 0L) + 
                                (comments != null ? comments : 0L) + 
                                (reviews != null ? reviews : 0L) + 
                                (votes != null ? votes : 0L);
        }
        
        public Date getDate() {
            return date != null ? new Date(date.getTime()) : null;
        }
        
        public void setDate(Date date) {
            this.date = date != null ? new Date(date.getTime()) : null;
        }
    }
}
