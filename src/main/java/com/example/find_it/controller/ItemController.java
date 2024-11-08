package com.example.find_it.controller;

import com.example.find_it.dto.Request.FoundItemRequest;
import com.example.find_it.dto.Request.LostItemRequest;
import com.example.find_it.dto.Response.FoundItemResponse;
import com.example.find_it.dto.Response.LostItemResponse;
import com.example.find_it.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@Tag(name = "분실물/습득물 관리 API", description = "분실물과 습득물 관련 API")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Operation(summary = "분실물 등록", description = "새로운 분실물을 등록합니다.")
    @PostMapping("/lost/register")
    public ResponseEntity<String> registerLostItem(
            @Parameter(description = "분실물 정보") @RequestBody LostItemRequest lostItemRequest) {
        itemService.registerLostItem(lostItemRequest);
        return ResponseEntity.ok("Lost item registered successfully.");
    }

    @Operation(summary = "습득물 신고", description = "새로운 습득물을 신고합니다.")
    @PostMapping("/found/report")
    public ResponseEntity<String> reportFoundItem(
            @Parameter(description = "습득물 정보") @RequestBody FoundItemRequest foundItemRequest) {
        itemService.reportFoundItem(foundItemRequest);
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
}
