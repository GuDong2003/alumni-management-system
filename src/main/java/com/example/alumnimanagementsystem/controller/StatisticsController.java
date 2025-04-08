package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.service.UserService;
import com.example.alumnimanagementsystem.service.ActivityService;
import com.example.alumnimanagementsystem.service.DonationService;
import com.example.alumnimanagementsystem.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private DonationService donationService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 用户统计
        statistics.put("totalUsers", userService.count());
        
        // 活动统计
        statistics.put("totalActivities", activityService.count());
        statistics.put("activeActivities", activityService.countByStatus("ACTIVE"));
        
        // 捐赠统计
        statistics.put("totalDonations", donationService.count());
        statistics.put("totalDonationAmount", donationService.calculateTotalAmount());
        
        // 反馈统计
        statistics.put("totalFeedback", feedbackService.count());
        statistics.put("pendingFeedback", feedbackService.countByStatus("PENDING"));
        
        return ResponseEntity.ok(statistics);
    }
} 