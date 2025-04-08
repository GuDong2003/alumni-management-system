package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.User;
import com.example.alumnimanagementsystem.entity.UserRole;
import com.example.alumnimanagementsystem.dto.RegisterRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    // 创建用户
    @Transactional
    User createUser(User user);
    
    // 更新用户信息
    @Transactional
    User updateUser(Long id, User user);
    
    // 更新用户信息（重载方法）
    @Transactional
    User updateUser(User user);
    
    // 删除用户
    @Transactional
    void deleteUser(Long id);
    
    // 根据ID查找用户
    Optional<User> findById(Long id);
    
    // 查找所有用户
    List<User> findAll();
    
    // 根据用户名查找用户
    Optional<User> findByUsername(String username);
    
    // 根据邮箱查找用户
    Optional<User> findByEmail(String email);
    
    // 根据手机号查找用户
    Optional<User> findByPhone(String phone);
    
    // 根据角色查找用户
    List<User> findByRole(UserRole role);
    
    // 根据状态查找用户
    List<User> findByStatus(String status);
    
    // 根据毕业年份查找用户
    List<User> findByGraduationYear(Integer graduationYear);
    
    // 根据专业查找用户
    List<User> findByMajor(String major);
    
    // 更新用户状态
    @Transactional
    void updateStatus(Long id, String status);
    
    // 更新用户密码
    @Transactional
    void updatePassword(Long id, String password);
    
    // 更新用户角色
    @Transactional
    void updateRole(Long id, UserRole role);
    
    // 更新用户最后登录时间
    @Transactional
    void updateLastLogin(Long id);
    
    // 更新用户资料
    @Transactional
    void updateProfile(Long id, User profile);
    
    // 更新用户邮箱
    @Transactional
    void updateEmail(Long id, String email);
    
    // 更新用户手机号
    @Transactional
    void updatePhone(Long id, String phone);
    
    // 更新用户学生ID
    @Transactional
    void updateStudentId(Long id, String studentId);
    
    // 更新用户专业
    @Transactional
    void updateMajor(Long id, String major);
    
    // 更新用户毕业年份
    @Transactional
    void updateGraduationYear(Long id, Integer graduationYear);
    
    // 根据角色和状态查找用户
    List<User> findByRoleAndStatus(UserRole role, String status);
    
    // 根据毕业年份和专业查找用户
    List<User> findByGraduationYearAndMajor(Integer graduationYear, String major);
    
    // 根据角色和毕业年份查找用户
    List<User> findByRoleAndGraduationYear(UserRole role, Integer graduationYear);
    
    // 根据状态和专业查找用户
    List<User> findByStatusAndMajor(String status, String major);
    
    // 根据角色和专业查找用户
    List<User> findByRoleAndMajor(UserRole role, String major);
    
    // 根据状态和毕业年份查找用户
    List<User> findByStatusAndGraduationYear(String status, Integer graduationYear);
    
    // 根据角色、状态和毕业年份查找用户
    List<User> findByRoleAndStatusAndGraduationYear(UserRole role, String status, Integer graduationYear);
    
    // 根据角色、状态和专业查找用户
    List<User> findByRoleAndStatusAndMajor(UserRole role, String status, String major);
    
    // 根据角色、毕业年份和专业查找用户
    List<User> findByRoleAndGraduationYearAndMajor(UserRole role, Integer graduationYear, String major);
    
    // 根据状态、毕业年份和专业查找用户
    List<User> findByStatusAndGraduationYearAndMajor(String status, Integer graduationYear, String major);
    
    // 根据角色、状态、毕业年份和专业查找用户
    List<User> findByRoleAndStatusAndGraduationYearAndMajor(UserRole role, String status, Integer graduationYear, String major);
    
    // 搜索用户
    List<User> searchUsers(String keyword);
    
    // 获取用户数量
    long count();
    
    // 获取用户ID
    User getUserById(Long id);
    
    // 获取所有用户
    List<User> getAllUsers();
    
    // 获取用户名
    User getUserByUsername(String username);
    
    // 获取邮箱
    User getUserByEmail(String email);
    
    // 获取手机号
    User getUserByPhone(String phone);
    
    // 获取角色用户
    List<User> getUsersByRole(UserRole role);
    
    // 获取状态用户
    List<User> getUsersByStatus(String status);
    
    // 获取毕业年份用户
    List<User> getUsersByGraduationYear(Integer graduationYear);
    
    // 获取专业用户
    List<User> getUsersByMajor(String major);
    
    // 用户注册
    @Transactional
    void register(RegisterRequest request);
    
    // 用户登录
    User login(String username, String password);
    
    // 用户登出
    void logout();
    
    // 修改密码
    @Transactional
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    // 重置密码
    @Transactional
    void resetPassword(Long userId);
    
    // 检查用户名是否存在
    boolean existsByUsername(String username);
    
    // 检查邮箱是否存在
    boolean existsByEmail(String email);
    
    // 检查手机号是否存在
    boolean existsByPhone(String phone);
} 