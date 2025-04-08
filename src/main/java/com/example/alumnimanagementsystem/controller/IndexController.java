package com.example.alumnimanagementsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "Welcome to Alumni Management System API";
    }

    @GetMapping("/api/test")
    public String apiTest() {
        return "API Test Successful";
    }
} 