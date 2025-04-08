package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.Alumni;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AlumniMapper {
    // 插入校友信息
    void insert(Alumni alumni);
    
    // 更新校友信息
    void update(Alumni alumni);
    
    // 根据ID删除校友信息
    void delete(@Param("id") Long id);
    
    // 根据ID删除校友信息
    void deleteById(@Param("id") Long id);
    
    // 根据ID查询校友信息
    Alumni selectById(Long id);
    
    // 查询所有校友信息
    List<Alumni> selectAll();
    
    // 根据用户ID查询校友信息
    Alumni selectByUserId(@Param("userId") Long userId);
    
    // 根据行业查询校友信息
    List<Alumni> selectByIndustry(String industry);
    
    // 根据位置查询校友信息
    List<Alumni> selectByLocation(String location);
    
    // 根据活跃状态查询校友信息
    List<Alumni> selectByActive(boolean active);
    
    // 更新校友活跃状态
    void updateActive(@Param("id") Long id, @Param("active") boolean active);
    
    // 更新校友最后活动时间
    void updateLastActivityTime(@Param("id") Long id, @Param("lastActivityTime") LocalDateTime lastActivityTime);
    
    // 根据关键词搜索校友信息
    List<Alumni> search(String keyword);
    
    // 统计校友总数
    long count();
    
    // 根据学号查询校友信息
    Alumni selectByStudentId(@Param("studentId") String studentId);
    
    // 根据专业查询校友信息
    List<Alumni> selectByMajor(String major);
    
    // 根据毕业年份查询校友信息
    List<Alumni> selectByGraduationYear(Integer graduationYear);
    
    // 根据工作单位查询校友信息
    List<Alumni> selectByCompany(String company);
    
    // 根据职位查询校友信息
    List<Alumni> selectByPosition(String position);
    
    // 根据城市查询校友信息
    List<Alumni> selectByCity(String city);
    
    // 根据专业和毕业年份查询校友信息
    List<Alumni> selectByMajorAndGraduationYear(@Param("major") String major, @Param("graduationYear") Integer graduationYear);
    
    // 根据工作单位和职位查询校友信息
    List<Alumni> selectByCompanyAndPosition(@Param("company") String company, @Param("position") String position);
    
    // 根据城市和职位查询校友信息
    List<Alumni> selectByCityAndPosition(@Param("city") String city, @Param("position") String position);
    
    // 更新校友工作信息
    void updateWorkInfo(@Param("id") Long id, @Param("company") String company, @Param("position") String position);
    
    // 更新校友联系方式
    void updateContactInfo(@Param("id") Long id, @Param("phone") String phone, @Param("email") String email);
    
    // 更新校友地址信息
    void updateAddressInfo(@Param("id") Long id, @Param("city") String city, @Param("address") String address);
    
    // 根据专业和城市查询校友信息
    List<Alumni> selectByMajorAndCity(@Param("major") String major, @Param("city") String city);
    
    // 根据毕业年份和城市查询校友信息
    List<Alumni> selectByGraduationYearAndCity(@Param("graduationYear") Integer graduationYear, @Param("city") String city);
    
    // 根据工作单位和城市查询校友信息
    List<Alumni> selectByCompanyAndCity(@Param("company") String company, @Param("city") String city);
    
    // 根据用户ID删除校友信息
    void deleteByUserId(@Param("userId") Long userId);
} 