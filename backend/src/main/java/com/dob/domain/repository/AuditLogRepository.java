package com.dob.domain.repository;

import com.dob.domain.model.AuditLog;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findAll(int page, int size);
    long count();
    List<AuditLog> findByUserId(UUID userId, int page, int size);
    long countByUserId(UUID userId);
    List<AuditLog> findByUserIdWithFilters(UUID userId, String action, Instant createdAfter, Instant createdBefore, String search, int page, int size);
    long countByUserIdWithFilters(UUID userId, String action, Instant createdAfter, Instant createdBefore, String search);
}
