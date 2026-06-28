package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Grievance/complaint details")
public record GrievanceDto(
    @Schema(description = "Grievance UUID")
    UUID id,

    @Schema(description = "User UUID who filed the grievance")
    UUID userId,

    @Schema(description = "Name of the user who filed")
    String userName,

    @Schema(description = "Type of complaint", example = "Data Discrepancy")
    String complaintType,

    @Schema(description = "Detailed description of the issue")
    String description,

    @Schema(description = "Current status", example = "OPEN", allowableValues = {"OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"})
    String status,

    @Schema(description = "Priority level", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"})
    String priority,

    @Schema(description = "Admin UUID assigned to this grievance")
    UUID assignedTo,

    @Schema(description = "Name of the assigned admin")
    String assignedToName,

    @Schema(description = "Resolution notes")
    String resolution,

    @Schema(description = "When the grievance was resolved")
    Instant resolvedAt,

    @Schema(description = "Creation timestamp")
    Instant createdAt,

    @Schema(description = "Last update timestamp")
    Instant updatedAt
) {}
