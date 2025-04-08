package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.ActivityComment;
import com.example.alumnimanagementsystem.mapper.ActivityCommentMapper;
import com.example.alumnimanagementsystem.service.ActivityCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityCommentServiceImpl implements ActivityCommentService {

    @Autowired
    private ActivityCommentMapper commentMapper;

    @Override
    public List<ActivityComment> getCommentsByActivityId(Long activityId, Long userId) {
        List<ActivityComment> comments = commentMapper.selectByActivityId(activityId, userId);
        for (ActivityComment comment : comments) {
            List<ActivityComment> replies = commentMapper.selectReplies(comment.getId(), userId);
            comment.setReplies(replies);
        }
        return comments;
    }

    @Override
    public ActivityComment getCommentById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    @Transactional
    public ActivityComment createComment(ActivityComment comment) {
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    @Transactional
    public ActivityComment updateComment(ActivityComment comment) {
        commentMapper.update(comment);
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    @Transactional
    public ActivityComment createReply(Long commentId, ActivityComment reply) {
        reply.setParentId(commentId);
        commentMapper.insert(reply);
        return reply;
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        if (!hasLiked(commentId, userId)) {
            commentMapper.addLike(commentId, userId);
            commentMapper.incrementLikeCount(commentId);
        }
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        if (hasLiked(commentId, userId)) {
            commentMapper.removeLike(commentId, userId);
            commentMapper.decrementLikeCount(commentId);
        }
    }

    @Override
    public boolean hasLiked(Long commentId, Long userId) {
        return commentMapper.hasLiked(commentId, userId) > 0;
    }
} 