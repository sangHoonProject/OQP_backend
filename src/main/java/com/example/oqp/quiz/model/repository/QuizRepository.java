package com.example.oqp.quiz.model.repository;

import com.example.oqp.quiz.model.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {

    void deleteByContentId(Long contentId);
}
