package com.example.alumnimanagementsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityParticipant {
    private Long id;
    private Long activityId;
    private Long userId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 