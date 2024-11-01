package com.example.find_it.repository;

import com.example.find_it.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 특정 authId로 사용자를 찾는 메서드
    Optional<User> findByAuthId(String authId);

    // 특정 authId가 존재하는지 확인하는 메서드
    boolean existsByAuthId(String authId);

    // 포인트 기준으로 내림차순 정렬된 사용자 목록 반환
    List<User> findAllByOrderByPointsDesc();
}
