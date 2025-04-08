package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.Activity;
import com.example.alumnimanagementsystem.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(originPatterns = "*")
public class ActivityController {
    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;


    // 创建活动
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        return ResponseEntity.ok(activityService.createActivity(activity));
    }

    // 更新活动信息
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        return ResponseEntity.ok(activityService.updateActivity(id, activity));
    }

    // 删除活动
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.ok().build();
    }

    // 根据ID查找活动
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return activityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 获取所有活动
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityService.findAll());
    }

    // 根据标题查找活动
    @GetMapping("/title/{title}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> getActivitiesByTitle(@PathVariable String title) {
        return ResponseEntity.ok(activityService.findByTitle(title));
    }

    // 根据类型查找活动
    @GetMapping("/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> getActivitiesByType(@PathVariable String type) {
        return ResponseEntity.ok(activityService.findByType(type));
    }

    // 根据状态查找活动
    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> getActivitiesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(activityService.findByStatus(status));
    }

    // 模糊查询
    @GetMapping("/search/title")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> searchActivitiesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(activityService.findByTitleContaining(title));
    }

    @GetMapping("/type/{type}/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> getActivitiesByTypeAndStatus(
            @PathVariable String type,
            @PathVariable String status) {
        return ResponseEntity.ok(activityService.findByTypeAndStatus(type, status));
    }

    @GetMapping("/start-time/{startTime}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> getActivitiesByStartTime(@PathVariable String startTime) {
        return ResponseEntity.ok(activityService.findByStartTime(LocalDateTime.parse(startTime)));
    }

    @GetMapping("/end-time/{endTime}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> getActivitiesByEndTime(@PathVariable String endTime) {
        return ResponseEntity.ok(activityService.findByEndTime(LocalDateTime.parse(endTime)));
    }

    @GetMapping("/time-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> getActivitiesByTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        return ResponseEntity.ok(activityService.findByTimeRange(
                LocalDateTime.parse(startTime),
                LocalDateTime.parse(endTime)));
    }

    // 活动状态管理
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateActivityStatus(
            @PathVariable Long id,
            @PathVariable String status) {
        activityService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/participant-count/{count}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateActivityParticipantCount(
            @PathVariable Long id,
            @PathVariable Integer count) {
        activityService.updateParticipantCount(id, count);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Activity>> searchActivities(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return ResponseEntity.ok(activityService.searchActivities(title, type, status, startTime, endTime));
    }
} 