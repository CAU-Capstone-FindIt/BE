package com.example.find_it.service;

import com.example.find_it.domain.PointTransaction;
import com.example.find_it.domain.Reward;
import com.example.find_it.domain.RewardStatus;
import com.example.find_it.domain.User;
import com.example.find_it.dto.RewardDTO;
import com.example.find_it.repository.PointTransactionRepository;
import com.example.find_it.repository.RewardRepository;
import com.example.find_it.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    // 보상 설정
    public void setReward(RewardDTO rewardDTO) {
        Reward reward = new Reward();
        reward.setAmount(rewardDTO.getAmount());
        reward.setCurrency("Points");
        reward.setStatus(RewardStatus.PENDING);
        rewardRepository.save(reward);
    }

    // 보상 지급
    public void payReward(Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (reward.getStatus().equals(RewardStatus.PENDING)) {
            reward.setStatus(RewardStatus.PAID);
            rewardRepository.save(reward);

            User foundUser = reward.getFoundUser(); // 보상받는 사용자
            foundUser.setPoints(foundUser.getPoints() + reward.getAmount());

            PointTransaction transaction = new PointTransaction();
            transaction.setPoints(reward.getAmount());
            transaction.setUser(foundUser);
            pointTransactionRepository.save(transaction);
        }
    }
}

