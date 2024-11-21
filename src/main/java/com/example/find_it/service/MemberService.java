package com.example.find_it.service;

import com.example.find_it.domain.Member;
import com.example.find_it.dto.Request.KakaoLoginRequest;
import com.example.find_it.dto.Response.TokenResponse;
import com.example.find_it.exception.CustomException;
import com.example.find_it.exception.ErrorCode;
import com.example.find_it.repository.MemberRepository;
import com.example.find_it.utils.JwtProviderUtil;
import com.example.find_it.utils.KakaoOAuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtProviderUtil jwtProviderUtil;
    private final KakaoOAuthUtil kakaoOAuthUtil;
    private final UserDetailsService userDetailsService;

    public Member kakaoLogin(KakaoLoginRequest request) throws CustomException {
        String kakaoAccessToken;
        try {
            kakaoAccessToken = kakaoOAuthUtil.fetchKakaoAccessToken(request.getCode());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_FETCH_ACCESS_TOKEN_FAIL);
        }

        LinkedHashMap<String, Object> response;
        try {
            response = kakaoOAuthUtil.fetchKakaoUserData(kakaoAccessToken);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_FETCH_USER_DATA_FAIL);
        }

        String kakaoId = response.get("id").toString();

        LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) response.get("kakao_account");
        LinkedHashMap<String, Object> profile = (LinkedHashMap<String, Object>) kakaoAccount.get("profile");

        // kakaoId가 같은 Member가 존재 : 기존 멤버
        Member existedMember = memberRepository.findByKakaoId(kakaoId).orElse(null);
        if (existedMember != null) {
            return existedMember;
        }

        // kakaoId가 같은 Member가 없음 : 새로 가입
        Member newMember = Member.builder()
                .name(profile.get("nickname").toString())
                .kakaoId(kakaoId)
                .profileImage(profile.get("profile_image_url").toString())
                .build();

        memberRepository.save(newMember);
        return newMember;
    }

    public TokenResponse generateToken(Member member) throws CustomException {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(member.getKakaoId());
        } catch (UsernameNotFoundException e) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String accessToken = jwtProviderUtil.generateToken(authentication);

        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }
}
