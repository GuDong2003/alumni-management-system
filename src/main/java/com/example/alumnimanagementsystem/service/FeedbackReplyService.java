package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.FeedbackReply;
import java.util.List;

public interface FeedbackReplyService {
    FeedbackReply createReply(FeedbackReply reply);
    FeedbackReply updateReply(Long id, FeedbackReply reply);
    void deleteReply(Long id);
    FeedbackReply getReplyById(Long id);
    List<FeedbackReply> getAllReplies();
    List<FeedbackReply> getRepliesByFeedbackId(Long feedbackId);
    List<FeedbackReply> getRepliesByUserId(Long userId);
    long count();
} 