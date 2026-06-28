package com.dob.application.service;

import com.dob.application.dto.PaymentDto;
import com.dob.application.dto.PaymentOrderRequest;
import com.dob.application.dto.PaymentVerifyRequest;
import com.dob.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Local development stub — no Razorpay connection needed.
 */
@Slf4j
@Service
@Profile("local")
public class LocalPaymentService {

    public PaymentDto createOrder(UserPrincipal principal, PaymentOrderRequest request) {
        log.info("LOCAL MODE: Creating payment order for user {} amount {}", principal.id(), request.amount());
        return PaymentDto.builder()
            .id(UUID.randomUUID())
            .userId(principal.id())
            .amount(request.amount())
            .gst(request.amount().multiply(new BigDecimal("0.18")))
            .total(request.amount().multiply(new BigDecimal("1.18")))
            .razorpayOrderId("order_local_" + UUID.randomUUID().toString().substring(0, 8))
            .status("CREATED")
            .paymentType(request.paymentType())
            .createdAt(Instant.now())
            .build();
    }

    public PaymentDto verify(UserPrincipal principal, PaymentVerifyRequest request) {
        log.info("LOCAL MODE: Verifying payment {} for user {}", request.razorpayOrderId(), principal.id());
        return PaymentDto.builder()
            .id(UUID.randomUUID())
            .userId(principal.id())
            .amount(new BigDecimal("2500.00"))
            .gst(new BigDecimal("450.00"))
            .total(new BigDecimal("2950.00"))
            .razorpayOrderId(request.razorpayOrderId())
            .razorpayPaymentId(request.razorpayPaymentId())
            .status("PAID")
            .paymentType("MEMBERSHIP")
            .createdAt(Instant.now())
            .build();
    }

    public List<PaymentDto> getHistory(UserPrincipal principal) {
        return List.of();
    }

    public PaymentDto requestRefund(UserPrincipal principal, String paymentId) {
        log.info("LOCAL MODE: Refunding payment {}", paymentId);
        return PaymentDto.builder()
            .id(UUID.fromString(paymentId))
            .userId(principal.id())
            .status("REFUNDED")
            .createdAt(Instant.now())
            .build();
    }
}
