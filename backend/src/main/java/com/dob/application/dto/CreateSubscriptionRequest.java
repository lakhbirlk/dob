package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new subscription for a membership plan")
public record CreateSubscriptionRequest(
    @NotBlank(message = "Plan is required")
    @Schema(description = "Plan type", example = "RESEARCH", allowableValues = {"RESEARCH", "COMPANY"})
    String plan
) {}
