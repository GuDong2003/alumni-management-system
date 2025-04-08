package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.Activity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActivityService {
    @Transactional
    Activity createActivity(Activity activity);
    
    @Transactional
    Activity updateActivity(Long id, Activity activity);
    
    @Transactional
    void deleteActivity(Long id);
    
    Optional<Activity> findById(Long id);
    
    List<Activity> findAll();
    
    List<Activity> findByTitle(String title);
    
    List<Activity> findByType(String type);
    
    List<Activity> findByStatus(String status);
    
    List<Activity> findByTitleContaining(String title);
    
    List<Activity> findByTypeAndStatus(String type, String status);
    
    List<Activity> findByStartTime(LocalDateTime startTime);
    
    List<Activity> findByEndTime(LocalDateTime endTime);
    
    List<Activity> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    @Transactional
    void updateStatus(Long id, String status);
    
    @Transactional
    void updateParticipantCount(Long id, Integer count);
    
    Activity getActivityById(Long id);
    
    List<Activity> getAllActivities();
    
    List<Activity> getActivitiesByStatus(String status);
    
    List<Activity> getActivitiesByType(String type);
    
    List<Activity> getActivitiesByLocation(String location);
    
    List<Activity> getActivitiesByStartTime(String startTime);
    
    List<Activity> getActivitiesByEndTime(String endTime);
    
    Activity updateActivity(Activity activity);
    
    List<Activity> searchActivities(String keyword);
    
    long count();
    
    long countByStatus(String status);
    
    List<Activity> searchActivities(String title, String type, String status, String startTime, String endTime);
} 