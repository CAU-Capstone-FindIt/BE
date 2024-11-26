package com.example.find_it.service;

import com.example.find_it.domain.*;
import com.example.find_it.dto.Request.*;
import com.example.find_it.dto.Response.FoundItemCommentResponse;
import com.example.find_it.dto.Response.FoundItemResponse;
import com.example.find_it.dto.Response.LostItemCommentResponse;
import com.example.find_it.dto.Response.LostItemResponse;
import com.example.find_it.exception.CustomException;
import com.example.find_it.exception.ErrorCode;
import com.example.find_it.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final MemberRepository memberRepository;
    private final RewardRepository rewardRepository;
    private final FoundItemCommentRepository foundItemCommentRepository;
    private final LostItemCommentRepository lostItemCommentRepository;
    private final S3Service s3Service;

    @Transactional
    public void registerLostItem(LostItemRequest lostItemDTO, Member member) throws IOException {
        // Check if member has enough points if reward is requested
        if (lostItemDTO.getRewardAmount() != null && lostItemDTO.getRewardAmount() > 0) {
            if (member.getPoints() < lostItemDTO.getRewardAmount()) {
                throw new CustomException(ErrorCode.INSUFFICIENT_POINTS);
            }
            // Deduct points from member
            member.adjustPoints(-lostItemDTO.getRewardAmount());
            memberRepository.save(member);
        }

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

        String imageUrl = null;
        if (lostItemDTO.getImage() != null && !lostItemDTO.getImage().isEmpty()) {
            String fileName = "lost-item-" + System.currentTimeMillis() + ".jpg"; // 고유한 파일 이름 생성
            imageUrl = s3Service.uploadFile(lostItemDTO.getImage(), "lost-items", fileName); // S3에 업로드
        }


        // LostItem 생성 및 저장
        LostItem lostItem = new LostItem();
        lostItem.setDescription(lostItemDTO.getDescription());
        lostItem.setName(lostItemDTO.getName());
        lostItem.setCategory(lostItemDTO.getCategory());
        lostItem.setColor(lostItemDTO.getColor());
        lostItem.setBrand(lostItemDTO.getBrand());
        lostItem.setReportDate(lostItemDTO.getReportDate());
        lostItem.setLatitude(lostItemDTO.getLatitude()); // 위도
        lostItem.setLongitude(lostItemDTO.getLongitude()); // 경도
        lostItem.setAddress(lostItemDTO.getAddress()); // 주소
        lostItem.setMember(member);
        lostItem.setReward(reward);
        lostItem.setStatus(lostItemDTO.getStatus());
        lostItem.setImage(imageUrl); // S3 이미지 URL 저장

        lostItemRepository.save(lostItem);
    }


    @Transactional
    public void reportFoundItem(FoundItemRequest foundItemDTO, Member member) throws IOException {
        // S3 이미지 업로드 처리
        String imageUrl = null;
        if (foundItemDTO.getImage() != null && !foundItemDTO.getImage().isEmpty()) {
            String fileName = "found-item-" + System.currentTimeMillis() + ".jpg"; // 고유한 파일 이름 생성
            imageUrl = s3Service.uploadFile(foundItemDTO.getImage(), "found-items", fileName); // S3에 업로드
        }

        // FoundItem 생성 및 저장
        FoundItem foundItem = new FoundItem();
        foundItem.setName(foundItemDTO.getName());            // name 필드 추가 처리
        foundItem.setDescription(foundItemDTO.getDescription());
        foundItem.setReportDate(foundItemDTO.getReportDate());
        foundItem.setLatitude(foundItemDTO.getLatitude());    // 위도
        foundItem.setLongitude(foundItemDTO.getLongitude()); // 경도
        foundItem.setAddress(foundItemDTO.getAddress());      // 주소
        foundItem.setMember(member);
        foundItem.setImage(imageUrl);                         // S3 이미지 URL 저장
        foundItem.setCategory(foundItemDTO.getCategory());    // Category Enum으로 설정
        foundItem.setColor(foundItemDTO.getColor());
        foundItem.setBrand(foundItemDTO.getBrand());

        foundItemRepository.save(foundItem);
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
    public FoundItemCommentResponse registerFoundItemComment(FoundItemCommentRequest request, Member member) {
        // Fetch the FoundItem entity
        FoundItem foundItem = foundItemRepository.findById(request.getFoundItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.FOUND_ITEM_NOT_FOUND));

        // Create the comment
        FoundItemComment comment = new FoundItemComment();
        comment.setMember(member); // Associate the comment with the authenticated member
        comment.setFoundItem(foundItem);
        comment.setContent(request.getContent());

        // Handle parent comment for replies (optional)
        if (request.getParentCommentId() != null) {
            FoundItemComment parentComment = foundItemCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));
            comment.setParentComment(parentComment);
        }

        FoundItemComment savedComment = foundItemCommentRepository.save(comment);
        return toFoundItemCommentResponse(savedComment);
    }


    @Transactional
    public FoundItemCommentResponse updateFoundItemComment(Long commentId, FoundItemCommentRequest request, Member member) {
        // Fetch the comment from the repository
        FoundItemComment comment = foundItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // Validate ownership
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED);
        }

        // Update the content
        comment.setContent(request.getContent());

        // Save the updated comment
        FoundItemComment updatedComment = foundItemCommentRepository.save(comment);

        // Convert to response DTO and return
        return toFoundItemCommentResponse(updatedComment);
    }


    @Transactional
    public void deleteFoundItemComment(Long commentId, Member member) {
        // Fetch the comment from the repository
        FoundItemComment comment = foundItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // Validate ownership
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED);
        }

        // Delete the comment
        foundItemCommentRepository.delete(comment);
    }


    @Transactional
    public LostItemCommentResponse registerLostItemComment(LostItemCommentRequest request, Member member) {
        LostItem lostItem = lostItemRepository.findById(request.getLostItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.LOST_ITEM_NOT_FOUND));

        LostItemComment comment = new LostItemComment();
        comment.setMember(member);
        comment.setLostItem(lostItem);
        comment.setContent(request.getContent());

        if (request.getParentCommentId() != null) {
            LostItemComment parentComment = lostItemCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));
            comment.setParentComment(parentComment);
        }

        LostItemComment savedComment = lostItemCommentRepository.save(comment);
        return toLostItemCommentResponse(savedComment);
    }



    @Transactional
    public LostItemCommentResponse updateLostItemComment(Long commentId, LostItemCommentRequest request, Member member) {
        LostItemComment comment = lostItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED);
        }

        comment.setContent(request.getContent());
        LostItemComment updatedComment = lostItemCommentRepository.save(comment);
        return toLostItemCommentResponse(updatedComment);
    }



    @Transactional
    public void deleteLostItemComment(Long commentId, Member member) {
        LostItemComment comment = lostItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED);
        }

        lostItemCommentRepository.delete(comment);
    }

    @Transactional
    public List<LostItem> advancedSearchLostItems(LostItemSearchRequest searchCriteria) {
        return lostItemRepository.findAll().stream()
                .filter(item -> {
                    int matchCount = 0;

                    if (searchCriteria.getName() != null && searchCriteria.getName().equalsIgnoreCase(item.getName())) {
                        matchCount++;
                    }
                    if (searchCriteria.getBrand() != null && searchCriteria.getBrand().equalsIgnoreCase(item.getBrand())) {
                        matchCount++;
                    }
                    if (searchCriteria.getColor() != null && searchCriteria.getColor().equalsIgnoreCase(item.getColor())) {
                        matchCount++;
                    }
                    if (searchCriteria.getCategory() != null && searchCriteria.getCategory() == item.getCategory()) {
                        matchCount++;
                    }
                    if (searchCriteria.getAddress() != null && searchCriteria.getAddress().equalsIgnoreCase(item.getAddress())) {
                        matchCount++;
                    }
                    // Check if lostDate is between startDate and endDate
                    if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
                        if (item.getReportDate() != null &&
                                (item.getReportDate().isEqual(searchCriteria.getStartDate()) || item.getReportDate().isAfter(searchCriteria.getStartDate())) &&
                                (item.getReportDate().isEqual(searchCriteria.getEndDate()) || item.getReportDate().isBefore(searchCriteria.getEndDate()))) {
                            matchCount++;
                        }
                    }

                    return matchCount >= 3; // Match at least 3 conditions
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<FoundItem> advancedSearchFoundItems(FoundItemSearchRequest searchCriteria) {
        return foundItemRepository.findAll().stream()
                .filter(item -> {
                    int matchCount = 0;

                    if (searchCriteria.getName() != null && searchCriteria.getName().equalsIgnoreCase(item.getName())) {
                        matchCount++;
                    }
                    if (searchCriteria.getBrand() != null && searchCriteria.getBrand().equalsIgnoreCase(item.getBrand())) {
                        matchCount++;
                    }
                    if (searchCriteria.getColor() != null && searchCriteria.getColor().equalsIgnoreCase(item.getColor())) {
                        matchCount++;
                    }
                    if (searchCriteria.getCategory() != null && searchCriteria.getCategory() == item.getCategory()) {
                        matchCount++;
                    }
                    if (searchCriteria.getAddress() != null && searchCriteria.getAddress().equalsIgnoreCase(item.getAddress())) {
                        matchCount++;
                    }
                    // Check if foundDate is between startDate and endDate
                    if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
                        if (item.getReportDate() != null &&
                                (item.getReportDate().isEqual(searchCriteria.getStartDate()) || item.getReportDate().isAfter(searchCriteria.getStartDate())) &&
                                (item.getReportDate().isEqual(searchCriteria.getEndDate()) || item.getReportDate().isBefore(searchCriteria.getEndDate()))) {
                            matchCount++;
                        }
                    }

                    return matchCount >= 3; // Match at least 3 conditions
                })
                .collect(Collectors.toList());
    }


    public LostItemResponse getLostItemDetails(Long lostItemId) {
        LostItem lostItem = lostItemRepository.findById(lostItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.LOST_ITEM_NOT_FOUND));

        return toLostItemResponseWithComments(lostItem);
    }

    public FoundItemResponse getFoundItemDetails(Long foundItemId) {
        FoundItem foundItem = foundItemRepository.findById(foundItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.FOUND_ITEM_NOT_FOUND));

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
        response.setReportDate(lostItem.getReportDate());
        response.setLatitude(lostItem.getLatitude()); // 위도
        response.setLongitude(lostItem.getLongitude()); // 경도
        response.setAddress(lostItem.getAddress()); // 주소
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
        response.setNickname(comment.getMember().getNickname()); // 작성자 닉네임 설정
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
        response.setName(foundItem.getName());                // name 필드 추가 반환
        response.setDescription(foundItem.getDescription());
        response.setReportDate(foundItem.getReportDate());
        response.setLatitude(foundItem.getLatitude()); // 위도
        response.setLongitude(foundItem.getLongitude()); // 경도
        response.setAddress(foundItem.getAddress()); // 주소
        response.setImage(foundItem.getImage());
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
        response.setNickname(comment.getMember().getNickname()); // 작성자 닉네임 설정
        response.setFoundItemId(comment.getFoundItem().getId());
        response.setContent(comment.getContent());
        response.setCreatedDate(comment.getCreatedDate());
        response.setModifiedDate(comment.getModifiedDate());
        return response;
    }

    public List<FoundItemResponse> getAllFoundItemsWithDetails() {
        return foundItemRepository.findAll().stream()
                .map(this::toFoundItemResponseWithComments) // 댓글 포함된 상세 응답으로 변환
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> getAllLostItemsWithDetails() {
        return lostItemRepository.findAll().stream()
                .map(this::toLostItemResponseWithComments) // 댓글 포함된 상세 응답으로 변환
                .collect(Collectors.toList());
    }

}
