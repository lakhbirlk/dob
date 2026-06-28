package com.dob.domain.repository;

import com.dob.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPan(String pan);
    User save(User user);
    boolean existsByEmail(String email);
    boolean existsByPan(String pan);
}
