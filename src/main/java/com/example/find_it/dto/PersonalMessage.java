package com.example.find_it.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PersonalMessage {
    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime timestamp;
    private Long itemId; // LostItem 또는 FoundItem ID
    private String itemType; // "LOST" 또는 "FOUND"

    @Builder
    public PersonalMessage(Long senderId, Long receiverId, String message, LocalDateTime timestamp, Long itemId, String itemType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now(); // timestamp 기본값 설정
        this.itemId = itemId;
        this.itemType = itemType;
    }

    public PersonalMessage(Long senderId, Long receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }
}

