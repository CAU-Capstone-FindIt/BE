package com.example.find_it.repository;

import com.example.find_it.domain.FoundItemComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoundItemCommentRepository extends JpaRepository<FoundItemComment, Long> {
}
