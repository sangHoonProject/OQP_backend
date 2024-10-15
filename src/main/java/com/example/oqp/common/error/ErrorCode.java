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
    USER_VALIDATION(413, "email 형식 또는 user_id 첫글자는 영문 10자 이상 50자 미만으로 만드세요");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
