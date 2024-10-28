package com.example.find_it.service;

import com.example.find_it.domain.*;
import com.example.find_it.dto.RewardDTO;
import com.example.find_it.repository.PointTransactionRepository;
import com.example.find_it.repository.RewardRepository;
import com.example.find_it.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;

    // 보상 설정
    public void setReward(RewardDTO rewardDTO, Long lostUserId) {
        User lostUser = userRepository.findById(lostUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 분실자가 보상 포인트를 설정할 수 있도록 만듭니다.
        if (lostUser.getPoints() < rewardDTO.getAmount()) {
            throw new IllegalArgumentException("Insufficient points for setting the reward.");
        }

        // 보상 정보 생성 및 설정
        Reward reward = new Reward();
        reward.setAmount(rewardDTO.getAmount());
        reward.setCurrency("Points");
        reward.setStatus(RewardStatus.PENDING); // 보류 중
        reward.setLostUser(lostUser); // 보상을 설정한 사용자 (분실자)

        // 분실자 포인트 차감
        lostUser.setPoints(lostUser.getPoints() - rewardDTO.getAmount());
        rewardRepository.save(reward);
    }

    // 보상 지급
    @Transactional
    public void payReward(Long rewardId, Long foundUserId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("Reward not found"));

        // 보상 지급 상태 확인
        if (!reward.getStatus().equals(RewardStatus.PENDING)) {
            throw new IllegalStateException("Reward is not in a payable state.");
        }

        // 보상 수령자 (습득자) 확인
        User foundUser = userRepository.findById(foundUserId)
                .orElseThrow(() -> new IllegalArgumentException("Found user not found"));

        // 보상 상태를 'PAID'로 설정하여 지급 완료
        reward.setStatus(RewardStatus.PAID);
        reward.setFoundUser(foundUser); // 습득자를 보상 객체에 설정
        rewardRepository.save(reward);

        // 습득자의 포인트 업데이트 및 거래 내역 저장
        foundUser.setPoints(foundUser.getPoints() + reward.getAmount());
        PointTransaction transaction = new PointTransaction();
        transaction.setPoints(reward.getAmount());
        transaction.setUser(foundUser);
        pointTransactionRepository.save(transaction);
    }
}
