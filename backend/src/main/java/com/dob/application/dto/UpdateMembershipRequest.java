package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Request to update a member's subscription")
public record UpdateMembershipRequest(

    @Schema(description = "Plan type", example = "MONTHLY")
    String planType,

    @Schema(description = "New end date (ISO format)", example = "2027-06-28")
    LocalDate endDate,

    @Schema(description = "Download limit", example = "100")
    Integer downloadLimit,

    @Schema(description = "Action: EXTEND, CANCEL, ACTIVATE", example = "EXTEND")
    String action
) {}
