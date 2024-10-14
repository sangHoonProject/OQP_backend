package com.example.oqp.user.controller;

import com.example.oqp.common.error.response.ErrorResponse;
import com.example.oqp.common.security.jwt.JwtAccessResponse;
import com.example.oqp.common.security.jwt.JwtLoginResponse;
import com.example.oqp.user.controller.reqeust.*;
import com.example.oqp.user.model.dto.UserDto;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @ApiResponse(responseCode = "200", description = "회원가입 성공시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "411", description = "이미 가입된 user id로 가입 요청 시 411 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "412", description = "이미 가입된 nickname으로 가입 요청시 412 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        UserEntity register = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.OK).body(register);
    }

    @Operation(summary = "사용자 로그인", description = "LoginRequest 를 사용해서 사용자 로그인 후 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JwtLoginResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "로그인 실패시 400 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        JwtLoginResponse login = userService.login(request);

        response.setHeader("Authorization", login.getAccessToken());
        response.setHeader("Refresh-Token", login.getRefreshToken());

        return (login != null) ? ResponseEntity.ok().body(login) : ResponseEntity.badRequest().body("로그인 실패");
    }

    @Operation(summary = "access token 재발급",
            description = "refresh token 을 사용해서 access token, refresh token 재발급 기존 refresh token은 만료" +
                    "정석은 jwt토큰을 DB에 저장해야함",
            security = {@SecurityRequirement(name = "Refresh-Token")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공 시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAccessResponse.class))
            }),
            @ApiResponse(responseCode = "409", description = "토큰이 없을경우 409 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "410", description = "refresh 토큰 만료시 410 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PostMapping("/refresh")
    public ResponseEntity<JwtAccessResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        JwtAccessResponse accessToken = userService.refresh(request);

        response.setHeader("Authorization", accessToken.getAccessToken());
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    @Operation(summary = "사용자 찾기", description = "nickname을 이용해서 사용자를 조회")
    @Parameter(name = "nickname", description = "사용자 닉네임")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공시 200반환", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto  .class))
            ),
            @ApiResponse(responseCode = "407", description = "사용자를 찾지 못하면 407반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @GetMapping("/search/{nickname}")
    public ResponseEntity<?> found(@PathVariable String nickname){
        UserDto found = userService.found(nickname);
        return ResponseEntity.ok().body(found);
    }

    @Operation(summary = "사용자 계정 삭제", security = @SecurityRequirement(name = "Authorization"))
    @Parameter(name = "id", description = "사용자 고유키(id)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제가 완료되면 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "407", description = "사용자를 찾을 수 없으면 407 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "408", description = "삭제하려는 사용자와 다른 사용자면 408 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        boolean delete = userService.delete(id, request);
        return ResponseEntity.ok().body("삭제 성공");
    }

    @Operation(summary = "사용자 계정 정보 수정", description = "해더에 있는 토큰으로 같은 사용자인지 검증하고 맞다면 정보를 수정함 수정하지 않는 정보는 null로 요청하면 됌",
            security = @SecurityRequirement(name = "Authorization")
    )
    @Parameter(name = "id", description = "사용자 계정 고유키")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "408", description = "사용자가 같지 않을 시 408 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "409", description = "토큰이 없을 시 409 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PatchMapping("/modify/{id}")
    public ResponseEntity<?> modify(@PathVariable Long id, @RequestBody UserModifyRequest modifyRequest, HttpServletRequest request){
        UserEntity modify = userService.modify(id, modifyRequest, request);

        return ResponseEntity.ok().body(modify);
    }

    @Operation(summary = "임시 비밀번호 발급", description = "비밀번호를 분실했을 경우 가입할때 사용한 이메일로 임시 비밀번호를 발급해줌")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공시 200 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "407", description = "사용자를 찾지 못할 경우 407 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmail(@RequestBody FindByPasswordRequest request){

        userService.sendEmail(request);

        return ResponseEntity.ok().body("이메일이 전송되었습니다.");
    }
}
