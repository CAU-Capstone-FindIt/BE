package com.example.find_it.controller;

import com.example.find_it.domain.FoundItem;
import com.example.find_it.domain.LostItem;
import com.example.find_it.dto.FoundItemDTO;
import com.example.find_it.dto.LostItemDTO;
import com.example.find_it.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@Tag(name = "분실물/습득물 관리 API", description = "분실물과 습득물 관련 API")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Operation(summary = "분실물 등록", description = "새로운 분실물을 등록합니다.")
    @PostMapping("/lost/register")
    public ResponseEntity<String> registerLostItem(
            @Parameter(description = "분실물 정보") @RequestBody LostItemDTO lostItemDTO) {
        itemService.registerLostItem(lostItemDTO);
        return ResponseEntity.ok("Lost item registered successfully.");
    }

    @Operation(summary = "습득물 신고", description = "새로운 습득물을 신고합니다.")
    @PostMapping("/found/report")
    public ResponseEntity<String> reportFoundItem(
            @Parameter(description = "습득물 정보") @ModelAttribute FoundItemDTO foundItemDTO) {
        itemService.reportFoundItem(foundItemDTO);
        return ResponseEntity.ok("Found item reported successfully.");
    }

    @Operation(summary = "분실물 검색", description = "설명을 기반으로 분실물을 검색합니다.")
    @GetMapping("/lost/search")
    public ResponseEntity<List<LostItem>> searchLostItems(
            @Parameter(description = "검색할 설명") @RequestParam(required = false) String description) {
        List<LostItem> lostItems = itemService.searchLostItems(description);
        return ResponseEntity.ok(lostItems);
    }

    @Operation(summary = "습득물 검색", description = "설명을 기반으로 습득물을 검색합니다.")
    @GetMapping("/found/search")
    public ResponseEntity<List<FoundItem>> searchFoundItems(
            @Parameter(description = "검색할 설명") @RequestParam(required = false) String description) {
        List<FoundItem> foundItems = itemService.searchFoundItems(description);
        return ResponseEntity.ok(foundItems);
    }
}