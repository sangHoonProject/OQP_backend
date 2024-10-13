package com.example.oqp.content.model.repository;

import com.example.oqp.content.model.entity.QContentEntity;
import com.example.oqp.user.model.entity.QUserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomContentRepositoryImpl implements CustomContentRepository {

    private final JPAQueryFactory queryFactory;
    private QUserEntity qUserEntity = QUserEntity.userEntity;
    private QContentEntity qContentEntity = QContentEntity.contentEntity;

    @Override
    public List<Object> searchByKeyword(String keyword) {
        return List.of();
    }
}
