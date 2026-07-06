package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPan(String pan);
    boolean existsByEmail(String email);
    boolean existsByPan(String pan);
    List<UserEntity> findByRole(UserEntity.UserRole role, Pageable pageable);
    long countByRole(UserEntity.UserRole role);
}
