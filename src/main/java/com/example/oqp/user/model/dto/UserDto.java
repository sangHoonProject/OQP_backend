package com.example.oqp.user.model.dto;

import com.example.oqp.common.enums.Role;
import com.example.oqp.content.model.entity.ContentEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private List<ContentEntity> content;

}
