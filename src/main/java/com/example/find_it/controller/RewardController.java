package com.example.find_it.controller;

import com.example.find_it.dto.RewardDTO;
import com.example.find_it.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    // 보상 설정
    @PostMapping("/set")
    public ResponseEntity<String> setReward(@RequestBody RewardDTO rewardDTO) {
        rewardService.setReward(rewardDTO);
        return ResponseEntity.ok("Reward set successfully.");
    }

    // 보상 지급
    @PostMapping("/pay/{rewardId}")
    public ResponseEntity<String> payReward(@PathVariable Long rewardId) {
        rewardService.payReward(rewardId);
        return ResponseEntity.ok("Reward paid successfully.");
    }
}

