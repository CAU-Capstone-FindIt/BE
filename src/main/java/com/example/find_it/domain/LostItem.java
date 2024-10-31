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

    private String description;  // Item description (e.g., "아이폰 미니 13")

    private String name;         // Name of the item (e.g., "아이폰 미니 13")
    private String category;     // Category of the item (e.g., "전자기기")
    private String color;        // Color of the item (e.g., "초록색")
    private String brand;        // Brand of the item (e.g., "Apple")

    private LocalDate lostDate;  // Date the item was lost

    @OneToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;

    @Enumerated(EnumType.STRING)
    private LostItemStatus status;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
