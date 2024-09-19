package com.example.find_it.repository;

import com.example.find_it.domain.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    // 필요한 경우 추가 메서드를 정의할 수 있습니다.
    // 예: 상태별 보상 찾기
    List<Reward> findByStatus(String status);
}

