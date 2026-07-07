package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new subscription for a membership plan")
public record CreateSubscriptionRequest(
    @NotBlank(message = "Plan is required")
    @Schema(description = "Plan type", example = "CREDITS_10",
            allowableValues = {"CREDITS_3", "CREDITS_5", "CREDITS_10", "CREDITS_20", "CREDITS_30", "COMPANY"})
    String plan
) {}
