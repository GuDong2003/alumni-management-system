package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.Announcement;
import com.example.alumnimanagementsystem.mapper.AnnouncementMapper;
import com.example.alumnimanagementsystem.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> findAll() {
        return announcementMapper.findAll();
    }

    @Override
    public Announcement findById(Long id) {
        return announcementMapper.findById(id);
    }

    @Override
    @Transactional
    public void create(Announcement announcement) {
        if ("PUBLISHED".equals(announcement.getStatus())) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        announcementMapper.insert(announcement);
    }

    @Override
    @Transactional
    public void update(Announcement announcement) {
        try {
            System.out.println("开始更新公告: " + announcement.getId());
            System.out.println("更新数据: " + announcement);
            
            Announcement existing = announcementMapper.findById(announcement.getId());
            if (existing == null) {
                System.out.println("公告不存在: " + announcement.getId());
                throw new RuntimeException("公告不存在");
            }
            
            System.out.println("当前公告状态: " + existing.getStatus());
            System.out.println("新状态: " + announcement.getStatus());

            if (!"PUBLISHED".equals(existing.getStatus()) && "PUBLISHED".equals(announcement.getStatus())) {
                System.out.println("状态从非发布变为发布，设置发布时间");
                announcement.setPublishTime(LocalDateTime.now());
            }
            
            if ("PUBLISHED".equals(existing.getStatus()) && !"PUBLISHED".equals(announcement.getStatus())) {
                System.out.println("状态从发布变为非发布，清除发布时间");
                announcement.setPublishTime(null);
            }

            System.out.println("执行数据库更新");
            announcementMapper.update(announcement);
            System.out.println("更新完成");
        } catch (Exception e) {
            System.out.println("更新失败: " + e.getMessage());
            throw new RuntimeException("更新公告失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        announcementMapper.deleteById(id);
    }
} 