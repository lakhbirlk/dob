package com.dob.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class UnlockedCompany {
    private UUID id;
    private UUID memberId;
    private UUID companyId;
    private int creditsUsed;
    private Instant unlockedAt;
    private UUID unlockedBy;
}
