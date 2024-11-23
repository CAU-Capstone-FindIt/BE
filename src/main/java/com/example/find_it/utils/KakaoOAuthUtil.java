package com.example.find_it.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

@Component
public class KakaoOAuthUtil {
    @Value("${kakao.oauth.client_id}")
    private String clientId;
    @Value("${kakao.oauth.redirect_url}")
    private String redirectUrl;
    private final String kakaoOauthUrl = "https://kauth.kakao.com/oauth/token";
    private final String kakaoDataUrl = "https://kapi.kakao.com/v2/user/me";

    public String fetchKakaoAccessToken(String code) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUrl);
        params.add("code", code);

        HttpEntity<?> http = new HttpEntity<>(params, headers);
        URI uri = new URI(kakaoOauthUrl);

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uri, HttpMethod.POST, http, LinkedHashMap.class);
        return "Bearer " + response.getBody().get("access_token");
    }

    public LinkedHashMap<String, Object> fetchKakaoUserData(String kakaoAccessToken) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", kakaoAccessToken);
        HttpEntity<?> http = new HttpEntity<>(headers);
        URI uri = new URI(kakaoDataUrl);

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uri, HttpMethod.GET, http, LinkedHashMap.class);
        return (LinkedHashMap<String, Object>) response.getBody();
    }
}
