package com.dob.domain.event;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class RefundRequestedEvent {
    private UUID paymentId;
    private UUID userId;
    private UUID membershipId;
    private String reason;
    private Instant timestamp;
}
