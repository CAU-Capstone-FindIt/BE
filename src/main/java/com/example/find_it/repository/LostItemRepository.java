package com.example.find_it.repository;

import com.example.find_it.domain.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {
    // 분실물 설명을 포함하는 항목 검색
    List<LostItem> findByDescriptionContaining(String description);
}