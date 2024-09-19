package com.example.find_it.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardDTO {
    private Long rewardId;
    private Long foundUserId;
    private int amount;
}
