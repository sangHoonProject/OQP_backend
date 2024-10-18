package com.example.oqp.quiz.controller.request;

import com.example.oqp.quiz.model.entity.QuizEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QuizModifyRequest {

    private Long id;

    private String problem;

    private String correct;

    public static QuizEntity toEntity(QuizEntity quiz, QuizModifyRequest quizModifyRequest, String path) {
        if(quizModifyRequest == null){
            return null;
        }

        if(quizModifyRequest.getProblem() != null){
            quiz.setProblem(quizModifyRequest.getProblem());
        }

        if(quizModifyRequest.getCorrect() != null){
            quiz.setCorrect(quizModifyRequest.getCorrect());
        }

        if(path != null){
            quiz.setImage(path);
        }

        return quiz;
    }

    public static QuizEntity toEntity(QuizEntity quiz, QuizModifyRequest quizModifyRequest) {
        QuizEntity quizEntity = toEntity(quiz, quizModifyRequest, null);
        return quizEntity;
    }
}
