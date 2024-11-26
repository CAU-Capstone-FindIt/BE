package com.example.find_it.dto.Request;

import com.example.find_it.domain.Category;
import com.example.find_it.domain.LostItemStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LostItemRequest {
    private String name;
    private String description;
    private Category category;
    private String color;
    private String brand;
    private LocalDate reportDate;
    private Double latitude;
    private Double longitude;
    private String address;
    private Integer rewardAmount;  // Changed from rewardId to rewardAmount
    private LostItemStatus status;

    private MultipartFile image;
}


