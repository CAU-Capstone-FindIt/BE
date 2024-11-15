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
    private final UserRepository userRepository;
    private final RewardRepository rewardRepository;
    private final FoundItemCommentRepository foundItemCommentRepository;
    private final LostItemCommentRepository lostItemCommentRepository;

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

    // 댓글 등록
    @Transactional
    public FoundItemCommentResponse registerFoundItemComment(FoundItemCommentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        FoundItem foundItem = foundItemRepository.findById(request.getFoundItemId())
                .orElseThrow(() -> new IllegalArgumentException("Found item not found"));

        FoundItemComment comment = new FoundItemComment();
        comment.setUser(user);
        comment.setFoundItem(foundItem);
        comment.setContent(request.getContent());

        // 부모 댓글이 있을 경우 설정
        if (request.getParentCommentId() != null && request.getParentCommentId() != 0) {
            FoundItemComment parentComment = foundItemCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        FoundItemComment savedComment = foundItemCommentRepository.save(comment);
        return toFoundItemCommentResponseWithChildren(savedComment);
    }


    // 댓글 수정
    @Transactional
    public FoundItemCommentResponse updateFoundItemComment(Long commentId, FoundItemCommentRequest request) {
        FoundItemComment comment = foundItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setContent(request.getContent());
        FoundItemComment updatedComment = foundItemCommentRepository.save(comment);

        return toFoundItemCommentResponse(updatedComment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteFoundItemComment(Long commentId) {
        FoundItemComment comment = foundItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        foundItemCommentRepository.delete(comment);
    }

    // LostItem 댓글 등록
    @Transactional
    public LostItemCommentResponse registerLostItemComment(LostItemCommentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LostItem lostItem = lostItemRepository.findById(request.getLostItemId())
                .orElseThrow(() -> new IllegalArgumentException("Lost item not found"));

        LostItemComment comment = new LostItemComment();
        comment.setUser(user);
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

    // LostItem 댓글 수정
    @Transactional
    public LostItemCommentResponse updateLostItemComment(Long commentId, LostItemCommentRequest request) {
        LostItemComment comment = lostItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setContent(request.getContent());
        LostItemComment updatedComment = lostItemCommentRepository.save(comment);

        return toLostItemCommentResponse(updatedComment);
    }

    // LostItem 댓글 삭제
    @Transactional
    public void deleteLostItemComment(Long commentId) {
        LostItemComment comment = lostItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        lostItemCommentRepository.delete(comment);
    }

    //LostItem 상세 조회
    public LostItemResponse getLostItemDetails(Long lostItemId) {
        // LostItem을 찾고, 없으면 예외 발생
        LostItem lostItem = lostItemRepository.findById(lostItemId)
                .orElseThrow(() -> new IllegalArgumentException("Lost item not found"));

        // LostItemResponse로 변환하고, 댓글을 포함하여 반환
        LostItemResponse response = toLostItemResponseWithComments(lostItem);
        return response;
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

        List<LostItemCommentResponse> comments = lostItem.getComments().stream()
                .map(this::toLostItemCommentResponse)
                .collect(Collectors.toList());
        response.setComments(comments);

        return response;
    }

    private LostItemResponse toLostItemResponseWithComments(LostItem lostItem) {
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

        // 최상위 댓글 목록을 가져오고, 각각에 대한 대댓글을 포함하여 설정
        List<LostItemCommentResponse> comments = lostItem.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)  // 최상위 댓글만 필터링
                .map(this::toLostItemCommentResponseWithChildren)
                .collect(Collectors.toList());
        response.setComments(comments);

        return response;
    }

    private LostItemCommentResponse toLostItemCommentResponseWithChildren(LostItemComment comment) {
        LostItemCommentResponse response = toLostItemCommentResponse(comment);

        // 자식 댓글을 계층적으로 포함
        List<LostItemCommentResponse> childResponses = comment.getChildComments() != null
                ? comment.getChildComments().stream()
                .map(this::toLostItemCommentResponseWithChildren)  // 재귀적으로 자식 댓글 처리
                .collect(Collectors.toList())
                : List.of();  // 자식 댓글이 없을 때 빈 리스트 설정
        response.setChildComments(childResponses);

        return response;
    }


    private FoundItemCommentResponse toFoundItemCommentResponseWithChildren(FoundItemComment comment) {
        FoundItemCommentResponse response = toFoundItemCommentResponse(comment);

        // 자식 댓글을 계층적으로 포함
        List<FoundItemCommentResponse> childResponses = comment.getChildComments() != null
                ? comment.getChildComments().stream()
                .map(this::toFoundItemCommentResponseWithChildren)  // 재귀적으로 자식 댓글 처리
                .collect(Collectors.toList())
                : List.of();  // 자식 댓글이 없을 때 빈 리스트 설정
        response.setChildComments(childResponses);

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

        List<FoundItemCommentResponse> comments = foundItem.getComments().stream()
                .map(this::toFoundItemCommentResponse)
                .collect(Collectors.toList());
        response.setComments(comments);

        return response;
    }

    public FoundItemResponse toFoundItemResponseWithComments(FoundItem foundItem) {
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

        // 최상위 댓글 목록을 가져오고, 각각에 대한 대댓글을 포함하여 설정
        List<FoundItemCommentResponse> comments = foundItem.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)  // 최상위 댓글만 필터링
                .map(this::toFoundItemCommentResponseWithChildren)
                .collect(Collectors.toList());
        response.setComments(comments);

        return response;
    }



    private FoundItemCommentResponse toFoundItemCommentResponse(FoundItemComment comment) {
        FoundItemCommentResponse response = new FoundItemCommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getUser().getId());
        response.setFoundItemId(comment.getFoundItem().getId());
        response.setContent(comment.getContent());
        response.setCreatedDate(comment.getCreatedDate());
        response.setModifiedDate(comment.getModifiedDate());
        return response;
    }

    private LostItemCommentResponse toLostItemCommentResponse(LostItemComment comment) {
        LostItemCommentResponse response = new LostItemCommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getUser().getId());
        response.setLostItemId(comment.getLostItem().getId());
        response.setContent(comment.getContent());
        response.setCreatedDate(comment.getCreatedDate());
        response.setModifiedDate(comment.getModifiedDate());
        return response;
    }
}
