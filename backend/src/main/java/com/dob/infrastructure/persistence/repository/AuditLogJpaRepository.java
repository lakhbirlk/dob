package com.dob.infrastructure.persistence.repository;

import com.dob.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, UUID> {
    Page<AuditLogEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByUserId(UUID userId);

    @Query("SELECT a FROM AuditLogEntity a WHERE a.userId = :userId " +
           "AND (:action IS NULL OR a.action = :action) " +
           "AND (:createdAfter IS NULL OR a.createdAt >= :createdAfter) " +
           "AND (:createdBefore IS NULL OR a.createdAt <= :createdBefore) " +
           "AND (:search IS NULL OR LOWER(a.details) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "   OR LOWER(a.action) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLogEntity> findByUserIdWithFilters(
        @Param("userId") UUID userId,
        @Param("action") String action,
        @Param("createdAfter") Instant createdAfter,
        @Param("createdBefore") Instant createdBefore,
        @Param("search") String search,
        Pageable pageable
    );

    @Query("SELECT COUNT(a) FROM AuditLogEntity a WHERE a.userId = :userId " +
           "AND (:action IS NULL OR a.action = :action) " +
           "AND (:createdAfter IS NULL OR a.createdAt >= :createdAfter) " +
           "AND (:createdBefore IS NULL OR a.createdAt <= :createdBefore) " +
           "AND (:search IS NULL OR LOWER(a.details) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "   OR LOWER(a.action) LIKE LOWER(CONCAT('%', :search, '%')))")
    long countByUserIdWithFilters(
        @Param("userId") UUID userId,
        @Param("action") String action,
        @Param("createdAfter") Instant createdAfter,
        @Param("createdBefore") Instant createdBefore,
        @Param("search") String search
    );
}
