package com.example.find_it.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FoundItemDTO {
    private Long userId;
    private String description;
    private LocalDate foundDate;
    private Double latitude;
    private Double longitude;
    private String address;
    private String photo; // Base64 인코딩된 이미지 또는 파일 경로

}
