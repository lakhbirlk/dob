package com.dob.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter @Builder
public class UnlockStatusResponse {
    private UUID companyId;
    private boolean unlocked;
    private Instant unlockedAt;
    private int creditsUsed;
}
