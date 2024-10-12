package com.example.oqp.user.service;

import com.example.oqp.common.enums.Role;
import com.example.oqp.common.error.CustomException;
import com.example.oqp.common.error.ErrorCode;
import com.example.oqp.common.security.custom.CustomUserDetails;
import com.example.oqp.common.security.custom.CustomUserDetailsService;
import com.example.oqp.common.security.jwt.JwtAccessResponse;
import com.example.oqp.common.security.jwt.JwtLoginResponse;
import com.example.oqp.common.security.jwt.JwtUtil;
import com.example.oqp.user.controller.reqeust.LoginRequest;
import com.example.oqp.user.controller.reqeust.RegisterRequest;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.model.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    public UserEntity register(RegisterRequest registerRequest) {

        if(userRepository.existsByUserId(registerRequest.getUserId())){
            throw new CustomException(ErrorCode.ALREADY_SAVE_ID);
        }
        if(userRepository.existsByNickname(registerRequest.getNickname())){
            throw new CustomException(ErrorCode.ALREADY_SAVE_NICKNAME);
        }

        UserEntity user = toUserEntity(registerRequest);
        return userRepository.save(user);
    }

    public JwtLoginResponse login(LoginRequest request) {
        UserEntity byUserId = userRepository.findByUserId(request.getUserId());

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword()));
        String accessToken = jwtUtil.generateAccessToken(authenticate);
        String refreshToken = jwtUtil.generateRefreshToken(authenticate);

        return JwtLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(byUserId.getNickname())
                .build();
    }


    public JwtAccessResponse refresh(HttpServletRequest request) {
        String token = request.getHeader("Refresh-Token");
        log.info("token : {}", token);

        if (token == null) {
            log.error("Refresh Token이 제공되지 않았습니다.");
            throw new CustomException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            log.info("claim : {}", claims.toString());

            Date exp = new Date(((Integer) claims.get("exp")).longValue() * 1000);
            if (exp.after(new Date())) {
                Long id = claims.get("id", Long.class);
                UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));

                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getUserId());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                String accessToken = jwtUtil.generateAccessToken(authentication);
                String refreshToken = jwtUtil.generateRefreshToken(authentication);

                return JwtAccessResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            } else {
                log.error("Refresh Token 만료");
                throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
            }
        } catch (Exception e) {
            log.error("UserService refresh Fail : {}", e.getMessage());
            throw new RuntimeException("UserService refresh Fail", e);
        }
    }


    public UserEntity toUserEntity(RegisterRequest request){
        return UserEntity.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userId(request.getUserId())
                .star(0)
                .postingCount(0)
                .role(Role.ROLE_USER)
                .registerAt(LocalDateTime.now())
                .build();
    }

}
