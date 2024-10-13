package com.example.oqp.content.model.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomContentRepository {

    List<Object> searchByKeyword(String keyword);
}
