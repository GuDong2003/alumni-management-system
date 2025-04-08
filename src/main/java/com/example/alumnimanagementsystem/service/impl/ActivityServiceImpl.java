package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.Activity;
import com.example.alumnimanagementsystem.mapper.ActivityMapper;
import com.example.alumnimanagementsystem.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional
    public Activity createActivity(Activity activity) {
        activity.setCreatedAt(LocalDateTime.now());
        activity.setUpdatedAt(LocalDateTime.now());
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    @Transactional
    public Activity updateActivity(Long id, Activity activity) {
        Activity existingActivity = activityMapper.selectById(id);
        if (existingActivity == null) {
            throw new RuntimeException("活动不存在");
        }
        activity.setId(id);
        activity.setUpdatedAt(LocalDateTime.now());
        activityMapper.update(activity);
        return activity;
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        activityMapper.deleteById(id);
    }

    @Override
    public Optional<Activity> findById(Long id) {
        return Optional.ofNullable(activityMapper.selectById(id));
    }

    @Override
    public List<Activity> findAll() {
        return activityMapper.selectAll();
    }

    @Override
    public List<Activity> findByTitle(String title) {
        return activityMapper.selectByTitle(title);
    }

    @Override
    public List<Activity> findByType(String type) {
        return activityMapper.selectByType(type);
    }

    @Override
    public List<Activity> findByStatus(String status) {
        return activityMapper.selectByStatus(status);
    }

    @Override
    public List<Activity> findByTitleContaining(String title) {
        return activityMapper.selectByTitleContaining(title);
    }

    @Override
    public List<Activity> findByTypeAndStatus(String type, String status) {
        return activityMapper.selectByTypeAndStatus(type, status);
    }

    @Override
    public List<Activity> findByStartTime(LocalDateTime startTime) {
        return activityMapper.selectByStartTime(startTime.toString());
    }

    @Override
    public List<Activity> findByEndTime(LocalDateTime endTime) {
        return activityMapper.selectByEndTime(endTime.toString());
    }

    @Override
    public List<Activity> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return activityMapper.selectByTimeRange(startTime, endTime);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        activityMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void updateParticipantCount(Long id, Integer count) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        activityMapper.updateParticipantCount(id, count);
    }

    @Override
    public Activity getActivityById(Long id) {
        return activityMapper.selectById(id);
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityMapper.selectAll();
    }

    @Override
    public List<Activity> getActivitiesByStatus(String status) {
        return activityMapper.selectByStatus(status);
    }

    @Override
    public List<Activity> getActivitiesByType(String type) {
        return activityMapper.selectByType(type);
    }

    @Override
    public List<Activity> getActivitiesByLocation(String location) {
        return activityMapper.selectByLocation(location);
    }

    @Override
    public List<Activity> getActivitiesByStartTime(String startTime) {
        return activityMapper.selectByStartTime(startTime);
    }

    @Override
    public List<Activity> getActivitiesByEndTime(String endTime) {
        return activityMapper.selectByEndTime(endTime);
    }

    @Override
    public List<Activity> searchActivities(String keyword) {
        return activityMapper.search(keyword);
    }

    @Override
    public List<Activity> searchActivities(String title, String type, String status, String startTime, String endTime) {
        List<Activity> result = activityMapper.selectAll();
        
        // 按标题过滤
        if (title != null && !title.isEmpty()) {
            result = result.stream()
                .filter(activity -> activity.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // 按类型过滤
        if (type != null && !type.isEmpty()) {
            result = result.stream()
                .filter(activity -> activity.getType().equals(type))
                .collect(Collectors.toList());
        }
        
        // 按状态过滤
        if (status != null && !status.isEmpty()) {
            result = result.stream()
                .filter(activity -> activity.getStatus().equals(status))
                .collect(Collectors.toList());
        }
        
        // 按时间范围过滤
        if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            result = result.stream()
                .filter(activity -> 
                    (activity.getStartTime().isAfter(start) || activity.getStartTime().equals(start)) &&
                    (activity.getEndTime().isBefore(end) || activity.getEndTime().equals(end)))
                .collect(Collectors.toList());
        }
        
        return result;
    }

    @Override
    public long count() {
        return activityMapper.count();
    }

    @Override
    public long countByStatus(String status) {
        return activityMapper.countByStatus(status);
    }

    @Override
    public Activity updateActivity(Activity activity) {
        return updateActivity(activity.getId(), activity);
    }
} 