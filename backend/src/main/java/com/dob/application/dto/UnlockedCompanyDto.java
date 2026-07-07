package com.dob.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter @Builder
public class UnlockedCompanyDto {
    private UUID companyId;
    private String publicCompanyId;
    private String companyName;
    private String sector;
    private String city;
    private String state;
    private int creditsUsed;
    private Instant unlockedAt;
}
