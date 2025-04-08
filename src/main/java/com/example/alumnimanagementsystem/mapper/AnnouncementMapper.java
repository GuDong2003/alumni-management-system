package com.example.alumnimanagementsystem.mapper;

import com.example.alumnimanagementsystem.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    List<Announcement> findAll();
    
    Announcement findById(Long id);
    
    void insert(Announcement announcement);
    
    void update(Announcement announcement);
    
    void deleteById(Long id);
    
    List<Announcement> findByStatus(String status);
} 