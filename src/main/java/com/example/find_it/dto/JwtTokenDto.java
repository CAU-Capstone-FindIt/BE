package com.example.find_it.dto;

public class JwtTokenDto {
    private String token;
    private String tokenType = "Bearer"; // 기본 값
    private long expiration; // 만료 시간(ms)

    public JwtTokenDto(String token, long expiration) {
        this.token = token;
        this.expiration = expiration;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiration() {
        return expiration;
    }
}
