package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.ActivityParticipation;
import com.example.alumnimanagementsystem.service.ActivityParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities/{activityId}/participants")
public class ActivityParticipationController {

    @Autowired
    private ActivityParticipationService participationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivityParticipation>> getParticipants(@PathVariable Long activityId) {
        List<ActivityParticipation> participants = participationService.getParticipantsByActivityId(activityId);
        return ResponseEntity.ok(participants);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> register(@PathVariable Long activityId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body("用户ID不能为空");
        }
        try {
            ActivityParticipation participation = participationService.register(activityId, userId);
            return ResponseEntity.ok(participation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器内部错误");
        }
    }

    @PutMapping("/{userId}/status/{status}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long activityId,
            @PathVariable Long userId,
            @PathVariable String status) {
        try {
            ActivityParticipation participation = participationService.updateStatus(activityId, userId, status);
            return ResponseEntity.ok(participation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器内部错误");
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancel(
            @PathVariable Long activityId,
            @PathVariable Long userId) {
        try {
            participationService.cancel(activityId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器内部错误");
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
} 