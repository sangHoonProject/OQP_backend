package com.example.oqp.common.security.jwt;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtLoginResponse {
    private String accessToken;
    private String refreshToken;
    private String nickname;
}
