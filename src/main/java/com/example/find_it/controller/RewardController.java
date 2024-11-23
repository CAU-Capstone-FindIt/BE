package com.example.find_it.controller;

import com.example.find_it.dto.RewardDTO;
import com.example.find_it.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private final RewardService rewardService;

    @Operation(summary = "분실물에 대한 보상을 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보상이 성공적으로 설정되었습니다."),
            @ApiResponse(responseCode = "400", description = "포인트가 부족하거나 유효하지 않은 사용자입니다."),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류")
    })
    @PostMapping("/set/{lostUserId}")
    public ResponseEntity<String> setReward(
            @RequestBody(description = "보상 정보", required = true) RewardDTO rewardDTO,
            @PathVariable Long lostUserId) {
        rewardService.setReward(rewardDTO, lostUserId);
        return ResponseEntity.ok("보상이 성공적으로 설정되었습니다.");
    }

    @Operation(summary = "습득물 사용자에게 보상을 지급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보상이 성공적으로 지급되었습니다."),
            @ApiResponse(responseCode = "400", description = "보상이 지급 가능한 상태가 아니거나 유효하지 않은 사용자입니다."),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류")
    })
    @PostMapping("/pay/{rewardId}/{foundUserId}")
    public ResponseEntity<String> payReward(
            @PathVariable Long rewardId,
            @PathVariable Long foundUserId) {
        rewardService.payReward(rewardId, foundUserId);
        return ResponseEntity.ok("보상이 성공적으로 지급되었습니다.");
    }
}

