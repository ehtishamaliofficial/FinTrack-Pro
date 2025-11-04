package com.fintrackpro.infrastructure.adapter.output.persistence.repository;

import com.fintrackpro.domain.model.User;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmailOrUsername(String email, String username);
}
