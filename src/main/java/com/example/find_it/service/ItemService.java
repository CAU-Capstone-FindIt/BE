package com.example.find_it.service;

import com.example.find_it.domain.*;
import com.example.find_it.dto.Request.FoundItemRequest;
import com.example.find_it.dto.Request.LostItemRequest;
import com.example.find_it.dto.Response.FoundItemResponse;
import com.example.find_it.dto.Response.LostItemResponse;
import com.example.find_it.repository.*;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void registerLostItem(LostItemRequest lostItemDTO) {
        User user = userRepository.findById(lostItemDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user has enough points if reward is requested
        if (lostItemDTO.getRewardAmount() != null && lostItemDTO.getRewardAmount() > 0) {
            if (user.getPoints() < lostItemDTO.getRewardAmount()) {
                throw new IllegalArgumentException("Insufficient points for setting the reward.");
            }
            // Deduct points from user
            user.adjustPoints(-lostItemDTO.getRewardAmount());
            userRepository.save(user);
        }

        Location location = saveLocation(lostItemDTO.getLatitude(), lostItemDTO.getLongitude(), lostItemDTO.getAddress());

        // Create reward if amount is specified
        Reward reward = null;
        if (lostItemDTO.getRewardAmount() != null && lostItemDTO.getRewardAmount() > 0) {
            reward = new Reward();
            reward.setAmount(lostItemDTO.getRewardAmount());
            reward.setCurrency("Points");
            reward.setStatus(RewardStatus.PENDING);
            reward.setLostUser(user);
            reward = rewardRepository.save(reward);
        }

        LostItem lostItem = new LostItem();
        lostItem.setDescription(lostItemDTO.getDescription());
        lostItem.setName(lostItemDTO.getName());
        lostItem.setCategory(lostItemDTO.getCategory());
        lostItem.setColor(lostItemDTO.getColor());
        lostItem.setBrand(lostItemDTO.getBrand());
        lostItem.setLostDate(lostItemDTO.getLostDate());
        lostItem.setLocation(location);
        lostItem.setUser(user);
        lostItem.setReward(reward);
        lostItem.setStatus(lostItemDTO.getStatus());

        lostItemRepository.save(lostItem);
    }

    public void reportFoundItem(FoundItemRequest foundItemDTO) {
        User user = userRepository.findById(foundItemDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Location location = saveLocation(foundItemDTO.getLatitude(), foundItemDTO.getLongitude(), foundItemDTO.getAddress());

        FoundItem foundItem = new FoundItem();
        foundItem.setDescription(foundItemDTO.getDescription());
        foundItem.setFoundDate(foundItemDTO.getFoundDate());
        foundItem.setLocation(location);
        foundItem.setUser(user);
        foundItem.setPhoto(foundItemDTO.getPhoto());
        foundItem.setCategory(foundItemDTO.getCategory());  // Category Enum으로 설정
        foundItem.setColor(foundItemDTO.getColor());
        foundItem.setBrand(foundItemDTO.getBrand());

        foundItemRepository.save(foundItem);
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

    public List<LostItem> searchLostItems(String description) {
        return lostItemRepository.findByDescriptionContaining(description);
    }

    public List<FoundItem> searchFoundItems(String description) {
        return foundItemRepository.findByDescriptionContaining(description);
    }

    public List<FoundItem> getAllFoundItems() {
        return foundItemRepository.findAll();
    }

    public List<LostItem> getAllLostItems(){
        return lostItemRepository.findAll();
    }

    public LostItemResponse toLostItemResponse(LostItem lostItem) {
        LostItemResponse response = new LostItemResponse();
        response.setId(lostItem.getId());
        response.setUserId(lostItem.getUser().getId());
        response.setName(lostItem.getName());
        response.setCategory(lostItem.getCategory());
        response.setColor(lostItem.getColor());
        response.setBrand(lostItem.getBrand());
        response.setDescription(lostItem.getDescription());
        response.setLostDate(lostItem.getLostDate());
        response.setAddress(lostItem.getLocation().getAddress());
        response.setRewardId(lostItem.getReward() != null ? lostItem.getReward().getId() : null);
        response.setStatus(lostItem.getStatus());
        response.setCreatedDate(lostItem.getCreatedDate());
        response.setModifiedDate(lostItem.getModifiedDate());
        return response;
    }

    // FoundItemResponse로 변환하는 메서드 추가
    public FoundItemResponse toFoundItemResponse(FoundItem foundItem) {
        FoundItemResponse response = new FoundItemResponse();
        response.setId(foundItem.getId());
        response.setUserId(foundItem.getUser().getId());
        response.setDescription(foundItem.getDescription());
        response.setFoundDate(foundItem.getFoundDate());
        response.setAddress(foundItem.getLocation().getAddress());
        response.setPhoto(foundItem.getPhoto());
        response.setCategory(foundItem.getCategory());
        response.setColor(foundItem.getColor());
        response.setBrand(foundItem.getBrand());
        response.setCreatedDate(foundItem.getCreatedDate());
        response.setModifiedDate(foundItem.getModifiedDate());
        return response;
    }
}
