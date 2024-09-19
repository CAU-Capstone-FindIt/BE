package com.example.find_it.controller;

import com.example.find_it.domain.FoundItem;
import com.example.find_it.domain.LostItem;
import com.example.find_it.dto.FoundItemDTO;
import com.example.find_it.dto.LostItemDTO;
import com.example.find_it.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // 분실물 등록
    @PostMapping("/lost/register")
    public ResponseEntity<String> registerLostItem(@RequestBody LostItemDTO lostItemDTO) {
        itemService.registerLostItem(lostItemDTO);
        return ResponseEntity.ok("Lost item registered successfully.");
    }

    // 습득물 신고
    @PostMapping("/found/report")
    public ResponseEntity<String> reportFoundItem(@ModelAttribute FoundItemDTO foundItemDTO) {
        itemService.reportFoundItem(foundItemDTO);
        return ResponseEntity.ok("Found item reported successfully.");
    }

    // 분실물 검색
    @GetMapping("/lost/search")
    public ResponseEntity<List<LostItem>> searchLostItems(@RequestParam(required = false) String description) {
        List<LostItem> lostItems = itemService.searchLostItems(description);
        return ResponseEntity.ok(lostItems);
    }

    // 습득물 검색
    @GetMapping("/found/search")
    public ResponseEntity<List<FoundItem>> searchFoundItems(@RequestParam(required = false) String description) {
        List<FoundItem> foundItems = itemService.searchFoundItems(description);
        return ResponseEntity.ok(foundItems);
    }
}

