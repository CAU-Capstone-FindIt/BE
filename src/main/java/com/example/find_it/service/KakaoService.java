package com.example.find_it.service;

import com.example.find_it.dao.FCMTokenDao;
import com.example.find_it.domain.User;
import com.example.find_it.dto.JwtTokenDto;
import com.example.find_it.dto.LoginRequest;
import com.example.find_it.dto.Response.KakaoTokenResponseDto;
import com.example.find_it.dto.Response.KakaoUserInfoResponseDto;
import com.example.find_it.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import io.netty.handler.codec.http.HttpHeaderValues;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final FCMTokenDao fcmTokenDao;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    public JwtTokenDto generateJwtToken(User user) {
        String token = Jwts.builder()
                .setSubject(user.getAuthId())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return new JwtTokenDto(token, jwtExpirationMs);
    }

    // 카카오에서 액세스 토큰을 가져오는 메서드
    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        if (kakaoTokenResponseDto == null) {
            throw new IllegalStateException("Failed to retrieve access token from Kakao");
        }

        log.info("[KakaoService] Access Token: {}", kakaoTokenResponseDto.getAccessToken());
        return kakaoTokenResponseDto.getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        if (userInfo == null) {
            throw new IllegalStateException("Failed to retrieve user info from Kakao");
        }

        log.info("[KakaoService] Kakao User ID: {}", userInfo.getId());
        return userInfo;
    }

    @Transactional
    public JwtTokenDto registerOrLoginWithKakao(KakaoUserInfoResponseDto userInfo, LoginRequest loginRequest) {
        String authId = "KAKAO_" + userInfo.getId();
        log.info("Attempting to find or create user with authId: {}", authId);

        User user = userRepository.findByAuthId(authId)
                .orElseGet(() -> {
                    log.info("Creating new user with authId: {}", authId);
                    return userRepository.save(
                            User.createKakaoUser(authId, userInfo.getKakaoAccount().getProfile().getNickName(),
                                    userInfo.getKakaoAccount().getProfile().getProfileImageUrl()));
                });

        if (loginRequest != null && loginRequest.getToken() != null) {
            fcmTokenDao.saveToken(authId, loginRequest.getToken());
            fcmService.sendWelcomeNotification(loginRequest.getToken());
        } else {
            log.warn("No FCM token provided. Skipping FCM notification.");
        }

        // JWT 토큰 생성 및 반환
        return generateJwtToken(user);
    }

}
