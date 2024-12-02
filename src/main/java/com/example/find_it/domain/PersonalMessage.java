package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime timestamp;

    private Long itemId; // LostItem 또는 FoundItem ID
    private String itemType; // "LOST" 또는 "FOUND"

    @ManyToOne
    @JoinColumn(name = "lost_item_id", insertable = false, updatable = false)
    private LostItem lostItem;

    @ManyToOne
    @JoinColumn(name = "found_item_id", insertable = false, updatable = false)
    private FoundItem foundItem;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now(); // 메시지 생성 시 기본값 설정
    }
}
