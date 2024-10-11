package com.example.oqp.content.model.entity;

import com.example.oqp.user.model.entity.UserEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conetnt")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "title")
    @Schema(description = "제목", example = "con")
    private String title;

    @Column(nullable = false, name = "front_image")
    @Schema(description = "썸네일 url")
    private String frontImage;

    @Column(nullable = false, name = "writer")
    @Schema(description = "작성자")
    private String writer;

    @Column(nullable = false, name = "create_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createAt;

    @Column(nullable = false, name = "category")
    @Schema(description = "카테고리")
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;

}
