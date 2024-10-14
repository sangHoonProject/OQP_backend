package com.example.oqp.content.model.repository;

import com.example.oqp.content.model.entity.ContentEntity;

import java.util.List;

public interface CustomContentRepository {

    List<ContentEntity> searchByKeyword(String keyword);
}
