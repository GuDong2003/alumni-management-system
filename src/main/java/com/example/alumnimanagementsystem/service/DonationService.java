package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.Donation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface DonationService {
    
    /**
     * 创建捐赠记录
     */
    @Transactional
    Donation createDonation(Donation donation);
    
    /**
     * 更新捐赠记录
     */
    @Transactional
    Donation updateDonation(Donation donation);
    
    /**
     * 删除捐赠记录
     */
    @Transactional
    void deleteDonation(Long id);
    
    /**
     * 根据ID查询捐赠记录
     */
    Donation getDonationById(Long id);
    
    /**
     * 查询所有捐赠记录
     */
    List<Donation> getAllDonations();
    
    /**
     * 根据捐赠者ID查询捐赠记录
     */
    List<Donation> getDonationsByDonorId(Long donorId);
    
    /**
     * 根据状态查询捐赠记录
     */
    List<Donation> getDonationsByStatus(String status);
    
    /**
     * 分页查询捐赠记录
     */
    List<Donation> getDonationsPage(int page, int size);
    
    /**
     * 获取总记录数
     */
    long getTotalCount();
    
    /**
     * 更新捐赠状态
     */
    @Transactional
    void updateDonationStatus(Long id, String status);

    /**
     * 获取捐赠总数
     */
    long count();

    /**
     * 计算捐赠总额
     */
    BigDecimal calculateTotalAmount();
} 