package com.example.find_it.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LostItemCommentRequest {
    private Long lostItemId;       // 해당 LostItem ID
    private String content;        // 댓글 내용

    private Long parentCommentId;  // 부모 댓글 ID (대댓글일 경우 설정)
}
