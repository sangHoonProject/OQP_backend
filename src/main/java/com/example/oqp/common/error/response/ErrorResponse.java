package com.example.oqp.common.error.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String message;
}
