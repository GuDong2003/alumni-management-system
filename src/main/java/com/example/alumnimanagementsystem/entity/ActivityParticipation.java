package com.example.alumnimanagementsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityParticipation {
    private Long id;
    private Long activityId;
    private Long userId;
    private String status;
    private LocalDateTime registerTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 用户信息
    private String name;
    private String email;
    private String phone;
} 