package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "points", nullable = false)
    private int points = 0;

    // Builder 패턴을 사용한 생성자
    @Builder
    public Member(String kakaoId, String name, String profileImage, int points) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.profileImage = profileImage;
        this.points = points;
    }

    public static Member createKakaoUser(String kakaoId, String name, String profileImage) {
        return Member.builder()
                .kakaoId(kakaoId)
                .name(name)
                .profileImage(profileImage)
                .points(0)   // 초기 포인트 설정
                .build();
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
