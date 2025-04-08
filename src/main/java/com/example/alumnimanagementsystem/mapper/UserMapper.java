package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.User;
import com.example.alumnimanagementsystem.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    void insert(User user);
    
    User selectById(@Param("id") Long id);
    
    User selectByUsername(@Param("username") String username);
    
    User selectByEmail(@Param("email") String email);
    
    User selectByPhone(@Param("phone") String phone);
    
    List<User> selectAll();
    
    List<User> selectByRole(@Param("role") UserRole role);
    
    List<User> selectByStatus(@Param("status") String status);
    
    List<User> selectByGraduationYear(@Param("graduationYear") Integer graduationYear);
    
    List<User> selectByMajor(@Param("major") String major);
    
    void update(User user);
    
    void deleteById(@Param("id") Long id);
    
    boolean existsByUsername(@Param("username") String username);
    
    boolean existsByEmail(@Param("email") String email);
    
    boolean existsByPhone(@Param("phone") String phone);
    
    void updatePassword(@Param("id") Long id, @Param("password") String password);
    
    void updateStatus(@Param("id") Long id, @Param("status") String status);
    
    void updateRole(@Param("id") Long id, @Param("role") UserRole role);
    
    void updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime lastLoginTime);
    
    void updateEmail(@Param("id") Long id, @Param("email") String email);

    void updatePhone(@Param("id") Long id, @Param("phone") String phone);

    void updateStudentId(@Param("id") Long id, @Param("studentId") String studentId);

    void updateMajor(@Param("id") Long id, @Param("major") String major);

    void updateGraduationYear(@Param("id") Long id, @Param("graduationYear") Integer graduationYear);

    List<User> selectByRoleAndStatus(@Param("role") UserRole role, @Param("status") String status);

    List<User> selectByGraduationYearAndMajor(@Param("graduationYear") Integer graduationYear, @Param("major") String major);

    List<User> selectByRoleAndGraduationYear(@Param("role") UserRole role, @Param("graduationYear") Integer graduationYear);

    List<User> selectByStatusAndMajor(@Param("status") String status, @Param("major") String major);

    List<User> selectByRoleAndMajor(@Param("role") UserRole role, @Param("major") String major);

    List<User> selectByStatusAndGraduationYear(@Param("status") String status, @Param("graduationYear") Integer graduationYear);

    List<User> selectByRoleAndStatusAndGraduationYear(@Param("role") UserRole role, @Param("status") String status, @Param("graduationYear") Integer graduationYear);

    List<User> selectByRoleAndStatusAndMajor(@Param("role") UserRole role, @Param("status") String status, @Param("major") String major);

    List<User> selectByRoleAndGraduationYearAndMajor(@Param("role") UserRole role, @Param("graduationYear") Integer graduationYear, @Param("major") String major);

    List<User> selectByStatusAndGraduationYearAndMajor(@Param("status") String status, @Param("graduationYear") Integer graduationYear, @Param("major") String major);

    List<User> selectByRoleAndStatusAndGraduationYearAndMajor(@Param("role") UserRole role, @Param("status") String status, @Param("graduationYear") Integer graduationYear, @Param("major") String major);

    List<User> search(@Param("keyword") String keyword);

    long count();
} 