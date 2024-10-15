package com.example.oqp.common.error;

import com.example.oqp.common.error.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        int errorCode = ErrorCode.USER_VALIDATION.getStatus();
        errorResponse.setMessage("이메일 형식 또는 유저 아이디 10자 이상 50 미만으로 만드세요");
        errorResponse.setStatus(errorCode);


        log.info("errorCode : {}", errorCode);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode));
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(CustomException e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(e.getErrorCode().getStatus())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }
}
