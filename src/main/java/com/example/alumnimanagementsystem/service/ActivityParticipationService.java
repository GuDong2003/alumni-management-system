package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.ActivityParticipation;
import java.util.List;

public interface ActivityParticipationService {
    List<ActivityParticipation> getParticipantsByActivityId(Long activityId);
    ActivityParticipation register(Long activityId, Long userId);
    ActivityParticipation updateStatus(Long activityId, Long userId, String status);
    void cancel(Long activityId, Long userId);
} 