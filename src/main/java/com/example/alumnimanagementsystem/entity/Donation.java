package com.example.alumnimanagementsystem.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Donation {
    private Long id;
    private Long donorId;
    private BigDecimal amount;
    private String donationType;
    private LocalDateTime donationDate;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 