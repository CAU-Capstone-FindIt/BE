package com.example.find_it.controller;

import com.example.find_it.domain.User;
import com.example.find_it.dto.LoginRequest;
import com.example.find_it.repository.UserRepository;
import com.example.find_it.service.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<User> kakaoCallback(@RequestParam("code") String code,
                                              @RequestParam(value = "token", required = false) String fcmToken) {
        // FCM 토큰이 있는 경우에만 저장
        LoginRequest loginRequest = new LoginRequest();
        if (fcmToken != null) {
            loginRequest.setToken(fcmToken);
        }

        // 액세스 토큰 얻기 및 사용자 정보 가져오기
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        var userInfo = kakaoService.getUserInfo(accessToken);

        // 사용자 등록 또는 로그인 처리 및 FCM 토큰 저장 (있을 경우)
        User user = kakaoService.registerOrLoginWithFCM(userInfo, loginRequest);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader, @RequestParam("email") String email) {
        try {
            // Bearer 토큰에서 실제 액세스 토큰 추출
            String accessToken = authHeader.replace("Bearer ", "");

            // 카카오 로그아웃 호출
            kakaoService.logout(accessToken, email);

            return ResponseEntity.ok("Successfully logged out");
        } catch (Exception e) {
            log.error("Logout failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to logout: " + e.getMessage());
        }
    }
}
