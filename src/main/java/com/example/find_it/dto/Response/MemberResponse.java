package com.example.find_it.dto.Response;

import com.example.find_it.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long memberId;
    private String name;
    private String nickname; // nickname 추가
    private String kakaoId;
    private String profileImage;
    private int points; // points 추가

    public static MemberResponse getMemberResponse(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .nickname(member.getNickname()) // nickname 반환
                .kakaoId(member.getKakaoId())
                .profileImage(member.getProfileImage())
                .points(member.getPoints()) // points 반환
                .build();
    }
}
