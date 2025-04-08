package com.example.alumnimanagementsystem.service;

import com.example.alumnimanagementsystem.entity.Alumni;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface AlumniService {
    @Transactional
    Alumni createAlumni(Alumni alumni);
    
    @Transactional
    Alumni updateAlumni(Alumni alumni);
    
    @Transactional
    void deleteAlumni(Long id);
    
    Alumni getAlumniById(Long id);
    
    List<Alumni> getAllAlumni();
    
    Alumni getAlumniByUserId(Long userId);
    
    List<Alumni> getAlumniByIndustry(String industry);
    
    List<Alumni> getAlumniByLocation(String location);
    
    List<Alumni> getAlumniByActive(boolean active);
    
    @Transactional
    void updateActive(Long id, boolean active);
    
    @Transactional
    void updateLastActivityTime(Long id);
    
    List<Alumni> searchAlumni(String keyword);
    
    long count();
    
    List<Alumni> findByPosition(String position);
    
    List<Alumni> findByCity(String city);
    
    List<Alumni> findByMajorAndGraduationYear(String major, Integer graduationYear);
    
    List<Alumni> findByCompanyAndPosition(String company, String position);
    
    List<Alumni> findByCityAndPosition(String city, String position);
    
    @Transactional
    void updateWorkInfo(Long id, String company, String position);
    
    @Transactional
    void updateContactInfo(Long id, String phone, String email);
    
    @Transactional
    void updateAddressInfo(Long id, String city, String address);
    
    List<Alumni> findByMajorAndCity(String major, String city);
    
    List<Alumni> findByGraduationYearAndCity(Integer graduationYear, String city);
    
    List<Alumni> findByCompanyAndCity(String company, String city);
} 