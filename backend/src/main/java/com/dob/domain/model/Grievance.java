package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class Grievance {
    private UUID id;
    private UUID userId;
    private String complaintType;
    private String description;
    private GrievanceStatus status;
    private GrievancePriority priority;
    private UUID assignedTo;
    private String resolution;
    private Instant resolvedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public void assignTo(UUID adminId) {
        this.assignedTo = adminId;
        this.status = GrievanceStatus.IN_PROGRESS;
    }

    public void resolve(String resolution) {
        this.resolution = resolution;
        this.status = GrievanceStatus.RESOLVED;
        this.resolvedAt = Instant.now();
    }

    public void close() {
        this.status = GrievanceStatus.CLOSED;
    }

    public enum GrievanceStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    public enum GrievancePriority {
        LOW, MEDIUM, HIGH, URGENT
    }
}
