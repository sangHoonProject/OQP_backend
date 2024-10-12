package com.example.oqp.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(407, "사용자를 찾을 수 없음"),
    USER_NOT_SAME(408, "사용자가 같지 않습니다."),

    NOT_FOUND_REFRESH_TOKEN(408, "token이 없습니다."),
    EXPIRED_REFRESH_TOKEN(409, "refresh token 만료"),

    ALREADY_SAVE_ID(410, "이미 사용된 user id"),
    ALREADY_SAVE_NICKNAME(411, "이미 사용된 nickname");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
