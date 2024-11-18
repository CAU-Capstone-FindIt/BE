package com.example.find_it.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class FCMInitializer {

    @Value("${fcm.certification}")
    private String googleApplicationCredentials;

    @PostConstruct
    public void initialize() {
        log.info("Initializing Firebase with certification: {}", googleApplicationCredentials);

        File file = new File(googleApplicationCredentials);
        if (!file.exists()) {
            log.error("Firebase configuration file not found at {}", googleApplicationCredentials);
            throw new RuntimeException("Firebase configuration file not found");
        }

        try (InputStream is = new FileInputStream(file)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialization complete");
            }
        } catch (IOException e) {
            log.error("Error initializing FirebaseApp: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
