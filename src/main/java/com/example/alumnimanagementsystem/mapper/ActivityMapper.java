package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDateTime;

@Mapper
public interface ActivityMapper {
    // 插入活动信息
    int insert(Activity activity);
    
    // 更新活动信息
    int update(Activity activity);
    
    // 删除活动信息
    int deleteById(Long id);
    
    // 根据ID查询活动
    Activity selectById(Long id);
    
    // 查询所有活动
    List<Activity> selectAll();
    
    // 根据标题查询活动
    List<Activity> selectByTitle(String title);
    
    // 根据类型查询活动
    List<Activity> selectByType(String type);
    
    // 根据状态查询活动
    List<Activity> selectByStatus(String status);
    
    // 根据标题模糊查询活动
    List<Activity> selectByTitleContaining(String title);
    
    // 根据类型和状态查询活动
    List<Activity> selectByTypeAndStatus(@Param("type") String type, @Param("status") String status);
    
    // 根据开始时间查询活动
    List<Activity> selectByStartTime(String startTime);
    
    // 根据结束时间查询活动
    List<Activity> selectByEndTime(String endTime);
    
    // 根据时间范围查询活动
    List<Activity> selectByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    // 更新活动状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 更新活动参与人数
    int updateParticipantCount(@Param("id") Long id, @Param("count") Integer count);
    
    // 根据位置查询活动
    List<Activity> selectByLocation(String location);
    
    // 根据关键词搜索活动
    List<Activity> search(String keyword);
    
    // 统计活动总数
    long count();
    
    // 统计特定状态的活动总数
    long countByStatus(String status);
} 