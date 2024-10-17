package com.example.oqp.quiz.controller.request;

import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.quiz.model.entity.QuizEntity;
import lombok.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAddRequest {

    private String problem;

    private String correct;

    public static QuizEntity toQuizEntity(QuizAddRequest quizAddRequest, String path, ContentEntity contentEntity) {
        return QuizEntity.builder()
                .problem(quizAddRequest.getProblem())
                .image(path)
                .correct(quizAddRequest.getCorrect())
                .createAt(LocalDateTime.now())
                .content(contentEntity)
                .build();

    }
}
