package com.example.oqp.conmmon.converter;

import com.example.oqp.conmmon.enums.Role;
import com.example.oqp.user.controller.reqeust.RegisterRequest;
import com.example.oqp.user.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Converter {

    private final PasswordEncoder passwordEncoder;

    public UserEntity toUserEntity(RegisterRequest request){
        return UserEntity.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .userId(request.getUserId())
                .star(0)
                .postingCount(0)
                .role(Role.ROLE_USER)
                .registerAt(LocalDateTime.now())
                .build();
    }
}

