package com.example.oqp.user.controller.reqeust;

import com.example.oqp.user.model.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserModifyRequest {
    private String userId;

    private String password;

    private String nickname;

    private String name;

    private String email;

    public static UserEntity patch(UserEntity user, UserModifyRequest request) {
        if(request.getUserId() != null) {
            user.setUserId(request.getUserId());
        }

        if(request.getEmail() != null) {
            user.setUserId(request.getUserId());
        }

        if(request.getName() != null) {
            user.setUserId(request.getUserId());
        }

        if(request.getPassword() != null) {
            user.setUserId(request.getUserId());
        }

        if(request.getNickname() != null) {
            user.setUserId(request.getUserId());
        }

        return user;
    }
}
