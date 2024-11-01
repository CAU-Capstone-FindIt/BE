package com.example.find_it.controller;

import com.example.find_it.dto.RewardDTO;
import com.example.find_it.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @Operation(summary = "Set a reward for a lost item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reward set successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient points or invalid user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/set/{lostUserId}")
    public ResponseEntity<String> setReward(
            @RequestBody(description = "Reward details", required = true) RewardDTO rewardDTO,
            @PathVariable Long lostUserId) {
        rewardService.setReward(rewardDTO, lostUserId);
        return ResponseEntity.ok("Reward set successfully.");
    }

    @Operation(summary = "Pay a reward to a found item user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reward paid successfully"),
            @ApiResponse(responseCode = "400", description = "Reward not in payable state or invalid user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/pay/{rewardId}/{foundUserId}")
    public ResponseEntity<String> payReward(
            @PathVariable Long rewardId,
            @PathVariable Long foundUserId) {
        rewardService.payReward(rewardId, foundUserId);
        return ResponseEntity.ok("Reward paid successfully.");
    }
}
