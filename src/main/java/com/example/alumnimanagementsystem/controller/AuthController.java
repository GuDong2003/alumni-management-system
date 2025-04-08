package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.dto.LoginRequest;
import com.example.alumnimanagementsystem.dto.RegisterRequest;
import com.example.alumnimanagementsystem.entity.Alumni;
import com.example.alumnimanagementsystem.entity.User;
import com.example.alumnimanagementsystem.mapper.AlumniMapper;
import com.example.alumnimanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AlumniMapper alumniMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 获取用户信息
        User user = userService.getUserByUsername(loginRequest.getUsername());
        
        // 获取校友信息
        Alumni alumni = alumniMapper.selectByUserId(user.getId());
        
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登录成功");
        response.put("token", "mock-token-" + System.currentTimeMillis());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("id", user.getId());
        
        // 从用户表获取学号、专业和毕业年份
        response.put("studentId", user.getStudentId());
        response.put("major", user.getMajor());
        response.put("graduationYear", user.getGraduationYear());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            userService.register(registerRequest);
            return ResponseEntity.ok().body("注册成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(@RequestParam String username) {
        try {
            // 获取用户信息
            User user = userService.getUserByUsername(username);
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
            data.put("name", user.getName());
            data.put("gender", user.getGender());

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
} 