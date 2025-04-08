package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.Activity;
import com.example.alumnimanagementsystem.mapper.ActivityMapper;
import com.example.alumnimanagementsystem.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional
    public Activity createActivity(Activity activity) {
        // 验证必填字段
        if (activity.getTitle() == null || activity.getTitle().trim().isEmpty()) {
            throw new RuntimeException("活动标题不能为空");
        }
        if (activity.getDescription() == null || activity.getDescription().trim().isEmpty()) {
            throw new RuntimeException("活动描述不能为空");
        }
        if (activity.getType() == null || activity.getType().trim().isEmpty()) {
            throw new RuntimeException("活动类型不能为空");
        }
        if (activity.getStartTime() == null) {
            throw new RuntimeException("开始时间不能为空");
        }
        if (activity.getEndTime() == null) {
            throw new RuntimeException("结束时间不能为空");
        }
        if (activity.getLocation() == null || activity.getLocation().trim().isEmpty()) {
            throw new RuntimeException("活动地点不能为空");
        }
        if (activity.getMaxParticipants() == null || activity.getMaxParticipants() <= 0) {
            throw new RuntimeException("最大参与人数必须大于0");
        }

        // 验证时间
        if (activity.getStartTime().isAfter(activity.getEndTime())) {
            throw new RuntimeException("结束时间必须晚于开始时间");
        }

        // 验证活动类型
        String[] validTypes = {"REUNION", "SHARING", "CAREER", "SPORTS", "MENTORING", 
                              "DONATION", "CULTURE", "FORUM", "WELCOME", "ANNUAL"};
        boolean isValidType = false;
        for (String type : validTypes) {
            if (type.equals(activity.getType())) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            throw new RuntimeException("无效的活动类型");
        }

        // 设置默认值
        activity.setStatus("DRAFT");
        activity.setCurrentParticipants(0);
        activity.setCreatedAt(LocalDateTime.now());
        activity.setUpdatedAt(LocalDateTime.now());

        // 插入数据
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
        try {
            // 验证状态值
            if (status == null || status.trim().isEmpty()) {
                throw new RuntimeException("状态值不能为空");
            }

            // 获取活动
            Activity activity = activityMapper.selectById(id);
            if (activity == null) {
                throw new RuntimeException("活动不存在");
            }

            // 更新状态
            int result = activityMapper.updateStatus(id, status);
            if (result <= 0) {
                throw new RuntimeException("更新状态失败");
            }
        } catch (Exception e) {
            // 记录错误日志
            logger.error("更新活动状态失败: id={}, status={}, error={}", id, status, e.getMessage());
            throw new RuntimeException("更新状态失败: " + e.getMessage());
        }
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