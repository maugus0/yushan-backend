package com.yushan.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyActiveUsersResponseDTO {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    
    private Long dau; // Daily Active Users
    private Long wau; // Weekly Active Users  
    private Long mau; // Monthly Active Users
    
    private List<ActivityDataPoint> hourlyBreakdown;
    
    public static class ActivityDataPoint {
        private Integer hour;
        private Long activeUsers;
        private Long newUsers;
        private Long readingSessions;
        
        public ActivityDataPoint() {}
        
        public ActivityDataPoint(Integer hour, Long activeUsers, Long newUsers, Long readingSessions) {
            this.hour = hour;
            this.activeUsers = activeUsers;
            this.newUsers = newUsers;
            this.readingSessions = readingSessions;
        }
        
        // Getters and Setters
        public Integer getHour() { return hour; }
        public void setHour(Integer hour) { this.hour = hour; }
        
        public Long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
        
        public Long getNewUsers() { return newUsers; }
        public void setNewUsers(Long newUsers) { this.newUsers = newUsers; }
        
        public Long getReadingSessions() { return readingSessions; }
        public void setReadingSessions(Long readingSessions) { this.readingSessions = readingSessions; }
    }
    
    public DailyActiveUsersResponseDTO() {}
    
    public Date getDate() {
        return date != null ? new Date(date.getTime()) : null;
    }
    
    public void setDate(Date date) {
        this.date = date != null ? new Date(date.getTime()) : null;
    }
    
    public List<ActivityDataPoint> getHourlyBreakdown() {
        return hourlyBreakdown != null ? new ArrayList<>(hourlyBreakdown) : new ArrayList<>();
    }

    public void setHourlyBreakdown(List<ActivityDataPoint> hourlyBreakdown) {
        this.hourlyBreakdown = hourlyBreakdown != null ? new ArrayList<>(hourlyBreakdown) : new ArrayList<>();
    }
    
    // Additional getters and setters
    public Long getDau() { return dau; }
    public void setDau(Long dau) { this.dau = dau; }
    
    public Long getWau() { return wau; }
    public void setWau(Long wau) { this.wau = wau; }
    
    public Long getMau() { return mau; }
    public void setMau(Long mau) { this.mau = mau; }
}
