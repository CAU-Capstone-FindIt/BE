package com.example.find_it.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://findit.p-e.kr:8080",
                                "http://finditforcau.s3-website.ap-northeast-2.amazonaws.com"
                        )
                        .allowedMethods("*") // 모든 HTTP 메서드 허용
                        .allowedHeaders("*") // 모든 헤더 허용
                        .allowCredentials(true) // 자격 증명 허용
                        .maxAge(3600); // CORS 설정 캐시 시간 설정
            }
        };
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
