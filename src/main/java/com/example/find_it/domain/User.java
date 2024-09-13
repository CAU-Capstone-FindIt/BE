package com.example.find_it.domain;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class User {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;
}
