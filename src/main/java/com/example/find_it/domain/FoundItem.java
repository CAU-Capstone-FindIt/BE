package com.example.find_it.domain;

import com.example.find_it.domain.PersonalMessage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class FoundItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "found_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;

    private String description;
    private LocalDate reportDate;

    @Enumerated(EnumType.STRING)
    private FoundItemStatus status;

    // Location 필드 통합
    private Double latitude;  // 위도
    private Double longitude; // 경도
    private String address;   // 주소

    private String image;

    @Enumerated(EnumType.STRING)
    private Category category;  // Category Enum으로 수정

    private String color;
    private String brand;

    private String revisedName;
    private String revisedBrand;
    private String revisedColor;
    private String revisedAddress;

    @OneToMany(mappedBy = "foundItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoundItemComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "foundItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonalMessage> messages = new ArrayList<>();
}
