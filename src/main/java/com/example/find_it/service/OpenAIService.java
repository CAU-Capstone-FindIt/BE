package com.example.find_it.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;

import java.util.*;


@Slf4j
@Service
public class OpenAIService {

    private WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final WebClient.Builder webClientBuilder;

    public OpenAIService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String testOpenAIConnection() {
        String prompt = "오늘 날씨는 어때?";

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        // Make a simple API request to OpenAI
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String analyzeImage(String base64Image) {
        try {
            // System 메시지와 사용자 메시지를 포함하는 메시지 배열 생성
            Map<String, Object> systemMessage = Map.of(
                    "role", "system",
                    "content", "You are an assistant that analyzes images to identify item name, category, color, keywords, and brand in the given order."
            );

            Map<String, Object> userMessage = Map.of(
                    "role", "user",
                    "content", "data:image/jpeg;base64," + base64Image
            );

            // 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(systemMessage, userMessage));
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.5);

            // OpenAI API로 POST 요청 전송
            return webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            log.error("Error processing image analysis request: ", e);
            throw new RuntimeException("Failed to analyze image with OpenAI", e);
        }
    }

}

