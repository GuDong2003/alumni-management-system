package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.Feedback;
import com.example.alumnimanagementsystem.entity.User;
import com.example.alumnimanagementsystem.service.FeedbackService;
import com.example.alumnimanagementsystem.service.UserService;
import com.example.alumnimanagementsystem.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 创建反馈
     */
    @PostMapping
    public Result<?> createFeedback(@RequestBody Feedback feedback) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                return Result.error("用户未登录或不存在");
            }
            
            User user = userOpt.get();
            
            // 设置用户ID
            feedback.setUserId(user.getId());
            
            // 创建反馈
            Feedback created = feedbackService.createFeedback(feedback);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error("创建反馈失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新反馈
     */
    @PutMapping("/{id}")
    public Result<?> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        try {
            feedback.setId(id);
            Feedback updated = feedbackService.updateFeedback(feedback);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("更新反馈失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除反馈
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteFeedback(@PathVariable Long id) {
        try {
            boolean success = feedbackService.deleteFeedback(id);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除反馈失败: 反馈不存在或已被删除");
            }
        } catch (Exception e) {
            return Result.error("删除反馈失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取反馈
     */
    @GetMapping("/{id}")
    public Result<?> getFeedbackById(@PathVariable Long id) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            if (feedback != null) {
                return Result.success(feedback);
            } else {
                return Result.error("反馈不存在");
            }
        } catch (Exception e) {
            return Result.error("获取反馈失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有反馈（分页）
     */
    @GetMapping
    public Result<?> getAllFeedbacks(@RequestParam(defaultValue = "1") int page, 
                                     @RequestParam(defaultValue = "10") int size) {
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksPage(page, size);
            int total = feedbackService.getTotalCount();
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", feedbacks);
            data.put("total", total);
            data.put("page", page);
            data.put("size", size);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取反馈列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID获取反馈
     */
    @GetMapping("/user/{userId}")
    public Result<?> getFeedbacksByUserId(@PathVariable Long userId) {
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksByUserId(userId);
            return Result.success(feedbacks);
        } catch (Exception e) {
            return Result.error("获取用户反馈失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据状态获取反馈
     */
    @GetMapping("/status/{status}")
    public Result<?> getFeedbacksByStatus(@PathVariable String status,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksByStatus(status);
            int total = feedbackService.getCountByStatus(status);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", feedbacks);
            data.put("total", total);
            data.put("page", page);
            data.put("size", size);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取反馈列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据类型获取反馈
     */
    @GetMapping("/type/{type}")
    public Result<?> getFeedbacksByType(@PathVariable String type) {
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbacksByType(type);
            return Result.success(feedbacks);
        } catch (Exception e) {
            return Result.error("获取反馈列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新反馈状态
     */
    @PutMapping("/{id}/status")
    public Result<?> updateFeedbackStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean success = feedbackService.updateFeedbackStatus(id, status);
            if (success) {
                return Result.success();
            } else {
                return Result.error("更新反馈状态失败: 反馈不存在");
            }
        } catch (Exception e) {
            return Result.error("更新反馈状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 回复反馈
     */
    @PutMapping("/{id}/reply")
    public Result<?> replyToFeedback(@PathVariable Long id, 
                                     @RequestParam String reply,
                                     @RequestParam(defaultValue = "REPLIED") String status,
                                     @RequestHeader(value = "X-Current-Username", required = false) String headerUsername) {
        try {
            User replyUser;
            
            // 首先尝试使用请求头中的用户名
            if (headerUsername != null && !headerUsername.isEmpty()) {
                Optional<User> userOpt = userService.findByUsername(headerUsername);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    // 检查是否是管理员或超级管理员
                    if ("ADMIN".equals(user.getRole().toString()) || "SUPER_ADMIN".equals(user.getRole().toString())) {
                        replyUser = user;
                        System.out.println("使用请求头指定的用户回复反馈: " + replyUser.getUsername() + ", 角色: " + replyUser.getRole());
                    } else {
                        // 如果不是管理员，尝试使用admin账号
                        replyUser = userService.findByUsername("admin").orElse(null);
                        System.out.println("请求头用户不是管理员，尝试使用admin账号");
                    }
                } else {
                    // 找不到用户，尝试使用admin账号
                    replyUser = userService.findByUsername("admin").orElse(null);
                    System.out.println("找不到请求头指定的用户，尝试使用admin账号");
                }
            } else {
                // 没有请求头，尝试使用admin账号
                replyUser = userService.findByUsername("admin").orElse(null);
                System.out.println("请求头中没有用户名，尝试使用admin账号");
            }
            
            // 如果admin账号不存在，尝试superadmin账号
            if (replyUser == null) {
                replyUser = userService.findByUsername("superadmin")
                        .orElseThrow(() -> new RuntimeException("找不到admin或superadmin用户"));
                System.out.println("admin账号不存在，使用superadmin账号");
            }
            
            System.out.println("最终使用的回复用户: " + replyUser.getUsername() + ", ID: " + replyUser.getId() + ", 姓名: " + replyUser.getName() + ", 角色: " + replyUser.getRole());
            
            boolean success = feedbackService.replyToFeedback(id, reply, replyUser.getId(), replyUser.getName(), status);
            if (success) {
                return Result.success();
            } else {
                return Result.error("回复反馈失败: 反馈不存在");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈信息
            return Result.error("回复反馈失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索反馈
     */
    @GetMapping("/search")
    public Result<?> searchFeedbacks(@RequestParam String keyword) {
        try {
            List<Feedback> feedbacks = feedbackService.searchFeedbacks(keyword);
            return Result.success(feedbacks);
        } catch (Exception e) {
            return Result.error("搜索反馈失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户的反馈
     */
    @GetMapping("/my")
    public Result<?> getMyFeedbacks() {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                return Result.error("用户未登录或不存在");
            }
            
            User user = userOpt.get();
            
            List<Feedback> feedbacks = feedbackService.getFeedbacksByUserId(user.getId());
            return Result.success(feedbacks);
        } catch (Exception e) {
            return Result.error("获取我的反馈失败: " + e.getMessage());
        }
    }
} 