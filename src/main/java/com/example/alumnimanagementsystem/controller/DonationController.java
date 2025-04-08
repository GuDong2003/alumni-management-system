package com.example.alumnimanagementsystem.controller;

import com.example.alumnimanagementsystem.entity.Donation;
import com.example.alumnimanagementsystem.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(originPatterns = "*")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @PostMapping
    public ResponseEntity<Donation> createDonation(@RequestBody Donation donation) {
        return ResponseEntity.ok(donationService.createDonation(donation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Donation> updateDonation(@PathVariable Long id, @RequestBody Donation donation) {
        donation.setId(id);
        return ResponseEntity.ok(donationService.updateDonation(donation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        donationService.deleteDonation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donation> getDonation(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getDonationById(id));
    }

    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<Donation>> getDonationsByDonorId(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationService.getDonationsByDonorId(donorId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Donation>> getDonationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(donationService.getDonationsByStatus(status));
    }

    @GetMapping("/page")
    public ResponseEntity<List<Donation>> getDonationsPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(donationService.getDonationsPage(page, size));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCount() {
        return ResponseEntity.ok(donationService.getTotalCount());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateDonationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        donationService.updateDonationStatus(id, status);
        return ResponseEntity.ok().build();
    }
} 