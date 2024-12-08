package com.example.find_it.dto.Request;

import com.example.find_it.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FoundItemRequest {
    private Long userId;
    private String description;
    private LocalDate foundDate;
    private Double latitude;
    private Double longitude;
    private String address;
    private String photo;

    // Additional fields to match the UI
    private Category category;
    private String color;
    private String brand;
}
