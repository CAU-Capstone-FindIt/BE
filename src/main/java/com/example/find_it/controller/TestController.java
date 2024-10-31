package com.example.find_it.controller;

import com.example.find_it.service.OpenAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> getSimilarItemsFromUrl(@RequestParam("imageUrl") String imageUrl) {

        try {
            log.info("Received image URL: {}", imageUrl);

            // Pass only the image URL to the OpenAIService (prompt is handled internally)
            String response = openAIService.getSimilarItems(imageUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process image URL or OpenAI response.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process image URL or OpenAI response: " + e.getMessage());
        }
    }


}
