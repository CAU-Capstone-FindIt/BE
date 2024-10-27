package com.example.find_it.controller;

import com.example.find_it.service.OpenAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
