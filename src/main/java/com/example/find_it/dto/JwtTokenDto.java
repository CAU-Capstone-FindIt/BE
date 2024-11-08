package com.example.find_it.dto;

public class JwtTokenDto {
    private String token;
    private long expirationTime;
    private String nickname;
    private String profileImage;

    public JwtTokenDto(String token, long expirationTime, String nickname, String profileImage) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
