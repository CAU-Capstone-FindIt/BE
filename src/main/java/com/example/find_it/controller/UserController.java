// UserController.java
package com.example.find_it.controller;

import com.example.find_it.domain.Member;
import com.example.find_it.dto.Request.KakaoLoginRequest;
import com.example.find_it.dto.Request.MemberUpdateRequest;
import com.example.find_it.dto.Response.KakaoUserInfoResponseDto;
import com.example.find_it.dto.Response.MemberResponse;
import com.example.find_it.dto.Response.TokenResponse;
import com.example.find_it.service.MemberService;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/users")
@RestController
@Tag(name = "유저 관리", description = "유저 정보 및 캐릭터 관리 API")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserController {

    private final MemberService memberService;

    @Operation(summary = "유저 정보 조회", description = "자신의 유저 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Member myMember = memberService.getMemberByPrincipal(userDetails);
        MemberResponse response = MemberResponse.getMemberResponse(myMember);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 통해 JWT를 발급합니다.")
    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TokenResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        Member member = memberService.kakaoLogin(request);
        TokenResponse response = memberService.generateToken(member);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "유저 정보 수정", description = "자신의 유저 정보를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<MemberResponse> updateUserInfo(MemberUpdateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Member myMember = memberService.getMemberByPrincipal(userDetails);
        Member updatedMember = memberService.updateMyMember(myMember, request);
        MemberResponse response = MemberResponse.getMemberResponse(updatedMember);
        return ResponseEntity.ok(response);
    }
}
