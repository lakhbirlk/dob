package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class AuditLog {
    private UUID id;
    private UUID userId;
    private String action;
    private UUID companyId;
    private String outcome;
    private String details;
    private String ipAddress;
    private Instant createdAt;
}
