package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.AuditLog;
import com.dob.domain.repository.AuditLogRepository;
import com.dob.infrastructure.persistence.entity.AuditLogEntity;
import com.dob.infrastructure.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpa;

    @Override
    public AuditLog save(AuditLog auditLog) {
        var entity = toEntity(auditLog);
        entity = jpa.save(entity);
        return toDomain(entity);
    }

    @Override
    public List<AuditLog> findAll(int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpa.findAll(pageable).stream().map(this::toDomain).toList();
    }

    @Override
    public long count() {
        return jpa.count();
    }

    @Override
    public List<AuditLog> findByUserId(UUID userId, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpa.findByUserIdOrderByCreatedAtDesc(userId, pageable).stream().map(this::toDomain).toList();
    }

    @Override
    public long countByUserId(UUID userId) {
        return jpa.countByUserId(userId);
    }

    @Override
    public List<AuditLog> findByUserIdWithFilters(UUID userId, String action, Instant createdAfter, Instant createdBefore, String search, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return jpa.findByUserIdWithFilters(userId, action, createdAfter, createdBefore, search, pageable)
            .stream().map(this::toDomain).toList();
    }

    @Override
    public long countByUserIdWithFilters(UUID userId, String action, Instant createdAfter, Instant createdBefore, String search) {
        return jpa.countByUserIdWithFilters(userId, action, createdAfter, createdBefore, search);
    }

    private AuditLog toDomain(AuditLogEntity e) {
        return AuditLog.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .action(e.getAction())
            .companyId(e.getCompanyId())
            .transactionId(e.getTransactionId())
            .outcome(e.getOutcome())
            .details(e.getDetails())
            .ipAddress(e.getIpAddress())
            .userAgent(e.getUserAgent())
            .createdAt(e.getCreatedAt())
            .build();
    }

    private AuditLogEntity toEntity(AuditLog a) {
        return AuditLogEntity.builder()
            .id(a.getId())
            .userId(a.getUserId())
            .action(a.getAction())
            .companyId(a.getCompanyId())
            .transactionId(a.getTransactionId())
            .outcome(a.getOutcome())
            .details(a.getDetails())
            .ipAddress(a.getIpAddress())
            .userAgent(a.getUserAgent())
            .createdAt(a.getCreatedAt())
            .build();
    }
}
