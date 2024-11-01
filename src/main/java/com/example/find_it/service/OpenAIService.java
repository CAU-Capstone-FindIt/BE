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

    public String getSimilarItems(String imageUrl) {
        String prompt = "지금부터 내가 보내주는 사진을 위의 조건에 부합하고 유의미한 데이터가 나오도록 분석해줘. 분석결과는 물건 명칭, 카테고리, 색상, 키워드 브랜드 순서로 출력해줘";

        try {
            // content를 List로 만들어 text와 image_url을 포함
            List<Map<String, Object>> contentList = new ArrayList<>();
            contentList.add(Map.of("type", "text", "text", prompt));
            contentList.add(Map.of(
                    "type", "image_url",
                    "image_url", Map.of("url", imageUrl)
            ));

            // messages 구조 생성
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", contentList);

            // 전체 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4");  // 또는 실제 사용할 모델명
            requestBody.put("messages", List.of(message));
            requestBody.put("max_tokens", 300);

            // Send request to OpenAI API
            String responseJson = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return responseJson;

        } catch (Exception e) {
            log.error("Error processing request: ", e);
            throw new RuntimeException("Failed to process image or OpenAI response", e);
        }
    }







}
