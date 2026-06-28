package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.GrievanceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrievanceJpaRepository extends JpaRepository<GrievanceEntity, UUID> {
    List<GrievanceEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<GrievanceEntity> findByStatus(GrievanceEntity.GrievanceStatus status, Pageable pageable);
    List<GrievanceEntity> findByAssignedTo(UUID adminId);
}
