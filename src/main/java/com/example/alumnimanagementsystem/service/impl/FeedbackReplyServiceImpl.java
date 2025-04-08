package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.FeedbackReply;
import com.example.alumnimanagementsystem.mapper.FeedbackReplyMapper;
import com.example.alumnimanagementsystem.service.FeedbackReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FeedbackReplyServiceImpl implements FeedbackReplyService {

    @Autowired
    private FeedbackReplyMapper feedbackReplyMapper;

    @Override
    @Transactional
    public FeedbackReply createReply(FeedbackReply reply) {
        feedbackReplyMapper.insert(reply);
        return reply;
    }

    @Override
    @Transactional
    public FeedbackReply updateReply(Long id, FeedbackReply reply) {
        reply.setId(id);
        feedbackReplyMapper.update(reply);
        return reply;
    }

    @Override
    @Transactional
    public void deleteReply(Long id) {
        feedbackReplyMapper.deleteById(id);
    }

    @Override
    public FeedbackReply getReplyById(Long id) {
        return feedbackReplyMapper.selectById(id);
    }

    @Override
    public List<FeedbackReply> getAllReplies() {
        return feedbackReplyMapper.selectAll();
    }

    @Override
    public List<FeedbackReply> getRepliesByFeedbackId(Long feedbackId) {
        return feedbackReplyMapper.selectByFeedbackId(feedbackId);
    }

    @Override
    public List<FeedbackReply> getRepliesByUserId(Long userId) {
        return feedbackReplyMapper.selectByUserId(userId);
    }

    @Override
    public long count() {
        return feedbackReplyMapper.count();
    }
} 