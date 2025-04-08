package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.ActivityComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityCommentMapper {
    @Select("SELECT c.*, " +
            "CASE WHEN l.id IS NOT NULL THEN true ELSE false END as hasLiked " +
            "FROM activity_comment c " +
            "LEFT JOIN comment_like l ON c.id = l.comment_id AND l.user_id = #{userId} " +
            "WHERE c.activity_id = #{activityId} AND c.parent_id IS NULL " +
            "ORDER BY c.created_at DESC")
    List<ActivityComment> selectByActivityId(@Param("activityId") Long activityId, @Param("userId") Long userId);

    @Select("SELECT c.*, " +
            "CASE WHEN l.id IS NOT NULL THEN true ELSE false END as hasLiked " +
            "FROM activity_comment c " +
            "LEFT JOIN comment_like l ON c.id = l.comment_id AND l.user_id = #{userId} " +
            "WHERE c.parent_id = #{parentId} " +
            "ORDER BY c.created_at ASC")
    List<ActivityComment> selectReplies(@Param("parentId") Long parentId, @Param("userId") Long userId);

    @Select("SELECT * FROM activity_comment WHERE id = #{id}")
    ActivityComment selectById(Long id);

    @Insert("INSERT INTO activity_comment (activity_id, user_id, user_name, content, parent_id, likes, created_at, updated_at, status) " +
            "VALUES (#{activityId}, #{userId}, #{userName}, #{content}, #{parentId}, 0, NOW(), NOW(), 'NORMAL')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ActivityComment comment);

    @Update("UPDATE activity_comment SET content = #{content}, updated_at = NOW() WHERE id = #{id}")
    int update(ActivityComment comment);

    @Delete("DELETE FROM activity_comment WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int hasLiked(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Insert("INSERT INTO comment_like (comment_id, user_id, created_at) VALUES (#{commentId}, #{userId}, NOW())")
    int addLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Delete("DELETE FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int removeLike(@Param("commentId") Long commentId, @Param("userId") Long userId);

    @Update("UPDATE activity_comment SET likes = likes + 1 WHERE id = #{commentId}")
    int incrementLikeCount(Long commentId);

    @Update("UPDATE activity_comment SET likes = likes - 1 WHERE id = #{commentId}")
    int decrementLikeCount(Long commentId);
} 