package com.example.oqp.user.service;

import com.example.oqp.conmmon.converter.Converter;
import com.example.oqp.user.controller.reqeust.RegisterRequest;
import com.example.oqp.user.model.entity.UserEntity;
import com.example.oqp.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Converter converter;

    public UserEntity register(RegisterRequest registerRequest) {
        if(userRepository.existsByUserId(registerRequest.getUserId())){
            throw new RuntimeException("사용자 id가 이미 가입되었습니다.");
        }
        if(userRepository.existsByNickname(registerRequest.getNickname())){
            throw new RuntimeException("사용자 닉네임이 이미 가입되었습니다.");
        }

        UserEntity user = converter.toUserEntity(registerRequest);
        return userRepository.save(user);
    }
}
