package com.example.find_it.repository;

import com.example.find_it.domain.FoundItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {
    List<FoundItem> findByDescriptionContaining(String description);
}
