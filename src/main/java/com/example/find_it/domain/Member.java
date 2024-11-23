package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name", nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "points")
    private int points = 0;

    // 포인트를 더하거나 차감하는 메서드
    public void adjustPoints(int deltaPoints) {
        int newPoints = this.points + deltaPoints;
        if (newPoints < 0) {
            throw new IllegalArgumentException("포인트는 0보다 작을 수 없습니다.");
        }
        this.points = newPoints;
    }
}
