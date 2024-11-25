package com.example.find_it.dto.Response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FoundItemCommentResponse {
    private Long id;          // 댓글 ID
    private Long userId;      // 작성자 ID
    private String nickname; // 작성자 닉네임 추가
    private Long foundItemId; // 해당 FoundItem ID
    private String content;   // 댓글 내용
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private List<FoundItemCommentResponse> childComments;
}
