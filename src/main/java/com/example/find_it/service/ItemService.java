package com.example.find_it.service;

import com.example.find_it.domain.*;
import com.example.find_it.dto.FoundItemDTO;
import com.example.find_it.dto.LostItemDTO;
import com.example.find_it.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RewardRepository rewardRepository;
    private final OpenAIService openAIService;

    // 분실물 등록
    public void registerLostItem(LostItemDTO lostItemDTO) {
        // 사용자 정보 확인 및 로드
        User user = userRepository.findById(lostItemDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 위치 정보 생성
        Location location = new Location(lostItemDTO.getLatitude(), lostItemDTO.getLongitude(), lostItemDTO.getAddress());
        locationRepository.save(location);

        // 보상 정보 로드 (선택 사항일 수 있음)
        Reward reward = null;
        if (lostItemDTO.getRewardId() != null) {
            reward = rewardRepository.findById(lostItemDTO.getRewardId())
                    .orElseThrow(() -> new RuntimeException("Reward not found"));
        }

        // 분실물 생성 및 저장
        LostItem lostItem = new LostItem();
        lostItem.setDescription(lostItemDTO.getDescription());
        lostItem.setLostDate(lostItemDTO.getLostDate());
        lostItem.setLocation(location);
        lostItem.setUser(user);
        lostItem.setReward(reward);
        lostItem.setStatus(lostItemDTO.getStatus());

        lostItemRepository.save(lostItem);

        // OpenAI API를 사용해 유사 항목 추천
        String prompt = "Find similar lost items for description: " + lostItemDTO.getDescription();
        String result = openAIService.getSimilarItems(prompt);

        log.info("Recommended similar items: {}", result);
        // 결과를 분석하여 유사한 항목을 연결하고 알림 전송 로직을 추가할 수 있음
    }

    // 습득물 신고
    public void reportFoundItem(FoundItemDTO foundItemDTO) {
        // 사용자 정보 확인 및 로드
        User user = userRepository.findById(foundItemDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 위치 정보 생성
        Location location = new Location(foundItemDTO.getLatitude(), foundItemDTO.getLongitude(), foundItemDTO.getAddress());
        locationRepository.save(location);

        // 습득물 생성 및 저장
        FoundItem foundItem = new FoundItem();
        foundItem.setDescription(foundItemDTO.getDescription());
        foundItem.setFoundDate(foundItemDTO.getFoundDate());
        foundItem.setLocation(location);
        foundItem.setUser(user);
        foundItem.setPhoto(foundItemDTO.getPhoto());

        foundItemRepository.save(foundItem);

        // OpenAI API를 사용해 유사 항목 추천
        String prompt = "Find similar found items for description: " + foundItemDTO.getDescription();
        String result = openAIService.getSimilarItems(prompt);

        log.info("Recommended similar items: {}", result);
        // 결과를 분석하여 유사한 항목을 연결하고 알림 전송 로직을 추가할 수 있음
    }

    // 분실물 검색
    public List<LostItem> searchLostItems(String description) {
        return lostItemRepository.findByDescriptionContaining(description);
    }

    // 습득물 검색
    public List<FoundItem> searchFoundItems(String description) {
        return foundItemRepository.findByDescriptionContaining(description);
    }
}
