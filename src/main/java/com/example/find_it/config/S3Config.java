package com.example.find_it.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    private final String accessKey = System.getenv("AWS_ACCESS_KEY");
    private final String secretKey = System.getenv("AWS_SECRET_KEY");

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of("ap-northeast-2")) // 한국 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }
}

