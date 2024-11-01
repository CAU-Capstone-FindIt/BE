package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 포인트가 변동된 사용자

    private int points;  // 변동된 포인트 수량 (양수 또는 음수)

    private LocalDateTime transactionDate;  // 거래 일시

    private String description;  // 거래 설명 (예: "보상 지급", "포인트 차감" 등)

    // Builder 패턴을 사용한 생성자
    @Builder
    public PointTransaction(User user, int points, LocalDateTime transactionDate, String description) {
        this.user = user;
        this.points = points;
        this.transactionDate = (transactionDate != null) ? transactionDate : LocalDateTime.now();
        this.description = description;
    }
}
