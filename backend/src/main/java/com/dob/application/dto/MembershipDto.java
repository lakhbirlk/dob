package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Schema(description = "Membership subscription details")
public record MembershipDto(
    @Schema(description = "Membership UUID")
    UUID id,

    @Schema(description = "User UUID")
    UUID userId,

    @Schema(description = "Plan type", example = "MONTHLY")
    String planType,

    @Schema(description = "Membership status", example = "ACTIVE", allowableValues = {"ACTIVE", "EXPIRED", "CANCELLED", "REFUNDED"})
    String status,

    @Schema(description = "Subscription start date", example = "2026-06-01")
    LocalDate startDate,

    @Schema(description = "Subscription end date", example = "2026-07-01")
    LocalDate endDate,

    @Schema(description = "Monthly download quota", example = "50")
    int downloadLimit,

    @Schema(description = "Downloads consumed this period", example = "3")
    int downloadsUsed,

    @Schema(description = "Creation timestamp")
    Instant createdAt
) {}
