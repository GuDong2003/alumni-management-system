package com.example.alumnimanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.alumnimanagementsystem", "com.example.alumnimanagementsystem.controller"})
public class AlumniManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlumniManagementSystemApplication.class, args);
    }

}