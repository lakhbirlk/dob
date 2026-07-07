package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CreditTransaction {
    private UUID id;
    private UUID memberId;
    private UUID companyId;
    private int creditsUsed;
    private String transactionType;
    private int balanceBefore;
    private int balanceAfter;
    private String status;
    private String transactionId;
    private Instant createdAt;
}
