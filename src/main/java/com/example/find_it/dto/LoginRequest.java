package com.example.find_it.dto;

public class LoginRequest {
    private String token;
    private String code; // 추가된 code 필드

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() { // 추가된 getter
        return code;
    }

    public void setCode(String code) { // 추가된 setter
        this.code = code;
    }
}
