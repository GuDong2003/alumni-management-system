package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.Announcement;
import java.util.List;

public interface AnnouncementService {
    List<Announcement> findAll();
    Announcement findById(Long id);
    void create(Announcement announcement);
    void update(Announcement announcement);
    void deleteById(Long id);
} 