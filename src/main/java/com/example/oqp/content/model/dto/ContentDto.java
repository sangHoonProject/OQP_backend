package com.example.oqp.content.model.dto;

import com.example.oqp.quiz.model.dto.QuizDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContentDto {
    private Long id;

    private String title;

    private String frontImage;

    private String writer;

    private LocalDateTime createAt;

    private String category;

    private Integer rating;

    private Long userId;

    private List<QuizDto> quiz;
}
