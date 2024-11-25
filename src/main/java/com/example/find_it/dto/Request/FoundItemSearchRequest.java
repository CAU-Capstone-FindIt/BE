package com.example.find_it.dto.Request;

import com.example.find_it.domain.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class FoundItemSearchRequest {
    private String name;                 // 습득물 이름
    private String brand;                // 브랜드
    private String color;                // 색상
    private Category category;           // 카테고리
    private String address;              // 주소
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;         // 검색 시작 날짜
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;           // 검색 종료 날짜
}