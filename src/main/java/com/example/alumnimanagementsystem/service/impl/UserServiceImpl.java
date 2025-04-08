package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.dto.RegisterRequest;
import com.example.alumnimanagementsystem.entity.Alumni;
import com.example.alumnimanagementsystem.entity.User;
import com.example.alumnimanagementsystem.entity.UserRole;
import com.example.alumnimanagementsystem.mapper.AlumniMapper;
import com.example.alumnimanagementsystem.mapper.UserMapper;
import com.example.alumnimanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AlumniMapper alumniMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        try {
            User existingUser = userMapper.selectById(id);
            if (existingUser == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 设置ID
            user.setId(id);
            
            // 如果前端没有传递这些值，才使用原来的值，否则使用前端传递的值
            if (user.getRole() == null) {
                user.setRole(existingUser.getRole());
            }
            
            if (user.getStatus() == null || user.getStatus().isEmpty()) {
                user.setStatus(existingUser.getStatus());
            }
            
            if (user.getStudentId() == null || user.getStudentId().isEmpty()) {
                user.setStudentId(existingUser.getStudentId());
            }
            
            if (user.getMajor() == null || user.getMajor().isEmpty()) {
                user.setMajor(existingUser.getMajor());
            }
            
            if (user.getGraduationYear() == null) {
                user.setGraduationYear(existingUser.getGraduationYear());
            }
            
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                user.setPassword(existingUser.getPassword());
            }
            user.setUpdatedAt(LocalDateTime.now());
            
            // 更新用户基本信息
            userMapper.update(user);

            // 更新校友信息
            Alumni alumni = alumniMapper.selectByUserId(id);
            if (alumni != null) {
                // 更新现有校友信息
                alumni.setUpdatedAt(LocalDateTime.now());
                alumniMapper.update(alumni);
            }

            return user;
        } catch (Exception e) {
            throw new RuntimeException("更新用户信息失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        try {
            // 先检查用户是否存在
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 如果是校友用户，先删除校友信息
            if (user.getRole() == UserRole.ALUMNI) {
                Alumni alumni = alumniMapper.selectByUserId(id);
                if (alumni != null) {
                    alumniMapper.deleteByUserId(id);
                }
            }
            
            // 最后删除用户信息
            userMapper.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("删除用户失败: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    @Override
    public List<User> findAll() {
        List<User> users = userMapper.selectAll();
        // 获取每个用户的校友信息，但不再设置已被移除的字段
        return users;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.selectByUsername(username));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.selectByEmail(email));
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return Optional.ofNullable(userMapper.selectByPhone(phone));
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return userMapper.selectByRole(role);
    }

    @Override
    public List<User> findByStatus(String status) {
        return userMapper.selectByStatus(status);
    }

    @Override
    public List<User> findByGraduationYear(Integer graduationYear) {
        return userMapper.selectByGraduationYear(graduationYear);
    }

    @Override
    public List<User> findByMajor(String major) {
        return userMapper.selectByMajor(major);
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void updateRole(Long id, UserRole role) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 阻止将用户角色设置为超级管理员
        if (role == UserRole.SUPER_ADMIN) {
            System.out.println("警告：尝试将用户 ID: " + id + " 设置为超级管理员，操作被拒绝");
            throw new RuntimeException("不允许将用户设置为超级管理员");
        }
        
        // 如果用户已经是超级管理员，不允许修改其角色
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            System.out.println("警告：尝试修改超级管理员 ID: " + id + " 的角色，操作被拒绝");
            throw new RuntimeException("不允许修改超级管理员的角色");
        }
        
        System.out.println("更新用户角色 - 用户ID: " + id + ", 当前角色: " + user.getRole() + ", 新角色: " + role);
        userMapper.updateRole(id, role);
        System.out.println("用户角色已更新 - 用户ID: " + id + ", 角色: " + role);
    }

    @Override
    @Transactional
    public void updateLastLogin(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateLastLoginTime(id, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateProfile(Long id, User profile) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        profile.setId(id);
        profile.setUpdatedAt(LocalDateTime.now());
        userMapper.update(profile);
    }

    @Override
    @Transactional
    public void updateEmail(Long id, String email) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateEmail(id, email);
    }

    @Override
    @Transactional
    public void updatePhone(Long id, String phone) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updatePhone(id, phone);
    }

    @Override
    @Transactional
    public void updateStudentId(Long id, String studentId) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateStudentId(id, studentId);
    }

    @Override
    @Transactional
    public void updateMajor(Long id, String major) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateMajor(id, major);
    }

    @Override
    @Transactional
    public void updateGraduationYear(Long id, Integer graduationYear) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.updateGraduationYear(id, graduationYear);
    }

    @Override
    public List<User> findByRoleAndStatus(UserRole role, String status) {
        return userMapper.selectByRoleAndStatus(role, status);
    }

    @Override
    public List<User> findByGraduationYearAndMajor(Integer graduationYear, String major) {
        return userMapper.selectByGraduationYearAndMajor(graduationYear, major);
    }

    @Override
    public List<User> findByRoleAndGraduationYear(UserRole role, Integer graduationYear) {
        return userMapper.selectByRoleAndGraduationYear(role, graduationYear);
    }

    @Override
    public List<User> findByStatusAndMajor(String status, String major) {
        return userMapper.selectByStatusAndMajor(status, major);
    }

    @Override
    public List<User> findByRoleAndMajor(UserRole role, String major) {
        return userMapper.selectByRoleAndMajor(role, major);
    }

    @Override
    public List<User> findByStatusAndGraduationYear(String status, Integer graduationYear) {
        return userMapper.selectByStatusAndGraduationYear(status, graduationYear);
    }

    @Override
    public List<User> findByRoleAndStatusAndGraduationYear(UserRole role, String status, Integer graduationYear) {
        return userMapper.selectByRoleAndStatusAndGraduationYear(role, status, graduationYear);
    }

    @Override
    public List<User> findByRoleAndStatusAndMajor(UserRole role, String status, String major) {
        return userMapper.selectByRoleAndStatusAndMajor(role, status, major);
    }

    @Override
    public List<User> findByRoleAndGraduationYearAndMajor(UserRole role, Integer graduationYear, String major) {
        return userMapper.selectByRoleAndGraduationYearAndMajor(role, graduationYear, major);
    }

    @Override
    public List<User> findByStatusAndGraduationYearAndMajor(String status, Integer graduationYear, String major) {
        return userMapper.selectByStatusAndGraduationYearAndMajor(status, graduationYear, major);
    }

    @Override
    public List<User> findByRoleAndStatusAndGraduationYearAndMajor(UserRole role, String status, Integer graduationYear, String major) {
        return userMapper.selectByRoleAndStatusAndGraduationYearAndMajor(role, status, graduationYear, major);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userMapper.search(keyword);
    }

    @Override
    public long count() {
        return userMapper.count();
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        // 不再设置已被移除的字段
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public List<User> getUsersByRole(UserRole role) {
        return userMapper.selectByRole(role);
    }

    @Override
    public List<User> getUsersByStatus(String status) {
        return userMapper.selectByStatus(status);
    }

    @Override
    public List<User> getUsersByGraduationYear(Integer graduationYear) {
        return userMapper.selectByGraduationYear(graduationYear);
    }

    @Override
    public List<User> getUsersByMajor(String major) {
        return userMapper.selectByMajor(major);
    }

    @Override
    public User updateUser(User user) {
        return updateUser(user.getId(), user);
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (request.getEmail() != null && userMapper.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 检查手机号是否已存在
        if (request.getPhone() != null && userMapper.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 检查学号是否已存在 - 使用search方法替代
        List<User> existingUsers = userMapper.search(request.getStudentId());
        for (User existingUser : existingUsers) {
            if (request.getStudentId().equals(existingUser.getStudentId())) {
                throw new RuntimeException("学号已存在");
            }
        }

        // 创建用户记录
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStudentId(request.getStudentId());
        user.setMajor(request.getMajor());
        user.setGraduationYear(request.getGraduationYear());
        user.setGender(request.getGender());
        user.setRole(UserRole.ALUMNI);
        user.setStatus("ACTIVE");  // 设置用户状态为活跃
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.insert(user);

        // 创建校友记录
        Alumni alumni = new Alumni();
        alumni.setUserId(user.getId());
        
        // 只设置 Alumni 表中存在的字段，从RegisterRequest对象获取值
        if (request.getCurrentWorkplace() != null) {
            alumni.setCurrentCompany(request.getCurrentWorkplace());
        }
        if (request.getCurrentPosition() != null) {
            alumni.setCurrentPosition(request.getCurrentPosition());
        }
        if (request.getBirthDate() != null) {
            alumni.setBirthDate(request.getBirthDate());
        }
        alumni.setActive(true);
        alumni.setCreatedAt(LocalDateTime.now());
        alumni.setUpdatedAt(LocalDateTime.now());
        
        alumniMapper.insert(alumni);
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("账号已被禁用");
        }
        updateLastLogin(user.getId());
        return user;
    }

    @Override
    @Transactional
    public void logout() {
        // 可以在这里添加登出相关的逻辑，比如清除token等
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        updatePassword(userId, newPassword);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 这里可以生成随机密码或发送重置密码邮件
        String defaultPassword = "123456";
        updatePassword(userId, defaultPassword);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userMapper.existsByPhone(phone);
    }
} 