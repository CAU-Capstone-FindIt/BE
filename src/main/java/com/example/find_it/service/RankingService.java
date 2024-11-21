package com.example.find_it.service;

import com.example.find_it.domain.Member;
import com.example.find_it.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;

    /**
     * 모든 사용자를 포인트 순으로 정렬하여 랭킹을 계산하고 반환합니다.
     * @return 포인트 순으로 정렬된 사용자 리스트
     */
    public List<Member> getUsersRankedByPoints() {
        return userRepository.findAllByOrderByPointsDesc();
    }

    /**
     * 특정 사용자의 현재 랭킹을 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자 랭킹 (1위부터 시작)
     */
    public int getUserRanking(Long userId) {
        List<Member> rankedUsers = getUsersRankedByPoints();
        for (int i = 0; i < rankedUsers.size(); i++) {
            if (rankedUsers.get(i).getId().equals(userId)) {
                return i + 1;  // 랭킹은 1부터 시작
            }
        }
        return -1; // 사용자 ID가 존재하지 않는 경우
    }
}
