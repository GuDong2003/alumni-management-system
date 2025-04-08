package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.Feedback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackService {
    
    /**
     * 创建反馈
     * @param feedback 反馈对象
     * @return 创建后的反馈，包含ID
     */
    @Transactional
    Feedback createFeedback(Feedback feedback);
    
    /**
     * 更新反馈
     * @param feedback 反馈对象
     * @return 更新后的反馈
     */
    @Transactional
    Feedback updateFeedback(Feedback feedback);
    
    /**
     * 删除反馈
     * @param id 反馈ID
     * @return 是否删除成功
     */
    @Transactional
    boolean deleteFeedback(Long id);
    
    /**
     * 根据ID获取反馈
     * @param id 反馈ID
     * @return 反馈对象
     */
    Feedback getFeedbackById(Long id);
    
    /**
     * 获取所有反馈
     * @return 反馈列表
     */
    List<Feedback> getAllFeedbacks();
    
    /**
     * 根据用户ID获取反馈
     * @param userId 用户ID
     * @return 反馈列表
     */
    List<Feedback> getFeedbacksByUserId(Long userId);
    
    /**
     * 根据状态获取反馈
     * @param status 状态
     * @return 反馈列表
     */
    List<Feedback> getFeedbacksByStatus(String status);
    
    /**
     * 根据类型获取反馈
     * @param type 类型
     * @return 反馈列表
     */
    List<Feedback> getFeedbacksByType(String type);
    
    /**
     * 根据用户ID和状态获取反馈
     * @param userId 用户ID
     * @param status 状态
     * @return 反馈列表
     */
    List<Feedback> getFeedbacksByUserIdAndStatus(Long userId, String status);
    
    /**
     * 根据类型和状态获取反馈
     * @param type 类型
     * @param status 状态
     * @return 反馈列表
     */
    List<Feedback> getFeedbacksByTypeAndStatus(String type, String status);
    
    /**
     * 根据时间范围获取反馈
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 反馈列表
     */
    List<Feedback> getFeedbacksByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据关键词搜索反馈
     * @param keyword 关键词
     * @return 反馈列表
     */
    List<Feedback> searchFeedbacks(String keyword);
    
    /**
     * 分页获取反馈
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 反馈列表
     */
    List<Feedback> getFeedbacksPage(int page, int size);
    
    /**
     * 获取总记录数
     * @return 总记录数
     */
    int getTotalCount();
    
    /**
     * 根据状态获取反馈数量
     * @param status 状态
     * @return 反馈数量
     */
    int getCountByStatus(String status);
    
    /**
     * 更新反馈状态
     * @param id 反馈ID
     * @param status 新状态
     * @return 是否更新成功
     */
    @Transactional
    boolean updateFeedbackStatus(Long id, String status);
    
    /**
     * 回复反馈
     * @param id 反馈ID
     * @param reply 回复内容
     * @param replyUserId 回复人ID
     * @param replyUserName 回复人姓名
     * @param status 新状态
     * @return 是否回复成功
     */
    @Transactional
    boolean replyToFeedback(Long id, String reply, Long replyUserId, String replyUserName, String status);
    
    /**
     * 获取总反馈数（兼容StatisticsController）
     * @return 总反馈数
     */
    long count();
    
    /**
     * 根据状态获取反馈数（兼容StatisticsController）
     * @param status 状态
     * @return 反馈数
     */
    long countByStatus(String status);
} 