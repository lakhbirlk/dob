package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Payload for creating a Razorpay payment order")
public record PaymentOrderRequest(
    @Schema(description = "Payment amount in INR", example = "2950.00")
    @NotNull @Positive BigDecimal amount,

    @Schema(description = "Type of payment", example = "MEMBERSHIP", allowableValues = {"MEMBERSHIP", "LISTING"})
    @NotBlank String paymentType,

    @Schema(description = "Company UUID (required when paymentType is LISTING)")
    String companyId,

    @Schema(description = "Plan ID for credit-based plans", example = "CREDITS_10",
            allowableValues = {"CREDITS_3", "CREDITS_5", "CREDITS_10", "CREDITS_20", "CREDITS_30"})
    String planId
) {}
