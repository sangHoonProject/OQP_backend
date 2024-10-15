package com.example.oqp.content.model.entity;

import com.example.oqp.quiz.model.entity.QuizEntity;
import com.example.oqp.user.model.entity.UserEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content")
@Builder
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

    @Column(name = "rating")
    @Schema(description = "콘텐츠 별점")
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @OneToMany(mappedBy = "content")
    private List<QuizEntity> quizList;

}
