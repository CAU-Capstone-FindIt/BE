package com.example.find_it.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalMessageDto {
    private Long senderId;
    private Long receiverId;
    private String message;
    private Long itemId;
    private String itemType;
    private String itemName; // 아이템 이름
    private String itemImageUrl; // 추가: 아이템 이미지 URL
    private LocalDateTime timestamp;
}



