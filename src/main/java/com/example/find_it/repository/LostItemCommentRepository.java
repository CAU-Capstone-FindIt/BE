package com.example.find_it.repository;

import com.example.find_it.domain.LostItemComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemCommentRepository extends JpaRepository<LostItemComment, Long> {
}
