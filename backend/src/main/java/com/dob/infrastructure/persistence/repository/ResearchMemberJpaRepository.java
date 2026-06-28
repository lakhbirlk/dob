package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.ResearchMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResearchMemberJpaRepository extends JpaRepository<ResearchMemberEntity, UUID> {
    Optional<ResearchMemberEntity> findByUserId(UUID userId);
}
