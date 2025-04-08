package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.Alumni;
import com.example.alumnimanagementsystem.service.AlumniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {

    @Autowired
    private AlumniService alumniService;

    // 创建校友信息
    @PostMapping
    public ResponseEntity<?> createAlumni(@RequestBody Alumni alumni) {
        return ResponseEntity.ok(alumniService.createAlumni(alumni));
    }

    // 更新校友信息
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAlumni(@PathVariable Long id, @RequestBody Alumni alumni) {
        alumni.setId(id);
        return ResponseEntity.ok(alumniService.updateAlumni(alumni));
    }

    // 删除校友信息
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlumni(@PathVariable Long id) {
        alumniService.deleteAlumni(id);
        return ResponseEntity.ok().build();
    }

    // 获取校友信息
    @GetMapping("/{id}")
    public ResponseEntity<?> getAlumniById(@PathVariable Long id) {
        return ResponseEntity.ok(alumniService.getAlumniById(id));
    }

    // 获取所有校友信息
    @GetMapping
    public ResponseEntity<?> getAllAlumni() {
        return ResponseEntity.ok(alumniService.getAllAlumni());
    }

    // 根据用户ID获取校友信息
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAlumniByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(alumniService.getAlumniByUserId(userId));
    }

    // 根据行业获取校友信息
    @GetMapping("/industry/{industry}")
    public ResponseEntity<?> getAlumniByIndustry(@PathVariable String industry) {
        return ResponseEntity.ok(alumniService.getAlumniByIndustry(industry));
    }

    // 根据位置获取校友信息
    @GetMapping("/location/{location}")
    public ResponseEntity<?> getAlumniByLocation(@PathVariable String location) {
        return ResponseEntity.ok(alumniService.getAlumniByLocation(location));
    }

    // 根据活跃状态获取校友信息
    @GetMapping("/active/{active}")
    public ResponseEntity<?> getAlumniByActive(@PathVariable boolean active) {
        return ResponseEntity.ok(alumniService.getAlumniByActive(active));
    }

    // 更新校友活跃状态
    @PutMapping("/{id}/active")
    public ResponseEntity<?> updateActive(@PathVariable Long id, @RequestParam boolean active) {
        alumniService.updateActive(id, active);
        return ResponseEntity.ok().build();
    }

    // 更新校友活跃状态
    @PutMapping("/{id}/activity")
    public ResponseEntity<?> updateLastActivityTime(@PathVariable Long id) {
        alumniService.updateLastActivityTime(id);
        return ResponseEntity.ok().build();
    }

    // 搜索校友信息
    @GetMapping("/search")
    public ResponseEntity<?> searchAlumni(@RequestParam String keyword) {
        return ResponseEntity.ok(alumniService.searchAlumni(keyword));
    }

    // 获取校友数量
    @GetMapping("/count")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(alumniService.count());
    }

    // 根据职位获取校友信息
    @GetMapping("/position/{position}")
    public ResponseEntity<List<Alumni>> getAlumniByPosition(@PathVariable String position) {
        return ResponseEntity.ok(alumniService.findByPosition(position));
    }

    // 根据城市获取校友信息
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Alumni>> getAlumniByCity(@PathVariable String city) {
        return ResponseEntity.ok(alumniService.findByCity(city));
    }

    // 根据专业和毕业年份获取校友信息
    @GetMapping("/major/{major}/year/{graduationYear}")
    public ResponseEntity<List<Alumni>> getAlumniByMajorAndGraduationYear(
            @PathVariable String major,
            @PathVariable Integer graduationYear) {
        return ResponseEntity.ok(alumniService.findByMajorAndGraduationYear(major, graduationYear));
    }

    // 根据工作单位和职位获取校友信息
    @GetMapping("/company/{company}/position/{position}")
    public ResponseEntity<List<Alumni>> getAlumniByCompanyAndPosition(
            @PathVariable String company,
            @PathVariable String position) {
        return ResponseEntity.ok(alumniService.findByCompanyAndPosition(company, position));
    }

    // 根据城市和职位获取校友信息
    @GetMapping("/city/{city}/position/{position}")
    public ResponseEntity<List<Alumni>> getAlumniByCityAndPosition(
            @PathVariable String city,
            @PathVariable String position) {
        return ResponseEntity.ok(alumniService.findByCityAndPosition(city, position));
    }

    // 更新校友工作信息
    @PutMapping("/{id}/work")
    public ResponseEntity<Void> updateWorkInfo(
            @PathVariable Long id,
            @RequestParam String company,
            @RequestParam String position) {
        alumniService.updateWorkInfo(id, company, position);
        return ResponseEntity.ok().build();
    }

    // 更新校友联系方式
    @PutMapping("/{id}/contact")
    public ResponseEntity<Void> updateContactInfo(
            @PathVariable Long id,
            @RequestParam String phone,
            @RequestParam String email) {
        alumniService.updateContactInfo(id, phone, email);
        return ResponseEntity.ok().build();
    }

    // 更新校友地址信息
    @PutMapping("/{id}/address")
    public ResponseEntity<Void> updateAddressInfo(
            @PathVariable Long id,
            @RequestParam String city,
            @RequestParam String address) {
        alumniService.updateAddressInfo(id, city, address);
        return ResponseEntity.ok().build();
    }

    // 根据专业和城市获取校友信息
    @GetMapping("/major/{major}/city/{city}")
    public ResponseEntity<List<Alumni>> getAlumniByMajorAndCity(
            @PathVariable String major,
            @PathVariable String city) {
        return ResponseEntity.ok(alumniService.findByMajorAndCity(major, city));
    }

    // 根据毕业年份和城市获取校友信息
    @GetMapping("/year/{graduationYear}/city/{city}")
    public ResponseEntity<List<Alumni>> getAlumniByGraduationYearAndCity(
            @PathVariable Integer graduationYear,
            @PathVariable String city) {
        return ResponseEntity.ok(alumniService.findByGraduationYearAndCity(graduationYear, city));
    }

    // 根据工作单位和城市获取校友信息
    @GetMapping("/company/{company}/city/{city}")
    public ResponseEntity<List<Alumni>> getAlumniByCompanyAndCity(
            @PathVariable String company,
            @PathVariable String city) {
        return ResponseEntity.ok(alumniService.findByCompanyAndCity(company, city));
    }
} 