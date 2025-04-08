package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.ActivityParticipation;
import com.example.alumnimanagementsystem.mapper.ActivityParticipationMapper;
import com.example.alumnimanagementsystem.service.ActivityParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityParticipationServiceImpl implements ActivityParticipationService {

    @Autowired
    private ActivityParticipationMapper participationMapper;

    @Override
    public List<ActivityParticipation> getParticipantsByActivityId(Long activityId) {
        return participationMapper.selectByActivityId(activityId);
    }

    @Override
    @Transactional
    public ActivityParticipation register(Long activityId, Long userId) {
        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivityId(activityId);
        participation.setUserId(userId);
        participation.setStatus("REGISTERED");
        participationMapper.insert(participation);
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
        ActivityParticipation participation = new ActivityParticipation();
        participation.setActivityId(activityId);
        participation.setUserId(userId);
        participationMapper.delete(participation);
    }
} 