package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class LostItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lost_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;
    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;  // Category Enum으로 수정
    private String color;
    private String brand;

    private LocalDateTime lostDate;

    @OneToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;

    @Enumerated(EnumType.STRING)
    private LostItemStatus status;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "lostItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LostItemComment> comments = new ArrayList<>();
}
