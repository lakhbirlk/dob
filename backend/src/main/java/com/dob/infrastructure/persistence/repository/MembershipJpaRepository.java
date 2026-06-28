package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.MembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipJpaRepository extends JpaRepository<MembershipEntity, UUID> {
    Optional<MembershipEntity> findByUserIdAndStatus(UUID userId, MembershipEntity.MembershipStatus status);
}
