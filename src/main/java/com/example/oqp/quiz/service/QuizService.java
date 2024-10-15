package com.example.oqp.quiz.service;

import com.example.oqp.quiz.model.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
}
