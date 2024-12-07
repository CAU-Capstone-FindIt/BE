package com.example.find_it.service;

import com.example.find_it.domain.*;
import com.example.find_it.dto.Request.FoundItemCommentRequest;
import com.example.find_it.dto.Request.FoundItemRequest;
import com.example.find_it.dto.Request.LostItemCommentRequest;
import com.example.find_it.dto.Request.LostItemRequest;
import com.example.find_it.dto.Response.FoundItemCommentResponse;
import com.example.find_it.dto.Response.FoundItemResponse;
import com.example.find_it.dto.Response.LostItemCommentResponse;
import com.example.find_it.dto.Response.LostItemResponse;
import com.example.find_it.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;
    private final RewardRepository rewardRepository;
    private final FoundItemCommentRepository foundItemCommentRepository;
    private final LostItemCommentRepository lostItemCommentRepository;

    @Transactional
    public void registerLostItem(LostItemRequest lostItemDTO) {
        Member member = memberRepository.findById(lostItemDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // Check if member has enough points if reward is requested
        if (lostItemDTO.getRewardAmount() != null && lostItemDTO.getRewardAmount() > 0) {
            if (member.getPoints() < lostItemDTO.getRewardAmount()) {
                throw new IllegalArgumentException("Insufficient points for setting the reward.");
            }
            // Deduct points from member
            member.adjustPoints(-lostItemDTO.getRewardAmount());
            memberRepository.save(member);
        }

        Location location = saveLocation(lostItemDTO.getLatitude(), lostItemDTO.getLongitude(), lostItemDTO.getAddress());

        // Create reward if amount is specified
        Reward reward = null;
        if (lostItemDTO.getRewardAmount() != null && lostItemDTO.getRewardAmount() > 0) {
            reward = new Reward();
            reward.setAmount(lostItemDTO.getRewardAmount());
            reward.setCurrency("Points");
            reward.setStatus(RewardStatus.PENDING);
            reward.setLostUser(member);
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
        lostItem.setMember(member);
        lostItem.setReward(reward);
        lostItem.setStatus(lostItemDTO.getStatus());

        lostItemRepository.save(lostItem);
    }

    public void reportFoundItem(FoundItemRequest foundItemDTO) {
        Member member = memberRepository.findById(foundItemDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Location location = saveLocation(foundItemDTO.getLatitude(), foundItemDTO.getLongitude(), foundItemDTO.getAddress());

        FoundItem foundItem = new FoundItem();
        foundItem.setDescription(foundItemDTO.getDescription());
        foundItem.setFoundDate(foundItemDTO.getFoundDate());
        foundItem.setLocation(location);
        foundItem.setMember(member);
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

    public List<LostItem> getAllLostItems() {
        return lostItemRepository.findAll();
    }

    @Transactional
    public FoundItemCommentResponse registerFoundItemComment(FoundItemCommentRequest request) {
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        FoundItem foundItem = foundItemRepository.findById(request.getFoundItemId())
                .orElseThrow(() -> new IllegalArgumentException("Found item not found"));

        FoundItemComment comment = new FoundItemComment();
        comment.setMember(member);
        comment.setFoundItem(foundItem);
        comment.setContent(request.getContent());

        if (request.getParentCommentId() != null && request.getParentCommentId() != 0) {
            FoundItemComment parentComment = foundItemCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        FoundItemComment savedComment = foundItemCommentRepository.save(comment);
        return toFoundItemCommentResponseWithChildren(savedComment);
    }

    @Transactional
    public FoundItemCommentResponse updateFoundItemComment(Long commentId, FoundItemCommentRequest request) {
        FoundItemComment comment = foundItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setContent(request.getContent());
        FoundItemComment updatedComment = foundItemCommentRepository.save(comment);

        return toFoundItemCommentResponse(updatedComment);
    }

    @Transactional
    public void deleteFoundItemComment(Long commentId) {
        FoundItemComment comment = foundItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        foundItemCommentRepository.delete(comment);
    }

    @Transactional
    public LostItemCommentResponse registerLostItemComment(LostItemCommentRequest request) {
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        LostItem lostItem = lostItemRepository.findById(request.getLostItemId())
                .orElseThrow(() -> new IllegalArgumentException("Lost item not found"));

        LostItemComment comment = new LostItemComment();
        comment.setMember(member);
        comment.setLostItem(lostItem);
        comment.setContent(request.getContent());

        if (request.getParentCommentId() != null) {
            LostItemComment parentComment = lostItemCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        LostItemComment savedComment = lostItemCommentRepository.save(comment);
        return toLostItemCommentResponse(savedComment);
    }

    @Transactional
    public LostItemCommentResponse updateLostItemComment(Long commentId, LostItemCommentRequest request) {
        LostItemComment comment = lostItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setContent(request.getContent());
        LostItemComment updatedComment = lostItemCommentRepository.save(comment);

        return toLostItemCommentResponse(updatedComment);
    }

    @Transactional
    public void deleteLostItemComment(Long commentId) {
        LostItemComment comment = lostItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        lostItemCommentRepository.delete(comment);
    }

    public LostItemResponse getLostItemDetails(Long lostItemId) {
        LostItem lostItem = lostItemRepository.findById(lostItemId)
                .orElseThrow(() -> new IllegalArgumentException("Lost item not found"));

        return toLostItemResponseWithComments(lostItem);
    }

    public FoundItemResponse getFoundItemDetails(Long foundItemId) {
        FoundItem foundItem = foundItemRepository.findById(foundItemId)
                .orElseThrow(() -> new IllegalArgumentException("Found item not found"));

        return toFoundItemResponseWithComments(foundItem);
    }

    public LostItemResponse toLostItemResponse(LostItem lostItem) {
        LostItemResponse response = new LostItemResponse();
        response.setId(lostItem.getId());
        response.setUserId(lostItem.getMember().getId());
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

        List<LostItemCommentResponse> comments = lostItem.getComments().stream()
                .map(this::toLostItemCommentResponse)
                .collect(Collectors.toList());
        response.setComments(comments);

        return response;
    }

    public LostItemResponse toLostItemResponseWithComments(LostItem lostItem) {
        LostItemResponse response = toLostItemResponse(lostItem);

        List<LostItemCommentResponse> comments = lostItem.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(this::toLostItemCommentResponseWithChildren)
                .collect(Collectors.toList());
        response.setComments(comments);

        return response;
    }

    public LostItemCommentResponse toLostItemCommentResponseWithChildren(LostItemComment comment) {
        LostItemCommentResponse response = toLostItemCommentResponse(comment);

        List<LostItemCommentResponse> childResponses = comment.getChildComments() != null
                ? comment.getChildComments().stream()
                .map(this::toLostItemCommentResponseWithChildren)
                .collect(Collectors.toList())
                : List.of();

        response.setChildComments(childResponses);
        return response;
    }

    public LostItemCommentResponse toLostItemCommentResponse(LostItemComment comment) {
        LostItemCommentResponse response = new LostItemCommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getMember().getId());
        response.setLostItemId(comment.getLostItem().getId());
        response.setContent(comment.getContent());
        response.setCreatedDate(comment.getCreatedDate());
        response.setModifiedDate(comment.getModifiedDate());
        return response;
    }

    public FoundItemResponse toFoundItemResponse(FoundItem foundItem) {
        FoundItemResponse response = new FoundItemResponse();
        response.setId(foundItem.getId());
        response.setUserId(foundItem.getMember().getId());
        response.setDescription(foundItem.getDescription());
        response.setFoundDate(foundItem.getFoundDate());
        response.setAddress(foundItem.getLocation().getAddress());
        response.setPhoto(foundItem.getPhoto());
        response.setCategory(foundItem.getCategory());
        response.setColor(foundItem.getColor());
        response.setBrand(foundItem.getBrand());
        response.setCreatedDate(foundItem.getCreatedDate());
        response.setModifiedDate(foundItem.getModifiedDate());

        List<FoundItemCommentResponse> comments = foundItem.getComments().stream()
                .map(this::toFoundItemCommentResponse)
                .collect(Collectors.toList());
        response.setComments(comments);

        return response;
    }

    public FoundItemResponse toFoundItemResponseWithComments(FoundItem foundItem) {
        FoundItemResponse response = toFoundItemResponse(foundItem);

        List<FoundItemCommentResponse> comments = foundItem.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(this::toFoundItemCommentResponseWithChildren)
                .collect(Collectors.toList());
        response.setComments(comments);

        return response;
    }

    public FoundItemCommentResponse toFoundItemCommentResponseWithChildren(FoundItemComment comment) {
        FoundItemCommentResponse response = toFoundItemCommentResponse(comment);

        List<FoundItemCommentResponse> childResponses = comment.getChildComments() != null
                ? comment.getChildComments().stream()
                .map(this::toFoundItemCommentResponseWithChildren)
                .collect(Collectors.toList())
                : List.of();

        response.setChildComments(childResponses);
        return response;
    }

    public FoundItemCommentResponse toFoundItemCommentResponse(FoundItemComment comment) {
        FoundItemCommentResponse response = new FoundItemCommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getMember().getId());
        response.setFoundItemId(comment.getFoundItem().getId());
        response.setContent(comment.getContent());
        response.setCreatedDate(comment.getCreatedDate());
        response.setModifiedDate(comment.getModifiedDate());
        return response;
    }
}
