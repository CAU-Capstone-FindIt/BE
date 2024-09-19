package com.example.find_it.dto;

import com.example.find_it.domain.LostItemStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LostItemDTO {
    private Long userId;
    private String description;
    private LocalDate lostDate;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long rewardId; // 보상 관련 정보
    private LostItemStatus status; // 분실물 상태 (예: 'Lost', 'Found')

}

