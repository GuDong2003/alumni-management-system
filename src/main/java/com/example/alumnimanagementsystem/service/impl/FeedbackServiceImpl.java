package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.Feedback;
import com.example.alumnimanagementsystem.mapper.FeedbackMapper;
import com.example.alumnimanagementsystem.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    @Transactional
    public Feedback createFeedback(Feedback feedback) {
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        feedback.setCreatedAt(now);
        feedback.setUpdatedAt(now);
        
        // 如果状态为空，默认设置为待处理
        if (feedback.getStatus() == null || feedback.getStatus().isEmpty()) {
            feedback.setStatus("PENDING");
        }
        
        feedbackMapper.insert(feedback);
        return getFeedbackById(feedback.getId());
    }

    @Override
    @Transactional
    public Feedback updateFeedback(Feedback feedback) {
        // 更新时间
        feedback.setUpdatedAt(LocalDateTime.now());
        feedbackMapper.update(feedback);
        return getFeedbackById(feedback.getId());
    }

    @Override
    @Transactional
    public boolean deleteFeedback(Long id) {
        return feedbackMapper.deleteById(id) > 0;
    }

    @Override
    public Feedback getFeedbackById(Long id) {
        return feedbackMapper.findById(id);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackMapper.findAll();
    }

    @Override
    public List<Feedback> getFeedbacksByUserId(Long userId) {
        return feedbackMapper.findByUserId(userId);
    }

    @Override
    public List<Feedback> getFeedbacksByStatus(String status) {
        return feedbackMapper.findByStatus(status);
    }

    @Override
    public List<Feedback> getFeedbacksByType(String type) {
        return feedbackMapper.findByType(type);
    }

    @Override
    public List<Feedback> getFeedbacksByUserIdAndStatus(Long userId, String status) {
        return feedbackMapper.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Feedback> getFeedbacksByTypeAndStatus(String type, String status) {
        return feedbackMapper.findByTypeAndStatus(type, status);
    }

    @Override
    public List<Feedback> getFeedbacksByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return feedbackMapper.findByTimeRange(startTime, endTime);
    }

    @Override
    public List<Feedback> searchFeedbacks(String keyword) {
        return feedbackMapper.searchByContent(keyword);
    }

    @Override
    public List<Feedback> getFeedbacksPage(int page, int size) {
        int offset = (page - 1) * size;
        return feedbackMapper.findPage(offset, size);
    }

    @Override
    public int getTotalCount() {
        return feedbackMapper.getTotalCount();
    }

    @Override
    public int getCountByStatus(String status) {
        return feedbackMapper.getCountByStatus(status);
    }

    @Override
    @Transactional
    public boolean updateFeedbackStatus(Long id, String status) {
        LocalDateTime now = LocalDateTime.now();
        return feedbackMapper.updateStatus(id, status, now) > 0;
    }

    @Override
    @Transactional
    public boolean replyToFeedback(Long id, String reply, Long replyUserId, String replyUserName, String status) {
        LocalDateTime now = LocalDateTime.now();
        return feedbackMapper.replyFeedback(id, reply, replyUserId, replyUserName, now, status, now) > 0;
    }

    @Override
    public long count() {
        return getTotalCount();
    }

    @Override
    public long countByStatus(String status) {
        return getCountByStatus(status);
    }
} 