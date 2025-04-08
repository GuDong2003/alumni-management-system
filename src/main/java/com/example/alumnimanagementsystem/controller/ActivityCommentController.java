package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.ActivityComment;
import com.example.alumnimanagementsystem.service.ActivityCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities/{activityId}/comments")
public class ActivityCommentController {

    @Autowired
    private ActivityCommentService commentService;

    @GetMapping
    public ResponseEntity<List<ActivityComment>> getComments(@PathVariable Long activityId) {
        Long userId = getCurrentUserId();
        List<ActivityComment> comments = commentService.getCommentsByActivityId(activityId, userId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<ActivityComment> createComment(
            @PathVariable Long activityId,
            @RequestBody ActivityComment comment) {
        Long userId = getCurrentUserId();
        comment.setActivityId(activityId);
        comment.setUserId(userId);
        ActivityComment createdComment = commentService.createComment(comment);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ActivityComment> updateComment(
            @PathVariable Long commentId,
            @RequestBody ActivityComment comment) {
        comment.setId(commentId);
        ActivityComment updatedComment = commentService.updateComment(comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{commentId}/replies")
    public ResponseEntity<ActivityComment> createReply(
            @PathVariable Long activityId,
            @PathVariable Long commentId,
            @RequestBody ActivityComment reply) {
        Long userId = getCurrentUserId();
        reply.setActivityId(activityId);
        reply.setUserId(userId);
        ActivityComment createdReply = commentService.createReply(commentId, reply);
        return ResponseEntity.ok(createdReply);
    }

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId) {
        Long userId = getCurrentUserId();
        commentService.likeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        Long userId = getCurrentUserId();
        commentService.unlikeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
} 