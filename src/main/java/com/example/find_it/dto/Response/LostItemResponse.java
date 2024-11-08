package com.example.find_it.dto.Response;

import com.example.find_it.domain.Category;
import com.example.find_it.domain.LostItemStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LostItemResponse {
    private Long id;                  // 분실물 ID
    private Long userId;              // 사용자 ID
    private String name;              // 분실물 이름 (예: "아이폰 미니 13")
    private Category category;        // 분실물 카테고리 (예: "전자기기")
    private String color;             // 분실물 색상 (예: "초록색")
    private String brand;             // 분실물 브랜드 (예: "Apple")
    private String description;       // 분실물에 대한 설명
    private LocalDate lostDate;       // 분실 날짜
    private String address;           // 위치 주소
    private Long rewardId;            // 보상 관련 정보 (보상 ID)
    private LostItemStatus status;    // 분실물 상태 (예: 'Lost', 'Found')
    private LocalDateTime createdDate;    // 생성일
    private LocalDateTime modifiedDate;   // 수정일
}

