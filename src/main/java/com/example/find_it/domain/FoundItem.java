package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class FoundItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "found_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;
    private LocalDate foundDate;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private String photo;

    @Enumerated(EnumType.STRING)
    private Category category;  // Category Enum으로 수정
    private String color;
    private String brand;
}
