package com.example.find_it.service;

import com.example.find_it.domain.*;
import com.example.find_it.dto.FoundItemDTO;
import com.example.find_it.dto.LostItemDTO;
import com.example.find_it.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private FoundItemRepository foundItemRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RewardRepository rewardRepository;

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

