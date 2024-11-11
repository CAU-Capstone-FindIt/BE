package com.example.find_it.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoundItemCommentRequest {
    private Long userId;      // 작성자 ID
    private Long foundItemId; // 해당 FoundItem ID
    private String content;   // 댓글 내용
}
