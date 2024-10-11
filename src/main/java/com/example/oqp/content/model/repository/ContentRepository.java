package com.example.oqp.content.model.repository;

import com.example.oqp.content.model.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
}
