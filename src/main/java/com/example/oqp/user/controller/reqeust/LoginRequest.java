package com.example.oqp.user.controller.reqeust;

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
public class LoginRequest {

    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{4,19}$")
    private String userId;

    private String password;
}
