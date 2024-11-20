package com.example.find_it.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor // Required for deserialization (e.g., Jackson)
public class PersonalMessage {
    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime timestamp;

    @Builder
    public PersonalMessage(Long senderId, Long receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = LocalDateTime.now(); // Automatically sets the timestamp
    }
}
