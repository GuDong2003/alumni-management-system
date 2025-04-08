package com.example.alumnimanagementsystem.dto;

import com.example.alumnimanagementsystem.entity.Alumni;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 校友数据传输对象，用于前后端数据交互
 */
@Data
public class AlumniDTO {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String studentId;
    private String major;
    private Integer graduationYear;
    private String currentCompany;
    private String currentPosition;
    private String industry;
    private String location;
    private String bio;
    private boolean active;
    private LocalDateTime lastActivityTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 从实体类转换为DTO
    public static AlumniDTO fromAlumni(Alumni alumni) {
        AlumniDTO dto = new AlumniDTO();
        dto.setId(alumni.getId());
        dto.setUserId(alumni.getUserId());
        dto.setCurrentCompany(alumni.getCurrentCompany());
        dto.setCurrentPosition(alumni.getCurrentPosition());
        dto.setIndustry(alumni.getIndustry());
        dto.setLocation(alumni.getLocation());
        dto.setBio(alumni.getBio());
        dto.setActive(alumni.isActive());
        dto.setLastActivityTime(alumni.getLastActivityTime());
        dto.setCreatedAt(alumni.getCreatedAt());
        dto.setUpdatedAt(alumni.getUpdatedAt());
        return dto;
    }

    public Alumni toAlumni() {
        Alumni alumni = new Alumni();
        alumni.setId(this.id);
        alumni.setUserId(this.userId);
        alumni.setCurrentCompany(this.currentCompany);
        alumni.setCurrentPosition(this.currentPosition);
        alumni.setIndustry(this.industry);
        alumni.setLocation(this.location);
        alumni.setBio(this.bio);
        alumni.setActive(this.active);
        alumni.setLastActivityTime(this.lastActivityTime);
        alumni.setCreatedAt(this.createdAt);
        alumni.setUpdatedAt(this.updatedAt);
        return alumni;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 