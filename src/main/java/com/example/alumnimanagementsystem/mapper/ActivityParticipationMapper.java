package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.ActivityParticipation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityParticipationMapper {
    @Select("SELECT ap.*, u.name, u.email, u.phone " +
            "FROM activity_participation ap " +
            "JOIN user u ON ap.user_id = u.id " +
            "WHERE ap.activity_id = #{activityId} " +
            "ORDER BY ap.register_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "activityId", column = "activity_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "registerTime", column = "register_time"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "name", column = "name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone")
    })
    List<ActivityParticipation> selectByActivityId(Long activityId);

    @Insert("INSERT INTO activity_participation (activity_id, user_id, status, register_time) " +
            "VALUES (#{activityId}, #{userId}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ActivityParticipation participation);

    @Update("UPDATE activity_participation SET status = #{status}, updated_at = NOW() " +
            "WHERE activity_id = #{activityId} AND user_id = #{userId}")
    int updateStatus(ActivityParticipation participation);

    @Delete("DELETE FROM activity_participation WHERE activity_id = #{activityId} AND user_id = #{userId}")
    int delete(ActivityParticipation participation);
} 