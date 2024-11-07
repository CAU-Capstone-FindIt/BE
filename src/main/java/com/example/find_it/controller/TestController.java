package com.example.find_it.controller;

import com.example.find_it.service.OpenAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final OpenAIService openAIService;

    public TestController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping("/openai")
    public ResponseEntity<String> testOpenAIConnection() {
        String response = openAIService.testOpenAIConnection();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/get-similar-items/base64")
    public ResponseEntity<String> analyzeImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is missing.");
            }
            // 이미지 파일을 Base64로 인코딩
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // OpenAI API에 이미지 분석 요청
            String analysisResult = openAIService.analyzeImage(base64Image);

            return ResponseEntity.ok(analysisResult);
        } catch (Exception e) {
            log.error("Failed to analyze image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to analyze image: " + e.getMessage());
        }
    }


}
