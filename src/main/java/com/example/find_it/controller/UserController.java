package com.example.find_it.controller;

import com.example.find_it.domain.User;
import com.example.find_it.dto.LoginRequest;
import com.example.find_it.dto.Response.KakaoUserInfoResponseDto;
import com.example.find_it.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final KakaoService kakaoService;

    @GetMapping("/api/users/login/callback")
    public Map<String, Object> kakaoCallback(@RequestParam("code") String code) {
        // 액세스 토큰을 가져옴
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        // 액세스 토큰으로 사용자 정보 가져옴
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        // 사용자 등록 또는 로그인 처리
        User user = kakaoService.registerOrLoginWithKakao(userInfo, new LoginRequest());

        // 프론트엔드에 전달할 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("user", user);

        log.info("123");

        return response;
    }
}