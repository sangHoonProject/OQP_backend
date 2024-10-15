package com.example.oqp.user.controller.reqeust;

import com.example.oqp.user.model.entity.UserEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserModifyRequest {

    private String userId;

    private String password;

    private String nickname;

    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    public static UserEntity patch(UserEntity user, UserModifyRequest request) {
        if(request.getUserId() != null) {
            user.setUserId(request.getUserId());
        }

        if(request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if(request.getName() != null) {
            user.setName(request.getName());
        }

        if(request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }

        if(request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        return user;
    }
}
