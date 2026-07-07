package com.dob.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class CreditSummaryDto {
    private int totalCredits;
    private int creditsUsed;
    private int availableCredits;
    private int totalUnlocked;
    private String planType;
    private String planName;
}
