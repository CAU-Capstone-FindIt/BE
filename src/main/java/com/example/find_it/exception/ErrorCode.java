package com.example.find_it.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // OAuth
    KAKAO_FETCH_ACCESS_TOKEN_FAIL(HttpStatus.UNAUTHORIZED, "oauth-001", "카카오 엑세스 토큰을 획득하는 데에 실패하였습니다."),
    KAKAO_FETCH_USER_DATA_FAIL(HttpStatus.UNAUTHORIZED, "oauth-002", "카카오 유저 정보를 획득하는 데에 실패하였습니다."),

    // DB
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "db-001", "DB에서 유저를 찾을 수 없습니다."),

    // ItemService 관련 에러
    LOST_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "item-001", "해당 분실물을 찾을 수 없습니다."),
    FOUND_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "item-002", "해당 습득물을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "item-003", "댓글을 찾을 수 없습니다."),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "item-004", "권한이 없습니다."),
    INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "item-005", "보상을 설정하기 위한 포인트가 부족합니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "item-006", "부모 댓글을 찾을 수 없습니다."),
    INVALID_LOST_ITEM_STATUS(HttpStatus.BAD_REQUEST, "item-007", "분실물 상태를 REGISTERED에서 RETURNED로만 변경할 수 있습니다."),
    INVALID_REWARD_STATUS(HttpStatus.BAD_REQUEST, "item-008", "보상을 지급할 수 없는 상태입니다."),
    INVALID_POINT_AMOUNT(HttpStatus.BAD_REQUEST, "point-001", "충전 금액은 1 이상이어야 합니다."),

    DUMMY_ERROR_CODE(HttpStatus.OK, "DUMMY", "DUMMY");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

