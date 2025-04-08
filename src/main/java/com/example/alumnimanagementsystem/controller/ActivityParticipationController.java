package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.ActivityParticipation;
import com.example.alumnimanagementsystem.service.ActivityParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities/{activityId}/participants")
public class ActivityParticipationController {

    @Autowired
    private ActivityParticipationService participationService;

    @GetMapping
    public ResponseEntity<List<ActivityParticipation>> getParticipants(@PathVariable Long activityId) {
        List<ActivityParticipation> participants = participationService.getParticipantsByActivityId(activityId);
        return ResponseEntity.ok(participants);
    }

    @PostMapping
    public ResponseEntity<ActivityParticipation> register(@PathVariable Long activityId) {
        Long userId = getCurrentUserId();
        ActivityParticipation participation = participationService.register(activityId, userId);
        return ResponseEntity.ok(participation);
    }

    @PutMapping("/{userId}/status/{status}")
    public ResponseEntity<ActivityParticipation> updateStatus(
            @PathVariable Long activityId,
            @PathVariable Long userId,
            @PathVariable String status) {
        ActivityParticipation participation = participationService.updateStatus(activityId, userId, status);
        return ResponseEntity.ok(participation);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long activityId,
            @PathVariable Long userId) {
        participationService.cancel(activityId, userId);
        return ResponseEntity.ok().build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
} 