package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Response after creating a subscription — returns the ID, amount due, and current status")
public record CreateSubscriptionResponse(
    @Schema(description = "Subscription (payment) UUID")
    UUID subscriptionId,

    @Schema(description = "Total amount in INR including GST", example = "2950.00")
    BigDecimal amount,

    @Schema(description = "Payment status", example = "PENDING")
    String status
) {}
