package com.dob.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter @Builder
public class UnlockCompanyResponse {
    private String transactionId;
    private UUID companyId;
    private int creditsUsed;
    private int remainingCredits;
    private String status;
    private String message;
}
