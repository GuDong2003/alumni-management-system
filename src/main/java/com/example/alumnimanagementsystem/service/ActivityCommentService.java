package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.ActivityComment;
import java.util.List;

public interface ActivityCommentService {
    List<ActivityComment> getCommentsByActivityId(Long activityId, Long userId);
    ActivityComment getCommentById(Long id);
    ActivityComment createComment(ActivityComment comment);
    ActivityComment updateComment(ActivityComment comment);
    void deleteComment(Long id);
    ActivityComment createReply(Long commentId, ActivityComment reply);
    void likeComment(Long commentId, Long userId);
    void unlikeComment(Long commentId, Long userId);
    boolean hasLiked(Long commentId, Long userId);
} 