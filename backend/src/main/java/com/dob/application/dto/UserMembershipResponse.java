package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Current active membership details for the authenticated user")
public record UserMembershipResponse(
    @Schema(description = "Plan type", example = "CREDITS_10", allowableValues = {"CREDITS_3", "CREDITS_5", "CREDITS_10", "CREDITS_20", "CREDITS_30", "COMPANY"})
    String plan,

    @Schema(description = "Membership status", example = "ACTIVE")
    String status,

    @Schema(description = "Membership start date")
    LocalDate startDate,

    @Schema(description = "Membership expiry date")
    LocalDate expiryDate
) {}
