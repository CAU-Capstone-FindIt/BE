package com.example.find_it.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("분실물 찾기 서비스 API")
                        .description("분실물과 습득물을 등록하고 검색하는 API")
                        .version("1.0.0"));
    }
}