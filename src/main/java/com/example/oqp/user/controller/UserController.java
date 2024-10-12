package com.example.oqp.user.controller;

import com.example.oqp.common.security.jwt.JwtAccessResponse;
import com.example.oqp.common.security.jwt.JwtLoginResponse;
import com.example.oqp.user.controller.reqeust.LoginRequest;
import com.example.oqp.user.controller.reqeust.RegisterRequest;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 회원가입", description = "registerRequest 를 사용해서 사용자 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공시 200"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패시 400")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        UserEntity register = userService.register(registerRequest);
        return (register != null) ? ResponseEntity.ok().body(register) : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "사용자 로그인", description = "LoginRequest 를 사용해서 사용자 로그인 후 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공시 200 반환"),
            @ApiResponse(responseCode = "400", description = "로그인 실패시 400 반환"),
            @ApiResponse(responseCode = "409", description = "id or nickname 이미 가입된 데이터로 가입 요청시 반환")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        JwtLoginResponse login = userService.login(request);

        response.setHeader("Authorization", login.getAccessToken());
        response.setHeader("Refresh-Token", login.getRefreshToken());

        return (login != null) ? ResponseEntity.ok().body(login) : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "access token 재발급",
            description = "refresh token 을 사용해서 access token, refresh token 재발급 기존 refresh token은 만료" +
                    "정석은 jwt토큰을 DB에 저장해야함"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공 시 200 반환"),
            @ApiResponse(responseCode = "408", description = "토큰 만료 또는 토큰이 존재하지 않을 시 408 반환")
    })
    @PostMapping("/refresh")
    public ResponseEntity<JwtAccessResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        JwtAccessResponse accessToken = userService.refresh(request);

        response.setHeader("Authorization", accessToken.getAccessToken());
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }
}
