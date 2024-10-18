package com.example.oqp.quiz.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuizDto {

    private Long id;

    private String problem;

    private String image;

    private String correct;

    private LocalDateTime createAt;

    private Long contentId;

}
