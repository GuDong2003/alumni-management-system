package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.FeedbackReply;
import com.example.alumnimanagementsystem.service.FeedbackReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/feedback-replies")
public class FeedbackReplyController {

    @Autowired
    private FeedbackReplyService feedbackReplyService;

    @PostMapping
    public ResponseEntity<?> createReply(@RequestBody FeedbackReply reply) {
        return ResponseEntity.ok(feedbackReplyService.createReply(reply));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReply(@PathVariable Long id, @RequestBody FeedbackReply reply) {
        return ResponseEntity.ok(feedbackReplyService.updateReply(id, reply));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable Long id) {
        feedbackReplyService.deleteReply(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReplyById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackReplyService.getReplyById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllReplies() {
        return ResponseEntity.ok(feedbackReplyService.getAllReplies());
    }

    @GetMapping("/feedback/{feedbackId}")
    public ResponseEntity<?> getRepliesByFeedbackId(@PathVariable Long feedbackId) {
        return ResponseEntity.ok(feedbackReplyService.getRepliesByFeedbackId(feedbackId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRepliesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(feedbackReplyService.getRepliesByUserId(userId));
    }
} 