package com.example.oqp.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_FOUND_REFRESH_TOKEN(408, "refresh token이 없습니다."),
    EXPIRED_REFRESH_TOKEN(408, "refresh token 만료"),

    ALREADY_SAVE_ID(409, "이미 사용된 id"),
    ALREADY_SAVE_NICKNAME(409, "이미 사용된 nickname");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
