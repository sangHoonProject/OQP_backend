package com.example.oqp.quiz.model.entity;

import com.example.oqp.content.model.entity.ContentEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "quiz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Quiz 고유키")
    private Long id;

    @Column(name = "problem")
    @Schema(description = "문제")
    private String problem;

    @Column(name = "image")
    @Schema(description = "퀴즈 이미지 url")
    private String image;

    @Column(name = "correct")
    @Schema(description = "퀴즈 정답")
    private String correct;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private ContentEntity content;
}
