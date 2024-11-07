//package com.example.find_it.service;
//
//import com.example.find_it.domain.User;
//import com.example.find_it.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    /**
//     * authId를 사용해 사용자 정보를 불러오는 메서드
//     *
//     * @param authId 카카오 인증 ID
//     * @return UserDetails - Spring Security 인증을 위한 객체
//     * @throws UsernameNotFoundException authId에 해당하는 사용자가 없을 경우 예외 발생
//     */
//    @Override
//    public UserDetails loadUserByUsername(String authId) throws UsernameNotFoundException {
//        User user = userRepository.findByAuthId(authId)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with authId: " + authId));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getAuthId(), "", Collections.emptyList()
//        );
//    }
//}
