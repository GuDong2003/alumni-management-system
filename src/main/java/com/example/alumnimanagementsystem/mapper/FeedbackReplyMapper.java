package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.FeedbackReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedbackReplyMapper {
    void insert(FeedbackReply reply);
    void update(FeedbackReply reply);
    void deleteById(Long id);
    FeedbackReply selectById(Long id);
    List<FeedbackReply> selectAll();
    List<FeedbackReply> selectByFeedbackId(Long feedbackId);
    List<FeedbackReply> selectByUserId(Long userId);
    long count();
} 