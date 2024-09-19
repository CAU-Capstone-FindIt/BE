package com.example.find_it.domain;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
public class LostItem extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "lost_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;

    @OneToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;

    @Enumerated
    private Status status;

}
