package com.example.find_it.controller;

import com.example.find_it.domain.Member;
import com.example.find_it.dto.Request.FoundItemCommentRequest;
import com.example.find_it.dto.Request.FoundItemRequest;
import com.example.find_it.dto.Request.LostItemCommentRequest;
import com.example.find_it.dto.Request.LostItemRequest;
import com.example.find_it.dto.Response.FoundItemCommentResponse;
import com.example.find_it.dto.Response.FoundItemResponse;
import com.example.find_it.dto.Response.LostItemCommentResponse;
import com.example.find_it.dto.Response.LostItemResponse;
import com.example.find_it.service.ItemService;
import com.example.find_it.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "분실물/습득물 관리 API", description = "분실물과 습득물 관련 API")
public class ItemController {

    @Autowired
    private final ItemService itemService;
    private final MemberService memberService;

    @Operation(summary = "분실물 등록", description = "새로운 분실물을 등록합니다.")
    @PostMapping("/lost/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> registerLostItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "분실물 정보") @RequestBody LostItemRequest lostItemRequest) {

        // Fetch the authenticated member using userDetails
        Member member = memberService.getMemberByPrincipal(userDetails);

        // Pass the LostItemRequest and Member to the service
        itemService.registerLostItem(lostItemRequest, member);

        return ResponseEntity.ok("Lost item registered successfully.");
    }


    @Operation(summary = "습득물 등록", description = "새로운 습득물을 등록합니다.")
    @PostMapping("/found/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> reportFoundItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "습득물 정보") @RequestBody FoundItemRequest foundItemRequest) {

        // Fetch the authenticated member using userDetails
        Member member = memberService.getMemberByPrincipal(userDetails);

        // Pass the FoundItemRequest and Member to the service
        itemService.reportFoundItem(foundItemRequest, member);

        return ResponseEntity.ok("Found item reported successfully.");
    }


    @Operation(summary = "분실물 검색", description = "설명을 기반으로 분실물을 검색합니다.")
    @GetMapping("/lost/search")
    public ResponseEntity<List<LostItemResponse>> searchLostItems(
            @Parameter(description = "검색할 설명") @RequestParam(required = false) String description) {
        List<LostItemResponse> lostItems = itemService.searchLostItems(description).stream()
                .map(itemService::toLostItemResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lostItems);
    }

    @Operation(summary = "습득물 검색", description = "설명을 기반으로 습득물을 검색합니다.")
    @GetMapping("/found/search")
    public ResponseEntity<List<FoundItemResponse>> searchFoundItems(
            @Parameter(description = "검색할 설명") @RequestParam(required = false) String description) {
        List<FoundItemResponse> foundItems = itemService.searchFoundItems(description).stream()
                .map(itemService::toFoundItemResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(foundItems);
    }

    @Operation(summary = "모든 습득물 조회", description = "모든 습득물 항목을 조회합니다.")
    @GetMapping("/found/all")
    public ResponseEntity<List<FoundItemResponse>> getAllFoundItems() {
        List<FoundItemResponse> foundItems = itemService.getAllFoundItems().stream()
                .map(itemService::toFoundItemResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(foundItems);
    }

    @Operation(summary = "모든 분실물 조회", description = "모든 습득물 항목을 조회합니다.")
    @GetMapping("/lost/all")
    public ResponseEntity<List<LostItemResponse>> getAllLostItems() {
        List<LostItemResponse> lostItems = itemService.getAllLostItems().stream()
                .map(itemService::toLostItemResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lostItems);
    }

    @Operation(summary = "분실물 댓글 등록", description = "분실물에 댓글을 등록합니다.")
    @PostMapping("/lost/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LostItemCommentResponse> registerLostItemComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "분실물 댓글 정보") @RequestBody LostItemCommentRequest request) {

        // Fetch the authenticated member using userDetails
        Member member = memberService.getMemberByPrincipal(userDetails);

        // Pass the authenticated member along with the request to the service
        LostItemCommentResponse response = itemService.registerLostItemComment(request, member);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "분실물 댓글 수정", description = "분실물 댓글을 수정합니다.")
    @PutMapping("/lost/comment/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LostItemCommentResponse> updateLostItemComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Parameter(description = "수정할 댓글 정보") @RequestBody LostItemCommentRequest request) {

        // Fetch the authenticated member
        Member member = memberService.getMemberByPrincipal(userDetails);

        // Pass the commentId, request, and authenticated member to the service
        LostItemCommentResponse response = itemService.updateLostItemComment(commentId, request, member);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "분실물 댓글 삭제", description = "분실물 댓글을 삭제합니다.")
    @DeleteMapping("/lost/comment/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteLostItemComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {

        // Fetch the authenticated member
        Member member = memberService.getMemberByPrincipal(userDetails);

        // Pass the commentId and authenticated member to the service
        itemService.deleteLostItemComment(commentId, member);

        return ResponseEntity.ok("Comment deleted successfully.");
    }


    @Operation(summary = "습득물 댓글 등록", description = "습득물에 댓글을 등록합니다.")
    @PostMapping("/found/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FoundItemCommentResponse> registerFoundItemComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "습득물 댓글 정보") @RequestBody FoundItemCommentRequest request) {

        // Fetch the authenticated member using userDetails
        Member member = memberService.getMemberByPrincipal(userDetails);

        // Pass the authenticated member along with the request to the service
        FoundItemCommentResponse response = itemService.registerFoundItemComment(request, member);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "습득물 댓글 수정", description = "습득물 댓글을 수정합니다.")
    @PutMapping("/found/comment/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FoundItemCommentResponse> updateFoundItemComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Parameter(description = "수정할 댓글 정보") @RequestBody FoundItemCommentRequest request) {

        // Fetch the authenticated member using userDetails
        Member member = memberService.getMemberByPrincipal(userDetails);

        // Pass the member to the service for ownership validation
        FoundItemCommentResponse response = itemService.updateFoundItemComment(commentId, request, member);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "습득물 댓글 삭제", description = "습득물 댓글을 삭제합니다.")
    @DeleteMapping("/found/comment/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteFoundItemComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {

        // Fetch the authenticated member using userDetails
        Member member = memberService.getMemberByPrincipal(userDetails);

        // Pass the member to the service for ownership validation
        itemService.deleteFoundItemComment(commentId, member);

        return ResponseEntity.ok("Found item comment deleted successfully.");
    }


    @Operation(summary = "분실물 상세 조회", description = "특정 분실물의 상세 정보를 댓글과 함께 조회합니다.")
    @GetMapping("/lost/{lostItemId}")
    public ResponseEntity<LostItemResponse> getLostItemDetails(
            @Parameter(description = "분실물 ID") @PathVariable Long lostItemId) {
        LostItemResponse response = itemService.getLostItemDetails(lostItemId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "습득물 상세 조회", description = "특정 습득물의 상세 정보를 댓글과 함께 조회합니다.")
    @GetMapping("/found/{foundItemId}")
    public ResponseEntity<FoundItemResponse> getFoundItemDetails(
            @Parameter(description = "습득물 ID") @PathVariable Long foundItemId) {
        FoundItemResponse response = itemService.getFoundItemDetails(foundItemId);
        return ResponseEntity.ok(response);
    }

}
