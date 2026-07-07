package com.dob.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter @Builder
public class CreditTransactionDto {
    private UUID id;
    private UUID companyId;
    private String companyName;
    private int creditsUsed;
    private String transactionType;
    private int balanceBefore;
    private int balanceAfter;
    private String status;
    private String transactionId;
    private Instant createdAt;
}
