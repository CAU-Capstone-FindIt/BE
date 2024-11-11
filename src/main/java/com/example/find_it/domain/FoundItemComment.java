package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
public class FoundItemComment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "found_item_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "found_item_id")  // FoundItem과의 연관 관계
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FoundItem foundItem;

    @Column(columnDefinition = "TEXT")
    private String content;
}
