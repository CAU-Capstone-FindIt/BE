package com.example.find_it.service;

import com.example.find_it.domain.Member;
import com.example.find_it.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {
        Member member = memberRepository.findByKakaoId(kakaoId).orElseThrow(() -> new UsernameNotFoundException(kakaoId));
        return new User(member.getKakaoId(), "N/A", new ArrayList<>());
    }
}
