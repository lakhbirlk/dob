package com.dob.domain.repository;

import com.dob.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findAll(int page, int size);
    long count();
}
