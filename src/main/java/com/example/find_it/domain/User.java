package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String authId;  // 카카오 ID를 Auth ID로 사용

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "points", nullable = false)
    private int points = 0;

    // Builder 패턴을 사용한 생성자
    @Builder
    public User(String authId, String name, String profileImage, int points) {
        this.authId = authId;
        this.name = name;
        this.profileImage = profileImage;
        this.points = points;
    }

    public static User createKakaoUser(String authId, String name, String profileImage) {
        return User.builder()
                .authId(authId)
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
