package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 포인트가 변동된 사용자

    private int points;  // 변동된 포인트 수량 (양수 또는 음수)

    private LocalDateTime transactionDate = LocalDateTime.now();  // 거래 일시

    private String description;  // 거래 설명 (예: "보상 지급", "포인트 차감" 등)
}
