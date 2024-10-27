package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "member")
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String authId;  // kakaoId를 authId로 사용

    private String profileImage;
    private int points;
    private int ranking;

    // 카카오 회원가입을 위한 정적 팩토리 메소드
    public static User createKakaoUser(String authId, String name, String profileImage) {
        User user = new User();
        user.setAuthId(authId);
        user.setName(name);
        user.setProfileImage(profileImage);
        user.setPoints(0); // 초기 포인트
        user.setRanking(1);   // 초기 랭크
        return user;
    }
}