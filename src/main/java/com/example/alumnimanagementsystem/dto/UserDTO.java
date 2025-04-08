package com.example.alumnimanagementsystem.dto;

import com.example.alumnimanagementsystem.entity.User;
import com.example.alumnimanagementsystem.entity.UserRole;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String studentId;
    private String major;
    private Integer graduationYear;
    private LocalDateTime lastLogin;
    private UserRole role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 从实体转换为DTO
     */
    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setStudentId(user.getStudentId());
        dto.setMajor(user.getMajor());
        dto.setGraduationYear(user.getGraduationYear());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setLastLogin(user.getLastLogin());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
    
    /**
     * 转换为实体
     */
    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setStudentId(this.studentId);
        user.setMajor(this.major);
        user.setGraduationYear(this.graduationYear);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setLastLogin(this.lastLogin);
        user.setRole(this.role);
        user.setStatus(this.status);
        user.setCreatedAt(this.createdAt);
        user.setUpdatedAt(this.updatedAt);
        return user;
    }
} 