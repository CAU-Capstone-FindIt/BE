package com.example.find_it.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class Leaderboard {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "leaderboard_id")
    private Long id;

}
