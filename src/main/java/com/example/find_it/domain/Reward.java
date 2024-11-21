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

    private int amount;          // 보상 포인트 수량
    private String currency;      // 보상 포인트의 통화 단위

    @Enumerated(EnumType.STRING)
    private RewardStatus status;  // 보상의 상태 (Pending, Paid, Expired 등)

    // 보상을 설정한 사용자 (분실자)
    @ManyToOne
    @JoinColumn(name = "lost_user_id")
    private Member lostUser;        // 분실자 정보 (포인트를 차감할 대상)

    // 보상을 받는 사용자 (습득자)
    @ManyToOne
    @JoinColumn(name = "found_user_id")
    private Member foundUser;       // 습득자 정보 (포인트를 지급받을 대상)

    // 기본 생성자 추가 (JPA를 위한 기본 생성자)
    public Reward() {
    }
}
