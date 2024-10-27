package com.example.find_it.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Repository
public class FCMTokenDao {
    private final ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();

    // FCM 토큰 저장
    public void saveToken(String authId, String token) {
        tokenStore.put(authId, token);
    }

    // FCM 토큰 조회
    public String getToken(String authId) {
        return tokenStore.get(authId);
    }

    // FCM 토큰 삭제
    public void deleteToken(String authId) {
        tokenStore.remove(authId);
    }

    // FCM 토큰 존재 여부 확인
    public boolean hasKey(String authId) {
        return tokenStore.containsKey(authId);
    }
}
