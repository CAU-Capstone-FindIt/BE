package com.example.find_it.dto.Response;

import com.example.find_it.domain.Category;
import com.example.find_it.domain.LostItemStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FoundItemResponse {
    private Long id;                      // FoundItem의 ID
    private Long userId;                  // 작성자 ID
    private String name;
    private String description;           // 설명
    private LocalDate reportDate;          // 발견된 날짜
    private Double latitude;              // 위치 위도
    private Double longitude;             // 위치 경도
    private String address;               // 위치 주소
    private String image;                 // 사진 URL

    // Additional fields
    private Category category;            // 카테고리
    private String color;                 // 색상
    private String brand;                 // 브랜드
    private LostItemStatus status;    // 분실물 상태 (예: 'Lost', 'Found')

    // 생성 및 수정 시간
    private LocalDateTime createdDate;    // 생성 시간
    private LocalDateTime modifiedDate;   // 수정 시간

    private List<FoundItemCommentResponse> comments;
}
