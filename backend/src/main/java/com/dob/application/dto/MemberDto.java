package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Member details for admin view, including subscription info")
public record MemberDto(

    @Schema(description = "User UUID")
    UUID id,

    @Schema(description = "Full name", example = "Rahul Sharma")
    String fullName,

    @Schema(description = "Email address", example = "member@example.com")
    String email,

    @Schema(description = "Phone number", example = "9876543211")
    String phone,

    @Schema(description = "PAN card number", example = "ABCDE1234F")
    String pan,

    @Schema(description = "Account active")
    boolean active,

    @Schema(description = "Email verified")
    boolean emailVerified,

    @Schema(description = "Account creation date")
    Instant createdAt,

    // --- Subscription (nullable) ---
    @Schema(description = "Membership UUID, null if no subscription")
    UUID membershipId,

    @Schema(description = "Plan type", example = "MONTHLY")
    String planType,

    @Schema(description = "Subscription status", example = "ACTIVE")
    String membershipStatus,

    @Schema(description = "Subscription start date")
    LocalDate membershipStartDate,

    @Schema(description = "Subscription end date")
    LocalDate membershipEndDate,

    @Schema(description = "Monthly download limit", example = "50")
    int downloadLimit,

    @Schema(description = "Downloads used this period", example = "3")
    int downloadsUsed
) {}
