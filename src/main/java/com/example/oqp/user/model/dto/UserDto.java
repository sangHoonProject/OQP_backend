package com.example.oqp.user.model.dto;

import com.example.oqp.common.enums.Role;
import com.example.oqp.content.model.dto.ContentDto;
import com.example.oqp.content.model.entity.ContentEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private Long id;

    private String userId;

    private String nickname;

    private String password;

    private LocalDateTime registerAt;

    private Integer star;

    private Integer postingCount;

    private Role role;

    private String name;

    private String email;

    private List<ContentDto> content;

}
