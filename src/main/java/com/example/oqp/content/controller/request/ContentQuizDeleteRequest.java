package com.example.oqp.content.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ContentQuizDeleteRequest {
    @NotNull
    private Long contentId;

    private Long quizId;
}
