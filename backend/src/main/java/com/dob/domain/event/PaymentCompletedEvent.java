package com.dob.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class PaymentCompletedEvent {
    private UUID paymentId;
    private UUID userId;
    private BigDecimal amount;
    private String paymentType;
    private Instant timestamp;
}
