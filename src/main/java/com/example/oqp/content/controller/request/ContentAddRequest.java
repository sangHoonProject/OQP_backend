package com.example.oqp.content.controller.request;

import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.quiz.controller.request.QuizAddRequest;
import com.example.oqp.quiz.model.entity.QuizEntity;
import com.example.oqp.user.model.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentAddRequest {
    private String title;

    private String category;

    private List<QuizAddRequest> quizAddRequests;

    public static ContentEntity toEntity(ContentAddRequest contentAddRequest, String path, UserEntity user, List<QuizEntity> quiz) {
        return ContentEntity.builder()
                .title(contentAddRequest.getTitle())
                .frontImage(path)
                .writer(user.getNickname())
                .category(contentAddRequest.getCategory())
                .createAt(LocalDateTime.now())
                .rating(0)
                .userId(user)
                .quizList(quiz)
                .build();
    }
}
