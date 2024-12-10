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

    @Column(name = "member_nickname")
    private String nickname;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "points")
    private int points;

    // Builder 수정: nickname 초기값 설정, 포인트 기본값 설정
    @Builder
    public Member(String name, String nickname, String kakaoId, String profileImage) {
        this.name = name;
        this.nickname = nickname != null ? nickname : name; // nickname이 null이면 name으로 설정
        this.kakaoId = kakaoId;
        this.profileImage = profileImage;
        this.points = 1000; // 기본 포인트 1000 설정
    }

    // 포인트를 더하거나 차감하는 메서드
    public void adjustPoints(int deltaPoints) {
        int newPoints = this.points + deltaPoints;
        if (newPoints < 0) {
            throw new IllegalArgumentException("포인트는 0보다 작을 수 없습니다.");
        }
        this.points = newPoints;
    }
}
