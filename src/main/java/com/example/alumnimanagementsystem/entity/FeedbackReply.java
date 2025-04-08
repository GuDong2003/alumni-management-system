package com.example.alumnimanagementsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedbackReply {
    private Long id;
    private Long feedbackId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 