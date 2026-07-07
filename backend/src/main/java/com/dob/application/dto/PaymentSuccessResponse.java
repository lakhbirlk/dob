package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Response after successful payment — contains activated membership details")
public record PaymentSuccessResponse(
    @Schema(description = "Activated membership UUID")
    UUID membershipId,

    @Schema(description = "Plan type that was activated", example = "CREDITS_10")
    String plan,

    @Schema(description = "Membership status", example = "ACTIVE")
    String status,

    @Schema(description = "Membership start date")
    LocalDate startDate,

    @Schema(description = "Membership expiry date")
    LocalDate expiryDate,

    @Schema(description = "Generated transaction ID", example = "TXN-d4e5f6a7")
    String transactionId,

    @Schema(description = "Total amount paid in INR", example = "2950.00")
    BigDecimal amountPaid
) {}
