package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.dto.RegisterRequest;
import com.example.alumnimanagementsystem.entity.User;
import com.example.alumnimanagementsystem.entity.UserRole;
import com.example.alumnimanagementsystem.entity.Alumni;
import com.example.alumnimanagementsystem.mapper.AlumniMapper;
import com.example.alumnimanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AlumniMapper alumniMapper;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("success", true);
                put("message", "注册成功");
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
            }});
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            // 检查用户名和密码是否为空
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "用户名或密码不能为空");
                }});
            }

            User user = userService.login(username, password);
            
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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
            }});
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            // 调用服务层的登出方法
            userService.logout();
            // 清除安全上下文
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("退出登录失败：" + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user, @RequestHeader(value = "X-Current-Username", required = false) String headerUsername) {
        try {
            System.out.println("收到更新用户请求, ID: " + id + ", 请求头用户名: " + headerUsername + ", 请求体用户名: " + user.getCurrentUsername());
            
            // 检查权限
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = null;
            
            // 尝试从认证上下文获取用户名
            if (authentication != null && authentication.isAuthenticated() && 
                authentication.getName() != null && !"anonymousUser".equals(authentication.getName())) {
                currentUsername = authentication.getName();
                System.out.println("从认证上下文获取用户名: " + currentUsername);
            }
            
            // 如果认证上下文中没有用户名，尝试从请求头或请求体获取
            if (currentUsername == null || "anonymousUser".equals(currentUsername)) {
                // 首先尝试从请求头获取
                if (headerUsername != null && !headerUsername.isEmpty()) {
                    currentUsername = headerUsername;
                    System.out.println("从请求头获取用户名: " + currentUsername);
                } 
                // 然后尝试从请求体获取
                else if (user.getCurrentUsername() != null && !user.getCurrentUsername().isEmpty()) {
                    currentUsername = user.getCurrentUsername();
                    System.out.println("从请求体获取用户名: " + currentUsername);
                }
            }
            
            // 如果还是无法获取用户名，则返回错误
            if (currentUsername == null || currentUsername.isEmpty() || "anonymousUser".equals(currentUsername)) {
                System.out.println("无法识别当前用户");
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "无法识别当前用户，请重新登录");
                }});
            }

            // 获取当前用户
            User currentUser = userService.getUserByUsername(currentUsername);
            if (currentUser == null) {
                System.out.println("当前用户不存在: " + currentUsername);
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "当前用户不存在");
                }});
            }
            
            System.out.println("已找到当前用户: " + currentUser.getUsername() + ", ID: " + currentUser.getId() + ", 角色: " + currentUser.getRole());

            // 检查权限
            if (!currentUser.getRole().equals(UserRole.SUPER_ADMIN) && 
                !currentUser.getRole().equals(UserRole.ADMIN) && 
                !currentUser.getId().equals(id)) {
                System.out.println("无权修改其他用户信息");
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "无权修改其他用户信息");
                }});
            }

            // 更新用户信息
            userService.updateUser(id, user);
            
            // 获取校友信息
            Alumni alumni = alumniMapper.selectByUserId(id);

            if (alumni == null && (
                user.getCurrentCompany() != null ||
                user.getCurrentPosition() != null ||
                user.getIndustry() != null ||
                user.getLocation() != null ||
                user.getBio() != null
            )) {
                try {
                    // 如果没有校友记录但提供了校友信息，创建新记录
                    alumni = new Alumni();
                    alumni.setUserId(id);
                    alumni.setCurrentCompany(user.getCurrentCompany());
                    alumni.setCurrentPosition(user.getCurrentPosition());
                    alumni.setIndustry(user.getIndustry());
                    alumni.setLocation(user.getLocation());
                    alumni.setBio(user.getBio());
                    // 不再设置name和gender字段
                    alumni.setCreatedAt(LocalDateTime.now());
                    alumni.setUpdatedAt(LocalDateTime.now());
                    alumniMapper.insert(alumni);
                } catch (Exception e) {
                    System.err.println("创建用户ID: " + id + " 的校友信息时出错: " + e.getMessage());
                }
            } else if (alumni != null) {
                try {
                    // 更新现有校友记录
                    alumni.setCurrentCompany(user.getCurrentCompany());
                    alumni.setCurrentPosition(user.getCurrentPosition());
                    alumni.setIndustry(user.getIndustry());
                    alumni.setLocation(user.getLocation());
                    alumni.setBio(user.getBio());
                    // 不再设置name和gender字段
                    alumni.setUpdatedAt(LocalDateTime.now());
                    alumniMapper.update(alumni);
                } catch (Exception e) {
                    System.err.println("更新用户ID: " + id + " 的校友信息时出错: " + e.getMessage());
                }
            }
            
            // 获取更新后的用户信息
            User updatedUser = userService.getUserById(id);
            Alumni updatedAlumni = null;
            try {
                updatedAlumni = alumniMapper.selectByUserId(id);
            } catch (Exception e) {
                System.err.println("获取更新后的用户ID: " + id + " 的校友信息时出错: " + e.getMessage());
            }
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "更新用户信息成功");
            Map<String, Object> data = new HashMap<>();
            
            // 用户信息
            data.put("id", updatedUser.getId());
            data.put("username", updatedUser.getUsername());
            data.put("name", updatedUser.getName());
            data.put("gender", updatedUser.getGender());
            data.put("email", updatedUser.getEmail());
            data.put("phone", updatedUser.getPhone());
            data.put("role", updatedUser.getRole());
            data.put("status", updatedUser.getStatus());
            data.put("studentId", updatedUser.getStudentId());
            data.put("major", updatedUser.getMajor());
            data.put("graduationYear", updatedUser.getGraduationYear());
            data.put("createdAt", updatedUser.getCreatedAt());
            data.put("updatedAt", updatedUser.getUpdatedAt());

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
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "更新用户信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("success", true);
                put("message", "删除成功");
            }});
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
            }});
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                Map<String, Object> userMap = new HashMap<>();
                // 添加用户基本信息
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("name", user.getName());
                userMap.put("gender", user.getGender());
                userMap.put("email", user.getEmail());
                userMap.put("phone", user.getPhone());
                userMap.put("role", user.getRole());
                userMap.put("status", user.getStatus());
                userMap.put("studentId", user.getStudentId());
                userMap.put("major", user.getMajor());
                userMap.put("graduationYear", user.getGraduationYear());
                userMap.put("createdAt", user.getCreatedAt());
                userMap.put("updatedAt", user.getUpdatedAt());
                userMap.put("lastLogin", user.getLastLogin());
                
                try {
                    // 获取并添加校友信息
                    Alumni alumni = alumniMapper.selectByUserId(id);
                    if (alumni != null) {
                        userMap.put("currentCompany", alumni.getCurrentCompany());
                        userMap.put("currentPosition", alumni.getCurrentPosition());
                        userMap.put("industry", alumni.getIndustry());
                        userMap.put("location", alumni.getLocation());
                        userMap.put("bio", alumni.getBio());
                    }
                } catch (Exception e) {
                    System.err.println("获取用户ID: " + id + " 的校友信息时出错: " + e.getMessage());
                    e.printStackTrace();
                }
                
                return ResponseEntity.ok(userMap);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取用户信息失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<User> users = userService.findAll();
            List<Map<String, Object>> userList = new ArrayList<>();
            
            // 获取每个用户的校友信息
            for (User user : users) {
                try {
                    if (user != null && user.getId() != null) {
                        Map<String, Object> userMap = new HashMap<>();
                        // 添加用户基本信息
                        userMap.put("id", user.getId());
                        userMap.put("username", user.getUsername());
                        userMap.put("name", user.getName());
                        userMap.put("gender", user.getGender());
                        userMap.put("email", user.getEmail());
                        userMap.put("phone", user.getPhone());
                        userMap.put("role", user.getRole());
                        userMap.put("status", user.getStatus());
                        userMap.put("studentId", user.getStudentId());
                        userMap.put("major", user.getMajor());
                        userMap.put("graduationYear", user.getGraduationYear());
                        userMap.put("createdAt", user.getCreatedAt());
                        userMap.put("updatedAt", user.getUpdatedAt());
                        userMap.put("lastLogin", user.getLastLogin());
                        
                        // 获取并添加校友信息
                        Alumni alumni = alumniMapper.selectByUserId(user.getId());
                        if (alumni != null) {
                            userMap.put("currentCompany", alumni.getCurrentCompany());
                            userMap.put("currentPosition", alumni.getCurrentPosition());
                            userMap.put("industry", alumni.getIndustry());
                            userMap.put("location", alumni.getLocation());
                            userMap.put("bio", alumni.getBio());
                        } else {
                            userMap.put("currentCompany", null);
                            userMap.put("currentPosition", null);
                            userMap.put("industry", null);
                            userMap.put("location", null);
                            userMap.put("bio", null);
                        }
                        
                        userList.add(userMap);
                    }
                } catch (Exception e) {
                    // 记录异常但继续处理其他用户
                    System.err.println("处理用户ID: " + user.getId() + " 的校友信息时出错: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // 按角色排序：SUPER_ADMIN > ADMIN > ALUMNI
            userList.sort((a, b) -> {
                Object roleAObj = a.get("role");
                Object roleBObj = b.get("role");
                
                // 使用自定义排序顺序
                int orderA = getRoleOrder(roleAObj);
                int orderB = getRoleOrder(roleBObj);
                
                return Integer.compare(orderA, orderB);
            });
            
            // 计算分页
            int total = userList.size();
            int start = (page - 1) * size;
            int end = Math.min(start + size, total);
            
            List<Map<String, Object>> pageContent = start < end ? userList.subList(start, end) : new ArrayList<>();
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", pageContent);
            response.put("totalElements", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取用户列表失败");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 辅助方法：获取角色的排序优先级
    private int getRoleOrder(Object roleObj) {
        if (roleObj == null) {
            return 2; // 默认最低优先级
        }
        
        String role = roleObj.toString();
        
        if ("SUPER_ADMIN".equals(role)) {
            return 0;  // 最高优先级
        } else if ("ADMIN".equals(role)) {
            return 1;  // 中等优先级
        } else {
            return 2;  // 最低优先级 (ALUMNI)
        }
    }

    @GetMapping("/current")
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
            data.put("name", user.getName());
            data.put("gender", user.getGender());
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

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<User> getUserByPhone(@PathVariable String phone) {
        return userService.findByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable UserRole role) {
        return ResponseEntity.ok(userService.findByRole(role));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(userService.findByStatus(status));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long id,
            @PathVariable String status) {
        userService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/role/{role}")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @PathVariable UserRole role) {
        try {
            // 不允许将用户角色设置为超级管理员
            if (role == UserRole.SUPER_ADMIN) {
                System.out.println("拒绝请求：尝试将用户 ID: " + id + " 设置为超级管理员");
                return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "不允许将用户设置为超级管理员");
                }});
            }
            
            System.out.println("正在更新用户ID: " + id + " 的角色为: " + role);
            userService.updateRole(id, role);
            System.out.println("用户角色更新成功");
            
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("success", true);
                put("message", "用户角色更新成功");
            }});
        } catch (Exception e) {
            System.out.println("更新用户角色失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", e.getMessage());
            }});
        }
    }

    @GetMapping("/check/username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.existsByUsername(username));
    }

    @GetMapping("/check/email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @GetMapping("/check/phone")
    public ResponseEntity<?> checkPhone(@RequestParam String phone) {
        return ResponseEntity.ok(userService.existsByPhone(phone));
    }
} 