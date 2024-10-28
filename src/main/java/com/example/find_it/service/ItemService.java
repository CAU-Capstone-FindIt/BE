package com.example.find_it.service;

import com.example.find_it.domain.*;
import com.example.find_it.dto.FoundItemDTO;
import com.example.find_it.dto.LostItemDTO;
import com.example.find_it.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findById(lostItemDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Location location = saveLocation(lostItemDTO.getLatitude(), lostItemDTO.getLongitude(), lostItemDTO.getAddress());

        Reward reward = retrieveReward(lostItemDTO.getRewardId());

        LostItem lostItem = new LostItem();
        lostItem.setDescription(lostItemDTO.getDescription());
        lostItem.setLostDate(lostItemDTO.getLostDate());
        lostItem.setLocation(location);
        lostItem.setUser(user);
        lostItem.setReward(reward);
        lostItem.setStatus(lostItemDTO.getStatus());

        lostItemRepository.save(lostItem);

        // 유사 항목 추천
        recommendSimilarItems("lost", lostItemDTO.getDescription());
    }

    // 습득물 신고
    public void reportFoundItem(FoundItemDTO foundItemDTO) {
        User user = userRepository.findById(foundItemDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Location location = saveLocation(foundItemDTO.getLatitude(), foundItemDTO.getLongitude(), foundItemDTO.getAddress());

        FoundItem foundItem = new FoundItem();
        foundItem.setDescription(foundItemDTO.getDescription());
        foundItem.setFoundDate(foundItemDTO.getFoundDate());
        foundItem.setLocation(location);
        foundItem.setUser(user);
        foundItem.setPhoto(foundItemDTO.getPhoto());

        foundItemRepository.save(foundItem);

        // 유사 항목 추천
        recommendSimilarItems("found", foundItemDTO.getDescription());
    }

    private Location saveLocation(double latitude, double longitude, String address) {
        Location location = new Location(latitude, longitude, address);
        return locationRepository.save(location);
    }

    private Reward retrieveReward(Long rewardId) {
        if (rewardId != null) {
            return rewardRepository.findById(rewardId)
                    .orElseThrow(() -> new IllegalArgumentException("Reward not found"));
        }
        return null;
    }

    private void recommendSimilarItems(String type, String description) {
        String prompt = String.format("Find similar %s items for description: %s", type, description);
        String result = openAIService.getSimilarItems(prompt);
        log.info("Recommended similar items: {}", result);
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
