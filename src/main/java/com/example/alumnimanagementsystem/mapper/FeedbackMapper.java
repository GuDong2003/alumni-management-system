package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.Feedback;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FeedbackMapper {
    
    @Insert("INSERT INTO feedback(user_id, title, content, type, status, created_at, updated_at) " +
            "VALUES(#{userId}, #{title}, #{content}, #{type}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Feedback feedback);
    
    @Update("UPDATE feedback SET user_id = #{userId}, title = #{title}, " +
            "content = #{content}, type = #{type}, status = #{status}, reply = #{reply}, " +
            "reply_user_id = #{replyUserId}, reply_user_name = #{replyUserName}, reply_time = #{replyTime}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Feedback feedback);
    
    @Delete("DELETE FROM feedback WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT * FROM feedback WHERE id = #{id}")
    Feedback findById(Long id);
    
    @Select("SELECT * FROM feedback ORDER BY " +
            "CASE status " +
            "  WHEN 'PENDING' THEN 1 " +
            "  WHEN 'PROCESSING' THEN 2 " +
            "  WHEN 'REPLIED' THEN 3 " +
            "  WHEN 'RESOLVED' THEN 4 " +
            "  WHEN 'CLOSED' THEN 5 " +
            "  ELSE 6 " +
            "END, " +
            "created_at DESC")
    List<Feedback> findAll();
    
    @Select("SELECT * FROM feedback WHERE user_id = #{userId} ORDER BY " +
            "CASE status " +
            "  WHEN 'PENDING' THEN 1 " +
            "  WHEN 'PROCESSING' THEN 2 " +
            "  WHEN 'REPLIED' THEN 3 " +
            "  WHEN 'RESOLVED' THEN 4 " +
            "  WHEN 'CLOSED' THEN 5 " +
            "  ELSE 6 " +
            "END, " +
            "created_at DESC")
    List<Feedback> findByUserId(Long userId);
    
    @Select("SELECT * FROM feedback WHERE status = #{status} ORDER BY created_at DESC")
    List<Feedback> findByStatus(String status);
    
    @Select("SELECT * FROM feedback WHERE type = #{type} ORDER BY " +
            "CASE status " +
            "  WHEN 'PENDING' THEN 1 " +
            "  WHEN 'PROCESSING' THEN 2 " +
            "  WHEN 'REPLIED' THEN 3 " +
            "  WHEN 'RESOLVED' THEN 4 " +
            "  WHEN 'CLOSED' THEN 5 " +
            "  ELSE 6 " +
            "END, " +
            "created_at DESC")
    List<Feedback> findByType(String type);
    
    @Select("SELECT * FROM feedback WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    List<Feedback> findByUserIdAndStatus(Long userId, String status);
    
    @Select("SELECT * FROM feedback WHERE type = #{type} AND status = #{status} ORDER BY created_at DESC")
    List<Feedback> findByTypeAndStatus(String type, String status);
    
    @Select("SELECT * FROM feedback WHERE created_at BETWEEN #{startTime} AND #{endTime} ORDER BY " +
            "CASE status " +
            "  WHEN 'PENDING' THEN 1 " +
            "  WHEN 'PROCESSING' THEN 2 " +
            "  WHEN 'REPLIED' THEN 3 " +
            "  WHEN 'RESOLVED' THEN 4 " +
            "  WHEN 'CLOSED' THEN 5 " +
            "  ELSE 6 " +
            "END, " +
            "created_at DESC")
    List<Feedback> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    @Select("SELECT * FROM feedback WHERE content LIKE CONCAT('%', #{keyword}, '%') OR title LIKE CONCAT('%', #{keyword}, '%') ORDER BY " +
            "CASE status " +
            "  WHEN 'PENDING' THEN 1 " +
            "  WHEN 'PROCESSING' THEN 2 " +
            "  WHEN 'REPLIED' THEN 3 " +
            "  WHEN 'RESOLVED' THEN 4 " +
            "  WHEN 'CLOSED' THEN 5 " +
            "  ELSE 6 " +
            "END, " +
            "created_at DESC")
    List<Feedback> searchByContent(String keyword);
    
    @Select("SELECT * FROM feedback ORDER BY " +
            "CASE status " +
            "  WHEN 'PENDING' THEN 1 " +
            "  WHEN 'PROCESSING' THEN 2 " +
            "  WHEN 'REPLIED' THEN 3 " +
            "  WHEN 'RESOLVED' THEN 4 " +
            "  WHEN 'CLOSED' THEN 5 " +
            "  ELSE 6 " +
            "END, " +
            "created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Feedback> findPage(int offset, int limit);
    
    @Select("SELECT COUNT(*) FROM feedback")
    int getTotalCount();
    
    @Select("SELECT COUNT(*) FROM feedback WHERE status = #{status}")
    int getCountByStatus(String status);
    
    @Update("UPDATE feedback SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(Long id, String status, LocalDateTime updatedAt);
    
    @Update("UPDATE feedback SET reply = #{reply}, reply_user_id = #{replyUserId}, " +
            "reply_user_name = #{replyUserName}, reply_time = #{replyTime}, " +
            "status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int replyFeedback(Long id, String reply, Long replyUserId, String replyUserName, 
                      LocalDateTime replyTime, String status, LocalDateTime updatedAt);
} 