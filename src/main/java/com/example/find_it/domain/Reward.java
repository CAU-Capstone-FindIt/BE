package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long id;

    private int amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private RewardStatus status; // 보상의 상태 (Pending, Paid, Expired)

    // 보상을 받는 사용자 (발견자)
    @ManyToOne
    @JoinColumn(name = "found_user_id")
    private User foundUser; // 보상을 받는 발견자
}
