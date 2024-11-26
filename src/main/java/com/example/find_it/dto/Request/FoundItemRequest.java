package com.example.find_it.dto.Request;

import com.example.find_it.domain.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class FoundItemRequest {
    private String name;
    private String description;
    private LocalDate reportDate;
    private Double latitude;
    private Double longitude;
    private String address;
    private MultipartFile image;

    // Additional fields to match the UI
    private Category category;
    private String color;
    private String brand;
}
