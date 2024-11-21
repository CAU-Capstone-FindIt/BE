package com.example.find_it.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 매핑할 Url 경로
    private final String logUrl = "/log/**";

    // 매핑될 로컬 파일 경로
    private final String logDir = "file:/app/build/libs/logs/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // log 파일
        registry.addResourceHandler(logUrl)
                .addResourceLocations(logDir);
    }
}
