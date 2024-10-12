package com.example.oqp.user.controller.reqeust;

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
}
