package com.example.alumnimanagementsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private String status;
    private LocalDateTime publishTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 