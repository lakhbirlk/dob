package com.dob.infrastructure.persistence.adapter;

import com.dob.domain.model.AuditLog;
import com.dob.domain.repository.AuditLogRepository;
import com.dob.infrastructure.persistence.entity.AuditLogEntity;
import com.dob.infrastructure.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

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

    private AuditLog toDomain(AuditLogEntity e) {
        return AuditLog.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .action(e.getAction())
            .companyId(e.getCompanyId())
            .outcome(e.getOutcome())
            .details(e.getDetails())
            .ipAddress(e.getIpAddress())
            .createdAt(e.getCreatedAt())
            .build();
    }

    private AuditLogEntity toEntity(AuditLog a) {
        return AuditLogEntity.builder()
            .id(a.getId())
            .userId(a.getUserId())
            .action(a.getAction())
            .companyId(a.getCompanyId())
            .outcome(a.getOutcome())
            .details(a.getDetails())
            .ipAddress(a.getIpAddress())
            .createdAt(a.getCreatedAt())
            .build();
    }
}
