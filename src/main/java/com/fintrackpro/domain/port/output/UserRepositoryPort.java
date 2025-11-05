package com.fintrackpro.domain.port.output;

import com.fintrackpro.domain.model.User;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    void deleteById(Long id);

    boolean existsByEmail(String email);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByEmailVerificationToken(String token);

}
