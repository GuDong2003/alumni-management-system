package com.example.alumnimanagementsystem.service.impl;

import com.example.alumnimanagementsystem.entity.Alumni;
import com.example.alumnimanagementsystem.mapper.AlumniMapper;
import com.example.alumnimanagementsystem.service.AlumniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlumniServiceImpl implements AlumniService {

    @Autowired
    private AlumniMapper alumniMapper;

    @Override
    @Transactional
    public Alumni createAlumni(Alumni alumni) {
        alumni.setActive(true);
        alumni.setLastActivityTime(LocalDateTime.now());
        alumniMapper.insert(alumni);
        return alumni;
    }

    @Override
    @Transactional
    public Alumni updateAlumni(Alumni alumni) {
        Alumni existingAlumni = alumniMapper.selectById(alumni.getId());
        if (existingAlumni == null) {
            throw new RuntimeException("Alumni not found with id: " + alumni.getId());
        }
        alumniMapper.update(alumni);
        return alumni;
    }

    @Override
    @Transactional
    public void deleteAlumni(Long id) {
        alumniMapper.deleteById(id);
    }

    @Override
    public Alumni getAlumniById(Long id) {
        return alumniMapper.selectById(id);
    }

    @Override
    public List<Alumni> getAllAlumni() {
        return alumniMapper.selectAll();
    }

    @Override
    public Alumni getAlumniByUserId(Long userId) {
        return alumniMapper.selectByUserId(userId);
    }

    @Override
    public List<Alumni> getAlumniByIndustry(String industry) {
        return alumniMapper.selectByIndustry(industry);
    }

    @Override
    public List<Alumni> getAlumniByLocation(String location) {
        return alumniMapper.selectByLocation(location);
    }

    @Override
    public List<Alumni> getAlumniByActive(boolean active) {
        return alumniMapper.selectByActive(active);
    }

    @Override
    @Transactional
    public void updateActive(Long id, boolean active) {
        alumniMapper.updateActive(id, active);
    }

    @Override
    @Transactional
    public void updateLastActivityTime(Long id) {
        alumniMapper.updateLastActivityTime(id, LocalDateTime.now());
    }

    @Override
    public List<Alumni> searchAlumni(String keyword) {
        return alumniMapper.search(keyword);
    }

    @Override
    public long count() {
        return alumniMapper.count();
    }

    @Override
    public List<Alumni> findByPosition(String position) {
        return alumniMapper.selectByPosition(position);
    }

    @Override
    public List<Alumni> findByCity(String city) {
        return alumniMapper.selectByCity(city);
    }

    @Override
    public List<Alumni> findByMajorAndGraduationYear(String major, Integer graduationYear) {
        return alumniMapper.selectByMajorAndGraduationYear(major, graduationYear);
    }

    @Override
    public List<Alumni> findByCompanyAndPosition(String company, String position) {
        return alumniMapper.selectByCompanyAndPosition(company, position);
    }

    @Override
    public List<Alumni> findByCityAndPosition(String city, String position) {
        return alumniMapper.selectByCityAndPosition(city, position);
    }

    @Override
    @Transactional
    public void updateWorkInfo(Long id, String company, String position) {
        alumniMapper.updateWorkInfo(id, company, position);
    }

    @Override
    @Transactional
    public void updateContactInfo(Long id, String phone, String email) {
        alumniMapper.updateContactInfo(id, phone, email);
    }

    @Override
    @Transactional
    public void updateAddressInfo(Long id, String city, String address) {
        alumniMapper.updateAddressInfo(id, city, address);
    }

    @Override
    public List<Alumni> findByMajorAndCity(String major, String city) {
        return alumniMapper.selectByMajorAndCity(major, city);
    }

    @Override
    public List<Alumni> findByGraduationYearAndCity(Integer graduationYear, String city) {
        return alumniMapper.selectByGraduationYearAndCity(graduationYear, city);
    }

    @Override
    public List<Alumni> findByCompanyAndCity(String company, String city) {
        return alumniMapper.selectByCompanyAndCity(company, city);
    }
} 