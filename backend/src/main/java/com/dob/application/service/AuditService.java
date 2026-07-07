package com.dob.application.service;

import com.dob.domain.model.AuditLog;
import com.dob.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Handles audit logging in a separate transaction so entries persist
 * regardless of failures in the calling service's transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId, UUID companyId, String action, String outcome, String details, String ipAddress) {
        log(userId, companyId, action, outcome, details, ipAddress, null, null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId, UUID companyId, String action, String outcome, String details, String ipAddress,
                    String userAgent, String transactionId) {
        try {
            auditLogRepository.save(AuditLog.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .action(action)
                .companyId(companyId)
                .transactionId(transactionId)
                .outcome(outcome)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(Instant.now())
                .build());
        } catch (Exception e) {
            log.error("Failed to persist audit log: {}", e.getMessage());
        }
    }
}
