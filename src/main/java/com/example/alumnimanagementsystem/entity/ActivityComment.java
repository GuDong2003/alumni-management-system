package com.example.alumnimanagementsystem.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityComment {
    private Long id;
    private Long activityId;
    private Long userId;
    private String userName;
    private String content;
    private Long parentId;
    private Integer likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private Boolean hasLiked;
    private List<ActivityComment> replies;
} 