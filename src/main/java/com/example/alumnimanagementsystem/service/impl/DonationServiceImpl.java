package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.Donation;
import com.example.alumnimanagementsystem.mapper.DonationMapper;
import com.example.alumnimanagementsystem.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationServiceImpl implements DonationService {

    @Autowired
    private DonationMapper donationMapper;

    @Override
    @Transactional
    public Donation createDonation(Donation donation) {
        donation.setCreatedAt(LocalDateTime.now());
        donation.setUpdatedAt(LocalDateTime.now());
        donationMapper.insert(donation);
        return donation;
    }

    @Override
    @Transactional
    public Donation updateDonation(Donation donation) {
        donation.setUpdatedAt(LocalDateTime.now());
        donationMapper.update(donation);
        return donation;
    }

    @Override
    @Transactional
    public void deleteDonation(Long id) {
        donationMapper.deleteById(id);
    }

    @Override
    public Donation getDonationById(Long id) {
        return donationMapper.selectById(id);
    }

    @Override
    public List<Donation> getAllDonations() {
        return donationMapper.selectAll();
    }

    @Override
    public List<Donation> getDonationsByDonorId(Long donorId) {
        return donationMapper.selectByDonorId(donorId);
    }

    @Override
    public List<Donation> getDonationsByStatus(String status) {
        return donationMapper.selectByStatus(status);
    }

    @Override
    public List<Donation> getDonationsPage(int page, int size) {
        int offset = (page - 1) * size;
        return donationMapper.selectPage(offset, size);
    }

    @Override
    public long getTotalCount() {
        return donationMapper.count();
    }

    @Override
    @Transactional
    public void updateDonationStatus(Long id, String status) {
        donationMapper.updateStatus(id, status);
    }

    @Override
    public long count() {
        return donationMapper.count();
    }

    @Override
    public BigDecimal calculateTotalAmount() {
        return donationMapper.calculateTotalAmount();
    }
} 