package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Payment transaction details")
public record PaymentDto(
    @Schema(description = "Payment UUID")
    UUID id,

    @Schema(description = "User UUID who made the payment")
    UUID userId,

    @Schema(description = "Associated membership UUID (for membership payments)")
    UUID membershipId,

    @Schema(description = "Associated company UUID (for listing payments)")
    UUID companyId,

    @Schema(description = "Selected plan ID for credit-based plans", example = "CREDITS_10")
    String planId,

    @Schema(description = "Base amount in INR", example = "2500.00")
    BigDecimal amount,

    @Schema(description = "GST amount in INR", example = "450.00")
    BigDecimal gst,

    @Schema(description = "Total amount (base + GST) in INR", example = "2950.00")
    BigDecimal total,

    @Schema(description = "Razorpay order ID")
    String razorpayOrderId,

    @Schema(description = "Razorpay payment ID")
    String razorpayPaymentId,

    @Schema(description = "Payment status", example = "PAID", allowableValues = {"CREATED", "PAID", "FAILED", "REFUNDED"})
    String status,

    @Schema(description = "Payment type", example = "MEMBERSHIP", allowableValues = {"MEMBERSHIP", "LISTING"})
    String paymentType,

    @Schema(description = "Creation timestamp")
    Instant createdAt
) {}
