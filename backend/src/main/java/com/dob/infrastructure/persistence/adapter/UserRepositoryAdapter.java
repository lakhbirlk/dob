package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.User;
import com.dob.domain.repository.UserRepository;
import com.dob.infrastructure.persistence.entity.UserEntity;
import com.dob.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpa;

    @Override
    public Optional<User> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findByPan(String pan) {
        return jpa.findByPan(pan).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = toEntity(user);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean existsByPan(String pan) {
        return jpa.existsByPan(pan);
    }

    private User toDomain(UserEntity e) {
        return User.builder()
            .id(e.getId())
            .email(e.getEmail())
            .passwordHash(e.getPasswordHash())
            .pan(e.getPan())
            .fullName(e.getFullName())
            .phone(e.getPhone())
            .role(User.UserRole.valueOf(e.getRole().name()))
            .emailVerified(e.isEmailVerified())
            .active(e.isActive())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    private UserEntity toEntity(User u) {
        return UserEntity.builder()
            .id(u.getId())
            .email(u.getEmail())
            .passwordHash(u.getPasswordHash())
            .pan(u.getPan())
            .fullName(u.getFullName())
            .phone(u.getPhone())
            .role(UserEntity.UserRole.valueOf(u.getRole().name()))
            .emailVerified(u.isEmailVerified())
            .active(u.isActive())
            .createdAt(u.getCreatedAt())
            .updatedAt(u.getUpdatedAt())
            .build();
    }
}
