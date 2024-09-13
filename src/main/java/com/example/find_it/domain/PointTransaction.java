package com.example.find_it.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "point_transaction_id")
    private Long id;
}
