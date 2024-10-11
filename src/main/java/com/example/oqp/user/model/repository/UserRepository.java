package com.example.oqp.user.model.repository;

import com.example.oqp.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByNickname(String nickname);

    Boolean existsByUserId(String userId);

    UserEntity findByUserId(String userId);

    UserEntity findByName(String name);
}
