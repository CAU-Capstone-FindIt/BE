package com.example.find_it.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;
    private String email;
    private int points;
    private int rank;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LostItem> lostItems = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FoundItem> foundItems = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PointTransaction> pointTransactions = new ArrayList<>();

}
