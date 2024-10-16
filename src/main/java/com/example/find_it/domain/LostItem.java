package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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

    private LocalDate lostDate;

    @OneToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;

    @Enumerated(EnumType.STRING)
    private LostItemStatus status;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
