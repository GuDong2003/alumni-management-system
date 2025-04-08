package com.example.alumnimanagementsystem.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String name;
    private String password;
    private String email;
    private String phone;
    private UserRole role;
    private String status;
    private String studentId;
    private String major;
    private Integer graduationYear;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    
    // Alumni表字段的临时存储
    private String currentCompany;
    private String currentPosition;
    private String industry;
    private String location;
    private String bio;
    
    // 用于临时存储当前用户名，不映射到数据库
    private String currentUsername;
    
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }
    
    // Alumni字段的getter和setter
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
} 