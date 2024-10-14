package com.example.oqp.user.model.entity;

import com.example.oqp.common.enums.Role;
import com.example.oqp.content.model.entity.ContentEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User 고유키")
    private Long id;

    @Column(unique = true, nullable = false, name = "user_id")
    @Schema(description = "로그인에 사용할 id", example = "lwer1211")
    private String userId;

    @Column(unique = true, nullable = false, name = "nickname")
    @Schema(description = "user 닉네임", example = "lasdf")
    private String nickname;

    @Column(unique = true, nullable = false, name = "password")
    @Schema(description = "사용자 비밀번호", example = "askdfwk")
    private String password;

    @Column(name = "register_at")
    @Schema(description = "사용자 가입 날짜, 시간")
    private LocalDateTime registerAt;

    @Column(unique = true, nullable = false, name = "star")
    @Schema(description = "사용자 퀴즈 맞힌 수", example = "0")
    private Integer star;

    @Column(unique = true, nullable = false, name = "posting_count")
    @Schema(description = "퀴즈 추가한 갯수", example = "0")
    private Integer postingCount;

    @Column(nullable = false, name = "role")
    @Schema(description = "사용자 권한(admin의 의미가 없기때문에 ROLE_USER 만 사용)", example = "ROLE_USER")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, name = "name")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Column(nullable = false, name = "email")
    @Schema(description = "사용자 이메일", example = "hong@gmail.com")
    private String email;

    @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER) // 즉시 로딩으로 인한 성능 문제가 있을 수 있음
    @Schema(description = "퀴즈 콘텐츠")
    private List<ContentEntity> content;
}
