package com.example.find_it.dto.Response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LostItemCommentResponse {
    private Long id;                // 댓글 ID
    private Long userId;            // 작성자 ID
    private String nickname;
    private Long lostItemId;        // 해당 LostItem ID
    private String content;         // 댓글 내용
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // 대댓글(자식 댓글) 리스트 추가
    private List<LostItemCommentResponse> childComments;
}
