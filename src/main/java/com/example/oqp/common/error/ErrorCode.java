package com.example.oqp.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(407, "사용자를 찾을 수 없음"),
    USER_NOT_SAME(408, "사용자가 같지 않습니다."),

    NOT_FOUND_TOKEN(409, "token이 없습니다."),
    EXPIRED_REFRESH_TOKEN(410, "refresh token 만료"),

    ALREADY_SAVE_ID(411, "이미 사용된 user id"),
    ALREADY_SAVE_NICKNAME(412, "이미 사용된 nickname"),
    USER_VALIDATION(413, "email 형식에 맞게 입력하세요"),

    CONTENT_NOT_FOUND(420, "Content를 찾지 못했습니다."),
    CONTENT_NOT_FOUND_IMAGE(421, "썸네일이 없습니다."),
    QUIZ_IMAGE_OVER(422, "Quiz 이미지가 Quiz 보다 많습니다."),
    QUIZ_NOT_FOUND(423, "Quiz를 찾을 수 없습니다."),

    DELETE_FAIL(430, "사용자 삭제에 실패하였습니다.");


    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
