package com.dob.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RefundRequest(
    @NotBlank String paymentId,
    String reason
) {}
