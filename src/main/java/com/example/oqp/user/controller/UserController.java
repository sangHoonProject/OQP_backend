package com.example.oqp.user.controller;

import com.example.oqp.user.controller.reqeust.RegisterRequest;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공시 200"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패시 400")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        UserEntity register = userService.register(registerRequest);
        return (register != null) ? ResponseEntity.ok().body(register) : ResponseEntity.badRequest().build();
    }
}
