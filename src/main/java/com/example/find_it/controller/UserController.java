package com.example.find_it.controller;

import com.example.find_it.domain.User;
import com.example.find_it.repository.UserRepository;
import com.example.find_it.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final KakaoService kakaoService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/login")
    public RedirectView kakaoLogin() {
        String kakaoAuthUrl = String.format("https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s",
                clientId, redirectUri);
        return new RedirectView(kakaoAuthUrl);
    }

    @GetMapping("/login/callback")
    public ResponseEntity<User> kakaoCallback(@RequestParam("code") String code) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        var userInfo = kakaoService.getUserInfo(accessToken);
        User user = kakaoService.registerOrLogin(userInfo);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        // 여기서 로그아웃 처리 로직을 구현할 수 있습니다
        // 예: 세션 제거, 토큰 무효화 등
        return ResponseEntity.ok("Successfully logged out");
    }
}
