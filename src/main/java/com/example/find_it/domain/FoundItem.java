package com.example.find_it.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class FoundItem extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "found_item_id")
    private Long id;
}
