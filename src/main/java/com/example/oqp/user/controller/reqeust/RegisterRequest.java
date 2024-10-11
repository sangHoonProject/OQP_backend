package com.example.oqp.user.controller.reqeust;

import com.example.oqp.user.model.entity.UserEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterRequest {

    @NotBlank
    @Size(min = 10, max = 50)
    private String userId;

    @NotBlank
    @Max(45)
    private String nickname;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

}
