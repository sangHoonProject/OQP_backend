package com.example.oqp.content.model.repository;

import com.example.oqp.content.model.entity.ContentEntity;
import com.example.oqp.content.model.entity.QContentEntity;
import com.example.oqp.user.model.entity.QUserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomContentRepositoryImpl implements CustomContentRepository {

    private final JPAQueryFactory queryFactory;
    private QUserEntity qUserEntity = QUserEntity.userEntity;
    private QContentEntity qContentEntity = QContentEntity.contentEntity;

    @Override
    public List<ContentEntity> searchByKeyword(String keyword) {

        return queryFactory.selectFrom(qContentEntity)
                .join(qContentEntity.userId, qUserEntity)
                .where(qContentEntity.title.contains(keyword).or(qUserEntity.nickname.contains(keyword)))
                .fetch();
    }
}
