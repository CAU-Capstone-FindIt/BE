package com.example.find_it.service;

import com.example.find_it.domain.*;
import com.example.find_it.dto.RewardDTO;
import com.example.find_it.repository.MemberRepository;
import com.example.find_it.repository.PointTransactionRepository;
import com.example.find_it.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final MemberRepository userRepository;

    // 보상 설정
    public void setReward(RewardDTO rewardDTO, Long lostUserId) {
        Member lostUser = userRepository.findById(lostUserId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 분실자가 보상 포인트를 설정할 수 있도록 만듭니다.
        if (lostUser.getPoints() < rewardDTO.getAmount()) {
            throw new IllegalArgumentException("Insufficient points for setting the reward.");
        }

        // 보상 정보 생성 및 설정 (Setter 방식 사용)
        Reward reward = new Reward();
        reward.setAmount(rewardDTO.getAmount());
        reward.setCurrency("Points");
        reward.setStatus(RewardStatus.PENDING); // 보류 중
        reward.setLostUser(lostUser); // 보상을 설정한 사용자 (분실자)

        // 분실자 포인트 차감
        lostUser.adjustPoints(-rewardDTO.getAmount()); // 포인트 차감 메서드 사용
        userRepository.save(lostUser);
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
        Member foundUser = userRepository.findById(foundUserId)
                .orElseThrow(() -> new IllegalArgumentException("Found user not found"));

        // 보상 상태를 'PAID'로 설정하여 지급 완료
        reward.setStatus(RewardStatus.PAID);
        reward.setFoundUser(foundUser); // 습득자를 보상 객체에 설정
        rewardRepository.save(reward);

        // 습득자의 포인트 업데이트 및 거래 내역 저장
        foundUser.adjustPoints(reward.getAmount()); // 포인트 추가 메서드 사용
        userRepository.save(foundUser);

        PointTransaction transaction = PointTransaction.builder()
                .points(reward.getAmount())
                .member(foundUser)
                .description("보상 지급") // 거래 설명 추가 가능
                .build();

        pointTransactionRepository.save(transaction);
    }
}
