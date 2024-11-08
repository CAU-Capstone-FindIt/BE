package com.example.find_it.controller;

import com.example.find_it.dto.JwtTokenDto;
import com.example.find_it.dto.LoginRequest;
import com.example.find_it.dto.Response.KakaoUserInfoResponseDto;
import com.example.find_it.service.KakaoService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final KakaoService kakaoService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // jwtSecret을 이용해 JWT 서명 키를 생성하는 메서드
    private Key getSigningKey() {
        log.info("Using jwtSecret for signing key: {}", jwtSecret); // jwtSecret 로그 확인
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/login/callback")
    public ResponseEntity<JwtTokenDto> kakaoLogin(@RequestBody LoginRequest loginRequest) {
        String code = loginRequest.getCode(); // 프론트에서 받은 인가 코드
        String accessToken = kakaoService.getAccessTokenFromKakao(code); // 카카오 액세스 토큰 가져오기
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken); // 사용자 정보 가져오기
        JwtTokenDto jwtToken = kakaoService.registerOrLoginWithKakao(userInfo); // 사용자 등록 및 JWT 발급

        // JwtTokenDto를 JSON 형식으로 반환
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        log.info("Received Authorization header: {}", authHeader); // 토큰 로그 확인
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 토큰이 필요합니다.");
        }

        String token = authHeader.substring(7);
        try {
            log.info("Attempting to parse token: {}", token); // 파싱할 JWT 토큰 로그 확인
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("Parsed claims: {}", claims); // 파싱된 claims 로그 확인
            return ResponseEntity.ok("로그아웃 성공");
        } catch (Exception e) {
            log.error("JWT 검증 오류:", e); // 에러 로그 추가
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 JWT 토큰입니다.");
        }
    }
}
