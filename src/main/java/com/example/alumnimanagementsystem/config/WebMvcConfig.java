package com.example.alumnimanagementsystem.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "com.example.alumnimanagementsystem.controller")
public class WebMvcConfig implements WebMvcConfigurer {

    // @Override
    // public void configurePathMatch(PathMatchConfigurer configurer) {
    //     // 添加统一的API前缀
    //     configurer.addPathPrefix("/api", cls -> 
    //         cls.getPackage().getName().startsWith("com.example.alumnimanagementsystem.controller"));
    // }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
} 