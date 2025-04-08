package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.User;
import com.example.alumnimanagementsystem.entity.Alumni;
import com.example.alumnimanagementsystem.service.UserService;
import com.example.alumnimanagementsystem.mapper.AlumniMapper;
import com.example.alumnimanagementsystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/current-user")
@CrossOrigin(originPatterns = "*")
public class CurrentUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AlumniMapper alumniMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public ResponseEntity<?> getCurrentUser(@RequestParam String username) {
        try {
            // 根据用户名从数据库获取用户信息
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "用户不存在");
                }});
            }
            
            // 获取校友信息
            Alumni alumni = alumniMapper.selectByUserId(user.getId());

            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取用户信息成功");
            Map<String, Object> data = new HashMap<>();
            
            // 用户信息
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            data.put("role", user.getRole());
            data.put("status", user.getStatus());
            data.put("studentId", user.getStudentId());
            data.put("major", user.getMajor());
            data.put("graduationYear", user.getGraduationYear());
            data.put("createdAt", user.getCreatedAt());
            data.put("lastLogin", user.getLastLogin());

            // 校友信息
            if (alumni != null) {
                data.put("currentCompany", alumni.getCurrentCompany());
                data.put("currentPosition", alumni.getCurrentPosition());
                data.put("industry", alumni.getIndustry());
                data.put("location", alumni.getLocation());
                data.put("bio", alumni.getBio());
            }
            
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "获取用户信息失败：" + e.getMessage());
            }});
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User profile) {
        try {
            // 获取当前认证用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "用户未登录");
                }});
            }

            String username = authentication.getName();
            if (username == null || username.isEmpty()) {
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "无法获取用户信息");
                }});
            }

            // 获取当前用户
            User currentUser = userMapper.selectByUsername(username);
            if (currentUser == null) {
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "用户不存在");
                }});
            }
            
            // 更新用户信息
            profile.setId(currentUser.getId());
            profile.setRole(currentUser.getRole()); // 保留原有角色
            profile.setStatus(currentUser.getStatus()); // 保留原有状态
            profile.setStudentId(currentUser.getStudentId()); // 保留原有学号
            profile.setMajor(currentUser.getMajor()); // 保留原有专业
            profile.setGraduationYear(currentUser.getGraduationYear()); // 保留原有毕业年份
            userService.updateUser(currentUser.getId(), profile);
            
            // 更新校友信息
            Alumni alumni = alumniMapper.selectByUserId(currentUser.getId());
            if (alumni == null && (
                profile.getCurrentCompany() != null ||
                profile.getCurrentPosition() != null ||
                profile.getIndustry() != null ||
                profile.getLocation() != null ||
                profile.getBio() != null
            )) {
                // 如果没有校友记录但提供了校友信息，创建新记录
                alumni = new Alumni();
                alumni.setUserId(currentUser.getId());
                alumni.setCurrentCompany(profile.getCurrentCompany());
                alumni.setCurrentPosition(profile.getCurrentPosition());
                alumni.setIndustry(profile.getIndustry());
                alumni.setLocation(profile.getLocation());
                alumni.setBio(profile.getBio());
                alumni.setCreatedAt(LocalDateTime.now());
                alumni.setUpdatedAt(LocalDateTime.now());
                alumniMapper.insert(alumni);
            } else if (alumni != null) {
                // 更新现有校友记录
                alumni.setCurrentCompany(profile.getCurrentCompany());
                alumni.setCurrentPosition(profile.getCurrentPosition());
                alumni.setIndustry(profile.getIndustry());
                alumni.setLocation(profile.getLocation());
                alumni.setBio(profile.getBio());
                alumni.setUpdatedAt(LocalDateTime.now());
                alumniMapper.update(alumni);
            }
            
            // 获取更新后的用户信息
            User updatedUser = userService.getUserById(currentUser.getId());
            Alumni updatedAlumni = alumniMapper.selectByUserId(currentUser.getId());
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "更新个人信息成功");
            Map<String, Object> data = new HashMap<>();
            
            // 用户信息
            data.put("id", updatedUser.getId());
            data.put("username", updatedUser.getUsername());
            data.put("email", updatedUser.getEmail());
            data.put("phone", updatedUser.getPhone());
            data.put("role", updatedUser.getRole());
            data.put("status", updatedUser.getStatus());
            data.put("studentId", updatedUser.getStudentId());
            data.put("major", updatedUser.getMajor());
            data.put("graduationYear", updatedUser.getGraduationYear());
            data.put("createdAt", updatedUser.getCreatedAt());
            data.put("lastLogin", updatedUser.getLastLogin());

            // 校友信息
            if (updatedAlumni != null) {
                data.put("currentCompany", updatedAlumni.getCurrentCompany());
                data.put("currentPosition", updatedAlumni.getCurrentPosition());
                data.put("industry", updatedAlumni.getIndustry());
                data.put("location", updatedAlumni.getLocation());
                data.put("bio", updatedAlumni.getBio());
            }
            
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误信息到日志
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "更新个人信息失败：" + e.getMessage());
            }});
        }
    }
} 