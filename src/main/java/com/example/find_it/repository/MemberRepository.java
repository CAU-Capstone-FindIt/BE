package com.example.find_it.repository;

import com.example.find_it.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 포인트 기준으로 내림차순 정렬된 사용자 목록 반환
    List<Member> findAllByOrderByPointsDesc();

    boolean existsByKakaoId(String kakaoId);

    Optional<Member> findByKakaoId(String kakaoId);
}
