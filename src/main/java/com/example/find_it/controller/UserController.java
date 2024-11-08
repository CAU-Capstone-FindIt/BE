package com.example.find_it.controller;

import com.example.find_it.dto.JwtTokenDto;
import com.example.find_it.dto.LoginRequest;
import com.example.find_it.dto.Response.KakaoUserInfoResponseDto;
import com.example.find_it.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final KakaoService kakaoService;

    @PostMapping("/login/callback")
    public ResponseEntity<JwtTokenDto> kakaoLogin(@RequestBody LoginRequest loginRequest) {
        String code = loginRequest.getCode(); // 프론트에서 받은 인가 코드
        String accessToken = kakaoService.getAccessTokenFromKakao(code); // 카카오 액세스 토큰 가져오기
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken); // 사용자 정보 가져오기
        JwtTokenDto jwtToken = kakaoService.registerOrLoginWithKakao(userInfo); // 사용자 등록 및 JWT 발급

        // JwtTokenDto를 JSON 형식으로 반환
        return ResponseEntity.ok(jwtToken);
    }
}
