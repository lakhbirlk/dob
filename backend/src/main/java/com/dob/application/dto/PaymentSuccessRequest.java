package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to complete a simulated payment and activate the subscription")
public record PaymentSuccessRequest(
    @NotNull(message = "Subscription ID is required")
    @Schema(description = "Subscription UUID returned from /api/subscriptions/create")
    UUID subscriptionId
) {}
