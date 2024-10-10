package com.example.find_it.repository;

import com.example.find_it.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAuthId(String authId);
    boolean existsByAuthId(String authId);
}