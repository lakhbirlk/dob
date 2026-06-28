package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for verifying a Razorpay payment")
public record PaymentVerifyRequest(
    @Schema(description = "Razorpay order ID returned from create-order", example = "order_Qwerty12345")
    @NotBlank String razorpayOrderId,

    @Schema(description = "Razorpay payment ID from the payment gateway", example = "pay_Abcde67890")
    @NotBlank String razorpayPaymentId,

    @Schema(description = "Razorpay payment signature for verification", example = "signature_xyz...")
    @NotBlank String razorpaySignature
) {}
