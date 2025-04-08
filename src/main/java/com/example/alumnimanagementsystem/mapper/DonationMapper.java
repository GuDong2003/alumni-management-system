package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.Donation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface DonationMapper {
    // 插入捐赠记录
    int insert(Donation donation);
    
    // 更新捐赠记录
    int update(Donation donation);
    
    // 删除捐赠记录
    int deleteById(Long id);
    
    // 根据ID查询捐赠记录
    Donation selectById(Long id);
    
    // 查询所有捐赠记录
    List<Donation> selectAll();
    
    // 根据捐赠人ID查询捐赠记录
    List<Donation> selectByDonorId(Long donorId);
    
    // 根据项目ID查询捐赠
    List<Donation> selectByProjectId(Long projectId);
    
    // 根据捐赠类型查询捐赠
    List<Donation> selectByType(String type);
    
    // 根据状态查询捐赠记录
    List<Donation> selectByStatus(String status);
    
    // 根据捐赠金额范围查询捐赠
    List<Donation> selectByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    // 根据捐赠时间范围查询捐赠
    List<Donation> selectByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime);
    
    // 搜索捐赠
    List<Donation> search(String keyword);
    
    // 分页查询捐赠记录
    List<Donation> selectPage(@Param("offset") int offset, @Param("limit") int limit);
    
    // 获取总记录数
    long count();
    
    // 计算捐赠总额
    BigDecimal calculateTotalAmount();
    
    // 更新捐赠状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 更新捐赠金额
    int updateAmount(@Param("id") Long id, @Param("amount") Double amount);
} 