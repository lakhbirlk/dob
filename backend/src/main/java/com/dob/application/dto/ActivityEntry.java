package com.dob.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Standardized activity entry for the research member activity tracker.
 * Maps from the raw AuditLog to a display-friendly format.
 */
@Getter @Builder
public class ActivityEntry {
    private UUID id;
    private String activityType;
    private String category;
    private String description;
    private UUID companyId;
    private String companyName;
    private Integer creditsUsed;
    private String status;
    private String ipAddress;
    private String device;
    private String transactionId;
    private Instant timestamp;
}
