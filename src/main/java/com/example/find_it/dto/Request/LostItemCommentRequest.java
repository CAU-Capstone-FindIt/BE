package com.example.find_it.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LostItemCommentRequest {
    private Long userId;      // 작성자 ID
    private Long lostItemId;  // 해당 LostItem ID
    private String content;   // 댓글 내용
}
