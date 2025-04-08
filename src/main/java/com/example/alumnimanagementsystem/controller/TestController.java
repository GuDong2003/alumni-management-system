package com.example.alumnimanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String test() {
        return "Test Successful!";
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        Map<String, Object> response = new HashMap<>();

        System.out.println("接收到登录请求: username=" + username + ", password=" + password);
        System.out.println("请求对象: " + request);
        
        // 检查用户名和密码是否存在
        if (username == null || password == null) {
            response.put("success", false);
            response.put("message", "用户名或密码不能为空");
            return response;
        }
        
        // 打印出条件判断结果，方便调试
        boolean usernameMatch = "admin".equals(username);
        boolean passwordMatch = "admin".equals(password);
        System.out.println("用户名匹配: " + usernameMatch + ", 密码匹配: " + passwordMatch);
        
        // 为了确保登录成功，硬编码admin用户登录
        if (usernameMatch && passwordMatch) {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("username", username);
            response.put("role", "ADMIN");
            response.put("token", "mock-token-" + System.currentTimeMillis());
            System.out.println("登录成功，返回: " + response);
        } else {
            response.put("success", false);
            response.put("message", "密码错误");
            System.out.println("登录失败，返回: " + response);
        }
        
        return response;
    }

    @GetMapping("/create-admin")
    public Map<String, Object> createAdmin() {
        // 对明文密码'admin'进行加密
        String encryptedPassword = passwordEncoder.encode("admin");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "管理员密码已生成");
        response.put("encryptedPassword", encryptedPassword);
        response.put("forTest", "请使用此密码更新数据库");
        
        System.out.println("生成的管理员密码: " + encryptedPassword);
        
        return response;
    }

    @GetMapping("/admin-test")
    public String adminTest() {
        String encryptedPassword = passwordEncoder.encode("admin");
        System.out.println("生成的管理员密码: " + encryptedPassword);
        return "管理员密码已生成: " + encryptedPassword;
    }
} 