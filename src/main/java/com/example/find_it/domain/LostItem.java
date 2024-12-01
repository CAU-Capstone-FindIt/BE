package com.example.find_it.domain;

import com.example.find_it.dto.PersonalMessage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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
    @JoinColumn(name = "member_id")
    private Member member;

    private String description;
    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;  // Category Enum으로 수정

    private String color;
    private String brand;

    private LocalDate reportDate;

    @OneToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;

    @Enumerated(EnumType.STRING)
    private LostItemStatus status;

    // Location 필드 통합
    private Double latitude;  // 위도
    private Double longitude; // 경도
    private String address;   // 주소

    private String revisedName;
    private String revisedBrand;
    private String revisedColor;
    private String revisedAddress;

    @OneToMany(mappedBy = "lostItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LostItemComment> comments = new ArrayList<>();

    private String image;

    @OneToMany(mappedBy = "lostItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonalMessage> messages = new ArrayList<>();
}
