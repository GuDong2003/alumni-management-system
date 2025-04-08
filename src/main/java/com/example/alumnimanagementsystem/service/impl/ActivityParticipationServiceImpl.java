package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.ActivityParticipation;
import com.example.alumnimanagementsystem.entity.Activity;
import com.example.alumnimanagementsystem.mapper.ActivityParticipationMapper;
import com.example.alumnimanagementsystem.mapper.ActivityMapper;
import com.example.alumnimanagementsystem.service.ActivityParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityParticipationServiceImpl implements ActivityParticipationService {

    @Autowired
    private ActivityParticipationMapper participationMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<ActivityParticipation> getParticipantsByActivityId(Long activityId) {
        return participationMapper.selectByActivityId(activityId);
    }

    @Override
    @Transactional
    public ActivityParticipation register(Long activityId, Long userId) {
        // 验证活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        
        // 验证活动状态
        if (!"PUBLISHED".equals(activity.getStatus()) && !"ONGOING".equals(activity.getStatus())) {
            throw new RuntimeException("活动状态不允许参加");
        }
        
        // 验证是否已参加且状态不是已取消
        List<ActivityParticipation> existing = participationMapper.selectByActivityId(activityId);
        if (existing.stream().anyMatch(p -> p.getUserId().equals(userId) && !"CANCELLED".equals(p.getStatus()))) {
            throw new RuntimeException("已经参加该活动");
        }
        
        // 验证参与人数
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动人数已满");
        }
        
        // 如果存在已取消的记录，先删除
        existing.stream()
            .filter(p -> p.getUserId().equals(userId) && "CANCELLED".equals(p.getStatus()))
            .findFirst()
            .ifPresent(p -> {
                participationMapper.delete(p);
                // 更新活动参与人数
                activityMapper.updateParticipantCount(activityId, activity.getCurrentParticipants() - 1);
            });
        
        // 创建参与记录
        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivityId(activityId);
        participation.setUserId(userId);
        participation.setStatus("REGISTERED");
        participationMapper.insert(participation);
        
        // 更新活动参与人数
        activityMapper.updateParticipantCount(activityId, activity.getCurrentParticipants() + 1);
        
        return participation;
    }

    @Override
    @Transactional
    public ActivityParticipation updateStatus(Long activityId, Long userId, String status) {
        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivityId(activityId);
        participation.setUserId(userId);
        participation.setStatus(status);
        participationMapper.updateStatus(participation);
        return participation;
    }

    @Override
    @Transactional
    public void cancel(Long activityId, Long userId) {
        // 验证活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        
        // 验证是否已参加
        List<ActivityParticipation> existing = participationMapper.selectByActivityId(activityId);
        if (existing.stream().noneMatch(p -> p.getUserId().equals(userId))) {
            throw new RuntimeException("未参加该活动");
        }
        
        // 删除参与记录
        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivityId(activityId);
        participation.setUserId(userId);
        participationMapper.delete(participation);
        
        // 更新活动参与人数
        activityMapper.updateParticipantCount(activityId, activity.getCurrentParticipants() - 1);
    }
} 